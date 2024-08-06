package com.github.saintedlittle.commands

import com.github.saintedlittle.data.WhitelistDatabase
import com.github.saintedlittle.storage.language
import com.github.saintedlittle.utils.MessageUtils
import com.github.saintedlittle.utils.ColorUtils
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class BlockAllUsersCommand(private val database: WhitelistDatabase) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.size != 1) {
            sender.sendMessage(ColorUtils.colorize(with(MessageUtils) { formatCommandMessage("/blockallusers <username>") }))
            return false
        }

        val username = args[0]

        try {
            database.blockAllAccountsByUsername(username)
            val successMessage = (language["successfully_blocked_all"] ?: "All accounts with username {username} have been blocked!") as String
            sender.sendMessage(with(ColorUtils) { colorize(MessageUtils.formatMessage(successMessage, username)) })
        } catch (e: Exception) {
            sender.sendMessage(with(ColorUtils) { colorize("&cAn error occurred while blocking all accounts: ${e.message}") })
        }

        return true
    }
}
