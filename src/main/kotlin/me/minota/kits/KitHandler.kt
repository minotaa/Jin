package me.minota.kits

import me.minota.Jin
import me.minota.kits.list.FrogKit
import me.minota.kits.list.NinjaKit
import me.minota.kits.list.PyromancerKit
import me.minota.kits.list.TankKit
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class KitHandler {
    companion object {
        val kits = mutableListOf<Kit>()

        fun setup() {
            addKit(FrogKit())
            addKit(NinjaKit())
            addKit(PyromancerKit())
            addKit(TankKit())
        }

        fun getKit(id: String): Kit? {
            return kits.firstOrNull { it.id == id }
        }

        fun addKit(kit: Kit) {
            kits.add(kit)
            Bukkit.getPluginManager().registerEvents(kit, JavaPlugin.getPlugin(Jin::class.java))
        }
    }
}