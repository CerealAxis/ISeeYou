package xaviermc.top.iseeyou.anticheat.listeners

import xaviermc.top.iseeyou.anticheat.AntiCheatListener
import com.gmail.olexorus.themis.api.ActionEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class ThemisListener : Listener {
    private var aggregateMonitoring: Boolean = false
    fun setAggregateMonitoring(aggregate: Boolean) {
        aggregateMonitoring = aggregate
    }
    @EventHandler
    fun onAction(e: ActionEvent) {
        if (aggregateMonitoring) {
            AntiCheatListener.onAntiCheatAction(e.player)
        } else {
            AntiCheatListener.onAntiCheatAction(e.player, "Themis")
        }
    }
}