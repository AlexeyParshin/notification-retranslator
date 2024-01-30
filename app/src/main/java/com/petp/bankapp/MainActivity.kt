package com.petp.bankapp

import android.content.ComponentName
import android.content.Intent
import android.provider.Settings
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.content.Context
import android.app.NotificationManager
import androidx.core.app.NotificationCompat
import com.petp.bankapp.notifications.MyNotificationListenerService

class MainActivity : AppCompatActivity() {

    private lateinit var mainFrameService: MainFrameService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainFrameService = MainFrameService(this)

        // Start MyNotificationListenerService
        val serviceIntent = Intent(this, MyNotificationListenerService::class.java)
        startService(serviceIntent)

        if (!isNotificationServiceEnabled()) {
            startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
        }

        val sendNotificationImage = findViewById<ImageView>(R.id.sendNotificationImage)
        sendNotificationImage.setOnClickListener {
            sendNotification(this, "Notification Title", "Notification Text")
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

    private fun sendNotification(context: Context, title: String, text: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(0, notification)

        val intent = Intent("com.petp.bankapp.NOTIFICATION_SENT")
        context.sendBroadcast(intent)
    }
}