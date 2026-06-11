package com.ericgrandt.totaleconomy.impl

import com.ericgrandt.totaleconomy.data.AccountData
import com.ericgrandt.totaleconomy.data.CurrencyData
import com.ericgrandt.totaleconomy.economy.Economy
import com.ericgrandt.totaleconomy.exception.AccountNotFoundException
import com.ericgrandt.totaleconomy.exception.CurrencyNotFoundException
import com.ericgrandt.totaleconomy.exception.DatabaseException
import com.ericgrandt.totaleconomy.exception.MissingDefaultCurrencyException
import com.ericgrandt.totaleconomy.model.TEAccount
import com.ericgrandt.totaleconomy.model.TECurrency
import com.ericgrandt.totaleconomy.testutils.mockTransaction
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import org.slf4j.Logger
import java.math.BigDecimal
import java.sql.SQLException
import java.util.UUID
import kotlin.test.BeforeTest
import kotlin.test.Test

class EconomyTest {
    @MockK
    lateinit var loggerMock: Logger

    @MockK
    lateinit var accountDataMock: AccountData

    @MockK
    lateinit var currencyDataMock: CurrencyData

    private lateinit var sut: Economy

    @BeforeTest
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        mockTransaction()
        sut = Economy(loggerMock, accountDataMock, currencyDataMock)
    }

    @Test
    fun getDefaultCurrency_WithSuccess_ShouldReturnDefaultCurrency() {
        // Arrange
        val currency = TECurrency("USD", "Dollar", "Dollars", "$", 2, true)
        every { currencyDataMock.getDefaultCurrency() } returns Ok(currency)

        // Act
        val actual = sut.getDefaultCurrency()

        // Assert
        assertEquals(currency, actual)
    }

    @Test
    fun getDefaultCurrency_WithNoSuchElementException_ShouldThrowMissingDefaultCurrencyException() {
        // Arrange
        every { currencyDataMock.getDefaultCurrency() } returns Err(NoSuchElementException())

        // Act
        assertThrows<MissingDefaultCurrencyException> {
            sut.getDefaultCurrency()
        }
        verify {
            loggerMock.error("default currency not found", any<NoSuchElementException>())
        }
    }

    @Test
    fun getDefaultCurrency_WithSQLException_ShouldLogAndThrowDatabaseException() {
        // Arrange
        every { currencyDataMock.getDefaultCurrency() } returns Err(SQLException())

        // Act
        assertThrows<DatabaseException> {
            sut.getDefaultCurrency()
        }
        verify {
            loggerMock.error("database exception when getting default currency", any<SQLException>())
        }
    }

    @Test
    fun getCurrency_WithSuccess_ShouldReturnCurrency() {
        // Arrange
        val currency = TECurrency("USD", "Dollar", "Dollars", "$", 2, true)
        every { currencyDataMock.getCurrency("USD") } returns Ok(currency)

        // Act
        val actual = sut.getCurrency("USD")

        // Assert
        assertEquals(currency, actual)
    }

    @Test
    fun getCurrency_WithNoSuchElementException_ShouldThrowCurrencyNotFoundException() {
        // Arrange
        every { currencyDataMock.getCurrency("USD") } returns Err(NoSuchElementException())

        // Act
        assertThrows<CurrencyNotFoundException> {
            sut.getCurrency("USD")
        }
        verify {
            loggerMock.error("currency not found", any<NoSuchElementException>())
        }
    }

    @Test
    fun getCurrency_WithSQLException_ShouldLogAndThrowDatabaseException() {
        // Arrange
        every { currencyDataMock.getCurrency("USD") } returns Err(SQLException())

        // Act
        assertThrows<DatabaseException> {
            sut.getCurrency("USD")
        }
        verify {
            loggerMock.error("database exception when getting currency", any<SQLException>())
        }
    }

    @Test
    fun createAccount_WithSuccess_ShouldReturnAccount() {
        // Arrange
        val account = TEAccount(UUID.randomUUID(), "", BigDecimal.TEN)
        every { accountDataMock.createAccount(any(), any(), any()) } returns Ok(account)

        // Act
        val actual = sut.createAccount(account.playerId, account.currencyCode)

        // Assert
        assertEquals(account, actual)
    }

    @Test
    fun createAccount_WithNoSuchElementException_ShouldLogAndThrowAccountNotFoundException() {
        // Arrange
        val account = TEAccount(UUID.randomUUID(), "", BigDecimal.TEN)
        every { accountDataMock.createAccount(any(), any(), any()) } returns Err(NoSuchElementException())

        // Act
        assertThrows<AccountNotFoundException> {
            sut.createAccount(account.playerId, account.currencyCode)
        }
        verify {
            loggerMock.error("account not found after creation", any<NoSuchElementException>())
        }
    }

    @Test
    fun createAccount_WithSQLException_ShouldLogAndThrowDatabaseException() {
        // Arrange
        val account = TEAccount(UUID.randomUUID(), "", BigDecimal.TEN)
        every { accountDataMock.createAccount(any(), any(), any()) } returns Err(SQLException())

        // Act/Assert
        assertThrows<DatabaseException> {
            sut.createAccount(account.playerId, account.currencyCode)
        }
        verify {
            loggerMock.error("database exception when creating account", any<SQLException>())
        }
    }

    @Test
    fun getAccount_WithSuccess_ShouldReturnAccount() {
        // Arrange
        val account = TEAccount(UUID.randomUUID(), "", BigDecimal.TEN)
        every { accountDataMock.getAccount(any(), any()) } returns Ok(account)

        // Act
        val actual = sut.getAccount(account.playerId, account.currencyCode)

        // Assert
        assertEquals(account, actual)
    }

    @Test
    fun getAccount_WithNoSuchElementException_ShouldLogAndThrowAccountNotFoundException() {
        // Arrange
        val account = TEAccount(UUID.randomUUID(), "", BigDecimal.TEN)
        every { accountDataMock.getAccount(any(), any()) } returns Err(NoSuchElementException())

        // Act/Assert
        assertThrows<AccountNotFoundException> {
            sut.getAccount(account.playerId, account.currencyCode)
        }
        verify {
            loggerMock.warn("account not found", any<NoSuchElementException>())
        }
    }

    @Test
    fun getAccount_WithSQLException_ShouldLogAndThrowDatabaseException() {
        // Arrange
        val account = TEAccount(UUID.randomUUID(), "", BigDecimal.TEN)
        every { accountDataMock.getAccount(any(), any()) } returns Err(SQLException())

        // Act/Assert
        assertThrows<DatabaseException> {
            sut.getAccount(account.playerId, account.currencyCode)
        }
        verify {
            loggerMock.error("database exception when getting account", any<SQLException>())
        }
    }
}
