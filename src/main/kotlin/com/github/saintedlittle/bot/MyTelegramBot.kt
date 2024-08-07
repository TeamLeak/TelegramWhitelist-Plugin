package com.github.saintedlittle.bot

import com.github.saintedlittle.bot.manager.CommandManager
import com.github.saintedlittle.storage.configuration
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.objects.Update
import java.util.logging.FileHandler
import java.util.logging.Logger
import java.util.logging.SimpleFormatter

class MyTelegramBot : TelegramLongPollingBot() {

    val logger: Logger = Logger.getLogger(MyTelegramBot::class.java.name)

    init {
        setupLogger()
    }

    private fun setupLogger() {
        try {
            val fileHandler = FileHandler("telegram-bot.log", true)
            fileHandler.formatter = SimpleFormatter()
            logger.addHandler(fileHandler)
            logger.info("Logger initialized.")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getBotToken(): String {
        return configuration["botToken"] as String
    }

    override fun getBotUsername(): String {
        return configuration["botUsername"] as String
    }

    override fun onUpdateReceived(update: Update) {
        if (update.hasMessage() && update.message.hasText()) {
            val chatId = update.message.chatId.toString()
            val messageText = update.message.text
            val chat = update.message.chat
            val user = update.message.from

            logger.info("Received message: $messageText from chat ID: $chatId")

            CommandManager.processMessage(messageText, chat, user, this)
        }
    }
}