package ir.taher7.melodymine.commands.subcommands


import ir.taher7.melodymine.commands.SubCommand
import ir.taher7.melodymine.core.MelodyManager
import ir.taher7.melodymine.storage.Storage
import ir.taher7.melodymine.utils.Adventure.sendMessage
import ir.taher7.melodymine.utils.Adventure.toComponent
import org.bukkit.entity.Player
import java.util.*

class AdminMode : SubCommand() {

    override var name = "adminmode"
    override var description = Storage.adminmodeDescription
    override var syntax = "/voicechat adminmode"
    override var permission = "voicechat.adminmode"

    override fun handler(player: Player, args: Array<out String>) {
        val melodyPlayer = Storage.onlinePlayers[player.uniqueId.toString()] ?: return

        if (!melodyPlayer.webIsOnline) {
            player.sendMessage("<prefix> Para iniciar el Modo Administrador debes conectarte a la web".toComponent())
            return
        }

        if (!melodyPlayer.isActiveVoice) {
            player.sendMessage("<prefix>Para iniciar el Modo Administrador debes hacer click en el botón de iniciar el chat de voz en la web.".toComponent())
            return
        }

        if (melodyPlayer.adminMode) {
            MelodyManager.disableAdminMode(melodyPlayer.uuid)
            player.sendMessage("<prefix>El modo administrador ha sido desactivado.".toComponent())
        } else {
            MelodyManager.enableAdminMode(melodyPlayer.uuid)
            player.sendMessage("<prefix>El modo administrador ha sido activado.".toComponent())
        }
    }
}