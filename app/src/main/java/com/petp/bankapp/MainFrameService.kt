package com.petp.bankapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainFrameService(private val activity: AppCompatActivity) {

    companion object {
        const val ACTION_UPDATE_NOTIFICATION = "com.petp.bankapp.UPDATE_NOTIFICATION"
        const val EXTRA_NOTIFICATION_TEXT = "notification_text"
    }

    private var isFirstNotification = true

    private val notificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val text = intent.getStringExtra(EXTRA_NOTIFICATION_TEXT)
            if (text != null) {
                updateNotificationsFrame(text)
            }
        }
    }

    init {
        val intentFilter = IntentFilter(ACTION_UPDATE_NOTIFICATION)
        activity.registerReceiver(notificationReceiver, intentFilter)
    }

    fun updateNotificationsFrame(text: String) {
        val notificationsFrame = activity.findViewById<TextView>(R.id.notificationsFrame)
        if (isFirstNotification) {
            notificationsFrame.text = ""
            isFirstNotification = false
        }
        notificationsFrame.append("$text\n")
    }
}