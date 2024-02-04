package com.petp.bankapp.service

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.petp.bankapp.core.entity.Notification

class NotificationService(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("notifications", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun insertNotification(text: String) {
        val notifications = getNotifications().toMutableList()
        if (notifications.size >= 5) {
            notifications.removeAt(0) // remove the oldest notification if there are already 5 notifications
        }
        notifications.add(Notification(0, text)) // add the new notification
        val notificationsJson = gson.toJson(notifications)
        sharedPreferences.edit().putString("notifications", notificationsJson).apply()
    }

    fun getNotifications(): List<Notification> {
        val notificationsJson = sharedPreferences.getString("notifications", null)
        return if (notificationsJson != null) {
            val type = object : TypeToken<List<Notification>>() {}.type
            gson.fromJson(notificationsJson, type)
        } else {
            emptyList()
        }
    }
}