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

class AccountDataTest {
    @Test
    @Tag("Integration")
    fun createAccount_WithSuccess_ShouldReturnCreatedRowCount() {
        // Arrange
        TestUtils.connectToTestDb()

        val sut = AccountData()

        // Act/Assert
        transaction {
            val actual = sut.createAccount(UUID.randomUUID())
            val expected = Ok(1)

            assertEquals(expected, actual)
        }
    }

    @Test
    @Tag("Integration")
    fun createAccount_WithSQLException_ShouldReturnErrorResult() {
        // Arrange
        TestUtils.connectToTestDb(false)

        val uuid = UUID.randomUUID()

        val sut = AccountData()

        // Act/Assert
        transaction {
            val actual = sut.createAccount(uuid)

            // Assert
            assertThat(actual.getError())
                .isInstanceOf(SQLException::class.java)
        }
    }

    @Test
    @Tag("Integration")
    fun getAccount_WithSuccess_ShouldReturnAccount() {
        // Arrange
        TestUtils.connectToTestDb()
        val testAccount = TestUtils.seedAccount()

        val sut = AccountData()

        // Act/Assert
        transaction {
            val actual = sut.getAccount(testAccount.id)
            val expected = Ok(testAccount)

            assertEquals(expected, actual)
        }
    }

    @Test
    @Tag("Integration")
    fun getAccount_WithNoAccountFound_ShouldReturnAnInfoResult() {
        // Arrange
        TestUtils.connectToTestDb()

        val sut = AccountData()

        // Act/Assert
        transaction {
            val actual = sut.getAccount(UUID.randomUUID())
            val expected = Ok(null)

            assertEquals(expected, actual)
        }
    }

    @Test
    @Tag("Integration")
    fun getAccount_WithSQLException_ShouldReturnError() {
        // Arrange
        TestUtils.connectToTestDb(false)

        val sut = AccountData()

        // Act/Assert
        transaction {
            val actual = sut.getAccount(UUID.randomUUID())

            assertThat(actual.getError())
                .isInstanceOf(SQLException::class.java)
        }
    }
}
