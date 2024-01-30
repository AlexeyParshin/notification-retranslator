package com.petp.bankapp.notifications

import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.petp.bankapp.MainFrameService

class MyNotificationListenerService : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val notification = sbn.notification
        val extras = notification.extras
        val title = extras.getString("android.title")
        val text = extras.getCharSequence("android.text").toString()

        val intent = Intent(MainFrameService.ACTION_UPDATE_NOTIFICATION)
        intent.setPackage("com.petp.bankapp")
        intent.putExtra(MainFrameService.EXTRA_NOTIFICATION_TEXT, text)
        sendBroadcast(intent)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        // Handle notification removal if needed
    }
}