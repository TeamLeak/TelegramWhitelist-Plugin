package com.github.saintedlittle.utils

import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.IOException

class FileManager(private val plugin: JavaPlugin) {

    private val dbFolder: File = File(plugin.dataFolder, "db")
    private val langFolder: File = File(plugin.dataFolder, "lang")
    private val configFile: File = File(plugin.dataFolder, "config.json")
    private val dbFile: File = File(dbFolder, "database.db")

    init {
        createDirectoriesAndFiles()
    }

    private fun createDirectoriesAndFiles() {
        if (!dbFolder.exists()) dbFolder.mkdirs()

        if (!dbFile.exists()) try {
            dbFile.createNewFile()
        } catch (e: IOException) {
            plugin.logger.severe("Failed to create database file: ${e.message}")
        }

        if (!langFolder.exists()) langFolder.mkdirs()

        if (!configFile.exists()) try {
            configFile.createNewFile()
            val defaultConfig = """
                {
                    "language": "en",
                    "bot_token": "",
                    "botUsername": "",
                    "admin_list": [123456789, 987654321]
                }
            """.trimIndent()
            configFile.writeText(defaultConfig)
        } catch (e: IOException) {
            plugin.logger.severe("Failed to create config file: ${e.message}")
        }
    }

    fun getDatabaseUrl(): String {
        return "jdbc:sqlite:${dbFile.absolutePath}"
    }

    fun getConfig(): Map<String, Any> {
        return JsonFileManager.readJsonFile(configFile)
    }

    fun getLanguage(locale: String): Map<String, Any> {
        val langFile = File(langFolder, "$locale.json")
        return if (langFile.exists()) {
            JsonFileManager.readJsonFile(langFile)
        } else {
            plugin.logger.warning("Language file for locale '$locale' does not exist!")
            createLanguageFile()
        }
    }

    private fun createLanguageFile(): Map<String, Any> {
        val langFile = File(langFolder, "en.json")
        if (!langFile.exists()) {
            try {
                langFile.createNewFile()
                val defaultLang = mapOf(
                    "whitelist_checking" to "You are not on the whitelist!",
                    "successfully_blocked" to "User {username} has been blocked.",
                    "successfully_blocked_all" to "All accounts on account with one of usernames is {username} have been blocked!",
                    "usage_message" to "&cUsage:"
                )
                JsonFileManager.writeJsonFile(langFile, defaultLang)
                plugin.logger.info("Created default language file!")
            } catch (e: IOException) {
                plugin.logger.severe("Failed to create language file: ${e.message}")
            }
        }
        return JsonFileManager.readJsonFile(langFile)
    }
}
