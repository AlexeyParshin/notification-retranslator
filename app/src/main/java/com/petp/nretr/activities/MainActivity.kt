package com.petp.nretr.activities

import android.content.*
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.petp.nretr.R
import com.petp.nretr.service.MainFrameService
import com.petp.nretr.service.MainFrameService.Companion.ACTION_CLEAR_NOTIFICATION_FRAME
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var mainFrameService: MainFrameService

    companion object {
        const val ACTION_UPDATE_NOTIFICATION_FRAME = "com.petp.nretr.UPDATE_NOTIFICATION_FRAME"
        const val NEW_NOTIFICATIONS = "new_notifications"
    }

    private val frameUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val notifications = intent.getStringArrayListExtra(NEW_NOTIFICATIONS)
            if (notifications != null) {
                updateNotificationsFrame(notifications)
            }
        }
    }

    private val clearFrameReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            clearNotificationsFrame()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        registerReceiver(frameUpdateReceiver, IntentFilter(ACTION_UPDATE_NOTIFICATION_FRAME), RECEIVER_NOT_EXPORTED)
        registerReceiver(clearFrameReceiver, IntentFilter(ACTION_CLEAR_NOTIFICATION_FRAME), RECEIVER_NOT_EXPORTED)

        // do i need this?
        val serviceIntent = Intent(this, MainFrameService::class.java)
        startService(serviceIntent)

        if (!isNotificationServiceEnabled()) {
            startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
        }

        appsButtonInit()
        clearButtonInit()
    }

    private fun clearButtonInit() {
        val clearNotificationsButton: Button = findViewById(R.id.clear_notifications_button)
        clearNotificationsButton.setOnClickListener {
            mainFrameService.clearNotifications()
        }
    }

    private fun appsButtonInit() {
        val appsButton = findViewById<Button>(R.id.appsButton)
        appsButton.setOnClickListener {
            val intent = Intent(this, AppsListActivity::class.java)
            startActivity(intent)
        }
    }

    private fun isNotificationServiceEnabled(): Boolean {
        val pkgName = packageName
        val flat = Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
        return flat?.split(":")?.any { ComponentName.unflattenFromString(it)?.packageName == pkgName } ?: false
    }

    private fun updateNotificationsFrame(notifications: List<String>) {
        CoroutineScope(Dispatchers.Main).launch {
            val notificationsFrame = findViewById<TextView>(R.id.notificationsFrame)
            notificationsFrame.text = notifications.joinToString("\n") { it }
        }
    }

    private fun clearNotificationsFrame() {
        CoroutineScope(Dispatchers.Main).launch {
            val notificationsFrame = findViewById<TextView>(R.id.notificationsFrame)
            notificationsFrame.text = getString(R.string.no_notifications_found)
        }
    }
}