package com.petp.nretr.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.annotation.RequiresApi
import com.petp.nretr.activities.MainActivity.Companion.ACTION_UPDATE_NOTIFICATION_FRAME
import com.petp.nretr.activities.MainActivity.Companion.NEW_NOTIFICATIONS
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class MainFrameService @Inject constructor() : Service() {

    companion object {
        const val ACTION_UPDATE_NOTIFICATION = "com.petp.nretr.UPDATE_NOTIFICATION"
        const val ACTION_CLEAR_NOTIFICATION_FRAME = "com.petp.nretr.CLEAR_NOTIFICATION_FRAME"
        const val EXTRA_NOTIFICATION_TEXT = "notification_text"
    }

    @Inject
    lateinit var context: Context

    @Inject
    lateinit var notificationService: NotificationService

    @Inject
    lateinit var telegramBotService: TelegramBotService

    override fun onBind(intent: Intent?): Nothing {
        throw UnsupportedOperationException("Not implemented")
    }

    private val notificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val text = intent.getStringExtra(EXTRA_NOTIFICATION_TEXT)
            if (text != null) {
                notificationService.insertNotification(text)
                updateNotificationsFrame()
                telegramBotService.sendNotification(text)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        context.registerReceiver(
            notificationReceiver, IntentFilter(ACTION_UPDATE_NOTIFICATION),
            Context.RECEIVER_NOT_EXPORTED
        )
    }

    private fun updateNotificationsFrame() {
        val notifications = notificationService.getNotifications()

        // Create an Intent with the custom action
        val intent = Intent(ACTION_UPDATE_NOTIFICATION_FRAME)

        // Put the notifications data into the Intent
        intent.putStringArrayListExtra(NEW_NOTIFICATIONS, ArrayList(notifications))

        // Send the Intent
        context.sendBroadcast(intent)
    }

    fun clearNotifications() {
        notificationService.clearNotifications()

        // Send a broadcast to clear the notifications frame
        val intent = Intent(ACTION_CLEAR_NOTIFICATION_FRAME)
        context.sendBroadcast(intent)
    }
}