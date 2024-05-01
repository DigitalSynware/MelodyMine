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
        val coolDown = Storage.muteCoolDown
        if (coolDown.containsKey(player.uniqueId) && (System.currentTimeMillis() - coolDown[player.uniqueId]!!) <= 5000) {
            player.sendMessage("<prefix>You can use this command after <count_color>${((5000 - (System.currentTimeMillis() - coolDown[player.uniqueId]!!)) / 1000)}<text> second.".toComponent())
            return
        }

        if (args.size != 2) {
            player.sendMessage("<prefix>use: <i>${syntax} <player></i>.".toComponent())
            return
        }

        val targetPlayer = Bukkit.getPlayer(args[1])
        if (targetPlayer == null) {
            player.sendMessage("<prefix>Player does not exist.".toComponent())
            return
        }

        val melodyPlayer = Storage.onlinePlayers[player.uniqueId.toString()] ?: return
        if (melodyPlayer.isMute) {
            player.sendMessage("<prefix><count_color>${melodyPlayer.name} <text>is already muted.".toComponent())
            return
        }

        MelodyManager.mute(targetPlayer.uniqueId.toString())

        targetPlayer.player?.sendMessage("<prefix>You have muted in voice chat.".toComponent())
        player.sendMessage("<prefix><count_color>${targetPlayer.name} <text>has mute in voice chat.".toComponent())
        coolDown[player.uniqueId] = System.currentTimeMillis()
    }

}