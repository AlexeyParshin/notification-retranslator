package com.petp.bankapp.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.petp.bankapp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class MainFrameService(private val activity: AppCompatActivity) {

    companion object {
        const val ACTION_UPDATE_NOTIFICATION = "com.petp.bankapp.UPDATE_NOTIFICATION"
        const val EXTRA_NOTIFICATION_TEXT = "notification_text"
    }

    private val notificationService = NotificationService(activity.applicationContext)

    private val notificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val text = intent.getStringExtra(EXTRA_NOTIFICATION_TEXT)
            if (text != null) {
                notificationService.insertNotification(text)
                updateNotificationsFrame()
            }
        }
    }

    init {
        val intentFilter = IntentFilter(ACTION_UPDATE_NOTIFICATION)
        activity.registerReceiver(notificationReceiver, intentFilter, Context.RECEIVER_NOT_EXPORTED)
    }

    fun updateNotificationsFrame() {
        CoroutineScope(Dispatchers.IO).launch {
            val notifications = notificationService.getNotifications()
            withContext(Dispatchers.Main) {
                val notificationsFrame = activity.findViewById<TextView>(R.id.notificationsFrame)
                notificationsFrame.text = notifications.joinToString("\n") { it.text }
            }
        }
    }
}