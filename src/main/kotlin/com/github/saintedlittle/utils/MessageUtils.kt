package com.github.saintedlittle.utils

import com.github.saintedlittle.storage.language
import java.text.SimpleDateFormat
import java.util.Date
import org.bukkit.entity.Player

object MessageUtils {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    fun formatMessage(message: String, player: Player): String {
        val date = dateFormat.format(Date())
        return message
            .replace("{player}", player.name)
            .replace("{date}", date)
    }

    fun formatMessage(message: String, player: String): String {
        return message
            .replace("{player}", player)
    }

    fun formatCommandMessage(command: String): String {
        return (language["usage_message"] ?: "Usage: ") as String + " " + command
    }
}
