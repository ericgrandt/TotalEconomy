package com.ericgrandt.totaleconomy.impl

import com.ericgrandt.totaleconomy.data.entity.Balance
import com.ericgrandt.totaleconomy.econ.CommonEconomy
import com.ericgrandt.totaleconomy.model.DatabaseError
import com.ericgrandt.totaleconomy.result.Err
import com.ericgrandt.totaleconomy.result.Ok
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import net.kyori.adventure.text.Component
import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.OfflinePlayer
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.BeforeTest

class EconomyImplTest {
    @MockK
    lateinit var econMock: CommonEconomy

    @MockK
    lateinit var playerMock: OfflinePlayer

    private lateinit var sut: EconomyImpl

    @BeforeTest
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        sut = EconomyImpl(econMock)
    }

    @Test
    fun isEnabled_ShouldReturnTrue() {
        // Act
        val actual = sut.isEnabled

        // Assert
        assertTrue(actual)
    }

    @Test
    fun getName_ShouldReturnTotalEconomy() {
        // Act
        val actual =  sut.name

        // Assert
        assertEquals("Total Economy", actual)
    }

    @Test
    fun hasBankSupport_ShouldReturnFalse() {
        // Act
        val actual = sut.hasBankSupport()

        // Assert
        assertFalse(actual)
    }

    @Test
    fun fractionalDigits_ShouldReturnTwo() {
        // Act
        val actual = sut.fractionalDigits()

        // Assert
        assertEquals(2, actual)
    }

    @Test
    fun format_ShouldReturnCorrectString() {
        // Arrange
        every { econMock.format(any()) } returns Component.text("1.00 Diamond")
        val amount = 1.0

        // Act
        val actual = sut.format(amount)
        val expected = "1.00 Diamond"

        // Arrange
        assertEquals(expected, actual)
    }

    @Test
    fun currencyNamePlural_ShouldReturnDiamonds() {
        // Act
        every { econMock.currencyNamePlural() } returns "Diamonds"

        val actual = sut.currencyNamePlural()
        val expected = "Diamonds"

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun currencyNameSingular_ShouldReturnDiamond() {
        // Act
        every { econMock.currencyNameSingular() } returns "Diamond"

        val actual = sut.currencyNameSingular()
        val expected = "Diamond"

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun hasAccount_WithAccount_ShouldReturnTrue() {
        // Arrange
        every { econMock.hasAccount(any()) } returns Ok(true)
        every { playerMock.uniqueId } returns UUID.randomUUID()

        // Act
        val actual = sut.hasAccount(playerMock)

        // Assert
        assertTrue(actual)
    }

    @Test
    fun hasAccount_WithNoAccount_ShouldReturnFalse() {
        // Arrange
        every { econMock.hasAccount(any()) } returns Ok(false)
        every { playerMock.uniqueId } returns UUID.randomUUID()

        // Act
        val actual = sut.hasAccount(playerMock)

        // Assert
        assertFalse(actual)
    }

    @Test
    fun hasAccount_WithErrorResult_ShouldReturnFalse() {
        // Arrange
        every { econMock.hasAccount(any()) } returns Err(DatabaseError)
        every { playerMock.uniqueId } returns UUID.randomUUID()

        // Act
        val actual = sut.hasAccount(playerMock)

        // Assert
        assertFalse(actual)
    }

    @Test
    fun getBalance_WithSuccess_ShouldReturnBalance() {
        // Arrange
        every { econMock.getBalance(any()) } returns Ok(1.00)
        every { playerMock.uniqueId } returns UUID.randomUUID()

        // Act
        val actual = sut.getBalance(playerMock)
        val expected = 1.00

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun getBalance_WithErrorResult_ShouldReturnAZeroBalance() {
        // Arrange
        every { econMock.getBalance(any()) } returns Err(DatabaseError)
        every { playerMock.uniqueId } returns UUID.randomUUID()

        // Act
        val actual = sut.getBalance(playerMock)
        val expected = 0.00

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun has_WithAmountEqualToZero_ShouldReturnFalse() {
        // Arrange
        every { playerMock.uniqueId } returns UUID.randomUUID()

        // Act
        val actual = sut.has(playerMock, 0.00)

        // Assert
        assertFalse(actual)
    }

    @Test
    fun has_WithAmountLessThanZero_ShouldReturnFalse() {
        // Arrange
        every { playerMock.uniqueId } returns UUID.randomUUID()

        // Act
        val actual = sut.has(playerMock, -1.00)

        // Assert
        assertFalse(actual)
    }

    @Test
    fun has_WithEnoughBalance_ShouldReturnTrue() {
        // Arrange
        every { econMock.getBalance(any()) } returns Ok(4.00)
        every { playerMock.uniqueId } returns UUID.randomUUID()

        // Act
        val actual = sut.has(playerMock, 1.50)

        // Assert
        assertTrue(actual)
    }

    @Test
    fun has_WithInsufficientBalance_ShouldReturnFalse() {
        // Arrange
        every { econMock.getBalance(any()) } returns Ok(4.00)
        every { playerMock.uniqueId } returns UUID.randomUUID()

        // Act
        val actual = sut.has(playerMock, 5.50)

        // Assert
        assertFalse(actual)
    }

    @Test
    fun has_WithErrorGettingBalance_ShouldReturnFalse() {
        // Arrange
        every { econMock.getBalance(any()) } returns Err(DatabaseError)
        every { playerMock.uniqueId } returns UUID.randomUUID()

        // Act
        val actual = sut.has(playerMock, 1.00)

        // Assert
        assertFalse(actual)
    }

    @Test
    fun withdrawPlayer_WithSuccessWithdrawing_ShouldReturnSuccessResponse() {
        // Arrange
        val mockBalance = Balance(UUID.randomUUID(), UUID.randomUUID(), 15.0)
        every { econMock.withdrawFromBalance(any()) } returns Ok(mockBalance)
        every { playerMock.uniqueId } returns UUID.randomUUID()

        // Act
        val actual = sut.withdrawPlayer(playerMock, 5.0)
        val expected = EconomyResponse(5.0, 15.0, EconomyResponse.ResponseType.SUCCESS, "")

        // Assert
        assertEquals(expected.amount, actual.amount)
        assertEquals(expected.balance, actual.balance)
        assertEquals(expected.type, actual.type)
        assertEquals(expected.errorMessage, actual.errorMessage)
    }

    @Test
    fun withdrawPlayer_WithErrorWithdrawing_ShouldReturnFailureResponse() {
        // Arrange
        every { econMock.withdrawFromBalance(any()) } returns Err(DatabaseError)
        every { playerMock.uniqueId } returns UUID.randomUUID()

        // Act
        val actual = sut.withdrawPlayer(playerMock, 5.0)
        val expected = EconomyResponse(5.0, 0.0, EconomyResponse.ResponseType.FAILURE, "unable to withdraw from balance")

        // Assert
        assertEquals(expected.amount, actual.amount)
        assertEquals(expected.balance, actual.balance)
        assertEquals(expected.type, actual.type)
        assertEquals(expected.errorMessage, actual.errorMessage)
    }

    @Test
    fun withdrawPlayer_WithWithdrawAmountOfZero_ShouldReturnFailureResponse() {
        // Arrange
        every { econMock.withdrawFromBalance(any()) } returns Err(DatabaseError)
        every { playerMock.uniqueId } returns UUID.randomUUID()

        // Act
        val actual = sut.withdrawPlayer(playerMock, 0.0)
        val expected = EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "withdraw amount must be greater than 0")

        // Assert
        assertEquals(expected.amount, actual.amount)
        assertEquals(expected.balance, actual.balance)
        assertEquals(expected.type, actual.type)
        assertEquals(expected.errorMessage, actual.errorMessage)
    }

    @Test
    fun withdrawPlayer_WithWithdrawAmountLessThanZero_ShouldReturnFailureResponse() {
        // Arrange
        every { econMock.withdrawFromBalance(any()) } returns Err(DatabaseError)
        every { playerMock.uniqueId } returns UUID.randomUUID()

        // Act
        val actual = sut.withdrawPlayer(playerMock, -0.1)
        val expected = EconomyResponse(-0.1, 0.0, EconomyResponse.ResponseType.FAILURE, "withdraw amount must be greater than 0")

        // Assert
        assertEquals(expected.amount, actual.amount)
        assertEquals(expected.balance, actual.balance)
        assertEquals(expected.type, actual.type)
        assertEquals(expected.errorMessage, actual.errorMessage)
    }

    @Test
    fun depositPlayer_WithSuccessDepositing_ShouldReturnSuccessResponse() {
        // Arrange
        val mockBalance = Balance(UUID.randomUUID(), UUID.randomUUID(), 15.0)
        every { econMock.depositIntoBalance(any()) } returns Ok(mockBalance)
        every { playerMock.uniqueId } returns UUID.randomUUID()

        // Act
        val actual = sut.depositPlayer(playerMock, 5.0)
        val expected = EconomyResponse(5.0, 15.0, EconomyResponse.ResponseType.SUCCESS, "")

        // Assert
        assertEquals(expected.amount, actual.amount)
        assertEquals(expected.balance, actual.balance)
        assertEquals(expected.type, actual.type)
        assertEquals(expected.errorMessage, actual.errorMessage)
    }

    @Test
    fun depositPlayer_WithErrorDepositing_ShouldReturnFailureResponse() {
        // Arrange
        every { econMock.depositIntoBalance(any()) } returns Err(DatabaseError)
        every { playerMock.uniqueId } returns UUID.randomUUID()

        // Act
        val actual = sut.depositPlayer(playerMock, 5.0)
        val expected = EconomyResponse(5.0, 0.0, EconomyResponse.ResponseType.FAILURE, "unable to deposit into balance")

        // Assert
        assertEquals(expected.amount, actual.amount)
        assertEquals(expected.balance, actual.balance)
        assertEquals(expected.type, actual.type)
        assertEquals(expected.errorMessage, actual.errorMessage)
    }

    @Test
    fun depositPlayer_WithDepositAmountOfZero_ShouldReturnFailureResponse() {
        // Arrange
        every { econMock.depositIntoBalance(any()) } returns Err(DatabaseError)
        every { playerMock.uniqueId } returns UUID.randomUUID()

        // Act
        val actual = sut.depositPlayer(playerMock, 0.0)
        val expected = EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "deposit amount must be greater than 0")

        // Assert
        assertEquals(expected.amount, actual.amount)
        assertEquals(expected.balance, actual.balance)
        assertEquals(expected.type, actual.type)
        assertEquals(expected.errorMessage, actual.errorMessage)
    }

    @Test
    fun depositPlayer_WithDepositAmountLessThanZero_ShouldReturnFailureResponse() {
        // Arrange
        every { econMock.depositIntoBalance(any()) } returns Err(DatabaseError)
        every { playerMock.uniqueId } returns UUID.randomUUID()

        // Act
        val actual = sut.depositPlayer(playerMock, -0.1)
        val expected = EconomyResponse(-0.1, 0.0, EconomyResponse.ResponseType.FAILURE, "deposit amount must be greater than 0")

        // Assert
        assertEquals(expected.amount, actual.amount)
        assertEquals(expected.balance, actual.balance)
        assertEquals(expected.type, actual.type)
        assertEquals(expected.errorMessage, actual.errorMessage)
    }

    @Test
    fun createPlayerAccount_WithSuccess_ShouldReturnTrue() {
        // Arrange
        every { econMock.createAccount(any()) } returns Ok(1)
        every { playerMock.uniqueId } returns UUID.randomUUID()

        // Act
        val actual = sut.createPlayerAccount(playerMock)

        // Assert
        assertTrue(actual)
    }

    @Test
    fun createPlayerAccount_WithNoAccountCreated_ShouldReturnFalse() {
        // Arrange
        every { econMock.createAccount(any()) } returns Ok(0)
        every { playerMock.uniqueId } returns UUID.randomUUID()

        // Act
        val actual = sut.createPlayerAccount(playerMock)

        // Assert
        assertFalse(actual)
    }

    @Test
    fun createPlayerAccount_WithErrorCreatingAccount_ShouldReturnFalse() {
        // Arrange
        every { econMock.createAccount(any()) } returns Err(DatabaseError)
        every { playerMock.uniqueId } returns UUID.randomUUID()

        // Act
        val actual = sut.createPlayerAccount(playerMock)

        // Assert
        assertFalse(actual)
    }
}