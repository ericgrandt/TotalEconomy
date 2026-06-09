package com.ericgrandt.totaleconomy.command

import com.ericgrandt.totaleconomy.model.Sender

interface Command {
    suspend fun execute(
        sender: Sender,
        args: Map<String, CommandArgument>,
    ): CommandResult
}
