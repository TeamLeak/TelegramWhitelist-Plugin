package com.github.saintedlittle.bot.core

import com.github.saintedlittle.bot.MyTelegramBot
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User

abstract class Command(val name: String) {
    abstract fun execute(arguments: List<String>, chat: Chat?, user: User?, bot: MyTelegramBot): String

    open fun handleCallback(data: String, chat: Chat, user: User): String {
        return "Callback handling not implemented."
    }
}