package com.github.saintedlittle.bot.command

import com.github.saintedlittle.bot.core.Command
import com.github.saintedlittle.bot.annotation.KeyboardLayout
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import com.github.saintedlittle.bot.MyTelegramBot
import com.github.saintedlittle.bot.utils.KeyboardUtil
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

@KeyboardLayout(rows = ["BUTTON 1,BUTTON 2", "BUTTON 3,BUTTON 4"])
class HelpCommand : Command("/help") {
    override fun execute(arguments: List<String>, chat: Chat?, user: User?, bot: MyTelegramBot): String {
        val keyboard = KeyboardUtil.createKeyboardFromAnnotation(this::class)

        val message = SendMessage().apply {
            this.chatId = chat?.id.toString()
            text = "Available commands:\n/start - Start the bot\n/help - Show this help message"
            replyMarkup = keyboard
        }

        try {
            bot.execute(message)
        } catch (e: TelegramApiException) {
            bot.logger.severe("Failed to send message: ${e.message}")
        }

        return "Help command executed."
    }

    override fun handleCallback(data: String, chat: Chat, user: User): String {
        return "Handling callback with data: $data"
    }
}
