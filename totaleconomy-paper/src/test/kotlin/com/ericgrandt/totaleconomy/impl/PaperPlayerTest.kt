package com.ericgrandt.totaleconomy.impl

import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import org.bukkit.entity.Player
import org.junit.jupiter.api.Test
import kotlin.test.BeforeTest

class PaperPlayerTest {
    @MockK
    lateinit var playerMock: Player

    @BeforeTest
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
    }

    @Test
    fun getUniqueId() {
    }

    @Test
    fun getName() {
    }

    @Test
    fun sendMessage() {
    }
}
