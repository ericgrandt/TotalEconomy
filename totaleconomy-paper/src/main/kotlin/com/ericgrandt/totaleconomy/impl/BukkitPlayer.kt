package com.ericgrandt.totaleconomy.impl

import com.ericgrandt.totaleconomy.game.CommonPlayer
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import java.util.UUID

class BukkitPlayer : CommonPlayer {
    private var player: Player

    constructor(player: Player) {
       this.player = player
    }

    override fun sendMessage(message: Component) {
        player.sendMessage(message)
    }

    override fun getUniqueID(): UUID {
        return player.uniqueId
    }

    override fun getName(): String {
        return player.name
    }
}