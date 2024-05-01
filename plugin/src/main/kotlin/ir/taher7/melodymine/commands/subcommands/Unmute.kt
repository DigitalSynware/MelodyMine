package ir.taher7.melodymine.commands.subcommands

import ir.taher7.melodymine.commands.SubCommand
import ir.taher7.melodymine.core.MelodyManager
import ir.taher7.melodymine.storage.Storage
import ir.taher7.melodymine.utils.Adventure.sendMessage
import ir.taher7.melodymine.utils.Adventure.toComponent
import org.bukkit.entity.Player

class Unmute : SubCommand() {
    override var name = "unmute"
    override var description = Storage.unmuteDescription
    override var syntax = "/voicechat unmute"
    override var permission = "voicechat.unmute"
    override fun handler(player: Player, args: Array<out String>) {

        if (args.size != 2) {
            player.sendMessage("<prefix>Modo de uso: <i>${syntax} <jugador></i>.".toComponent())
            return
        }
        val filter = Storage.onlinePlayers.values.filter { melodyPlayer ->
            melodyPlayer.name.equals(args[1], true)
        }
        if (filter.isEmpty()) {
            player.sendMessage("<prefix>El jugador que intentas silenciar no está conectado.".toComponent())
            return
        }
        val targetPlayer = filter[0]
        if (!targetPlayer.isMute) {
            player.sendMessage("<prefix>${targetPlayer.name} <text>no está silenciado.".toComponent())
            return
        }

        MelodyManager.unMute(targetPlayer.uuid)
        targetPlayer.player?.sendMessage("<prefix>Has sido desilenciado en el chat de voz.".toComponent())
        player.sendMessage("<prefix><count_color>${targetPlayer.name} <text>ha sido desilenciado en el chat de voz.".toComponent())
    }
}
