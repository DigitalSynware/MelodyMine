package ir.taher7.melodymine.commands.subcommands

import ir.taher7.melodymine.commands.SubCommand
import ir.taher7.melodymine.core.MelodyManager
import ir.taher7.melodymine.storage.Storage
import ir.taher7.melodymine.utils.Adventure.sendMessage
import ir.taher7.melodymine.utils.Adventure.toComponent
import org.bukkit.entity.Player
import java.util.*

class Toggle : SubCommand() {

    private val coolDown = hashMapOf<UUID, Long>()

    override var name = "toggle"
    override var description = Storage.toggleDescription
    override var syntax = "/voicechat toggle"
    override var permission = "voicechat.toggle"

    override fun handler(player: Player, args: Array<out String>) {

        val melodyPlayer = Storage.onlinePlayers[player.uniqueId.toString()] ?: return

        if (melodyPlayer.isToggle) {
            MelodyManager.disableLogger(melodyPlayer.uuid)
            player.sendMessage("<prefix>Log de mensajes desactivado.".toComponent())
        } else {
            MelodyManager.enableLogger(melodyPlayer.uuid)
            player.sendMessage("<prefix>Log de mensajes activado.".toComponent())
        }
        coolDown[player.uniqueId] = System.currentTimeMillis()
    }
}