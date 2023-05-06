package me.minota.kits.list

import me.minota.features.SpawnFeature
import me.minota.kits.Kit
import me.minota.utils.ItemBuilder
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class NinjaKit : Kit(
    "Ninja",
    mutableListOf("&6Kit Perks:",
        " &8-&7 Permanent Speed II",
        " &8-&7 Permanent Night Vision",
        " &8-&7 Invisible/Weak while sneaking, weakness remains for 5s after sneaking",
        " &8-&7 Sharpness I Iron Sword",
        " &8-&7 Protection I Diamond Chestplate"),
"NINJA",
    ItemStack(Material.FEATHER),
    1000.0
) {
    override fun giveItems(player: Player) {
        player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 99999, 1))
        player.addPotionEffect(PotionEffect(PotionEffectType.NIGHT_VISION, 99999, 0))
        val prot1Chestplate = ItemBuilder(Material.DIAMOND_CHESTPLATE)
            .isUnbreakable(true)
            .make()
        val sharp1IronSword = ItemBuilder(Material.IRON_SWORD)
            .isUnbreakable(true)
            .addEnchantment(Enchantment.DAMAGE_ALL, 1)
            .make()
        player.inventory.chestplate = prot1Chestplate
        player.inventory.setItem(0, sharp1IronSword)
        player.inventory.setItem(1, SpawnFeature.instance.unbreakable(Material.BOW))
        player.inventory.setItem(8, ItemStack(Material.ARROW, 16))
    }
}