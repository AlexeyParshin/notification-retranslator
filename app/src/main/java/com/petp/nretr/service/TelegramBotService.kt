package com.petp.nretr.service

import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.ServiceLifecycleDispatcher
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.Dispatcher
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ParseMode
import com.petp.nretr.BuildConfig
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TelegramBotService @Inject constructor() : LifecycleService() {
    @Inject
    lateinit var notificationService: NotificationService

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    private val chatId = ChatId.fromId(BuildConfig.TELEGRAM_CHAT_ID.toLong())

    private val bot = bot {
        token = BuildConfig.TELEGRAM_BOT_TOKEN

        dispatch {
            setupHistoryCommand(this)
        }
    }

    private val serviceLifecycleDispatcher = ServiceLifecycleDispatcher(this)

    override fun onCreate() {
        super.onCreate()
        serviceLifecycleDispatcher.onServicePreSuperOnCreate()
        serviceLifecycleDispatcher.onServicePreSuperOnBind()
        onStart()
    }

    private fun onStart() {
        bot.startPolling()
    }

    private fun onStop() {
        bot.stopPolling()
    }

    fun sendNotification(notification: String) {
        scope.launch {
            bot.sendMessage(chatId, notification).fold(
                {
                    Log.i("TelegramBotService", "Message sent: $notification")
                },
                {
                    Log.e("TelegramBotService", "Error sending message: ${it.get()}")
                }
            )
        }
    }

    private fun setupHistoryCommand(dispatcher: Dispatcher) {
        dispatcher.command("history") {
            sendHistory()
        }
    }

    private fun sendHistory() {
        scope.launch {
            val notifications = notificationService.getNotifications()
            val message = if (notifications.isNotEmpty()) {
                notifications.joinToString("\n")
            } else {
                "No notifications found."
            }
            bot.sendMessage(chatId, message, parseMode = ParseMode.MARKDOWN).fold(
                {
                    Log.i("TelegramBotService", "History sent")
                },
                {
                    Log.e("TelegramBotService", "Error sending history: ${it.get()}")
                },
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel() // cancel the job when the service is destroyed
        serviceLifecycleDispatcher.onServicePreSuperOnDestroy()
        onStop()
    }
}