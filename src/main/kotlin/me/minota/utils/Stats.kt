package me.minota.utils

import com.mongodb.MongoException
import com.mongodb.client.model.Filters
import com.mongodb.client.model.FindOneAndReplaceOptions
import me.lucko.helper.Events
import me.lucko.helper.Schedulers
import me.lucko.helper.profiles.plugin.external.caffeine.cache.Cache
import me.lucko.helper.profiles.plugin.external.caffeine.cache.Caffeine
import me.lucko.helper.utils.Log
import me.minota.Jin
import org.bson.Document
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin
import java.util.Objects
import java.util.UUID
import java.util.concurrent.TimeUnit

class StatsPlayer(val player: OfflinePlayer) {
    var gold: Double = 0.0
    var kills: Int = 0
    var deaths: Int = 0
    var level: Int = 0
    var xp: Double = 0.0
    var xpNeeded: Double = 100.0
    var kit: String = "DEFAULT"
    var unlockedKits: MutableList<String> = mutableListOf()
}

class StatsHandler {
    private val statsPlayerMap: Cache<UUID, StatsPlayer> = Caffeine.newBuilder()
        .maximumSize(10_000)
        .expireAfterAccess(6, TimeUnit.HOURS)
        .build()

    private fun updateCache(statsPlayer: StatsPlayer) {
        this.statsPlayerMap.put(statsPlayer.player.uniqueId, statsPlayer)
    }

    fun getStatsPlayer(player: Player): StatsPlayer? {
        Objects.requireNonNull(player, "player")
        return statsPlayerMap.getIfPresent(player.uniqueId)
    }

    fun lookupStatsPlayer(player: OfflinePlayer): StatsPlayer {
        Objects.requireNonNull(player, "player")
        if (statsPlayerMap.getIfPresent(player.uniqueId) != null) {
            return statsPlayerMap.getIfPresent(player.uniqueId)!!
        } else {
            val sPlayer = StatsPlayer(player)
            loadPlayerData(sPlayer)
            updateCache(sPlayer)
            return sPlayer
        }
    }

    fun savePlayerData(statsPlayer: StatsPlayer) {
        try {
            with (JavaPlugin.getPlugin(Jin::class.java).client.getDatabase("arenapvp").getCollection("stats")) {
                val filter = Filters.eq("uuid", statsPlayer.player.uniqueId)
                val document = Document("uuid", statsPlayer.player.uniqueId)
                    .append("gold", statsPlayer.gold)
                    .append("kills", statsPlayer.kills)
                    .append("deaths", statsPlayer.deaths)
                    .append("level", statsPlayer.level)
                    .append("xp", statsPlayer.xp)
                    .append("xpNeeded", statsPlayer.xpNeeded)
                    .append("kit", statsPlayer.kit)
                    .append("unlockedKits", statsPlayer.unlockedKits)
                this.findOneAndReplace(filter, document, FindOneAndReplaceOptions().upsert(true))
                Log.info("Saved stats for ${statsPlayer.player.name}")
            }
        } catch (e: MongoException) {
            Log.severe("Error saving player data for ${statsPlayer.player.name}", e)
        }
    }

    private fun loadPlayerData(statsPlayer: StatsPlayer) {
        try {
            with (JavaPlugin.getPlugin(Jin::class.java).client.getDatabase("arenapvp").getCollection("stats")) {
                val playerData = find(Filters.eq("uuid", statsPlayer.player.uniqueId)).first()
                if (playerData != null) {
                    statsPlayer.deaths = playerData.getInteger("deaths")
                    statsPlayer.kills = playerData.getInteger("kills")
                    statsPlayer.gold = playerData.getDouble("gold")
                    statsPlayer.level = playerData.getInteger("level")
                    statsPlayer.xp = playerData.getDouble("xp")
                    statsPlayer.xpNeeded = playerData.getDouble("xpNeeded")
                    statsPlayer.kit = playerData.getString("kit")
                    statsPlayer.unlockedKits = playerData.getList("unlockedKits", String::class.java)
                    Log.info("Loaded stats for ${statsPlayer.player.name}.")
                } else {
                    Log.info("Could not load stats for ${statsPlayer.player.name}.")
                }
            }
        } catch (e: MongoException) {
            e.printStackTrace()
        }
    }

    init {
        Events.subscribe(PlayerLoginEvent::class.java, EventPriority.MONITOR)
            .filter { it.result == PlayerLoginEvent.Result.ALLOWED }
            .handler { event ->
                val statsPlayer = StatsPlayer(event.player)
                updateCache(statsPlayer)
                Schedulers.async().run { loadPlayerData(statsPlayer) }
            }
        Events.subscribe(PlayerQuitEvent::class.java, EventPriority.MONITOR)
            .handler { event ->
                val statsPlayer = statsPlayerMap.getIfPresent(event.player.uniqueId)
                if (statsPlayer != null) {
                    Schedulers.async().run { savePlayerData(statsPlayer) }
                }
            }
    }
}