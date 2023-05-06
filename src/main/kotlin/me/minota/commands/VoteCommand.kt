package me.minota.commands

import me.minota.utils.Chat
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class VoteCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command?,
        label: String?,
        args: Array<out String>
    ): Boolean {
        Chat.send(sender as Player, "${Chat.serverPrefix} Vote for the server using the following links: ")
        Chat.send(sender, "&8- &fhttps://minecraft-server-list.com/server/492114/vote/")
        Chat.send(sender, "&8- &fhttps://crafty.gg/servers/applejuice.bar")
        Chat.send(sender, "&8- &fhttps://minecraftservers.org/vote/643596")
        Chat.send(sender, "&8- &fhttps://servers-minecraft.net/server-applejuice-bar.22782")
        Chat.send(sender, "&8- &fhttps://minecraft-mp.com/server/312263/vote/")
        Chat.send(sender, "&8- &fhttps://topminecraftservers.org/server/30693")
        Chat.send(sender, "&8- &fhttps://best-minecraft-servers.co/server-applejuice-bar.11819/vote")
        Chat.send(sender, "&8- &fhttps://minecraft.buzz/server/6321")
        Chat.send(sender, "&8- &fhttps://topg.org/minecraft-servers/server-649669")
        Chat.send(sender, "&8- &fhttps://craftlist.org/applejuice-bar")
        Chat.send(sender, "&8- &fhttps://minecraftlist.org/server/30002")
        Chat.send(sender, "&8- &fhttps://topmcservers.com/server/2246/vote")
        Chat.send(sender, "&8- &fhttps://www.earlyshiftserverlist.com/server-applejuice-bar.50/vote")
        Chat.send(sender, "&8- &fhttps://minecrafttop.com/server/applejuicebar-a14e03")
        Chat.send(sender, "&8- &fhttps://mc-lists.org/server-applejuice-bar.4593/vote")
        Chat.send(sender, "&8- &fhttps://www.planetminecraft.com/server/applejuice-bar/vote/")
        return true
    }

}