package com.ericgrandt.totaleconomy.game

import java.util.*

interface CommonPlayer : CommonSender {
    fun getUniqueID(): UUID
    fun getName(): String
}