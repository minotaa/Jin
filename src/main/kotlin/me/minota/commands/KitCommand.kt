package me.minota.commands

import me.minota.Jin
import me.minota.features.SpawnFeature
import me.minota.utils.Chat
import me.minota.utils.GUI
import me.minota.utils.ItemBuilder
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class KitCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command?,
        label: String?,
        args: Array<out String>
    ): Boolean {
        val gui = GUI()
        val player = sender as Player
        gui.name("&aKit Selector")
            .owner(player)
            .rows(1)
        val default = ItemBuilder(Material.IRON_CHESTPLATE)
            .name("&aDefault Kit")
            .addLore("&6Kit Perks:")
            .addLore(" &8-&7 Iron Sword")
            .addLore(" &8-&7 Chainmail/Iron Armor")
            .addLore(" ")
            .addLore("&7&oClean and simple.")
            .make()
        val frog = ItemBuilder(Material.SKULL_ITEM)
            .toSkull()
            .setOwner("Spettu")
            .name("&aFrog Kit")
            .addLore("&6Kit Perks:")
            .addLore(" &8-&7 Permanent Jump Boost IV")
            .addLore(" &8-&7 Sharpness I Diamond Sword")
            .addLore(" &8-&7 Protection I Leather Armor")
            .addLore(" ")
            .addLore("&7&oRibbit, ribbit!")
            .make()
        val pyromancer = ItemBuilder(Material.BLAZE_ROD)
            .name("&aPyromancer Kit")
            .addLore("&6Kit Perks:")
            .addLore(" &8-&7 Fire Wand (5 Charges)")
            .addLore(" &8-&7 Fire Protection I Iron Armor")
            .addLore(" &8-&7 Every 3 kills recharges Fire Wand.")
            .addLore(" &8-&7 Every kill gives you a Fire Resistance effect.")
            .addLore(" ")
            .addLore("&7&oSome people just like to watch the world burn.")
            .make()
        val ninja = ItemBuilder(Material.FEATHER)
            .name("&aNinja Kit")
            .addLore("&6Kit Perks:")
            .addLore(" &8-&7 Permanent Speed II")
            .addLore(" &8-&7 Permanent Night Vision")
            .addLore(" &8-&7 Invisible/Weak while sneaking, weakness remains for 5s after sneaking")
            .addLore(" &8-&7 Sharpness I Iron Sword")
            .addLore(" &8-&7 Protection I Diamond Chestplate")
            .addLore(" ")
            .addLore("&7&oNo... that would be your mother!")
            .make()
        gui.item(0, default).onClick runnable@ {
            it.isCancelled = true
            JavaPlugin.getPlugin(Jin::class.java).statsHandler.getStatsPlayer(player)!!.kit = "DEFAULT"
            Chat.send(player, "${Chat.gamePrefix} &7You have selected the &6Default Kit&7!")
            (player as Player).playSound(player.location, Sound.NOTE_PLING, 1f, 1f)
            player.closeInventory()
            if (SpawnFeature.instance.isInSpawn[player.uniqueId] != true) {
                Chat.send(player, "${Chat.gamePrefix} Your kit will be applied the next time you respawn!")
            } else {
                SpawnFeature.instance.send(player)
            }
            return@runnable
        }
        for ((index, kit) in JavaPlugin.getPlugin(Jin::class.java).statsHandler.getStatsPlayer(player)!!.unlockedKits.withIndex()) {
            when (kit) {
                "FROG" -> gui.item(index + 1, frog).onClick runnable@ {
                    it.isCancelled = true
                    JavaPlugin.getPlugin(Jin::class.java).statsHandler.getStatsPlayer(player as Player)!!.kit = "FROG"
                    Chat.send(player, "${Chat.gamePrefix} &7You have selected the &6Frog Kit&7!")
                    (player).playSound(player.location, Sound.NOTE_PLING, 1f, 1f)
                    player.closeInventory()
                    if (SpawnFeature.instance.isInSpawn[player.uniqueId] != true) {
                        Chat.send(player, "${Chat.gamePrefix} Your kit will be applied the next time you respawn!")
                    } else {
                        SpawnFeature.instance.send(player)
                    }
                    return@runnable
                }
                "PYROMANCER" -> gui.item(index + 1, pyromancer).onClick runnable@ {
                    it.isCancelled = true
                    JavaPlugin.getPlugin(Jin::class.java).statsHandler.getStatsPlayer(player as Player)!!.kit = "PYROMANCER"
                    Chat.send(player, "${Chat.gamePrefix} &7You have selected the &6Pyromancer Kit&7!")
                    (player as Player).playSound(player.location, Sound.NOTE_PLING, 1f, 1f)
                    player.closeInventory()
                    if (SpawnFeature.instance.isInSpawn[player.uniqueId] != true) {
                        Chat.send(player, "${Chat.gamePrefix} Your kit will be applied the next time you respawn!")
                    } else {
                        SpawnFeature.instance.send(player)
                    }
                    return@runnable
                }
                "NINJA" -> gui.item(index + 1, ninja).onClick runnable@ {
                    it.isCancelled = true
                    JavaPlugin.getPlugin(Jin::class.java).statsHandler.getStatsPlayer(player as Player)!!.kit = "NINJA"
                    Chat.send(player, "${Chat.gamePrefix} &7You have selected the &6Ninja Kit&7!")
                    (player as Player).playSound(player.location, Sound.NOTE_PLING, 1f, 1f)
                    player.closeInventory()
                    if (SpawnFeature.instance.isInSpawn[player.uniqueId] != true) {
                        Chat.send(player, "${Chat.gamePrefix} Your kit will be applied the next time you respawn!")
                    } else {
                        SpawnFeature.instance.send(player)
                    }
                    return@runnable
                }
            }
        }
        player.openInventory(gui.make())
        return true
    }
}