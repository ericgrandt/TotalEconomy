package com.ericgrandt.totaleconomy.data

import com.ericgrandt.totaleconomy.model.TECurrency
import com.ericgrandt.totaleconomy.testutils.TestUtils
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.getError
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Tag
import java.sql.SQLException
import kotlin.test.Test

class CurrencyDataTest {
    @Test
    @Tag("Integration")
    fun `getDefaultCurrency with success should return default currency`() {
        // Arrange
        TestUtils.connectToTestDb()
        TestUtils.seedDefaultCurrency()

        val sut = CurrencyData()

        // Act/Assert
        transaction {
            val actual = sut.getDefaultCurrency()
            val expectedTECurrency =
                TECurrency(
                    "USD",
                    "Dollar",
                    "Dollars",
                    "$",
                    2,
                    true,
                )
            val expected = Ok(expectedTECurrency)

            assertEquals(expected, actual)
        }
    }

    @Test
    @Tag("Integration")
    fun `getDefaultCurrency with error should return error result`() {
        // Arrange
        TestUtils.connectToTestDb(false)

        val sut = CurrencyData()

        // Act/Assert
        transaction {
            val actual = sut.getDefaultCurrency()

            assertThat(actual.getError())
                .isInstanceOf(SQLException::class.java)
        }
    }

    @Test
    @Tag("Integration")
    fun `getCurrency with success should return currency`() {
        // Arrange
        TestUtils.connectToTestDb()
        TestUtils.seedDefaultCurrency()

        val sut = CurrencyData()

        // Act/Assert
        transaction {
            val actual = sut.getCurrency("USD")
            val expectedTECurrency =
                TECurrency(
                    "USD",
                    "Dollar",
                    "Dollars",
                    "$",
                    2,
                    true,
                )
            val expected = Ok(expectedTECurrency)

            assertEquals(expected, actual)
        }
    }

    @Test
    @Tag("Integration")
    fun `getCurrency with error should return error result`() {
        // Arrange
        TestUtils.connectToTestDb(false)

        val sut = CurrencyData()

        // Act/Assert
        transaction {
            val actual = sut.getCurrency("")

            assertThat(actual.getError())
                .isInstanceOf(SQLException::class.java)
        }
    }

    @Test
    @Tag("Integration")
    fun `getCurrencyList with success should return currency list`() {
        // Arrange
        TestUtils.connectToTestDb()
        TestUtils.seedDefaultCurrency()

        val sut = CurrencyData()

        // Act/Assert
        transaction {
            val actual = sut.getCurrencyList()
            val expectedTECurrency =
                TECurrency(
                    "USD",
                    "Dollar",
                    "Dollars",
                    "$",
                    2,
                    true,
                )
            val expected = Ok(listOf(expectedTECurrency))

            assertEquals(expected, actual)
        }
    }

    @Test
    @Tag("Integration")
    fun `getCurrencyList with error should return error result`() {
        // Arrange
        TestUtils.connectToTestDb(false)

        val sut = CurrencyData()

        // Act/Assert
        transaction {
            val actual = sut.getCurrencyList()

            assertThat(actual.getError())
                .isInstanceOf(SQLException::class.java)
        }
    }
}
