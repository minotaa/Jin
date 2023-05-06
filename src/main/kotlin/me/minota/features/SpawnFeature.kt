package me.minota.features

import com.vexsoftware.votifier.model.VotifierEvent
import me.minota.Jin
import me.minota.kits.KitHandler
import me.minota.utils.*
import net.citizensnpcs.api.CitizensAPI
import net.citizensnpcs.api.npc.MemoryNPCDataStore
import net.minecraft.server.v1_8_R3.EntityLiving
import org.bukkit.*
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Arrow
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerPickupItemEvent
import org.bukkit.event.weather.WeatherChangeEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import sun.audio.AudioPlayer.player
import java.util.*
import kotlin.math.floor
import kotlin.math.round


class SpawnFeature : Listener {

    companion object {
        val instance = SpawnFeature()
    }

    @EventHandler
    fun onVotifierEvent(e: VotifierEvent) {
        val vote = e.vote
        val player = Bukkit.getOfflinePlayer(vote.username)
        if (player != null) {
            val statsPlayer = JavaPlugin.getPlugin(Jin::class.java).statsHandler.lookupStatsPlayer(player)
            val gold = generateRandomDecimalCashAmount(35)
            val xp = generateRandomDecimalCashAmount(15)
            statsPlayer.gold += gold
            statsPlayer.xp += xp
            if (statsPlayer.xp >= statsPlayer.xpNeeded) {
                statsPlayer.level += 1
                statsPlayer.xpNeeded = (statsPlayer.xpNeeded * 1.5)
                statsPlayer.xp = 0.0
                if (statsPlayer.player.isOnline) {
                    Chat.send(statsPlayer.player as Player, "${Chat.gamePrefix} &7You have leveled up to &bLevel ${statsPlayer.level}&7!")
                    statsPlayer.player.playSound(statsPlayer.player.location, Sound.LEVEL_UP, 1.0f, 1.0f)
                    statsPlayer.player.sendTitle(Chat.format("&6&lLEVEL UP!"), Chat.format("&7[${statsPlayer.level - 1}&7] &8> &7[${statsPlayer.level}&7]"))
                }
            }
            JavaPlugin.getPlugin(Jin::class.java).statsHandler.savePlayerData(statsPlayer)
            Chat.sendAll("${Chat.serverPrefix} &e${player.name} &7voted for the server and received &6${gold}g&7 and &b${xp} xp&7!")
        }
    }

    fun unbreakable(item: Material): ItemStack {
        val itemStack = ItemStack(item)
        val itemMeta = itemStack.itemMeta
        itemMeta!!.spigot().isUnbreakable = true
        itemStack.itemMeta = itemMeta
        return itemStack
    }

    fun giveKit(p: Player) {
        when (JavaPlugin.getPlugin(Jin::class.java).statsHandler.getStatsPlayer(p)!!.kit) {
            "DEFAULT" -> {
                p.inventory.helmet = unbreakable(Material.CHAINMAIL_HELMET)
                p.inventory.chestplate = unbreakable(Material.CHAINMAIL_CHESTPLATE)
                p.inventory.leggings = unbreakable(Material.CHAINMAIL_LEGGINGS)
                p.inventory.boots = unbreakable(Material.CHAINMAIL_BOOTS)
                if (Random().nextInt(3) == 0) {
                    p.inventory.helmet = unbreakable(Material.IRON_HELMET)
                } else if (Random().nextInt(3) == 1) {
                    p.inventory.chestplate = unbreakable(Material.IRON_CHESTPLATE)
                } else if (Random().nextInt(3) == 2) {
                    p.inventory.leggings = unbreakable(Material.IRON_LEGGINGS)
                } else {
                    p.inventory.boots = unbreakable(Material.IRON_BOOTS)
                }
                p.inventory.setItem(0, unbreakable(Material.IRON_SWORD))
                p.inventory.setItem(1, unbreakable(Material.BOW))
                p.inventory.setItem(2, unbreakable(Material.FISHING_ROD))
                p.inventory.setItem(8, ItemStack(Material.ARROW, 16))
            }
            else -> {
                val kit = KitHandler.getKit(JavaPlugin.getPlugin(Jin::class.java).statsHandler.getStatsPlayer(p)!!.kit)
                kit!!.giveItems(p)
            }
        }
    }

    val killstreak = hashMapOf<UUID, Int>()

    @EventHandler
    fun armorHandler(e: PlayerPickupItemEvent) {
        if (e.item.itemStack.type == Material.IRON_HELMET && e.player.inventory.helmet != null && e.player.inventory.helmet.type != Material.IRON_HELMET) {
            e.isCancelled = true
            e.item.remove()
            e.player.inventory.helmet = unbreakable(Material.IRON_HELMET)
            Chat.send(e.player, "${Chat.gamePrefix} Automatically equipped an &fIron Helmet&7!")
        } else if (e.item.itemStack.type == Material.IRON_CHESTPLATE && e.player.inventory.chestplate != null &&  e.player.inventory.chestplate.type != Material.IRON_CHESTPLATE) {
            e.isCancelled = true
            e.item.remove()
            e.player.inventory.chestplate = unbreakable(Material.IRON_CHESTPLATE)
            Chat.send(e.player, "${Chat.gamePrefix} Automatically equipped an &fIron Chestplate&7!")
        } else if (e.item.itemStack.type == Material.IRON_LEGGINGS && e.player.inventory.leggings != null && e.player.inventory.leggings.type != Material.IRON_LEGGINGS) {
            e.isCancelled = true
            e.item.remove()
            e.player.inventory.leggings = unbreakable(Material.IRON_LEGGINGS)
            Chat.send(e.player, "${Chat.gamePrefix} Automatically equipped &fIron Leggings&7!")
        } else if (e.item.itemStack.type == Material.IRON_BOOTS && e.player.inventory.boots != null && e.player.inventory.boots.type != Material.IRON_BOOTS) {
            e.isCancelled = true
            e.item.remove()
            e.player.inventory.boots = unbreakable(Material.IRON_BOOTS)
            Chat.send(e.player, "${Chat.gamePrefix} Automatically equipped &fIron Boots&7!")
        } else {
            e.isCancelled = true
        }
    }

    fun generateRandomDecimalCashAmount(bound: Int): Double {
        return floor(Random().nextDouble() * bound * 100) / 100
    }

    fun returnHealth(health: Double): String {
        var c = ""
        when {
            health >= 90 -> {
                c = "§2"
            }
            health >= 80 -> {
                c = "§a"
            }
            health >= 70 -> {
                c = "§6"
            }
            health >= 35 -> {
                c = "§e"
            }
            health >= 0 -> {
                c = "§c"
            }
            else -> {
                c = "§8"
            }
        }
        return c
    }

    @EventHandler
    fun onShoot(e: EntityDamageByEntityEvent) {
        if (e.damager.type == EntityType.ARROW && ((e.damager as Arrow).shooter) is Player && e.entity.type == EntityType.PLAYER) {
            Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Jin::class.java), {
                val shooter = ((e.damager as Arrow).shooter) as Player
                val victim = e.entity as Player
                val el: EntityLiving = (victim as CraftPlayer).handle
                val health = floor(victim.health / 2 * 10 + el.absorptionHearts / 2 * 10)
                val color = returnHealth(health)
                Chat.send(shooter, "${Chat.gamePrefix} &f${victim.name}&7 is at ${color}${health}%&7!")
            }, 1L)
        }
    }

    @EventHandler
    fun onNpcInteract(e: PlayerInteractEntityEvent) {
        if (e.rightClicked.type == EntityType.VILLAGER && CitizensAPI.getNPCRegistry().isNPC(e.rightClicked)) {
            val gui = GUI()
            if (CitizensAPI.getNPCRegistry().getNPC(e.rightClicked).name == Chat.format("&aShop")) {
                gui.name("&aShop")
                    .owner(e.player)
                    .rows(3)
                val youAlreadyHaveThisKit = ItemBuilder(Material.BARRIER)
                    .name("&cYou already have this kit!")
                    .make()
                for ((index, kit) in KitHandler.kits.withIndex()) {
                    if (!JavaPlugin.getPlugin(Jin::class.java).statsHandler.getStatsPlayer(e.player as Player)!!.unlockedKits.contains(kit.id)) {
                        val item = ItemBuilder(kit.icon.type)
                        for (line in kit.description) {
                            item.addLore(line)
                        }
                        item.addLore(" ")
                        item.addLore("&fPrice: &6${kit.price}g")
                        item.name("&a${kit.name}")
                        gui.item(index, item.make()).onClick runnable@ {
                            e.isCancelled = true
                            if (JavaPlugin.getPlugin(Jin::class.java).statsHandler.getStatsPlayer(it.whoClicked as Player)!!.gold < kit.price) {
                                (it.whoClicked as Player).playSound(it.whoClicked.location, Sound.VILLAGER_NO, 1f, 1f)
                                Chat.send(it.whoClicked as Player, "${Chat.gamePrefix} &7You don't have enough &6gold&7! (Missing &6${kit.price - JavaPlugin.getPlugin(Jin::class.java).statsHandler.getStatsPlayer(it.whoClicked as Player)!!.gold}g&7)")
                                return@runnable
                            }
                            JavaPlugin.getPlugin(Jin::class.java).statsHandler.getStatsPlayer(it.whoClicked as Player)!!.unlockedKits.add(kit.id)
                            JavaPlugin.getPlugin(Jin::class.java).statsHandler.getStatsPlayer(it.whoClicked as Player)!!.gold -= kit.price
                            Chat.send(it.whoClicked as Player, "${Chat.gamePrefix} &7You have unlocked the &6${kit.name} Kit&7!")
                            (it.whoClicked as Player).playSound(it.whoClicked.location, Sound.NOTE_PLING, 1f, 1f)
                            it.whoClicked.closeInventory()
                        }
                    } else {
                        gui.item(index, youAlreadyHaveThisKit).onClick runnable@ {
                            e.isCancelled = true
                            (it.whoClicked as Player).playSound(it.whoClicked.location, Sound.VILLAGER_NO, 1f, 1f)
                            Chat.send(it.whoClicked as Player, "${Chat.gamePrefix} &7You already have this kit!")
                            return@runnable
                        }
                    }
                }
                e.player.openInventory(gui.make())
            } else if (CitizensAPI.getNPCRegistry().getNPC(e.rightClicked).name == Chat.format("&aKit Selector")) {
                gui.name("&aKit Selector")
                    .owner(e.player)
                    .rows(1)
                val default = ItemBuilder(Material.IRON_CHESTPLATE)
                    .name("&aDefault Kit")
                    .addLore("&6Kit Perks:")
                    .addLore(" &8-&7 Iron Sword")
                    .addLore(" &8-&7 Chainmail/Iron Armor")
                    .addLore(" ")
                    .addLore("&7&oClean and simple.")
                    .make()
                gui.item(0, default).onClick runnable@ {
                    e.isCancelled = true
                    JavaPlugin.getPlugin(Jin::class.java).statsHandler.getStatsPlayer(e.player as Player)!!.kit = "DEFAULT"
                    Chat.send(e.player, "${Chat.gamePrefix} &7You have selected the &6Default Kit&7!")
                    (e.player as Player).playSound(e.player.location, Sound.NOTE_PLING, 1f, 1f)
                    e.player.closeInventory()
                    send(e.player)
                    return@runnable
                }
                for ((index, kit) in JavaPlugin.getPlugin(Jin::class.java).statsHandler.getStatsPlayer(e.player as Player)!!.unlockedKits.withIndex()) {
                    val k = KitHandler.getKit(kit)!!
                    val item = ItemBuilder(k.icon.type)
                        .name("&a${k.name}")
                    for (line in k.description) {
                        item.addLore(line)
                    }
                    gui.item(index + 1, item.make()).onClick runnable@ {
                        e.isCancelled = true
                        JavaPlugin.getPlugin(Jin::class.java).statsHandler.getStatsPlayer(e.player as Player)!!.kit = k.id
                        Chat.send(e.player, "${Chat.gamePrefix} &7You have selected the &6${k.name} Kit&7!")
                        (e.player as Player).playSound(e.player.location, Sound.NOTE_PLING, 1f, 1f)
                        e.player.closeInventory()
                        send(e.player)
                        return@runnable
                    }
                }
                e.player.openInventory(gui.make())
            }
        }
    }

    @EventHandler
    fun onPlayerDeath(e: EntityDamageEvent) {
        if (e.entityType == EntityType.PLAYER) {
            val p = e.entity as Player
            if (isInSpawn[p.uniqueId] == true || isInSpawn[(e.entity as Player).uniqueId] == true) {
                e.isCancelled = true
                return
            }
            if (e.cause == EntityDamageEvent.DamageCause.FALL) {
                e.isCancelled = true
                return
            }
            if (e.finalDamage >= (e.entity as Player).health) {
                e.damage = 0.0
                e.isCancelled = true
                if (e is EntityDamageByEntityEvent) {
                    if (e.damager.type == EntityType.PLAYER) {
                        if (killstreak[(e.damager as Player).uniqueId] == null) {
                            killstreak[(e.damager as Player).uniqueId] = 0
                        }
                        killstreak[(e.damager as Player).uniqueId] = killstreak[(e.damager as Player).uniqueId]!! + 1
                        if (killstreak[(e.damager as Player).uniqueId]!! >= 5) {
                            Chat.sendAll("${Chat.killStreakPrefix} &e${e.damager.name}&7 is on a &c${killstreak[(e.damager as Player).uniqueId]} killstreak&7!")
                        }
                        if (killstreak[(e.entity as Player).uniqueId]!! >= 5) {
                            Chat.sendAll("${Chat.killStreakPrefix} &e${e.entity.name}&7's killstreak of &6${killstreak[(e.entity as Player).uniqueId]} kills&7 was ended by &e${(e.damager as Player).name}&7!")
                        }
                        killstreak[(e.entity as Player).uniqueId] = 0
                        if (JavaPlugin.getPlugin(Jin::class.java).statsHandler.getStatsPlayer(e.damager as Player)!!.kit != "DEFAULT") {
                            val kit = KitHandler.getKit(JavaPlugin.getPlugin(Jin::class.java).statsHandler.getStatsPlayer(e.damager as Player)!!.kit)!!
                            kit.onKill(e.entity as Player, e.damager as Player)
                        }
                        JavaPlugin.getPlugin(Jin::class.java).statsHandler.getStatsPlayer((e.damager as Player))!!.kills++
                        JavaPlugin.getPlugin(Jin::class.java).statsHandler.getStatsPlayer((e.entity as Player))!!.deaths++
                        val gold = generateRandomDecimalCashAmount(25)
                        val xp = generateRandomDecimalCashAmount(10)
                        JavaPlugin.getPlugin(Jin::class.java).statsHandler.getStatsPlayer((e.damager as Player))!!.gold += gold
                        JavaPlugin.getPlugin(Jin::class.java).statsHandler.getStatsPlayer((e.damager as Player))!!.xp += xp
                        if (JavaPlugin.getPlugin(Jin::class.java).statsHandler.getStatsPlayer((e.damager as Player))!!.xp >= JavaPlugin.getPlugin(Jin::class.java).statsHandler.getStatsPlayer((e.damager as Player))!!.xpNeeded) {
                            JavaPlugin.getPlugin(Jin::class.java).statsHandler.getStatsPlayer((e.damager as Player))!!.level += 1
                            JavaPlugin.getPlugin(Jin::class.java).statsHandler.getStatsPlayer((e.damager as Player))!!.xpNeeded = (JavaPlugin.getPlugin(Jin::class.java).statsHandler.getStatsPlayer((e.damager as Player))!!.xpNeeded * 1.5)
                            JavaPlugin.getPlugin(Jin::class.java).statsHandler.getStatsPlayer((e.damager as Player))!!.xp = 0.0
                            Chat.send((e.damager as Player), "${Chat.gamePrefix} &7You have leveled up to &bLevel ${JavaPlugin.getPlugin(Jin::class.java).statsHandler.getStatsPlayer((e.damager as Player))!!.level}&7!")
                            (e.damager as Player).playSound((e.damager as Player).location, Sound.LEVEL_UP, 1.0f, 1.0f)
                            (e.damager as Player).sendTitle(Chat.format("&6&lLEVEL UP!"), Chat.format("&7[${JavaPlugin.getPlugin(Jin::class.java).statsHandler.getStatsPlayer((e.damager as Player))!!.level - 1}&7] &8> &7[${JavaPlugin.getPlugin(Jin::class.java).statsHandler.getStatsPlayer((e.damager as Player))!!.level}&7]"))
                        }

                        // Check if player is using pyromancer kit
                        Chat.send(e.damager as Player, "${Chat.gamePrefix} You &7killed &e${e.entity.name}&7 (${killstreak[(e.damager as Player).uniqueId]} kills)!")
                        Chat.send(e.damager as Player, "${Chat.gamePrefix} You received &6$gold gold&7 and &b$xp xp&7!")
                        Chat.send(e.entity as Player, "${Chat.gamePrefix} You were killed by &e${e.damager.name}&7!")
                        // Only add arrows if the player has less than 64
                        (e.damager as Player).inventory.addItem(ItemStack(Material.ARROW, 8))
                        (e.damager as Player).inventory.addItem(ItemStack(Material.GOLDEN_APPLE))
                    } else if (e.damager.type === EntityType.ARROW && (e.damager as Arrow).shooter as Player != e.entity as Player) {
                        val arrow = e.damager as Arrow
                        if (arrow.shooter is Player) {
                            if (killstreak[(arrow.shooter as Player).uniqueId] == null) {
                                killstreak[(arrow.shooter as Player).uniqueId] = 0
                            }
                            if (killstreak[(arrow.shooter as Player).uniqueId]!! >= 5) {
                                Chat.sendAll("${Chat.killStreakPrefix} &e${(arrow.shooter as Player).name}&7 is on a &c${killstreak[(arrow.shooter as Player).uniqueId]} killstreak&7!")
                            }
                            if (killstreak[(e.entity as Player).uniqueId]!! >= 5) {
                                Chat.sendAll("${Chat.killStreakPrefix} &e${e.entity.name}&7's killstreak of &6${killstreak[(e.entity as Player).uniqueId]} kills&7 was ended by &e${(arrow.shooter as Player).name}&7!")
                            }
                            killstreak[(e.entity as Player).uniqueId] = 0
                            killstreak[(arrow.shooter as Player).uniqueId] = killstreak[(arrow.shooter as Player).uniqueId]!! + 1
                            if (JavaPlugin.getPlugin(Jin::class.java).statsHandler.getStatsPlayer(arrow.shooter as Player)!!.kit != "DEFAULT") {
                                val kit = KitHandler.getKit(JavaPlugin.getPlugin(Jin::class.java).statsHandler.getStatsPlayer(arrow.shooter as Player)!!.kit)!!
                                kit.onKill(e.entity as Player, arrow.shooter as Player)
                            }
                            JavaPlugin.getPlugin(Jin::class.java).statsHandler.getStatsPlayer((arrow.shooter as Player))!!.kills++
                            JavaPlugin.getPlugin(Jin::class.java).statsHandler.getStatsPlayer((e.entity as Player))!!.deaths++
                            val gold = generateRandomDecimalCashAmount(25)
                            val xp = generateRandomDecimalCashAmount(10)
                            JavaPlugin.getPlugin(Jin::class.java).statsHandler.getStatsPlayer((arrow.shooter as Player))!!.gold += gold
                            JavaPlugin.getPlugin(Jin::class.java).statsHandler.getStatsPlayer((arrow.shooter as Player))!!.xp += xp

                            if (JavaPlugin.getPlugin(Jin::class.java).statsHandler.getStatsPlayer((arrow.shooter as Player))!!.xp >= JavaPlugin.getPlugin(Jin::class.java).statsHandler.getStatsPlayer((arrow.shooter as Player))!!.xpNeeded) {
                                JavaPlugin.getPlugin(Jin::class.java).statsHandler.getStatsPlayer((arrow.shooter as Player))!!.level += 1
                                JavaPlugin.getPlugin(Jin::class.java).statsHandler.getStatsPlayer((arrow.shooter as Player))!!.xpNeeded = (JavaPlugin.getPlugin(Jin::class.java).statsHandler.getStatsPlayer((arrow.shooter as Player))!!.xpNeeded * 1.5)
                                JavaPlugin.getPlugin(Jin::class.java).statsHandler.getStatsPlayer((arrow.shooter as Player))!!.xp = 0.0
                                Chat.send((e.damager as Player), "${Chat.gamePrefix} &7You have leveled up to &bLevel ${JavaPlugin.getPlugin(Jin::class.java).statsHandler.getStatsPlayer((arrow.shooter as Player))!!.level}&7!")
                                (e.damager as Player).playSound((e.damager as Player).location, Sound.LEVEL_UP, 1.0f, 1.0f)
                                (e.damager as Player).sendTitle(Chat.format("&6&lLEVEL UP!"), Chat.format("&7[${JavaPlugin.getPlugin(Jin::class.java).statsHandler.getStatsPlayer((arrow.shooter as Player))!!.level - 1}&7] &8> &7[${JavaPlugin.getPlugin(Jin::class.java).statsHandler.getStatsPlayer((arrow.shooter as Player))!!.level}&7]"))
                            }

                            Chat.send(arrow.shooter as Player, "${Chat.gamePrefix} You &7killed &e${e.entity.name}&7 (${killstreak[(arrow.shooter as Player).uniqueId]} kills)!")
                            Chat.send(arrow.shooter as Player, "${Chat.gamePrefix} You received &6${gold}g&7 and &b$xp xp&7!")
                            Chat.send(e.entity as Player, "${Chat.gamePrefix} You were shot by &e${(arrow.shooter as Player).name}&7!")
                            (arrow.shooter as Player).inventory.addItem(ItemStack(Material.ARROW, 16))
                            (arrow.shooter as Player).inventory.addItem(ItemStack(Material.GOLDEN_APPLE))
                        }
                    }
                } else {
                    Chat.send(e.entity as Player, "${Chat.gamePrefix} You &cdied&7!")
                }
                this.send((e.entity as Player))
            }
        }
    }

    fun send(p: Player) {
        try {
            val x = Settings.instance.data!!.getDouble("spawn.x")
            val y = Settings.instance.data!!.getDouble("spawn.y")
            val z = Settings.instance.data!!.getDouble("spawn.z")
            val yaw = Settings.instance.data!!.getDouble("spawn.yaw")
            val pitch = Settings.instance.data!!.getDouble("spawn.pitch")
            val world = Settings.instance.data!!.getString("spawn.world")
            val location = Location(Bukkit.getWorld(world), x, y, z, yaw.toFloat(), pitch.toFloat())
            p.foodLevel = 20
            p.inventory.clear()
            p.inventory.armorContents = null
            p.maxHealth = 20.0
            p.health = 20.0
            p.level = 0
            p.exp = 0F
            p.fireTicks = 0
            p.saturation = 20F
            (p as CraftPlayer).handle.dataWatcher.watch(9, 0.toByte())
            p.gameMode = GameMode.SURVIVAL
            p.playSound(p.location, Sound.LEVEL_UP, 1F, 1F)
            p.teleport(location)
            for (effect in p.activePotionEffects) {
                p.removePotionEffect(effect.type)
            }
            giveKit(p)
            isInSpawn[p.uniqueId] = true
        } catch (error: Exception) {
            error.printStackTrace()
            if (p.hasPermission("admin.setspawn")) {
                Chat.send(p, "${Chat.serverPrefix} The spawn has not been set yet. Please use &c/setspawn&7 to set the spawn.")
            }
        }
    }

    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        killstreak[(e.player).uniqueId] = 0
        try {
            if (!e.player.hasPlayedBefore()) {
                Chat.send(e.player, "${Chat.serverPrefix} Welcome to &capple&ajuice&7! Jump down from the arena to begin fighting!")
                Chat.send(e.player, "${Chat.serverPrefix} Join the discord at &9/discord&7!")
                Chat.send(e.player, "${Chat.serverPrefix} Vote using &/avote&7 to get rewards!")
            } else {
                Chat.send(e.player, "${Chat.serverPrefix} Welcome back to &capple&ajuice&7!")
            }
            Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Jin::class.java), {
                send(e.player)
            }, 1L)
        } catch (error: Exception) {
            error.printStackTrace()
            if (e.player.hasPermission("admin.setspawn")) {
                Chat.send(e.player, "${Chat.serverPrefix} The spawn has not been set yet. Please use &c/setspawn&7 to set the spawn.")
            }
        }
    }

    val isInSpawn = hashMapOf<UUID, Boolean>()

    fun isInRegion(playerLocation: Location, lowestPos: Location, highestPos: Location): Boolean {
        val x = playerLocation.x
        val y = playerLocation.y
        val z = playerLocation.z
        val lowx = lowestPos.x
        val lowy = lowestPos.y
        val lowz = lowestPos.z
        val highx = highestPos.x
        val highy = highestPos.y
        val highz = highestPos.z
        return (x in lowx..highx) && (y in lowy..highy) && (z in lowz..highz)
    }

    // Spawn Bounds:
    // -889, 44, 118
    // -879, 53, 137

    init {
        val x = Settings.instance.data!!.getDouble("spawn.x")
        val y = Settings.instance.data!!.getDouble("spawn.y")
        val z = Settings.instance.data!!.getDouble("spawn.z")
        val yaw = Settings.instance.data!!.getDouble("spawn.yaw")
        val pitch = Settings.instance.data!!.getDouble("spawn.pitch")
        val world = Settings.instance.data!!.getString("spawn.world")
        val spawnLocation = Location(Bukkit.getWorld(world), x, y, z, yaw.toFloat(), pitch.toFloat())

        val registry = CitizensAPI.createAnonymousNPCRegistry(MemoryNPCDataStore())

        val location = Location(Bukkit.getWorld("Spawn"), -889.0, 44.0, 118.0)
        val location2 = Location(Bukkit.getWorld("Spawn"), -879.0, 53.0, 137.0)
        val shopLocation = Location(Bukkit.getWorld("Spawn"), -880.5, 47.0, 131.5)
        val kitLocation = Location(Bukkit.getWorld("Spawn"), -880.5, 47.0, 123.5)
        val shopNpc = registry.createNPC(EntityType.VILLAGER, Chat.format("&aShop"))
        shopNpc.spawn(shopLocation)
        shopNpc.setAlwaysUseNameHologram(true)
        shopNpc.name = Chat.format("&aShop")
        shopNpc.faceLocation(spawnLocation)
        val kitNpc = registry.createNPC(EntityType.VILLAGER, Chat.format("&aKit Selector"))
        kitNpc.spawn(kitLocation)
        kitNpc.setAlwaysUseNameHologram(true)
        kitNpc.name = Chat.format("&aKit Selector")
        kitNpc.faceLocation(spawnLocation)
        object: BukkitRunnable() {
            override fun run() {
                Bukkit.getOnlinePlayers().forEach {
                    if (!isInRegion(it.location, location, location2) && isInSpawn[it.uniqueId] == true) {
                        Chat.send(it, "${Chat.serverPrefix} You have left the spawn area.")
                        // Push player forward when they exit the spawn
                        val dir: org.bukkit.util.Vector = it.location.direction
                        val vec = org.bukkit.util.Vector(dir.x * 3.5, dir.y * 3.5, dir.z * 3.5)
                        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Jin::class.java), {
                            it.velocity = vec
                        }, 2L)
                        isInSpawn[it.uniqueId] = false
                    }
                    if (it.isSneaking && JavaPlugin.getPlugin(Jin::class.java).statsHandler.getStatsPlayer(it)!!.kit == "NINJA") {
                        it.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, 20 * 3, 0, false, true))
                        it.addPotionEffect(PotionEffect(PotionEffectType.WEAKNESS, 20 * 5, 0, false, true))
                    }
                    val g = round(JavaPlugin.getPlugin(Jin::class.java).statsHandler.getStatsPlayer(it)!!.gold * 100.0) / 100.0
                    val progress = floor(JavaPlugin.getPlugin(Jin::class.java).statsHandler.getStatsPlayer(it)!!.xp / JavaPlugin.getPlugin(Jin::class.java).statsHandler.getStatsPlayer(it)!!.xpNeeded * 100).toInt()
                    ActionBar.sendActionBarMessage(it, "&fLevel: &b${JavaPlugin.getPlugin(Jin::class.java).statsHandler.getStatsPlayer(it)!!.level} &8(&b${progress}%&8) &8- &fGold: &6${g}g")
                }
            }
        }.runTaskTimer(JavaPlugin.getPlugin(Jin::class.java), 0L, 1L)
    }

    @EventHandler
    fun onPlayerDropItem(e: PlayerDropItemEvent) {
        e.isCancelled = true
    }

    @EventHandler
    fun onWeatherChange(e: WeatherChangeEvent) {
        e.isCancelled = true
    }

    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        e.isCancelled = true
    }

    @EventHandler
    fun onBlockPlace(e: BlockPlaceEvent) {
        e.isCancelled = true
    }

    @EventHandler
    fun onHungerBarChange(e: FoodLevelChangeEvent) {
        e.isCancelled = true
    }
}