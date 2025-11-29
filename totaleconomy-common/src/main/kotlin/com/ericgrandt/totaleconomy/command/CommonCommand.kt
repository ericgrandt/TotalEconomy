package com.ericgrandt.totaleconomy.command

import com.ericgrandt.totaleconomy.game.CommonSender
import kotlinx.coroutines.CoroutineScope

interface CommonCommand {
    fun runAsync(scope: CoroutineScope, sender: CommonSender, args: MutableMap<String, CommonParameter<*>>): Boolean
}