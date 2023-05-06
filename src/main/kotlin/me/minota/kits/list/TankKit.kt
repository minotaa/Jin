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

class TankKit : Kit(
    "Tank",
    mutableListOf("&6Kit Perks:",
    " &8-&7 Double Health",
    " &8-&7 Protection I Diamond Armor",
    " &8-&7 Sharpness I Stone Sword"),
    "TANK",
    ItemStack(Material.IRON_BLOCK),
    2000.0
) {
    override fun giveItems(player: Player) {
        player.maxHealth = 40.0
        player.health = 40.0
        player.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 99999, 0))
        val helmet = ItemBuilder(Material.DIAMOND_HELMET)
            .isUnbreakable(true)
            .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
            .make()
        val chestplate = ItemBuilder(Material.DIAMOND_CHESTPLATE)
            .isUnbreakable(true)
            .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
            .make()
        val leggings = ItemBuilder(Material.DIAMOND_LEGGINGS)
            .isUnbreakable(true)
            .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
            .make()
        val boots = ItemBuilder(Material.DIAMOND_BOOTS)
            .isUnbreakable(true)
            .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
            .make()
        player.inventory.helmet = helmet
        player.inventory.chestplate = chestplate
        player.inventory.leggings = leggings
        player.inventory.boots = boots
        val sword = ItemBuilder(Material.STONE_SWORD)
            .isUnbreakable(true)
            .addEnchantment(Enchantment.DAMAGE_ALL, 1)
            .make()
        player.inventory.setItem(0, sword)
        player.inventory.setItem(1, SpawnFeature.instance.unbreakable(Material.BOW))
        player.inventory.setItem(8, ItemStack(Material.ARROW, 1))
    }
}