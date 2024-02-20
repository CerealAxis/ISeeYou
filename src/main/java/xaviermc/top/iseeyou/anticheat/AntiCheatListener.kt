package xaviermc.top.iseeyou.anticheat

import xaviermc.top.iseeyou.EventListener
import xaviermc.top.iseeyou.instance
import xaviermc.top.iseeyou.toml
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.scheduler.BukkitRunnable
import java.io.File
import java.time.Duration
import java.time.LocalDateTime
import java.util.*


val suspiciousPhotographers: MutableMap<String, SuspiciousPhotographer> = mutableMapOf()

object AntiCheatListener : Listener {
    init {
        object : BukkitRunnable() {
            override fun run() {
                Bukkit.getScheduler().runTask(instance!!, Runnable {
                    val currentTime = System.currentTimeMillis()
                    suspiciousPhotographers.entries.removeIf {
                        if (currentTime - it.value.lastTagged >
                            Duration.ofMinutes(toml!!.data.recordSuspiciousPlayer.recordMinutes).toMillis()
                        ) {
                            it.value.photographer.stopRecording()
                            return@removeIf true
                        } else false
                    }
                })
            }
        }.runTaskTimer(instance!!, 0, 20 * 60 * 5)
    }

    fun onAntiCheatAction(player: Player, source: String? = null) {
        Bukkit.getScheduler().runTask(instance!!, Runnable {
            val photographerName = (player.name + "_sus_" + UUID.randomUUID().toString().replace("-", "")).substring(0, 16)
            val photographer = Bukkit.getPhotographerManager().createPhotographer(photographerName, player.location)

            if (photographer == null) {
                throw RuntimeException("Error creating suspicious photographer for player: {name: ${player.name}, UUID: ${player.uniqueId}}")
            }

            photographer.setFollowPlayer(player)
            val currentTime = LocalDateTime.now()
            val recordPath: String = if (toml!!.data.recordSuspiciousPlayer.aggregateMonitoring) {
                when (source) {
                    "Themis" -> toml!!.data.recordSuspiciousPlayer.themisRecordPath
                    "Matrix" -> toml!!.data.recordSuspiciousPlayer.matrixRecordPath
                    else -> throw IllegalArgumentException("Unknown source: $source")
                }
            } else {
                toml!!.data.recordSuspiciousPlayer.recordPath
            }
                .replace("\${name}", player.name)
                .replace("\${uuid}", player.uniqueId.toString())
            File(recordPath).mkdirs()
            val recordFile =
                File(recordPath + "/" + currentTime.format(EventListener.DATE_FORMATTER) + ".mcpr")
            if (recordFile.exists()) {
                recordFile.delete()
            }
            recordFile.createNewFile()
            photographer.setRecordFile(recordFile)
            suspiciousPhotographers[player.name] = SuspiciousPhotographer(
                photographer = photographer,
                name = player.name,
                lastTagged = System.currentTimeMillis(),
                source = source
            )
        })
    }

    @EventHandler
    fun onPlayerQuit(e: PlayerQuitEvent) {
        val suspiciousPhotographer = suspiciousPhotographers[e.player.name]
        if (suspiciousPhotographer != null) {
            suspiciousPhotographer.photographer.stopRecording()
            suspiciousPhotographers.remove(e.player.name)
        }
    }
}
