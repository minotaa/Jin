package me.minota.utils

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player

class Chat {
    companion object {
        val serverPrefix = "&4&lSERVER >>&7"
        val gamePrefix = "&6&lGAME >>&7"
        val killStreakPrefix = "&c&lKILLSTREAK >>&7"

        fun format(message: String): String {
            return ChatColor.translateAlternateColorCodes('&', message)
        }

        fun send(sender: Player, message: String) {
            sender.sendMessage(format(message))
        }

        fun sendAll(message: String) {
            Bukkit.getOnlinePlayers().forEach { send(it, format(message)) }
        }
    }
}