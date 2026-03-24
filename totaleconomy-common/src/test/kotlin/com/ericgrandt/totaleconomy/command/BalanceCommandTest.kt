package com.ericgrandt.totaleconomy.command

import com.ericgrandt.totaleconomy.econ.CommonEconomy
import com.ericgrandt.totaleconomy.game.CommonPlayer
import com.ericgrandt.totaleconomy.model.DatabaseError
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import net.kyori.adventure.text.Component
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.BeforeTest
import kotlin.test.assertTrue

class BalanceCommandTest {
    @MockK
    lateinit var econMock: CommonEconomy

    @MockK
    lateinit var playerMock: CommonPlayer

    @BeforeTest
    fun setUp() = MockKAnnotations.init(this, relaxUnitFun = true)

    @Test
    @Tag("Unit")
    fun runAsync_WithSuccess_ShouldSendCorrectMessage() = runTest {
        // Arrange
        every { econMock.getBalance(any()) } returns Ok(1.23)
        every { econMock.format(1.23) } returns Component.text("1.23 Diamonds")
        every { playerMock.getUniqueID() } returns UUID.randomUUID()

        val sut = BalanceCommand(econMock)

        // Act
        val actual = sut.runAsync(this, playerMock, mutableMapOf())

        // Assert
        testScheduler.advanceUntilIdle()

        assertTrue(actual)
        verify(exactly = 1) { playerMock.sendMessage(Component.text("You have ").append(Component.text("1.23 Diamonds"))) }
    }

    @Test
    @Tag("Unit")
    fun runAsync_WithFailure_ShouldSendErrorMessageToSender() = runTest {
        // Arrange
        every { econMock.getBalance(any()) } returns Err(DatabaseError)
        every { econMock.format(1.23) } returns Component.text("1.23 Diamonds")
        every { playerMock.getUniqueID() } returns UUID.randomUUID()

        val sut = BalanceCommand(econMock)

        // Act
        val actual = sut.runAsync(this, playerMock, mutableMapOf())

        // Assert
        testScheduler.advanceUntilIdle()

        assertTrue(actual)
        verify(exactly = 1) { playerMock.sendMessage(Component.text("An error occurred. Please contact an administrator.")) }
    }
}