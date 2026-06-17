package com.ericgrandt.totaleconomy.impl

import com.ericgrandt.totaleconomy.model.Player
import net.kyori.adventure.text.Component
import java.util.UUID
import org.bukkit.entity.Player as PlayerEntity

class PaperPlayer(
    private val player: PlayerEntity,
) : Player {
    override val uniqueId: UUID = player.uniqueId

    override val name: String = player.name

    override fun sendMessage(message: Component) {
        player.sendMessage(message)
    }
}
