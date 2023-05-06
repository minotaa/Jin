package me.minota.commands

import me.minota.utils.Chat
import me.minota.utils.Settings
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SetSpawnCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command?,
        label: String?,
        args: Array<out String>
    ): Boolean {
        if (sender !is Player) {
            sender.sendMessage("You must be a player to use this command.")
            return false
        }
        if (!sender.hasPermission("admin.setspawn")) {
            Chat.send(sender, "&cYou do not have permission to use this command.")
            return true
        }
        Settings.instance.data!!.set("spawn.world", sender.world.name)
        Settings.instance.data!!.set("spawn.x", sender.location.x)
        Settings.instance.data!!.set("spawn.y", sender.location.y)
        Settings.instance.data!!.set("spawn.z", sender.location.z)
        Settings.instance.data!!.set("spawn.yaw", sender.location.yaw)
        Settings.instance.data!!.set("spawn.pitch", sender.location.pitch)
        Settings.instance.saveData()
        Chat.send(sender, "${Chat.serverPrefix} Successfully set the server spawn to your location.")
        return true
    }
}