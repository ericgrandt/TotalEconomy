package com.ericgrandt.totaleconomy.model

import java.util.UUID

interface Player : Sender {
    /**
     * Retrieve the player's unique identifier.
     *
     * This UUID should correspond to the identifier provided by Minecraft.
     *
     * @return the [UUID] of the player
     */
    fun getUniqueID(): UUID

    /**
     * Retrieve the player's name.
     *
     * @return the player's name as a [String]
     */
    fun getName(): String
}
