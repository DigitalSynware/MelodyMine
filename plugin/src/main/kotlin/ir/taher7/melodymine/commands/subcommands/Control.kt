package ir.taher7.melodymine.commands.subcommands

import ir.taher7.melodymine.commands.SubCommand
import ir.taher7.melodymine.core.MelodyManager
import ir.taher7.melodymine.storage.Storage
import ir.taher7.melodymine.utils.Adventure.sendMessage
import ir.taher7.melodymine.utils.Adventure.toComponent
import org.bukkit.entity.Player
import java.util.*

class Control : SubCommand() {

    override var name = "control"
    override var description = Storage.controlDescription
    override var syntax = "/voicechat control"
    override var permission = "voicechat.control"
    override fun handler(player: Player, args: Array<out String>) {

        if (args.size != 2) {
            sendControlHelpMessage(player)
            return
        }

        val melodyPlayer = Storage.onlinePlayers[player.uniqueId.toString()] ?: return
        if (!melodyPlayer.isActiveVoice) {
            player.sendMessage("<prefix>Debes activar tu chat de voz, usa <i>/voicechat start link</i> para iniciar el chat de voz.".toComponent())
            return
        }

        if (args[1].equals("mute", true)) {
            MelodyManager.setPlayerSelfMute(melodyPlayer, !melodyPlayer.isSelfMute)
            if (melodyPlayer.isSelfMute) {
                player.sendMessage(Storage.unMuteToggleMessage.toComponent())
            } else {
                player.sendMessage(Storage.muteToggleMessage.toComponent())
            }
            return
        }

        if (args[1].equals("deafen", true)) {
            MelodyManager.setPlayerDeafen(melodyPlayer, !melodyPlayer.isDeafen)
            if (melodyPlayer.isDeafen) {
                player.sendMessage(Storage.unDeafenToggleMessage.toComponent())
            } else {
                player.sendMessage(Storage.deafenToggleMessage.toComponent())
            }
            return
        }

        sendControlHelpMessage(player)
    }

    private fun sendControlHelpMessage(player: Player) {
        player.sendMessage(Storage.contentHeader.toComponent())
        player.sendMessage("")
        player.sendMessage("<click:run_command:'${syntax} mute'><hover:show_text:'<text_hover>Haz click para ejecutar <i>${syntax} mute</i>'><text_hover>${syntax} mute <#FFF4E4><bold>|</bold> <text>Silenciate en el chat de voz</hover></click>".toComponent())
        player.sendMessage("<click:run_command:'${syntax} deafen'><hover:show_text:'<text_hover>Haz click para ejecutar <i>${syntax} deafen</i>'><text_hover>${syntax} deafen <#FFF4E4><bold>|</bold> <text>Ensordecete en el chat de voz.</hover></click>".toComponent())
        player.sendMessage("")
        player.sendMessage(Storage.contentFooter.toComponent())
    }
}