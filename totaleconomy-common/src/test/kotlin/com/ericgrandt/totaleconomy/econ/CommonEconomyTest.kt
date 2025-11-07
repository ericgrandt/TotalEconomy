package com.ericgrandt.totaleconomy.econ

import com.ericgrandt.totaleconomy.data.AccountData
import com.ericgrandt.totaleconomy.data.BalanceData
import com.ericgrandt.totaleconomy.data.entity.Account
import com.ericgrandt.totaleconomy.data.entity.Balance
import com.ericgrandt.totaleconomy.model.DatabaseError
import com.ericgrandt.totaleconomy.model.SetBalance
import com.ericgrandt.totaleconomy.model.WithdrawFromBalance
import com.ericgrandt.totaleconomy.result.Err
import com.ericgrandt.totaleconomy.result.Ok
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
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
    fun createAccount_WithSuccessResultFromAccountData_ShouldReturnTrue() {
        // Arrange
        every { accountDataMock.createAccount(any()) } returns Ok(true)

        val sut = CommonEconomy(accountDataMock, balanceDataMock)

        // Act
        val actual = sut.createAccount(UUID.randomUUID())
        val expected = Ok(true)

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
        every { balanceDataMock.getBalance(any()) } returns Ok(balanceMock)

        val sut = CommonEconomy(accountDataMock, balanceDataMock)

        // Act
        val actual = sut.getBalance(UUID.randomUUID())
        val expected = Ok(1.23)

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun getBalance_WithNullBalance_ShouldReturnZeroBalance() {
        // Arrange
        every { balanceDataMock.getBalance(any()) } returns Ok(null)

        val sut = CommonEconomy(accountDataMock, balanceDataMock)

        // Act
        val actual = sut.getBalance(UUID.randomUUID())
        val expected = Ok(0.00)

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun getBalance_WithErrorGettingBalance_ShouldReturnDatabaseError() {
        // Arrange
        every { balanceDataMock.getBalance(any()) } returns Err(SQLException())

        val sut = CommonEconomy(accountDataMock, balanceDataMock)

        // Act
        val actual = sut.getBalance(UUID.randomUUID())
        val expected = Err(DatabaseError)

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun setBalance_WithSuccess_ShouldReturnNumberOfUpdatedRows() {
        // Arrange
        every { balanceDataMock.setBalance(any()) } returns Ok(1)

        val input = SetBalance(UUID.randomUUID(), 10.0)

        val sut = CommonEconomy(accountDataMock, balanceDataMock)

        // Act
        val actual = sut.setBalance(input)
        val expected = Ok(1)

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun setBalance_WithErrorGettingBalance_ShouldReturnDatabaseError() {
        // Arrange
        every { balanceDataMock.setBalance(any()) } returns Err(SQLException())

        val input = SetBalance(UUID.randomUUID(), 10.0)

        val sut = CommonEconomy(accountDataMock, balanceDataMock)

        // Act
        val actual = sut.setBalance(input)
        val expected = Err(DatabaseError)

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun withdrawFromBalance_WithSuccess_ShouldReturnNumberOfUpdatedRows() {
        // Arrange
        every { balanceDataMock.withdrawFromBalance(any()) } returns Ok(1)

        val input = WithdrawFromBalance(UUID.randomUUID(), 10.0)

        val sut = CommonEconomy(accountDataMock, balanceDataMock)

        // Act
        val actual = sut.withdrawFromBalance(input)
        val expected = Ok(1)

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun withdrawFromBalance_WithErrorGettingBalance_ShouldReturnDatabaseError() {
        // Arrange
        every { balanceDataMock.withdrawFromBalance(any()) } returns Err(SQLException())

        val input = WithdrawFromBalance(UUID.randomUUID(), 10.0)

        val sut = CommonEconomy(accountDataMock, balanceDataMock)

        // Act
        val actual = sut.withdrawFromBalance(input)
        val expected = Err(DatabaseError)

        // Assert
        assertEquals(expected, actual)
    }
}