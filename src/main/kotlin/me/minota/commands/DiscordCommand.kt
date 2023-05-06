package me.minota.commands

import me.minota.utils.Chat
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class DiscordCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command?,
        label: String?,
        args: Array<out String>
    ): Boolean {
        Chat.send(sender as Player, "${Chat.serverPrefix} Join our discord at &9https://discord.gg/GQcwRfKMAD")
        return true
    }
}