package com.github.saintedlittle.bot

import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.meta.generics.BotSession

object BotRunner {

    fun startBot() {
        Thread {
            try {
                val botsApi = TelegramBotsApi(BotSession::class.java)
                botsApi.registerBot(MyTelegramBot())
            } catch (e: TelegramApiException) {
                e.printStackTrace()
            }
        }.start()
    }
}
