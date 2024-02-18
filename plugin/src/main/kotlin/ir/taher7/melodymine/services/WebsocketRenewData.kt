package ir.taher7.melodymine.services

import com.google.common.util.concurrent.ThreadFactoryBuilder
import ir.taher7.melodymine.MelodyMine
import ir.taher7.melodymine.core.MelodyManager
import ir.taher7.melodymine.models.*
import ir.taher7.melodymine.storage.Storage
import org.bukkit.scheduler.BukkitRunnable
import java.util.concurrent.Executors


class WebsocketRenewData {
    init {
        startConnectionTrack()
        startDistanceTrack()
    }

    val threadFactory =
        ThreadFactoryBuilder()
            .setNameFormat("${MelodyMine.instance.description.name.lowercase()}-renew-data-thread-%d")
            .build()

    val connectionThreadPool = Executors.newFixedThreadPool(10, threadFactory)
    val distanceThreadPool = Executors.newFixedThreadPool(10, threadFactory)

    private fun startConnectionTrack() {

        object : BukkitRunnable() {
            override fun run() {

                connectionThreadPool.submit {
                    if (Storage.onlinePlayers.isNotEmpty() && Websocket.socket.isActive) {
                        val playerList: MutableList<RenewConnectionData> = mutableListOf()
                        Storage.onlinePlayers.values.filter { player -> player.webIsOnline && player.isActiveVoice && !player.adminMode }
                            .forEach { melodyPlayer ->
                                val enableVoiceTask: MutableList<EnableVoiceTask> = mutableListOf()
                                val disableVoiceTask: MutableList<DisableVoiceTask> = mutableListOf()


                                Storage.onlinePlayers.values.filter { player -> player.uuid != melodyPlayer.uuid && player.webIsOnline && player.isActiveVoice && !player.adminMode && player.callTarget != melodyPlayer }
                                    .forEach { targetPlayer ->
                                        val playerLocation = melodyPlayer.player?.location
                                        val targetLocation = targetPlayer.player?.location

                                        if (playerLocation != null &&
                                            targetLocation != null &&
                                            playerLocation.world == targetLocation.world
                                        ) {
                                            val distance = playerLocation.distance(targetLocation)
                                            val maxDistance = Storage.maxDistance

                                            if (distance < (maxDistance + 50)) {
                                                if (!melodyPlayer.isSendOffer.contains(targetPlayer.uuid)) {
                                                    melodyPlayer.isSendOffer.add(targetPlayer.uuid)
                                                    if (!targetPlayer.isSendOffer.contains(melodyPlayer.uuid)) {
                                                        targetPlayer.socketID?.let { socketID ->
                                                            enableVoiceTask.add(
                                                                EnableVoiceTask(
                                                                    socketID = socketID
                                                                )
                                                            )
                                                        }

                                                    }
                                                }
                                            }


                                            if (distance > (maxDistance + 200)) {
                                                if (melodyPlayer.isSendOffer.contains(targetPlayer.uuid)) {
                                                    val targetSocketID = targetPlayer.socketID
                                                    if (targetSocketID != null) {
                                                        disableVoiceTask.add(
                                                            DisableVoiceTask(
                                                                socketID = targetSocketID
                                                            )
                                                        )
                                                        melodyPlayer.isSendOffer.remove(targetPlayer.uuid)
                                                    }
                                                }
                                            }
                                        }
                                    }

                                if (enableVoiceTask.isNotEmpty() || disableVoiceTask.isNotEmpty()) {
                                    playerList.add(
                                        RenewConnectionData(
                                            name = melodyPlayer.name,
                                            uuid = melodyPlayer.uuid,
                                            server = melodyPlayer.server,
                                            enableVoice = enableVoiceTask,
                                            disableVoice = disableVoiceTask,
                                        )
                                    )
                                }
                            }

                        MelodyManager.renewConnectionData(playerList)

                    }
                }
            }
        }.runTaskTimer(MelodyMine.instance, 0L, Storage.updateConnectionTime)
    }


    private fun startDistanceTrack() {
        object : BukkitRunnable() {
            override fun run() {
                distanceThreadPool.submit {
                    if (Storage.onlinePlayers.isNotEmpty() && Websocket.socket.isActive) {
                        val playerList: MutableList<RenewDistanceData> = mutableListOf()
                        Storage.onlinePlayers.values.filter { player -> player.webIsOnline && player.isActiveVoice && !player.adminMode }
                            .forEach { melodyPlayer ->
                                val volumeTask: MutableList<VolumeTask> = mutableListOf()

                                Storage.onlinePlayers.values.filter { player -> player.uuid != melodyPlayer.uuid && player.webIsOnline && player.isActiveVoice && !player.adminMode && player.callTarget != melodyPlayer }
                                    .forEach { targetPlayer ->
                                        val playerLocation = melodyPlayer.player?.location
                                        val targetLocation = targetPlayer.player?.location

                                        if (playerLocation != null &&
                                            targetLocation != null &&
                                            playerLocation.world == targetLocation.world
                                        ) {
                                            val distance = playerLocation.distance(targetLocation)
                                            val maxDistance = Storage.maxDistance


                                            if (distance < (maxDistance + 120)) {
                                                val targetSocketID = targetPlayer.socketID
                                                if (targetSocketID != null) {
                                                    melodyPlayer.player?.eyeLocation?.let { playerLocation ->
                                                        targetPlayer.player?.eyeLocation?.let { targetLocation ->
                                                            volumeTask.add(
                                                                VolumeTask(
                                                                    socketID = targetSocketID,
                                                                    distance = playerLocation.distance(targetLocation),
                                                                    playerLocation = Location(
                                                                        x = playerLocation.x,
                                                                        y = playerLocation.y,
                                                                        z = playerLocation.z,
                                                                    ),
                                                                    targetLocation = Location(
                                                                        x = targetLocation.x,
                                                                        y = targetLocation.y,
                                                                        z = targetLocation.z,
                                                                    ),
                                                                    playerDirection = Location(
                                                                        x = playerLocation.direction.x,
                                                                        y = playerLocation.direction.y,
                                                                        z = playerLocation.direction.z,
                                                                    ),
                                                                    targetDirection = Location(
                                                                        x = targetLocation.direction.x,
                                                                        y = targetLocation.direction.y,
                                                                        z = targetLocation.direction.z,
                                                                    )
                                                                )
                                                            )

                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                if (volumeTask.isNotEmpty()) {
                                    playerList.add(
                                        RenewDistanceData(
                                            name = melodyPlayer.name,
                                            uuid = melodyPlayer.uuid,
                                            server = melodyPlayer.server,
                                            volume = volumeTask,
                                        )
                                    )
                                }
                            }

                        MelodyManager.renewDistance(playerList)

                    }
                }
            }
        }.runTaskTimer(MelodyMine.instance, 0L, Storage.updateDistanceTime)

    }
}