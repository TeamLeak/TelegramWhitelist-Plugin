package com.github.saintedlittle

import com.github.saintedlittle.bot.BotRunner
import com.github.saintedlittle.commands.BlockAllUsersCommand
import com.github.saintedlittle.commands.BlockUserCommand
import com.github.saintedlittle.data.WhitelistDatabase
import com.github.saintedlittle.listeners.JoinEventListener
import com.github.saintedlittle.storage.configuration
import com.github.saintedlittle.storage.database
import com.github.saintedlittle.storage.language
import com.github.saintedlittle.utils.FileManager
import org.bukkit.plugin.java.JavaPlugin

class twhitelist : JavaPlugin() {

    override fun onEnable() {
        init()
        registerEvents()
        registerCommands()

        BotRunner.startBot()
    }

    override fun onDisable() {
        database.close()
    }

    private fun init() {
        val fileManager = FileManager(this)
        configuration = fileManager.getConfig()
        language = fileManager.getLanguage((configuration["language"] ?: "en") as String)
        database = WhitelistDatabase(fileManager.getDatabaseUrl())
    }

    private fun registerEvents() {
        val pluginManager = server.pluginManager
        pluginManager.registerEvents(JoinEventListener(), this)
    }

    private fun registerCommands() {
        getCommand("blockuser")?.setExecutor(BlockUserCommand(database))
        getCommand("blockallusers")?.setExecutor(BlockAllUsersCommand(database))
    }
}
