package com.petp.nretr.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import com.petp.nretr.BuildConfig
import com.petp.nretr.activities.MainActivity
import com.petp.nretr.activities.MainActivity.Companion.ACTION_UPDATE_NOTIFICATION_FRAME
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
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

        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            RECEIVER_NOT_EXPORTED
        } else {
            0
        }

        context.registerReceiver(notificationReceiver, IntentFilter(ACTION_UPDATE_NOTIFICATION), flags)
        updateNotificationsFrame()
    }

    private fun updateNotificationsFrame() {
        val notifications = notificationService.getNotifications().ifEmpty { return }

        // Create an Intent with the custom action
        val intent = Intent(ACTION_UPDATE_NOTIFICATION_FRAME)
        intent.setPackage(BuildConfig.APPLICATION_ID)

        // Put the notifications data into the Intent
        intent.putStringArrayListExtra(MainActivity.NOTIFICATIONS, ArrayList(notifications))

        // Send the Intent
        context.sendBroadcast(intent)
    }

    fun clearNotifications() {
        notificationService.clearNotifications()

        // Send a broadcast to clear the notifications frame
        val intent = Intent(ACTION_CLEAR_NOTIFICATION_FRAME)
        intent.setPackage(BuildConfig.APPLICATION_ID)
        context.sendBroadcast(intent)
    }
}