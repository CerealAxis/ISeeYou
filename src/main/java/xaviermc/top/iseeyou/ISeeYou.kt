package xaviermc.top.iseeyou

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import top.leavesmc.leaves.entity.Photographer
import xaviermc.top.iseeyou.anticheat.AntiCheatListener
import xaviermc.top.iseeyou.anticheat.listeners.MatrixListener
import xaviermc.top.iseeyou.anticheat.listeners.ThemisListener
import xaviermc.top.iseeyou.anticheat.suspiciousPhotographers
import xaviermc.top.iseeyou.lib.UpdateChecker
import xaviermc.top.iseeyou.metrics.Metrics
import java.io.IOException
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.time.Duration
import java.time.LocalDate
import java.util.*
import kotlin.io.path.isDirectory
import kotlin.math.pow
import com.moandjiezana.toml.Toml

var toml: TomlEx<ConfigData>? = null
var photographers = mutableMapOf<String, Photographer>()
var highSpeedPausedPhotographers = mutableSetOf<Photographer>()
var instance: JavaPlugin? = null

@Suppress("unused")
class ISeeYou : JavaPlugin(), CommandExecutor {
    private var outdatedRecordRetentionDays: Int = 0

    override fun onEnable() {
        instance = this
        setupConfig()
        if (toml != null) {
            if (toml!!.data.deleteTmpFileOnLoad) {
                try {
                    Files.walk(Paths.get(toml!!.data.recordPath), Int.MAX_VALUE, FileVisitOption.FOLLOW_LINKS)
                        .use { paths ->
                            paths.filter { it.isDirectory() && it.fileName.toString().endsWith(".tmp") }
                                .forEach { deleteTmpFolder(it) }
                        }
                } catch (_: IOException) {
                }
            }
            EventListener.pauseRecordingOnHighSpeedThresholdPerTickSquared =
                (toml!!.data.pauseRecordingOnHighSpeed.threshold / 20).pow(2.0)
            if (toml!!.data.clearOutdatedRecordFile.enabled) {
                cleanOutdatedRecordings()
                object : BukkitRunnable() {
                    override fun run() {
                        cleanOutdatedRecordings()
                    }
                }.runTaskTimer(this, 0, 20 * 60 * 60 * 24) // Every day
            }
            Bukkit.getPluginManager().registerEvents(EventListener, this)
        } else {
            logger.warning("Failed to initialize configuration. Plugin will not enable.")
            Bukkit.getPluginManager().disablePlugin(this)
        }

        Bukkit.getPluginManager().registerEvents(AntiCheatListener, this)

        if (Bukkit.getPluginManager().isPluginEnabled("Themis") ||
            toml!!.data.recordSuspiciousPlayer.enableThemisIntegration
        ) Bukkit.getPluginManager().registerEvents(ThemisListener(), this)

        if (Bukkit.getPluginManager().isPluginEnabled("Matrix") ||
            toml!!.data.recordSuspiciousPlayer.enableMatrixIntegration
        ) Bukkit.getPluginManager().registerEvents(MatrixListener(), this)

        if (toml!!.data.enableBstats) {
            val pluginId = 21068
            val metrics: Metrics = Metrics(this, pluginId)
            metrics.addCustomChart(
                Metrics.SimplePie(
                    "chart_id"
                ) { "My value" })
        }
        if (toml!!.data.enableUpdateChecker) {
            val updateChecker: UpdateChecker = UpdateChecker(this, 115177)
            updateChecker.checkForUpdates()
        }
        // Registering custom commands
        this.getCommand("icu")?.setExecutor(this)
    }

    private fun setupConfig() {
        toml = TomlEx("plugins/ISeeYou/config.toml", ConfigData::class.java)
        val errMsg = toml!!.data.isConfigValid()
        if (errMsg != null) {
            throw InvalidConfigurationException(errMsg)
        }
        toml!!.data.setConfig()
        outdatedRecordRetentionDays = toml!!.data.clearOutdatedRecordFile.days
        toml!!.save()
    }

    override fun onDisable() {
        for (photographer in photographers.values) {
            photographer.stopRecording()
        }
        photographers.clear()
        highSpeedPausedPhotographers.clear()
        suspiciousPhotographers.clear()
        instance = null
    }

    private fun deleteTmpFolder(folderPath: Path) {
        try {
            Files.walkFileTree(
                folderPath,
                EnumSet.noneOf(FileVisitOption::class.java),
                Int.MAX_VALUE,
                object : SimpleFileVisitor<Path>() {
                    @Throws(IOException::class)
                    override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
                        Files.delete(file)
                        return FileVisitResult.CONTINUE
                    }

                    @Throws(IOException::class)
                    override fun postVisitDirectory(dir: Path, exc: IOException?): FileVisitResult {
                        Files.delete(dir)
                        return FileVisitResult.CONTINUE
                    }
                })
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun cleanOutdatedRecordings() {
        try {
            val recordingsDir = Paths.get(".")
            logger.info("Start to delete outdated recordings in $recordingsDir")
            var deletedCount = 0
            Files.walk(recordingsDir).use { paths ->
                paths.filter { Files.isDirectory(it) && it.parent == recordingsDir }
                    .forEach { folder ->
                        deletedCount += deleteRecordingFiles(folder)
                    }
            }
            logger.info("Finished deleting outdated recordings in $recordingsDir, deleted $deletedCount files")
        } catch (e: IOException) {
            logger.severe("Error occurred while cleaning outdated recordings: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun deleteRecordingFiles(folderPath: Path): Int {
        var deletedCount = 0
        try {
            val currentDate = LocalDate.now()
            Files.walk(folderPath).use { paths ->
                paths.filter { Files.isRegularFile(it) && it.toString().endsWith(".mcpr") }
                    .forEach { file ->
                        val fileName = file.fileName.toString()
                        val creationDateStr = fileName.substringBefore('@')
                        val creationDate = LocalDate.parse(creationDateStr)
                        val daysSinceCreation =
                            Duration.between(creationDate.atStartOfDay(), currentDate.atStartOfDay()).toDays()
                        if (daysSinceCreation > outdatedRecordRetentionDays) {
                            try {
                                Files.delete(file)
                                logger.info("Deleted recording file: $fileName")
                                deletedCount++
                            } catch (e: IOException) {
                                logger.severe("Error occurred while deleting recording file: $fileName, Error: ${e.message}")
                                e.printStackTrace()
                            }
                        }
                    }
            }
        } catch (e: IOException) {
            logger.severe("Error occurred while processing recording files in folder: $folderPath, Error: ${e.message}")
            e.printStackTrace()
        }
        return deletedCount
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (command.name.equals("icu", ignoreCase = true)) {
            if (args.isEmpty()) {
                // Handle /icu command
                // Add logic here if needed
                return true
            }

            when (args[0]) {
                "place" -> {
                    if (args.size < 2 || args.size > 3) {
                        sender.sendMessage("Usage: /icu place [name] [<player>]")
                        return true
                    }
                    val name = args[1]
                    val player: Player? = if (args.size == 3) Bukkit.getPlayer(args[2]) else sender as? Player
                    if (player != null) {
                        // Place photographer at player's location
                        val photographer = Bukkit.getPhotographerManager().createPhotographer(name, player.location)
                        if (photographer != null) {
                            photographers[name] = photographer
                            sender.sendMessage("Photographer '$name' has been placed.")
                        } else {
                            sender.sendMessage("Failed to place photographer.")
                        }
                    } else {
                        sender.sendMessage("Player not found.")
                    }
                }

                "remove" -> {
                    if (args.size < 2) {
                        sender.sendMessage("Usage: /icu remove [name]")
                        return true
                    }
                    val name = args[1]
                    val photographer = photographers.remove(name)
                    if (photographer != null) {
                        photographer.stopRecording()
                        sender.sendMessage("Photographer '$name' has been removed.")
                    } else {
                        sender.sendMessage("Photographer '$name' not found.")
                    }
                }

                else -> sender.sendMessage("Unknown command. Usage: /icu place|remove [name] <player>")
            }
            return true
        }
        return false
    }
}