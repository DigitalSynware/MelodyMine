package ir.taher7.melodymine.commands.subcommands

import ir.taher7.melodymine.commands.SubCommand
import ir.taher7.melodymine.core.MelodyManager
import ir.taher7.melodymine.storage.Storage
import ir.taher7.melodymine.utils.Adventure
import ir.taher7.melodymine.utils.Adventure.sendMessage
import ir.taher7.melodymine.utils.Adventure.toComponent
import org.bukkit.entity.Player

class Reload : SubCommand() {
    override var name = "reload"
    override var description = Storage.reloadDescription
    override var syntax = "/voicechat reload"
    override var permission = "voicechat.reload"

    override fun handler(player: Player, args: Array<out String>) {
        Storage.reload()

        Adventure.initMiniMessage()
        player.sendMessage("<prefix>El plugin ha sido recargado correctamente".toComponent())
        Storage.onlinePlayers.values.forEach { melodyPlayer ->
            if (melodyPlayer.webIsOnline) {
                MelodyManager.sendSoundSetting(melodyPlayer.socketID!!)
            }
            if (melodyPlayer.isActiveVoice) {
                melodyPlayer.talkBossBar?.initTalkBossBar()
            }
        }
    }

}