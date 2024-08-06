package com.github.saintedlittle.bot

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow

object CommandManager {


    fun processMessage(messageText: String): String {
        return when {
            messageText.startsWith("/start") -> handleStartCommand()
            messageText.startsWith("/help") -> handleHelpCommand()
            else -> "Unknown command. Use /help to see available commands."
        }
    }

    private fun handleStartCommand(): String {
        return "Welcome to the bot! Use the buttons below."
    }

    private fun handleHelpCommand(): String {
        return "Available commands:\n/start - Start the bot\n/help - Show this help message"
    }

    fun createReplyKeyboard(): ReplyKeyboardMarkup {
        val keyboard = ReplyKeyboardMarkup()
        keyboard.resizeKeyboard = true

        val button1 = KeyboardButton("Button 1")
        val button2 = KeyboardButton("Button 2")

        val keyboardRow = listOf(button1, button2)
        keyboard.keyboard = listOf(KeyboardRow(keyboardRow))

        return keyboard
    }

}