package com.ericgrandt.totaleconomy.econ

import com.ericgrandt.totaleconomy.data.AccountData
import com.ericgrandt.totaleconomy.data.BalanceData
import com.ericgrandt.totaleconomy.data.entity.Account
import com.ericgrandt.totaleconomy.data.entity.Balance
import com.ericgrandt.totaleconomy.model.DatabaseError
import com.ericgrandt.totaleconomy.model.DatabaseErrorN
import com.ericgrandt.totaleconomy.model.DepositIntoBalance
import com.ericgrandt.totaleconomy.model.SetBalance
import com.ericgrandt.totaleconomy.model.WithdrawFromBalance
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.ericgrandt.totaleconomy.result.Err as ErrOld
import com.ericgrandt.totaleconomy.result.Ok as OkOld
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import net.kyori.adventure.text.Component
import java.sql.SQLException
import java.util.UUID
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CommonEconomyTest {
    @MockK
    lateinit var accountDataMock: AccountData

    @MockK
    lateinit var balanceDataMock: BalanceData

    @BeforeTest
    fun setUp() = MockKAnnotations.init(this, relaxUnitFun = true)

    @Test
    fun currencyNamePlural_ShouldReturnCorrectString() {
        // Arrange
        val sut = CommonEconomy(accountDataMock, balanceDataMock)

        // Act
        val actual = sut.currencyNamePlural()
        val expected = "Diamonds"

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun currencyNameSingular_ShouldReturnCorrectString() {
        // Arrange
        val sut = CommonEconomy(accountDataMock, balanceDataMock)

        // Act
        val actual = sut.currencyNameSingular()
        val expected = "Diamond"

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun createAccount_WithSuccessResultFromAccountData_ShouldReturnTrue() {
        // Arrange
        every { accountDataMock.createAccount(any()) } returns Ok(1)

        val sut = CommonEconomy(accountDataMock, balanceDataMock)

        // Act
        val actual = sut.createAccount(UUID.randomUUID())
        val expected = Ok(1)

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun createAccount_WithErrorFromAccountData_ShouldReturnDatabaseError() {
        // Arrange
        every { accountDataMock.createAccount(any()) } returns Err(SQLException())

        val sut = CommonEconomy(accountDataMock, balanceDataMock)

        // Act
        val actual = sut.createAccount(UUID.randomUUID())
        val expected = Err(DatabaseError)

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun hasAccount_WithAnAccount_ShouldReturnTrue() {
        // Arrange
        val accountMock = mockk<Account>()
        every { accountDataMock.getAccount(any()) } returns Ok(accountMock)

        val sut = CommonEconomy(accountDataMock, balanceDataMock)

        // Act
        val actual = sut.hasAccount(UUID.randomUUID())
        val expected = Ok(true)

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun hasAccount_WithNoAccount_ShouldReturnFalse() {
        // Arrange
        every { accountDataMock.getAccount(any()) } returns Ok(null)

        val sut = CommonEconomy(accountDataMock, balanceDataMock)

        // Act
        val actual = sut.hasAccount(UUID.randomUUID())
        val expected = Ok(false)

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun hasAccount_WithErrorGettingAccount_ShouldReturnDatabaseError() {
        // Arrange
        every { accountDataMock.getAccount(any()) } returns Err(SQLException())

        val sut = CommonEconomy(accountDataMock, balanceDataMock)

        // Act
        val actual = sut.hasAccount(UUID.randomUUID())
        val expected = Err(DatabaseError)

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun getBalance_WithBalance_ShouldReturnBalance() {
        // Arrange
        val balanceMock = mockk<Balance>()
        every { balanceMock.balance } returns 1.23
        every { balanceDataMock.getBalance(any()) } returns OkOld(balanceMock)

        val sut = CommonEconomy(accountDataMock, balanceDataMock)

        // Act
        val actual = sut.getBalance(UUID.randomUUID())
        val expected = OkOld(1.23)

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun getBalance_WithNullBalance_ShouldReturnZeroBalance() {
        // Arrange
        every { balanceDataMock.getBalance(any()) } returns OkOld(null)

        val sut = CommonEconomy(accountDataMock, balanceDataMock)

        // Act
        val actual = sut.getBalance(UUID.randomUUID())
        val expected = OkOld(0.00)

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun getBalance_WithErrorGettingBalance_ShouldReturnDatabaseError() {
        // Arrange
        every { balanceDataMock.getBalance(any()) } returns ErrOld(SQLException())

        val sut = CommonEconomy(accountDataMock, balanceDataMock)

        // Act
        val actual = sut.getBalance(UUID.randomUUID())
        val expected = ErrOld(DatabaseErrorN)

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun setBalance_WithSuccess_ShouldReturnNumberOfUpdatedRows() {
        // Arrange
        every { balanceDataMock.setBalance(any()) } returns OkOld(1)

        val input = SetBalance(UUID.randomUUID(), 10.0)

        val sut = CommonEconomy(accountDataMock, balanceDataMock)

        // Act
        val actual = sut.setBalance(input)
        val expected = OkOld(1)

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun setBalance_WithErrorGettingBalance_ShouldReturnDatabaseError() {
        // Arrange
        every { balanceDataMock.setBalance(any()) } returns ErrOld(SQLException())

        val input = SetBalance(UUID.randomUUID(), 10.0)

        val sut = CommonEconomy(accountDataMock, balanceDataMock)

        // Act
        val actual = sut.setBalance(input)
        val expected = ErrOld(DatabaseErrorN)

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun withdrawFromBalance_WithSuccess_ShouldReturnUpdatedBalance() {
        // Arrange
        val mockBalance = Balance(UUID.randomUUID(), UUID.randomUUID(), 1.0);

        every { balanceDataMock.withdrawFromBalance(any()) } returns OkOld(1)
        every { balanceDataMock.getBalance(any()) } returns OkOld(mockBalance)

        val input = WithdrawFromBalance(UUID.randomUUID(), 10.0)

        val sut = CommonEconomy(accountDataMock, balanceDataMock)

        // Act
        val actual = sut.withdrawFromBalance(input)
        val expected = OkOld(mockBalance)

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun withdrawFromBalance_WithErrorWithdrawing_ShouldReturnDatabaseError() {
        // Arrange
        val mockBalance = Balance(UUID.randomUUID(), UUID.randomUUID(), 1.0);

        every { balanceDataMock.withdrawFromBalance(any()) } returns ErrOld(SQLException())
        every { balanceDataMock.getBalance(any()) } returns OkOld(mockBalance)

        val input = WithdrawFromBalance(UUID.randomUUID(), 10.0)

        val sut = CommonEconomy(accountDataMock, balanceDataMock)

        // Act
        val actual = sut.withdrawFromBalance(input)
        val expected = ErrOld(DatabaseErrorN)

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun withdrawFromBalance_WithErrorGettingBalanceAfterWithdraw_ShouldReturnDatabaseError() {
        // Arrange
        every { balanceDataMock.withdrawFromBalance(any()) } returns OkOld(1)
        every { balanceDataMock.getBalance(any()) } returns ErrOld(SQLException())

        val input = WithdrawFromBalance(UUID.randomUUID(), 10.0)

        val sut = CommonEconomy(accountDataMock, balanceDataMock)

        // Act
        val actual = sut.withdrawFromBalance(input)
        val expected = ErrOld(DatabaseErrorN)

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun depositIntoBalance_WithSuccess_ShouldReturnUpdatedBalance() {
        // Arrange
        val mockBalance = Balance(UUID.randomUUID(), UUID.randomUUID(), 1.0);

        every { balanceDataMock.depositIntoBalance(any()) } returns OkOld(1)
        every { balanceDataMock.getBalance(any()) } returns OkOld(mockBalance)

        val input = DepositIntoBalance(UUID.randomUUID(), 10.0)

        val sut = CommonEconomy(accountDataMock, balanceDataMock)

        // Act
        val actual = sut.depositIntoBalance(input)
        val expected = OkOld(mockBalance)

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun depositIntoBalance_WithErrorDepositing_ShouldReturnDatabaseError() {
        // Arrange
        val mockBalance = Balance(UUID.randomUUID(), UUID.randomUUID(), 1.0);

        every { balanceDataMock.depositIntoBalance(any()) } returns ErrOld(SQLException())
        every { balanceDataMock.getBalance(any()) } returns OkOld(mockBalance)

        val input = DepositIntoBalance(UUID.randomUUID(), 10.0)

        val sut = CommonEconomy(accountDataMock, balanceDataMock)

        // Act
        val actual = sut.depositIntoBalance(input)
        val expected = ErrOld(DatabaseErrorN)

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun depositIntoBalance_WithErrorGettingBalanceAfterDeposit_ShouldReturnDatabaseError() {
        // Arrange
        every { balanceDataMock.depositIntoBalance(any()) } returns OkOld(1)
        every { balanceDataMock.getBalance(any()) } returns ErrOld(SQLException())

        val input = DepositIntoBalance(UUID.randomUUID(), 10.0)

        val sut = CommonEconomy(accountDataMock, balanceDataMock)

        // Act
        val actual = sut.depositIntoBalance(input)
        val expected = ErrOld(DatabaseErrorN)

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun format_WithAmountEqualToOne_ShouldReturnFormattedAmountWithSingularCurrencyName() {
        // Arrange
        val sut = CommonEconomy(accountDataMock, balanceDataMock)

        // Act
        val actual = sut.format(1.0)
        val expected = Component.text("1.00 Diamond")

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun format_WithAmountGreaterThanOne_ShouldReturnFormattedAmountWithPluralCurrencyName() {
        // Arrange
        val sut = CommonEconomy(accountDataMock, balanceDataMock)

        // Act
        val actual = sut.format(10.52)
        val expected = Component.text("10.52 Diamonds")

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun format_WithMoreThanTwoFractionalDigitsAndThirdNumberBeingUnderFive_ShouldRoundDown() {
        // Arrange
        val sut = CommonEconomy(accountDataMock, balanceDataMock)

        // Act
        val actual = sut.format(10.521651234)
        val expected = Component.text("10.52 Diamonds")

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun format_WithMoreThanTwoFractionalDigitsAndThirdNumberBeingFive_ShouldRoundUp() {
        // Arrange
        val sut = CommonEconomy(accountDataMock, balanceDataMock)

        // Act
        val actual = sut.format(10.5252)
        val expected = Component.text("10.53 Diamonds")

        // Assert
        assertEquals(expected, actual)
    }
}