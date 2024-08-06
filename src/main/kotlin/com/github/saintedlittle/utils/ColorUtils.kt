package com.github.saintedlittle.utils

import org.bukkit.ChatColor

object ColorUtils {

    fun colorize(message: String): String {
        return ChatColor.translateAlternateColorCodes('&', message)
    }
}
