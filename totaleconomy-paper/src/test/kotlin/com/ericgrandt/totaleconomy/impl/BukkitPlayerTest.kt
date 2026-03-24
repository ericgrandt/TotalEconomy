package com.ericgrandt.totaleconomy.impl

import com.ericgrandt.totaleconomy.game.CommonPlayer
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.bukkit.entity.Player
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

class BukkitPlayerTest {
    @MockK
    lateinit var playerMock: Player

    @BeforeTest
    fun setUp() = MockKAnnotations.init(this, relaxUnitFun = true)

    @Test
    @Tag("Unit")
    fun getUniqueId_ShouldReturnPlayerUuid() {
        // Arrange
        val uuid = UUID.randomUUID()
        every { playerMock.uniqueId } returns uuid

        val sut = BukkitPlayer(playerMock)

        // Act
        val actual = sut.getUniqueID()

        // Assert
        assertEquals(uuid, actual)
    }

    @Test
    @Tag("Unit")
    fun getName_ShouldReturnName() {
        // Arrange
        val name = "NiceName"
        every {playerMock.name } returns name

        val sut = BukkitPlayer(playerMock)

        // Act
        val actual = sut.getName()

        // Assert
        assertEquals(name, actual)
    }
}