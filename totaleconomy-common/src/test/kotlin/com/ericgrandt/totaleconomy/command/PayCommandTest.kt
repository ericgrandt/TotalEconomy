package com.ericgrandt.totaleconomy.command

import com.ericgrandt.totaleconomy.econ.CommonEconomy
import com.ericgrandt.totaleconomy.game.CommonPlayer
import com.ericgrandt.totaleconomy.model.InsufficientBalance
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

class PayCommandTest {
    @MockK
    lateinit var econMock: CommonEconomy

    @MockK
    lateinit var fromPlayerMock: CommonPlayer

    @MockK
    lateinit var toPlayerMock: CommonPlayer

    @BeforeTest
    fun setUp() = MockKAnnotations.init(this, relaxUnitFun = true)

    @Test
    @Tag("Unit")
    fun runAsync_WithSuccess_ShouldSendCorrectMessages() = runTest {
        // Arrange
        every { econMock.transferBalance(any()) } returns Ok(true)
        every { econMock.format(10.51) } returns Component.text("10.51 Diamonds")
        every { fromPlayerMock.getUniqueID() } returns UUID.randomUUID()
        every { fromPlayerMock.getName() } returns "From Player"
        every { toPlayerMock.getUniqueID() } returns UUID.randomUUID()
        every { toPlayerMock.getName() } returns "To Player"

        val sut = PayCommand(econMock)

        // Act
        val actual = sut.runAsync(this, fromPlayerMock, mutableMapOf("toPlayer" to CommonParameter(toPlayerMock), "amount" to CommonParameter(10.51)))

        // Assert
        testScheduler.advanceUntilIdle()

        assertTrue(actual)
        verify(exactly = 1) { fromPlayerMock.sendMessage(Component.text("You paid To Player ").append(Component.text("10.51 Diamonds"))) }
        verify(exactly = 1) {
            toPlayerMock.sendMessage(Component.text("You received ")
                .append(Component.text("10.51 Diamonds"))
                .append(Component.text(" from From Player")))
        }
    }

    @Test
    @Tag("Unit")
    fun runAsync_WithFailure_ShouldSendErrorMessageToSender() = runTest {
        // Arrange
        every { econMock.transferBalance(any()) } returns Err(InsufficientBalance)
        every { econMock.format(10.51) } returns Component.text("10.51 Diamonds")
        every { fromPlayerMock.getUniqueID() } returns UUID.randomUUID()
        every { fromPlayerMock.getName() } returns "From Player"
        every { toPlayerMock.getUniqueID() } returns UUID.randomUUID()
        every { toPlayerMock.getName() } returns "To Player"

        val sut = PayCommand(econMock)

        // Act
        val actual = sut.runAsync(this, fromPlayerMock, mutableMapOf("toPlayer" to CommonParameter(toPlayerMock), "amount" to CommonParameter(10.51)))

        // Assert
        testScheduler.advanceUntilIdle()

        assertTrue(actual)
        verify(exactly = 1) { fromPlayerMock.sendMessage(Component.text("Insufficient balance")) }
    }
}