package ir.taher7.melodymine.commands.subcommands

import ir.taher7.melodymine.commands.SubCommand
import ir.taher7.melodymine.core.MelodyManager
import ir.taher7.melodymine.storage.Storage
import ir.taher7.melodymine.utils.Adventure.sendMessage
import ir.taher7.melodymine.utils.Adventure.toComponent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

class Call : SubCommand() {
    private val coolDown = hashMapOf<UUID, Long>()

    override var name = "call"
    override var description = Storage.callDescription
    override var syntax = "/voicechat call"
    override var permission = "voicechat.call"
    override fun handler(player: Player, args: Array<out String>) {
        if (coolDown.containsKey(player.uniqueId) && (System.currentTimeMillis() - coolDown[player.uniqueId]!!) <= 5000) {
            player.sendMessage("<prefix>You can use this command after <count_color>${((5000 - (System.currentTimeMillis() - coolDown[player.uniqueId]!!)) / 1000)}<text> second.".toComponent())
            return
        }

        if (args.size > 3 || args.size < 2) {
            sendCallHelpMessage(player)
            return
        }

        when (args[1]) {

            "start" -> {

                if (!player.hasPermission("voicechat.call.start")) {
                    player.sendMessage("<prefix>No tienes permiso para usar este comando.".toComponent())
                    return
                }

                if (args.size != 3) {
                    sendCallHelpMessage(player)
                    return
                }

                val bukkitPlayer = Bukkit.getPlayer(args[2])
                if (bukkitPlayer == null) {
                    player.sendMessage("<prefix>El jugador no está en línea.".toComponent())
                    return
                }

                if (bukkitPlayer.uniqueId == player.uniqueId) {
                    player.sendMessage("<prefix>No puedes llamarte a ti mismo.".toComponent())
                    return
                }

                val melodyPlayer = Storage.onlinePlayers[player.uniqueId.toString()] ?: return
                if (!melodyPlayer.isActiveVoice) {
                    player.sendMessage("<prefix>Debes activar tu chat de voz, usa <i>/voicechat start link</i> para iniciar el chat de voz.".toComponent())
                    return
                }

                if (melodyPlayer.isInCall) {
                    player.sendMessage("<prefix>Ya estás en una llamada con <count_color>${melodyPlayer.callTarget?.name}<text>, si deseas iniciar una nueva llamada, haz <i>/voicechat call end</i>.".toComponent())
                    return
                }

                if (melodyPlayer.isCallPending) {
                    player.sendMessage("<prefix>Ya tienes una solicitud de llamada pendiente con <count_color>${melodyPlayer.callPendingTarget?.name}<text>, por favor espera a que termine la llamada pendiente.".toComponent())
                    return
                }

                if (melodyPlayer.callToggle) {
                    player.sendMessage("<prefix>Las solicitudes de llamada están deshabilitadas, usa <i>/voicechat call toggle</i> para habilitarla.".toComponent())
                    return
                }

                if (melodyPlayer.adminMode) {
                    player.sendMessage("<prefix>You Must First Disable Your AdminMode.".toComponent())
                    player.sendMessage("<prefix>Debes desactivar tu modo de administrador primero.".toComponent())
                    return
                }

                if (Storage.disableWorld.contains(player.world.name)) {
                    player.sendMessage("<prefix>Call is Disable in this World.".toComponent())
                    player.sendMessage("<prefix>Las llamadas están deshabilitadas en este mundo.".toComponent())
                    return
                }

                val targetPlayer = Storage.onlinePlayers[bukkitPlayer.uniqueId.toString()] ?: return
                if (!targetPlayer.isActiveVoice || targetPlayer.isInCall || targetPlayer.isCallPending || targetPlayer.callToggle || Storage.disableWorld.contains(targetPlayer.player?.world?.name)) {
                    player.sendMessage("<prefix><count_color>${targetPlayer.name} <text>no está disponible, por favor inténtalo de nuevo más tarde.".toComponent())
                    return
                }

                MelodyManager.startCall(melodyPlayer, targetPlayer)
                coolDown[player.uniqueId] = System.currentTimeMillis()
                return
            }

            "end" -> {
                if (!player.hasPermission("voicechat.call.end")) {
                    player.sendMessage("<prefix>No tienes permiso para usar este comando.".toComponent())
                    return
                }

                val melodyPlayer = Storage.onlinePlayers[player.uniqueId.toString()] ?: return
                if (!melodyPlayer.isActiveVoice) {
                    player.sendMessage("<prefix>Debes activar tu chat de voz, usa <i>/voicechat start link</i> para iniciar el chat de voz.".toComponent())
                    return
                }

                if (!melodyPlayer.isInCall) {
                    player.sendMessage("<prefix>No estás en ninguna llamada.".toComponent())
                    return
                }

                if (melodyPlayer.adminMode) {
                    player.sendMessage("<prefix>Debes desactivar tu modo de administrador primero.".toComponent())
                    return
                }

                val targetPlayer = Storage.onlinePlayers[melodyPlayer.callTarget?.uuid] ?: return
                if (!targetPlayer.isActiveVoice || !targetPlayer.isInCall || targetPlayer.isCallPending) {
                    player.sendMessage("<prefix><count_color>${targetPlayer.name} <text>no está disponible, por favor inténtalo de nuevo más tarde.".toComponent())
                    return
                }

                MelodyManager.endCall(melodyPlayer, targetPlayer)

                coolDown[player.uniqueId] = System.currentTimeMillis()
                return
            }

            "accept" -> {
                if (!player.hasPermission("voicechat.call.accept")) {
                    player.sendMessage("<prefix>No tienes permiso para usar este comando.".toComponent())
                    return
                }

                val melodyPlayer = Storage.onlinePlayers[player.uniqueId.toString()] ?: return
                if (!melodyPlayer.isActiveVoice) {
                    player.sendMessage("<prefix>Debes activar tu chat de voz, usa <i>/voicechat start link</i> para iniciar el chat de voz.".toComponent())
                    return
                }

                if (melodyPlayer.isInCall) {
                    player.sendMessage("<prefix>Ya estás en una llamada con <count_color>${melodyPlayer.callTarget?.name}<text>, si deseas aceptar la llamada, haz <i>/voicechat call end</i>.".toComponent())
                    return
                }

                if (!melodyPlayer.isCallPending) {
                    player.sendMessage("<prefix>No tienes ninguna solicitud de llamada.".toComponent())
                    return
                }

                if (melodyPlayer.callToggle) {
                    player.sendMessage("<prefix>Tu solicitud de llamada está deshabilitada, usa <i>/voicechat call toggle</i> para habilitarla.".toComponent())
                    return
                }

                if (melodyPlayer.adminMode) {
                    player.sendMessage("<prefix>Debes desactivar tu modo de administrador primero.".toComponent())
                    return
                }

                if (Storage.disableWorld.contains(player.world.name)) {
                    player.sendMessage("<prefix>Las llamadas están deshabilitadas en este mundo.".toComponent())
                    return
                }

                val targetPlayer = Storage.onlinePlayers[melodyPlayer.callPendingTarget?.uuid] ?: return
                if (!targetPlayer.isActiveVoice || targetPlayer.isInCall || !targetPlayer.isCallPending || targetPlayer.callToggle || targetPlayer.adminMode || Storage.disableWorld.contains(targetPlayer.player?.world?.name)) {
                    player.sendMessage("<prefix><count_color>${targetPlayer.name} <text>no está disponible, por favor inténtalo de nuevo más tarde.".toComponent())
                    return
                }

                MelodyManager.acceptCall(melodyPlayer, targetPlayer)

                coolDown[player.uniqueId] = System.currentTimeMillis()
                return
            }

            "deny" -> {
                if (!player.hasPermission("voicechat.call.deny")) {
                    player.sendMessage("<prefix>No tienes permiso para usar este comando.".toComponent())
                    return
                }

                val melodyPlayer = Storage.onlinePlayers[player.uniqueId.toString()] ?: return
                if (!melodyPlayer.isActiveVoice) {
                    player.sendMessage("<prefix>Para activar tu chat de voz, usa <i>/voicechat start link</i>.".toComponent())
                    return
                }

                if (melodyPlayer.isInCall) {
                    player.sendMessage("<prefix>Ya estás en una llamada con <count_color>${melodyPlayer.callTarget?.name}<text>, si deseas aceptar la llamada, haz <i>/voicechat call end</i>.".toComponent())
                    return
                }

                if (!melodyPlayer.isCallPending) {
                    player.sendMessage("<prefix>No tienes ninguna solicitud de llamada.".toComponent())
                    return
                }

                if (melodyPlayer.callToggle) {
                    player.sendMessage("<prefix>Tu solicitud de llamada está deshabilitada, usa <i>/voicechat call toggle</i> para habilitarla.".toComponent())
                    return
                }

                val targetPlayer = Storage.onlinePlayers[melodyPlayer.callPendingTarget?.uuid] ?: return
                if (!targetPlayer.isActiveVoice || targetPlayer.isInCall || !targetPlayer.isCallPending || targetPlayer.callToggle) {
                    player.sendMessage("<prefix><count_color>${targetPlayer.name} <text>no está disponible, por favor inténtalo de nuevo más tarde.".toComponent())
                    return
                }

                MelodyManager.denyCall(melodyPlayer, targetPlayer)

                coolDown[player.uniqueId] = System.currentTimeMillis()
                return
            }

            "toggle" -> {
                if (!player.hasPermission("voicechat.call.toggle")) {
                    player.sendMessage("<prefix>No tienes permiso para usar este comando.".toComponent())
                    return
                }

                val melodyPlayer = Storage.onlinePlayers[player.uniqueId.toString()] ?: return

                MelodyManager.toggleCall(melodyPlayer)
                coolDown[player.uniqueId] = System.currentTimeMillis()
                return
            }

            else -> sendCallHelpMessage(player)
        }

    }

    private fun sendCallHelpMessage(player: Player) {
        player.sendMessage(Storage.contentHeader.toComponent())
        player.sendMessage("")
        player.sendMessage("<click:run_command:'${syntax} start'><hover:show_text:'<text_hover>Haz click para ejecutar<i>${syntax} start</i>'><text_hover>${syntax} start <player> <#FFF4E4><bold>|</bold> <text>Llama a otro jugador.</hover></click>".toComponent())
        player.sendMessage("<click:run_command:'${syntax} end'><hover:show_text:'<text_hover>Haz click para ejecutar<i>${syntax} end</i>'><text_hover>${syntax} end <#FFF4E4><bold>|</bold> <text>Finaliza la llamada en la que estás.</hover></click>".toComponent())
        player.sendMessage("<click:run_command:'${syntax} accept'><hover:show_text:'<text_hover>Haz click para ejecutar <i>${syntax} accept</i>'><text_hover>${syntax} accept <#FFF4E4><bold>|</bold> <text>Acepta una llamada entrante.</hover></click>".toComponent())
        player.sendMessage("<click:run_command:'${syntax} deny'><hover:show_text:'<text_hover>Haz click para ejecutar <i>${syntax} deny</i>'><text_hover>${syntax} deny <#FFF4E4><bold>|</bold> <text>Rechaza una llamada entrante.</hover></click>".toComponent())
        player.sendMessage("<click:run_command:'${syntax} toggle'><hover:show_text:'<text_hover>Haz click para ejecutar <i>${syntax} toggle</i>'><text_hover>${syntax} toggle <#FFF4E4><bold>|</bold> <text>Activa o desactiva las llamadas entrantes.</hover></click>".toComponent())
        player.sendMessage("")
        player.sendMessage(Storage.contentFooter.toComponent())
    }
}
