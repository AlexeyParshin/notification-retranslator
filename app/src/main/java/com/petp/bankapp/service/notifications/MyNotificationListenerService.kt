package com.petp.bankapp.service.notifications

import android.content.Context
import android.content.Intent
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.annotation.RequiresApi
import com.petp.bankapp.service.MainFrameService

class MyNotificationListenerService : NotificationListenerService() {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val sharedPreferences = getSharedPreferences("checked_apps", Context.MODE_PRIVATE)
        val checkedAppPackageNames = sharedPreferences.getStringSet("checked_app_package_names", emptySet())

        // Check if the notification is from a checked app
        if (checkedAppPackageNames?.contains(sbn.packageName) == true) {
            val notification = sbn.notification
            val extras = notification.extras
            val text = extras.getCharSequence("android.text").toString()

            val intent = Intent(MainFrameService.ACTION_UPDATE_NOTIFICATION)
            intent.setPackage("com.petp.bankapp")
            intent.putExtra(MainFrameService.EXTRA_NOTIFICATION_TEXT, text)
            sendBroadcast(intent)
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        // Handle notification removal if needed
    }
}