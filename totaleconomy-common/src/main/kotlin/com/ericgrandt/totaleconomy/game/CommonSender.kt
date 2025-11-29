package com.ericgrandt.totaleconomy.game

import net.kyori.adventure.text.Component

interface CommonSender {
    fun sendMessage(message: Component)
}