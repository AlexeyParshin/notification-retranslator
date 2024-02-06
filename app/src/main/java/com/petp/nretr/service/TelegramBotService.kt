package com.petp.nretr.service

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.Dispatcher
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ParseMode
import com.petp.nretr.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TelegramBotService(
    private val notificationService: NotificationService,
    private val lifecycleOwner: LifecycleOwner
) {

    private val chatId = ChatId.fromId(BuildConfig.TELEGRAM_CHAT_ID.toLong())

    private val bot = bot {
        token = BuildConfig.TELEGRAM_BOT_TOKEN

        dispatch {
            setupHistoryCommand(this)
        }
    }

    init {
        bot.startPolling()
    }

    fun sendNotification(notification: String) {
        lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
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
        lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
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
}