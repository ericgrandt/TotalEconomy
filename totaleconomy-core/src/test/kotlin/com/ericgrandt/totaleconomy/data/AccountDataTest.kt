package com.ericgrandt.totaleconomy.data

import com.ericgrandt.totaleconomy.model.TEAccount
import com.ericgrandt.totaleconomy.testutils.TestUtils
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.getError
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Tag
import java.math.BigDecimal
import java.sql.SQLException
import java.util.UUID
import kotlin.test.Test

class AccountDataTest {
    @Test
    @Tag("Integration")
    fun createAccount_WithSuccess_ShouldReturnAccount() {
        // Arrange
        TestUtils.connectToTestDb()
        val currency = TestUtils.seedCurrency()
        val playerId = UUID.randomUUID()

        val sut = AccountData()

        // Act/Assert
        transaction {
            val actual = sut.createAccount(playerId, currency.code, BigDecimal.TEN)
            val expected = Ok(TEAccount(playerId, currency.code, BigDecimal.valueOf(10.0)))

            assertEquals(expected, actual)
        }
    }

    @Test
    @Tag("Integration")
    fun createAccount_WithError_ShouldReturnErrorResult() {
        // Arrange
        TestUtils.connectToTestDb(false)

        val sut = AccountData()

        // Act/Assert
        transaction {
            val actual = sut.createAccount(UUID.randomUUID(), "", BigDecimal.TEN)

            assertThat(actual.getError())
                .isInstanceOf(SQLException::class.java)
        }
    }

    @Test
    @Tag("Integration")
    fun getAccount_WithSuccess_ShouldReturnAccount() {
        // Arrange
        TestUtils.connectToTestDb()
        val currency = TestUtils.seedCurrency()
        val account = TestUtils.seedAccount(currency.code)

        val sut = AccountData()

        // Act/Assert
        transaction {
            val actual = sut.getAccount(account.playerId, currency.code)
            val expected = Ok(TEAccount(account.playerId, currency.code, BigDecimal.valueOf(10.0)))

            assertEquals(expected, actual)
        }
    }

    @Test
    @Tag("Integration")
    fun getAccount_WithError_ShouldReturnErrorResult() {
        // Arrange
        TestUtils.connectToTestDb(false)

        val sut = AccountData()

        // Act/Assert
        transaction {
            val actual = sut.getAccount(UUID.randomUUID(), "")

            assertThat(actual.getError())
                .isInstanceOf(SQLException::class.java)
        }
    }
}
