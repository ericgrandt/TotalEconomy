package com.ericgrandt.totaleconomy.game

import net.kyori.adventure.bossbar.BossBar
import java.util.*

interface CommonPlayer : CommonSender {
    val uniqueId: UUID

    val name: String
}