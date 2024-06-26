package ir.taher7.melodymine.commands.subcommands

import com.cryptomorin.xseries.ReflectionUtils
import ir.taher7.melodymine.commands.SubCommand
import ir.taher7.melodymine.core.MelodyManager
import ir.taher7.melodymine.storage.Storage
import ir.taher7.melodymine.utils.Adventure.sendMessage
import ir.taher7.melodymine.utils.Adventure.toComponent
import org.bukkit.entity.Player
import java.util.*

class Start : SubCommand() {

    private val coolDown = hashMapOf<UUID, Long>()

    override var name = "start"
    override var description = Storage.startDescription
    override var syntax = "/voicechat start"
    override var permission = "voicechat.start"

    override fun handler(player: Player, args: Array<out String>) {

        if (coolDown.containsKey(player.uniqueId) && (System.currentTimeMillis() - coolDown[player.uniqueId]!!) <= 60000) {
            player.sendMessage("<prefix>Podrás usar este comando de nuevo en <count_color>${((60000 - (System.currentTimeMillis() - coolDown[player.uniqueId]!!)) / 1000)}<text> segundos.".toComponent())
            return
        }
        when (args.size) {
            2 -> {
                if (ReflectionUtils.supports(13) && args[1].equals("qrcode", true)) {
                    if (!player.hasPermission("voicechat.qrcode")) {
                        player.sendMessage("<prefix>No tienes permiso para usar este comando.".toComponent())
                        return
                    }
//                    if (ReflectionUtils.supports(9) && player.inventory.itemInOffHand.type == Material.AIR) {
//                        MelodyManager.sendStartQRCode(player, 1, true)
//                        coolDown[player.uniqueId] = System.currentTimeMillis()
//                        return
//                    }
                    player.inventory.forEachIndexed { index, itemStack ->
                        if (index in 0..8) {
                            if (itemStack == null) {
                                MelodyManager.sendStartQRCode(player, index)
                                coolDown[player.uniqueId] = System.currentTimeMillis()
                                return
                            }
                        }
                    }
                    player.sendMessage("<prefix>Tu hotbar está llena!".toComponent())

                }

                if (args[1].equals("link", true)) {
                    MelodyManager.sendStartLink(player)
                    coolDown[player.uniqueId] = System.currentTimeMillis()
                    return
                }

                sendStartHelpMessage(player)
            }

            else -> {
                sendStartHelpMessage(player)
            }
        }
    }

    private fun sendStartHelpMessage(player: Player) {
        player.sendMessage(Storage.contentHeader.toComponent())
        player.sendMessage("")
        player.sendMessage("<click:run_command:'${syntax} link'><hover:show_text:'<text_hover>Haz click para ejecutar <i>${syntax} link</i>'><text_hover>${syntax} link <#FFF4E4><bold>|</bold> <text>Recibe un enlace de conexión</hover></click>".toComponent())
        player.sendMessage("<click:run_command:'${syntax} qrcode'><hover:show_text:'<text_hover>Haz click para ejecutar <i>${syntax} qrcode</i>'><text_hover>${syntax} qrcode <#FFF4E4><bold>|</bold> <text>Recibe un Código QR de conexión</hover></click>".toComponent())
        player.sendMessage("")
        player.sendMessage(Storage.contentFooter.toComponent())
    }
}