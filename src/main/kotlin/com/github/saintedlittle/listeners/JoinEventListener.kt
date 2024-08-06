package com.github.saintedlittle.listeners

import com.github.saintedlittle.storage.database
import com.github.saintedlittle.storage.language
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class JoinEventListener : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) = auth(event.player)

    private fun auth(player: Player) {
        if (database.isUsernameBlocked(player.name))
            player.kick(Component.text((language["whitelist_checking"] ?: "You are not on the whitelist!") as String, NamedTextColor.RED))
    }
}
