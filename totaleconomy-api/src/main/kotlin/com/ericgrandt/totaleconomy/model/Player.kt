package com.ericgrandt.totaleconomy.model

import java.util.UUID

interface Player : Sender {
    /**
     * The player's unique identifier.
     *
     * This UUID corresponds to the identifier provided by Minecraft.
     */
    val uniqueId: UUID

    /**
     * The player's name.
     */
    val name: String
}
