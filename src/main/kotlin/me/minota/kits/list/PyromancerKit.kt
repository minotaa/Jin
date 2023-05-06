package me.minota.kits.list

import me.minota.features.SpawnFeature
import me.minota.kits.Kit
import me.minota.utils.Chat
import me.minota.utils.ItemBuilder
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.*

class PyromancerKit : Kit(
    "Pyromancer",
    mutableListOf("&6Kit Perks:",
        " &8-&7 Fire Wand (5 Charges)",
        " &8-&7 Fire Protection I Iron Armor",
        " &8-&7 Every kill recharges Fire Wand by 1 charge.",
        " &8-&7 Every kill gives you a Fire Resistance effect."),
    "PYROMANCER",
    ItemStack(Material.BLAZE_ROD),
    1000.0
) {
    override fun giveItems(player: Player) {
        val cHelmet = ItemBuilder(Material.CHAINMAIL_HELMET)
            .isUnbreakable(true)
            .addEnchantment(Enchantment.PROTECTION_FIRE, 1)
            .make()
        val iHelmet = ItemBuilder(Material.IRON_HELMET)
            .isUnbreakable(true)
            .addEnchantment(Enchantment.PROTECTION_FIRE, 1)
            .make()
        val cChestplate = ItemBuilder(Material.CHAINMAIL_CHESTPLATE)
            .isUnbreakable(true)
            .addEnchantment(Enchantment.PROTECTION_FIRE, 1)
            .make()
        val iChestplate = ItemBuilder(Material.IRON_CHESTPLATE)
            .isUnbreakable(true)
            .addEnchantment(Enchantment.PROTECTION_FIRE, 1)
            .make()
        val cLeggings = ItemBuilder(Material.CHAINMAIL_LEGGINGS)
            .isUnbreakable(true)
            .addEnchantment(Enchantment.PROTECTION_FIRE, 1)
            .make()
        val iLeggings = ItemBuilder(Material.IRON_LEGGINGS)
            .isUnbreakable(true)
            .addEnchantment(Enchantment.PROTECTION_FIRE, 1)
            .make()
        val cBoots = ItemBuilder(Material.CHAINMAIL_BOOTS)
            .isUnbreakable(true)
            .addEnchantment(Enchantment.PROTECTION_FIRE, 1)
            .make()
        val iBoots = ItemBuilder(Material.IRON_BOOTS)
            .isUnbreakable(true)
            .addEnchantment(Enchantment.PROTECTION_FIRE, 1)
            .make()
        player.inventory.helmet = cHelmet
        player.inventory.chestplate = cChestplate
        player.inventory.leggings = cLeggings
        player.inventory.boots = cBoots
        if (Random().nextInt(3) == 0) {
            player.inventory.helmet = iHelmet
        } else if (Random().nextInt(3) == 1) {
            player.inventory.chestplate = iChestplate
        } else if (Random().nextInt(3) == 2) {
            player.inventory.leggings = iLeggings
        } else {
            player.inventory.boots = iBoots
        }
        player.inventory.setItem(0, SpawnFeature.instance.unbreakable(Material.IRON_SWORD))
        player.inventory.setItem(1, SpawnFeature.instance.unbreakable(Material.BOW))
        val blazeWand = ItemBuilder(Material.BLAZE_ROD)
            .isUnbreakable(true)
            .name("&7Fire Wand &8(&e火 火 火 火 火&8)")
            .addLore("&7Attack players to inflict fire damage!")
            .addLore("&7Will fizzle out after 5 attacks.")
            .addEnchantment(Enchantment.FIRE_ASPECT, 2)
            .make()
        player.inventory.setItem(2, blazeWand)
        player.inventory.setItem(8, ItemStack(Material.ARROW, 16))
    }

    override fun onKill(victim: Player, attacker: Player) {
        for (item in (attacker).inventory.contents) {
            if (item != null) {
                if (item.type == Material.BLAZE_ROD) {
                    if (item.itemMeta.displayName.startsWith(Chat.format("&7Fire Wand"))) {
                        val meta = item.itemMeta
                        meta.displayName = Chat.format("&7Fire Wand &8(&e火 火 火 火 火&8)")
                        meta.addEnchant(Enchantment.FIRE_ASPECT, 2, true)
                        item.itemMeta = meta
                        (attacker).playSound((attacker).location, Sound.FIREWORK_BLAST, 1.0f, 1.0f)
                        (attacker).sendTitle(Chat.format("&7 "), Chat.format("&7Your fire wand has been recharged!"))
                        (attacker).addPotionEffect(PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 60, 0))
                    }
                }
            }
        }
    }

    @EventHandler
    fun onPlayerDamage(e: EntityDamageEvent) {
        if (e is EntityDamageByEntityEvent) {
            if ((e.damager).type == EntityType.PLAYER) {
                if ((e.damager as Player).itemInHand != null && (e.damager as Player).itemInHand.hasItemMeta() && (e.damager as Player).itemInHand.itemMeta.displayName != null && (e.damager as Player).itemInHand.itemMeta.displayName.startsWith(Chat.format("&7Fire Wand"))) {
                    when ((e.damager as Player).itemInHand.itemMeta.displayName) {
                        Chat.format("&7Fire Wand &8(&e火 火 火 火 火&8)") -> {
                            val meta = (e.damager as Player).itemInHand.itemMeta
                            meta.displayName = Chat.format("&7Fire Wand &8(&e火 火 火 火&8)")
                            (e.damager as Player).itemInHand.itemMeta = meta
                            (e.damager as Player).playSound((e.damager as Player).location, Sound.BLAZE_HIT, 1f, 1f)
                        }
                        Chat.format("&7Fire Wand &8(&e火 火 火 火&8)") -> {
                            val meta = (e.damager as Player).itemInHand.itemMeta
                            meta.displayName = Chat.format("&7Fire Wand &8(&e火 火 火&8)")
                            (e.damager as Player).itemInHand.itemMeta = meta
                            (e.damager as Player).playSound((e.damager as Player).location, Sound.BLAZE_HIT, 1f, 1f)
                        }
                        Chat.format("&7Fire Wand &8(&e火 火 火&8)") -> {
                            val meta = (e.damager as Player).itemInHand.itemMeta
                            meta.displayName = Chat.format("&7Fire Wand &8(&e火 火&8)")
                            (e.damager as Player).itemInHand.itemMeta = meta
                            (e.damager as Player).playSound((e.damager as Player).location, Sound.BLAZE_HIT, 1f, 1f)
                        }
                        Chat.format("&7Fire Wand &8(&e火 火&8)") -> {
                            val meta = (e.damager as Player).itemInHand.itemMeta
                            meta.displayName = Chat.format("&7Fire Wand &8(&e火&8)")
                            (e.damager as Player).itemInHand.itemMeta = meta
                            (e.damager as Player).playSound((e.damager as Player).location, Sound.BLAZE_HIT, 1f, 1f)
                        }
                        Chat.format("&7Fire Wand &8(&e火&8)") -> {
                            val meta = (e.damager as Player).itemInHand.itemMeta
                            meta.displayName = Chat.format("&7Fire Wand &8(&4✖&8)")
                            meta.removeEnchant(Enchantment.FIRE_ASPECT)
                            (e.damager as Player).itemInHand.itemMeta = meta
                            (e.damager as Player).playSound((e.damager as Player).location, Sound.FIZZ, 1f, 1f)
                            Chat.send((e.damager as Player), "${Chat.gamePrefix} &7Your &6Fire Wand &7has fizzled out!")
                        }
                    }
                }
            }
        }
    }
}
