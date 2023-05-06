package me.minota.kits.list

import me.minota.features.SpawnFeature
import me.minota.kits.Kit
import me.minota.utils.ItemBuilder
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType


class FrogKit : Kit(
    "Frog",
    mutableListOf("&6Kit Perks:"," &8-&7 Permanent Jump Boost IV"," &8-&7 Sharpness I Diamond Sword"," &8-&7 Protection I Leather Armor"),
    "FROG",
    ItemStack(Material.GRASS),
    1000.0
) {
    override fun giveItems(player: Player) {
        val helmet = ItemBuilder(Material.LEATHER_HELMET)
            .isUnbreakable(true)
            .toLeatherArmor()
            .color(Color.GREEN)
            .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
            .make()
        val chestplate = ItemBuilder(Material.LEATHER_CHESTPLATE)
            .isUnbreakable(true)
            .toLeatherArmor()
            .color(Color.GREEN)
            .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
            .make()
        val leggings = ItemBuilder(Material.LEATHER_LEGGINGS)
            .isUnbreakable(true)
            .toLeatherArmor()
            .color(Color.GREEN)
            .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
            .make()
        val boots = ItemBuilder(Material.LEATHER_BOOTS)
            .isUnbreakable(true)
            .toLeatherArmor()
            .color(Color.GREEN)
            .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
            .make()
        player.inventory.helmet = helmet
        player.inventory.chestplate = chestplate
        player.inventory.leggings = leggings
        player.inventory.boots = boots
        player.addPotionEffect(PotionEffect(PotionEffectType.JUMP, 99999, 3))
        val sword = ItemBuilder(Material.DIAMOND_SWORD)
            .isUnbreakable(true)
            .make()
        player.inventory.setItem(0, sword)
        player.inventory.setItem(1, SpawnFeature.instance.unbreakable(Material.BOW))
        player.inventory.setItem(2, SpawnFeature.instance.unbreakable(Material.FISHING_ROD))
        player.inventory.setItem(8, ItemStack(Material.ARROW, 16))
    }
}