package com.ericgrandt.totaleconomy.econ

import com.ericgrandt.totaleconomy.data.AccountData
import com.ericgrandt.totaleconomy.data.entity.Account
import com.ericgrandt.totaleconomy.model.DatabaseError
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

    @BeforeTest
    fun setUp() = MockKAnnotations.init(this, relaxUnitFun = true)

    @Test
    fun createAccount_WithSuccessResultFromAccountData_ShouldReturnTrue() {
        // Arrange
        every { accountDataMock.createAccount(any()) } returns Ok(true)

        val sut = CommonEconomy(accountDataMock)

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

        val sut = CommonEconomy(accountDataMock)

        // Act
        val actual = sut.createAccount(UUID.randomUUID())
        val expected = Err(DatabaseError)

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun hasAccount_WithAnAccount_ShouldReturnTrue() {
        // Arrange
        val mockAccount = mockk<Account>()
        every { accountDataMock.getAccount(any()) } returns Ok(mockAccount)

        val sut = CommonEconomy(accountDataMock)

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

        val sut = CommonEconomy(accountDataMock)

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

        val sut = CommonEconomy(accountDataMock)

        // Act
        val actual = sut.hasAccount(UUID.randomUUID())
        val expected = Err(DatabaseError)

        // Assert
        assertEquals(expected, actual)
    }
}