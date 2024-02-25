package com.petp.nretr.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.petp.nretr.com.petp.nretr.repository.NotificationRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NotificationService @Inject constructor() : Service() {

    @Inject
    lateinit var notificationRepository: NotificationRepository
    override fun onBind(intent: Intent?): IBinder? {
        throw UnsupportedOperationException("Not implemented")
    }

    fun insertNotification(text: String) {
        notificationRepository.insertNotification(text)
    }

    fun getNotifications(): List<String> {
        return notificationRepository.getNotifications()
    }

    fun clearNotifications() {
        notificationRepository.clearNotifications()
    }
}