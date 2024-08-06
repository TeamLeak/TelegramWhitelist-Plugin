package com.github.saintedlittle.bot

import com.github.saintedlittle.bot.CommandManager.createReplyKeyboard
import com.github.saintedlittle.bot.CommandManager.processMessage
import com.github.saintedlittle.storage.configuration
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import java.util.logging.FileHandler
import java.util.logging.Logger
import java.util.logging.SimpleFormatter

class MyTelegramBot : TelegramLongPollingBot() {

    private val logger = Logger.getLogger(MyTelegramBot::class.java.name)

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

            logger.info("Received message: $messageText from chat ID: $chatId")

            val responseMessage = processMessage(messageText)

            val message = SendMessage()
            message.chatId = chatId
            message.text = responseMessage
            message.replyMarkup = createReplyKeyboard()

            try {
                execute(message)
                logger.info("Sent response message to chat ID: $chatId")
            } catch (e: TelegramApiException) {
                logger.severe("Failed to send message: ${e.message}")
            }
        }
    }

}
