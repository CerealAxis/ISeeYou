package xaviermc.top.iseeyou.anticheat.listeners

import xaviermc.top.iseeyou.anticheat.AntiCheatListener
import me.rerere.matrix.api.events.PlayerViolationEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class MatrixListener : Listener {
    private var aggregateMonitoring: Boolean = false
    fun setAggregateMonitoring(aggregate: Boolean) {
        aggregateMonitoring = aggregate
    }
    @EventHandler
    fun onPlayerViolation(e: PlayerViolationEvent) {
        if (aggregateMonitoring) {
            AntiCheatListener.onAntiCheatAction(e.player)
        } else {
            AntiCheatListener.onAntiCheatAction(e.player, "Matrix")
        }
    }
}