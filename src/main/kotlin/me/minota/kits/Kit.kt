package me.minota.kits

import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack

abstract class Kit(
    var name: String,
    var description: MutableList<String>,
    var id: String,
    var icon: ItemStack,
    var price: Double
): Listener {
    open fun giveItems(player: Player) {}
    open fun onKill(victim: Player, attacker: Player) {}
}