package com.petp.nretr.activities

import android.content.*
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.petp.nretr.R
import com.petp.nretr.service.MainFrameService
import com.petp.nretr.service.MainFrameService.Companion.ACTION_CLEAR_NOTIFICATION_FRAME
import com.petp.nretr.service.TelegramBotService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var mainFrameService: MainFrameService

    companion object {
        const val ACTION_UPDATE_NOTIFICATION_FRAME = "com.petp.nretr.UPDATE_NOTIFICATION_FRAME"
        const val NOTIFICATIONS = "notifications"
    }

    private val frameUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val notifications = intent.getStringArrayListExtra(NOTIFICATIONS)
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

        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            RECEIVER_NOT_EXPORTED
        } else {
            0
        }

        registerReceiver(frameUpdateReceiver, IntentFilter(ACTION_UPDATE_NOTIFICATION_FRAME), flags)
        registerReceiver(clearFrameReceiver, IntentFilter(ACTION_CLEAR_NOTIFICATION_FRAME), flags)

        val mainFrameService = Intent(this, MainFrameService::class.java)
        startService(mainFrameService)
        val telegramBotService = Intent(this, TelegramBotService::class.java)
        startService(telegramBotService)

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