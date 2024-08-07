package com.github.saintedlittle.bot.command

import com.github.saintedlittle.bot.MyTelegramBot
import com.github.saintedlittle.bot.annotation.AdminCommand
import com.github.saintedlittle.bot.core.Command
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

@AdminCommand
class ActivateCommand : Command("/activate") {
    override fun execute(arguments: List<String>, chat: Chat?, user: User?, bot: MyTelegramBot): String {

        val message = SendMessage().apply {
            this.chatId = chat?.id.toString()
            text = """Available commands:
                   /start - Start the bot
                   /help - Show this help message
                   """
        }

        try {
            bot.execute(message)
        } catch (e: TelegramApiException) {
            bot.logger.severe("Failed to send message: ${e.message}")
        }

        return "Help command executed."
    }

}