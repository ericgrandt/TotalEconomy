package com.ericgrandt.totaleconomy.data

import com.ericgrandt.totaleconomy.testutils.TestUtils
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.getError
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.junit.jupiter.api.Tag
import java.sql.SQLException
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

class BankDataTest {
    @Test
    @Tag("Integration")
    fun createBank_WithSuccess_ShouldReturnCreatedRowCount() {
        // Arrange
        TestUtils.connectToTestDb()
        val account = TestUtils.seedAccount()

        val sut = BankData()

        // Act/Assert
        transaction {
            val actual = sut.createBank(account.id)
            val expected = Ok(1)

            assertEquals(expected, actual)
        }
    }

    @Test
    @Tag("Integration")
    fun createBank_WithSQLException_ShouldReturnErrorResult() {
        // Arrange
        TestUtils.connectToTestDb(false)

        val sut = BankData()

        // Act/Assert
        transaction {
            val actual = sut.createBank(UUID.randomUUID())

            // Assert
            assertThat(actual.getError())
                .isInstanceOf(SQLException::class.java)
        }
    }

    @Test
    @Tag("Integration")
    fun getBank_WithBank_ShouldReturnBank() {
        // Arrange
        TestUtils.connectToTestDb()
        val testAccount = TestUtils.seedAccount()
        val testBank = TestUtils.seedBank(testAccount.id, null)

        val sut = BankData()

        // Act/Assert
        transaction {
            val actual = sut.getBank(testAccount.id)
            val expected = Ok(testBank)

            assertEquals(expected, actual)
        }
    }

    @Test
    @Tag("Integration")
    fun getBank_WithNoBank_ShouldReturnNull() {
        // Arrange
        TestUtils.connectToTestDb()

        val sut = BankData()

        // Act/Assert
        transaction {
            val actual = sut.getBank(UUID.randomUUID())
            val expected = Ok(null)

            assertEquals(expected, actual)
        }
    }

    @Test
    @Tag("Integration")
    fun getBank_WithSqlException_ShouldReturnErrResult() {
        // Arrange
        TestUtils.connectToTestDb(false)

        val sut = BalanceData()

        // Act/Assert
        transaction {
            val actual = sut.getBalance(UUID.randomUUID())

            assertThat(actual.getError())
                .isInstanceOf(SQLException::class.java)
        }
    }
}
