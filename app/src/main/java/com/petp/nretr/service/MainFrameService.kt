package com.petp.nretr.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.petp.nretr.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@AndroidEntryPoint
class MainFrameService(private val context: Context) {

    companion object {
        const val ACTION_UPDATE_NOTIFICATION = "com.petp.nretr.UPDATE_NOTIFICATION"
        const val EXTRA_NOTIFICATION_TEXT = "notification_text"
    }

    @Inject
    lateinit var notificationService: NotificationService

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
        context.registerReceiver(notificationReceiver, intentFilter, Context.RECEIVER_NOT_EXPORTED)
    }

    fun updateNotificationsFrame() {
        CoroutineScope(Dispatchers.IO).launch {
            val notifications = notificationService.getNotifications()
            withContext(Dispatchers.Main) {
                val notificationsFrame = context.findViewById<TextView>(R.id.notificationsFrame)
                notificationsFrame.text = notifications.joinToString("\n") { it.text }
            }
        }
    }
}