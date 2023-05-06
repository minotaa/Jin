package me.minota

import com.mongodb.MongoClient
import com.mongodb.MongoClientException
import com.mongodb.MongoClientURI
import com.mongodb.MongoException
import com.mongodb.client.model.Filters
import com.mongodb.client.model.FindOneAndReplaceOptions
import me.lucko.helper.plugin.ExtendedJavaPlugin
import me.lucko.helper.utils.Log
import me.minota.commands.DiscordCommand
import me.minota.commands.KitCommand
import me.minota.commands.SetSpawnCommand
import me.minota.commands.VoteCommand
import me.minota.features.SpawnFeature
import me.minota.kits.KitHandler
import me.minota.utils.Chat
import me.minota.utils.Settings
import me.minota.utils.StatsHandler
import org.bson.Document
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.ServerListPingEvent
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Objective
import org.bukkit.scoreboard.Scoreboard
import kotlin.math.floor

class Jin : ExtendedJavaPlugin(), Listener {

    lateinit var client: MongoClient
    lateinit var statsHandler: StatsHandler

    @EventHandler
    fun onServerListPing(e: ServerListPingEvent) {
        val text = Chat.format("&c&lapple&a&ljuice&7&l.bar")
        e.motd = text
    }

    override fun enable() {
        Settings.instance.setup(this)
        registerListener(SpawnFeature.instance)
        registerListener(this)

        KitHandler.setup()

        statsHandler = StatsHandler()
        getCommand("setspawn").executor = SetSpawnCommand()
        getCommand("discord").executor = DiscordCommand()
        getCommand("vote").executor = VoteCommand()
        getCommand("kit").executor = KitCommand()


        val manager = Bukkit.getScoreboardManager()
        val board: Scoreboard = manager.mainScoreboard
        val name: Objective
        val tab: Objective
        name = if (board.getObjective("HealthNamePL") == null) {
            board.registerNewObjective("HealthNamePL", "dummy")
        } else {
            board.getObjective("HealthNamePL")
        }
        tab = if (board.getObjective("HealthTabPL") == null) {
            board.registerNewObjective("HealthTabPL", "dummy")
        } else {
            board.getObjective("HealthTabPL")
        }

        val uri = Settings.instance.data!!.getString("database")
        try {
            client = MongoClient(MongoClientURI(uri))
        } catch (e: MongoClientException) {
            e.printStackTrace()
        }

        name.displaySlot = DisplaySlot.BELOW_NAME
        name.displayName = ChatColor.RED.toString() + "❤"
        tab.displaySlot = DisplaySlot.PLAYER_LIST
        Bukkit.getScheduler().runTaskTimer(this, {
            for (player in Bukkit.getOnlinePlayers()) {
                val health = floor(player.health / 20 * 100 + ((player as CraftPlayer).handle.absorptionHearts / 2) * 10).toInt()
                name.getScore(player.name).score = health
                tab.getScore(player.name).score = health
            }
        }, 1L, 1L)

        Log.info("Jin has been enabled.")
    }

    override fun disable() {
        Log.info("Jin has been disabled.")
    }
}