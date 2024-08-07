package com.github.saintedlittle.bot.manager

import com.github.saintedlittle.bot.core.Command
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import com.github.saintedlittle.bot.MyTelegramBot
import com.github.saintedlittle.bot.annotation.AdminCommand
import com.github.saintedlittle.bot.annotation.PublicCommand
import com.github.saintedlittle.bot.command.ActivateCommand
import com.github.saintedlittle.bot.command.HelpCommand
import com.github.saintedlittle.storage.configuration
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

object CommandManager {
    private val commands = loadCommands()
    private val adminIds = configuration["admin_list"] as List<Long>

    fun processMessage(messageText: String, chat: Chat, user: User, bot: MyTelegramBot) {
        val args = messageText.split(" ").drop(1)
        val command = commands.find { messageText.startsWith(it.name) }

        if (command != null && isCommandAccessible(command, user.id)) {
            command.execute(args, chat, user, bot)
        } else {
            val errorMessage = SendMessage().apply {
                this.chatId = chat.id.toString()
                text = "You do not have permission to execute this command or command not found."
            }

            try {
                bot.execute(errorMessage)
            } catch (e: TelegramApiException) {
                bot.logger.severe("Failed to send message: ${e.message}")
            }
        }
    }

    private fun loadCommands(): List<Command> = listOf(
        ActivateCommand(),
        HelpCommand()
    )

    private fun isCommandAccessible(command: Command, userId: Long): Boolean {
        val commandClass = command::class.java
        val isAdminCommand = commandClass.isAnnotationPresent(AdminCommand::class.java)
        val isPublicCommand = commandClass.isAnnotationPresent(PublicCommand::class.java)

        return (isAdminCommand && isUserAdmin(userId)) || (isPublicCommand && !isAdminCommand)
    }

    private fun isUserAdmin(userId: Long): Boolean = userId in adminIds
}
