package ir.taher7.melodymine.commands.subcommands

import ir.taher7.melodymine.commands.SubCommand
import ir.taher7.melodymine.core.MelodyManager
import ir.taher7.melodymine.storage.Storage
import ir.taher7.melodymine.utils.Adventure.sendMessage
import ir.taher7.melodymine.utils.Adventure.toComponent
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class Mute : SubCommand() {
    override var name = "mute"
    override var description = Storage.muteDescription
    override var syntax = "/voicechat mute"
    override var permission = "voicechat.mute"
    override fun handler(player: Player, args: Array<out String>) {
        if (args.size != 2) {
            player.sendMessage("<prefix>Modo de uso: <i>${syntax} <jugador></i>.".toComponent())
            return
        }

        val targetPlayer = Bukkit.getPlayer(args[1])
        if (targetPlayer == null) {
            player.sendMessage("<prefix>El jugador que intentas silenciar no está conectado.".toComponent())
            return
        }

        val melodyPlayer = Storage.onlinePlayers[player.uniqueId.toString()] ?: return
        if (melodyPlayer.isMute) {
            player.sendMessage("<prefix><count_color>${melodyPlayer.name} <text>ya está silenciado.".toComponent())
            return
        }

        MelodyManager.mute(targetPlayer.uniqueId.toString())

        targetPlayer.player?.sendMessage("<prefix>Has sido silenciado en el chat de voz.".toComponent())
        player.sendMessage("<prefix><count_color>${targetPlayer.name} <text>ha sido silenciado en el chat de voz.".toComponent())
    }

}