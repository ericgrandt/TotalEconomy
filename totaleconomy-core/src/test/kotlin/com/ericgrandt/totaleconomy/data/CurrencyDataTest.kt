package com.ericgrandt.totaleconomy.data

import com.ericgrandt.totaleconomy.model.TECurrency
import com.ericgrandt.totaleconomy.testutils.TestUtils
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.getError
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import java.sql.SQLException
import kotlin.test.Test

class CurrencyDataTest {
    @BeforeEach
    fun setUp() {
    }

    @Test
    @Tag("Integration")
    fun getCurrencyList_WithSuccess_ShouldReturnCurrencyList() {
        // Arrange
        TestUtils.connectToTestDb()
        TestUtils.seedCurrency()

        val sut = CurrencyData()

        // Act/Assert
        transaction {
            val actual = sut.getCurrencyList()
            val expectedTECurrency =
                TECurrency(
                    "USD",
                    "Dollar",
                    "Dollars",
                    null,
                    2,
                    true,
                )
            val expected = Ok(listOf(expectedTECurrency))

            assertEquals(expected, actual)
        }
    }

    @Test
    @Tag("Integration")
    fun getCurrencyList_WithError_ShouldReturnErrorResult() {
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
