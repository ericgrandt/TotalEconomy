package com.ericgrandt.totaleconomy.model

import net.kyori.adventure.text.Component

interface Sender {
    /**
     * Sends a message to the sender
     *
     * @param message the [Component] that will be sent to the sender
     */
    fun sendMessage(message: Component)
}
