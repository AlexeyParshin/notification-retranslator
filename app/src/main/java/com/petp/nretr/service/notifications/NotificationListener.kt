package com.petp.nretr.service.notifications

import android.content.Intent
import android.content.SharedPreferences
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.petp.nretr.module.CHECKED_APPS_PACKAGE_NAMES_KEY
import com.petp.nretr.service.MainFrameService.Companion.ACTION_UPDATE_NOTIFICATION
import com.petp.nretr.service.MainFrameService.Companion.EXTRA_NOTIFICATION_TEXT
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class NotificationListener : NotificationListenerService() {

    @Inject
    @Named("CheckedAppsPreferences")
    lateinit var checkedAppsPreferences: SharedPreferences

    override fun onNotificationPosted(statusBarNotification: StatusBarNotification) {
        val checkedAppPackageNames = checkedAppsPreferences.getStringSet(CHECKED_APPS_PACKAGE_NAMES_KEY, emptySet())

        // Check if the notification is from a checked app
        if (checkedAppPackageNames?.contains(statusBarNotification.packageName) == true) {
            val notification = statusBarNotification.notification
            val extras = notification.extras
            val text = extras.getCharSequence("android.text")?.toString() ?: return // if null -> skip
            val appName = statusBarNotification.packageName.substringAfterLast(delimiter = ".")
                .replaceFirstChar { it.uppercase() }
            val message = "$appName: $text"

            val intent = Intent(ACTION_UPDATE_NOTIFICATION)
            intent.setPackage("com.petp.nretr")
            intent.putExtra(EXTRA_NOTIFICATION_TEXT, message)
            sendBroadcast(intent)
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {}
}