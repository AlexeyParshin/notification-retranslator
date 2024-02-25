package com.petp.nretr.com.petp.nretr.repository

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.petp.nretr.module.NOTIFICATIONS
import javax.inject.Inject
import javax.inject.Named

class NotificationRepositoryImpl @Inject constructor(
    @Named("NotificationPreferences") private val sharedPreferences: SharedPreferences
) : NotificationRepository {
    companion object {
        private const val NOTIFICATION_LIMIT = 15
    }

    private val gson = Gson()
    override fun insertNotification(text: String) {
        val notifications = getNotifications().toMutableList()
        if (notifications.size >= NOTIFICATION_LIMIT) {
            notifications.removeAt(0) // remove the oldest notification if there are already 15 notifications
        }
        notifications.add(text) // add the new notification
        val notificationsJson = gson.toJson(notifications)
        sharedPreferences.edit().putString(NOTIFICATIONS, notificationsJson).apply()
    }

    override fun getNotifications(): List<String> {
        val notificationsJson = sharedPreferences.getString(NOTIFICATIONS, null)
        return if (notificationsJson != null) {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(notificationsJson, type)
        } else {
            emptyList()
        }
    }

    override fun clearNotifications() {
        sharedPreferences.edit().remove(NOTIFICATIONS).apply()
    }
}