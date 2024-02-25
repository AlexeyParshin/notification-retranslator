package com.petp.nretr.com.petp.nretr.repository

interface NotificationRepository {
    fun insertNotification(text: String)
    fun getNotifications(): List<String>
    fun clearNotifications()
}