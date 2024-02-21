package com.petp.nretr.service

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NotificationService @Inject constructor(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("notifications", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val NOTIFICATION_LIMIT = 15
    }

    fun insertNotification(text: String) {
        val notifications = getNotifications().toMutableList()
        if (notifications.size >= NOTIFICATION_LIMIT) {
            notifications.removeAt(0) // remove the oldest notification if there are already 15 notifications
        }
        notifications.add(text) // add the new notification
        val notificationsJson = gson.toJson(notifications)
        sharedPreferences.edit().putString("notifications", notificationsJson).apply()
    }

    fun getNotifications(): List<String> {
        val notificationsJson = sharedPreferences.getString("notifications", null)
        return if (notificationsJson != null) {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(notificationsJson, type)
        } else {
            emptyList()
        }
    }

    fun clearNotifications() {
        sharedPreferences.edit().remove("notifications").apply()
    }
}