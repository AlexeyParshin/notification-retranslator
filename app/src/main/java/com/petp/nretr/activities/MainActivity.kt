package com.petp.nretr.activities

import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.petp.nretr.R
import com.petp.nretr.service.MainFrameService
import com.petp.nretr.service.notifications.NotificationListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var mainFrameService: MainFrameService

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainFrameService = MainFrameService(this)

        // Start NotificationListener
        val serviceIntent = Intent(this, NotificationListener::class.java)
        startService(serviceIntent)

        if (!isNotificationServiceEnabled()) {
            startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
        }

        buttonInit()
    }

    private fun buttonInit() {
        val appsButton = findViewById<Button>(R.id.appsButton)
        appsButton.setOnClickListener {
            val intent = Intent(this, AppsListActivity::class.java)
            startActivity(intent)
        }
    }

    private fun isNotificationServiceEnabled(): Boolean {
        val pkgName = packageName
        val flat = Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
        if (!flat.isNullOrEmpty()) {
            val names = flat.split(":").toTypedArray()
            for (name in names) {
                val cn = ComponentName.unflattenFromString(name)
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.packageName)) {
                        return true
                    }
                }
            }
        }
        return false
    }
}