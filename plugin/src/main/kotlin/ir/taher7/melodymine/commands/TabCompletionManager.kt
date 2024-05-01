package ir.taher7.melodymine.commands

import com.cryptomorin.xseries.ReflectionUtils
import ir.taher7.melodymine.storage.Storage
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class TabCompletionManager : TabCompleter {
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>,
    ): List<String>? {
        when (args.size) {
            1 -> {
                return Storage.subCommands
                    .filter { subCommand -> sender.hasPermission(subCommand.permission) }
                    .map { subCommand -> subCommand.name }
                    .filter { name -> name.contains(args[0], true) }
            }

            2 -> {
                if (args[0].equals("start", true)) {
                    return if (ReflectionUtils.supports(13) && sender.hasPermission("voicechat.qrcode")) {
                        listOf("link", "qrcode").filter { item -> item.contains(args[1]) }
                    } else {
                        listOf("link").filter { item -> item.contains(args[1], true) }
                    }
                }

                if (args[0].equals("control", true)) {
                    return if (sender.hasPermission("voicechat.control")) {
                        listOf("mute", "deafen").filter { item -> item.contains(args[1], true) }
                    } else {
                        null
                    }
                }

                if (args[0].equals("call", true)) {
                    return if (sender.hasPermission("voicechat.call")) {
                        listOf("start", "end", "accept", "deny", "toggle").filter { item ->
                            item.contains(
                                args[1],
                                true
                            )
                        }
                    } else {
                        null
                    }
                }
            }

            else -> {}
        }

        return null
    }

}
