package com.ericgrandt.totaleconomy.data

import com.ericgrandt.totaleconomy.model.DepositIntoBalance
import com.ericgrandt.totaleconomy.model.SetBalance
import com.ericgrandt.totaleconomy.model.WithdrawFromBalance
import com.ericgrandt.totaleconomy.testutils.TestUtils
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.getError
import io.mockk.MockKAnnotations
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.junit.jupiter.api.Tag
import java.sql.SQLException
import java.util.UUID
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class BalanceDataTest {
    @BeforeTest
    fun setUp() = MockKAnnotations.init(this, relaxUnitFun = true)

    @Test
    @Tag("Integration")
    fun getBalance_WithBalance_ShouldReturnBalance() {
        // Arrange
        TestUtils.connectToTestDb()
        val testAccount = TestUtils.seedAccount()
        val testBalance = TestUtils.seedBalance(testAccount.id, null)

        val sut = BalanceData()

        // Act/Assert
        transaction {
            val actual = sut.getBalance(testAccount.id)
            val expected = Ok(testBalance)

            assertEquals(expected, actual)
        }
    }

    @Test
    @Tag("Integration")
    fun getBalance_WithNoBalance_ShouldReturnNull() {
        // Arrange
        TestUtils.connectToTestDb()
        val testAccount = TestUtils.seedAccount()

        val sut = BalanceData()

        // Act/Assert
        transaction {
            val actual = sut.getBalance(testAccount.id)
            val expected = Ok(null)

            assertEquals(expected, actual)
        }
    }

    @Test
    @Tag("Integration")
    fun getBalance_WithSqlException_ShouldReturnErrResult() {
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

    @Test
    @Tag("Integration")
    fun setBalance_WithUpdatedRow_ShouldReturnOne() {
        // Arrange
        TestUtils.connectToTestDb()
        val testAccount = TestUtils.seedAccount()
        TestUtils.seedBalance(testAccount.id, null)

        val input = SetBalance(testAccount.id, 10.0)

        val sut = BalanceData()

        // Act/Assert
        transaction {
            val actual = sut.setBalance(input)
            val expected = Ok(1)

            assertEquals(expected, actual)
        }
    }

    @Test
    @Tag("Integration")
    fun setBalance_WithNoUpdatedRow_ShouldReturnZero() {
        // Arrange
        TestUtils.connectToTestDb()
        val testAccount = TestUtils.seedAccount()

        val input = SetBalance(testAccount.id, 10.0)

        val sut = BalanceData()

        // Act/Assert
        transaction {
            val actual = sut.setBalance(input)
            val expected = Ok(0)

            assertEquals(expected, actual)
        }
    }

    @Test
    @Tag("Integration")
    fun setBalance_WithSqlException_ShouldReturnErrResult() {
        // Arrange
        TestUtils.connectToTestDb(false)

        val input = SetBalance(UUID.randomUUID(), 10.0)

        val sut = BalanceData()

        // Act/Assert
        transaction {
            val actual = sut.setBalance(input)

            assertThat(actual.getError())
                .isInstanceOf(SQLException::class.java)
        }
    }

    @Test
    @Tag("Integration")
    fun withdrawFromBalance_WithUpdatedRow_ShouldReturnOne() {
        // Arrange
        TestUtils.connectToTestDb()
        val testAccount = TestUtils.seedAccount()
        TestUtils.seedBalance(testAccount.id, null)

        val input = WithdrawFromBalance(testAccount.id, 10.0)

        val sut = BalanceData()

        // Act/Assert
        transaction {
            val actual = sut.withdrawFromBalance(input)
            val expected = Ok(1)

            assertEquals(expected, actual)
        }
    }

    @Test
    @Tag("Integration")
    fun withdrawFromBalance_WithNoUpdatedRow_ShouldReturnZero() {
        // Arrange
        TestUtils.connectToTestDb()
        val testAccount = TestUtils.seedAccount()

        val input = WithdrawFromBalance(testAccount.id, 10.0)

        val sut = BalanceData()

        // Act/Assert
        transaction {
            val actual = sut.withdrawFromBalance(input)
            val expected = Ok(0)

            assertEquals(expected, actual)
        }
    }

    @Test
    @Tag("Integration")
    fun withdrawFromBalance_WithSqlException_ShouldReturnErrResult() {
        // Arrange
        TestUtils.connectToTestDb(false)

        val input = WithdrawFromBalance(UUID.randomUUID(), 10.0)

        val sut = BalanceData()

        // Act/Assert
        transaction {
            val actual = sut.withdrawFromBalance(input)

            assertThat(actual.getError())
                .isInstanceOf(SQLException::class.java)
        }
    }

    @Test
    @Tag("Integration")
    fun depositIntoBalance_WithUpdatedRow_ShouldReturnOne() {
        // Arrange
        TestUtils.connectToTestDb()
        val testAccount = TestUtils.seedAccount()
        TestUtils.seedBalance(testAccount.id, null)

        val input = DepositIntoBalance(testAccount.id, 10.0)

        val sut = BalanceData()

        // Act/Assert
        transaction {
            val actual = sut.depositIntoBalance(input)
            val expected = Ok(1)

            assertEquals(expected, actual)
        }
    }

    @Test
    @Tag("Integration")
    fun depositIntoBalance_WithNoUpdatedRow_ShouldReturnZero() {
        // Arrange
        TestUtils.connectToTestDb()
        val testAccount = TestUtils.seedAccount()

        val input = DepositIntoBalance(testAccount.id, 10.0)

        val sut = BalanceData()

        // Act/Assert
        transaction {
            val actual = sut.depositIntoBalance(input)
            val expected = Ok(0)

            assertEquals(expected, actual)
        }
    }

    @Test
    @Tag("Integration")
    fun depositIntoBalance_WithSqlException_ShouldReturnErrResult() {
        // Arrange
        TestUtils.connectToTestDb(false)

        val input = DepositIntoBalance(UUID.randomUUID(), 10.0)

        val sut = BalanceData()

        // Act/Assert
        transaction {
            val actual = sut.depositIntoBalance(input)

            assertThat(actual.getError())
                .isInstanceOf(SQLException::class.java)
        }
    }

    // @Test
    // @Tag("Integration")
    // fun transferBalance_WithSuccess_ShouldUpdateBalance() {
    //    // Arrange
    //    TestUtils.resetDb()
    //    val testFromAccount = TestUtils.seedAccount()
    //    TestUtils.seedBalance(testFromAccount.id, null)
    //    val testToAccount = TestUtils.seedAccount()
    //    TestUtils.seedBalance(testToAccount.id, null)

    //    every { databaseMock.dataSource } returns mockk<HikariDataSource>()
    //    every { databaseMock.dataSource.connection } answers { TestUtils.getConnection() }

    //    val input = TransferBalance(testFromAccount.id, testToAccount.id, 1.0)

    //    val sut = BalanceData(databaseMock)

    //    // Act
    //    val actual = sut.transferBalance(input)
    //    val expectedFromBalance = 0.23
    //    val expectedToBalance = 2.23

    //    // Assert
    //    val updatedFromBalance = sut.getBalance(testFromAccount.id).get()
    //    val updatedToBalance = sut.getBalance(testToAccount.id).get()

    //    assertEquals(Ok(true), actual)
    //    assertEquals(expectedFromBalance, updatedFromBalance?.balance)
    //    assertEquals(expectedToBalance, updatedToBalance?.balance)
    // }

    // @Test
    // @Tag("Integration")
    // fun transferBalance_WithSqlException_ShouldReturnErrResult() {
    //    // Arrange
    //    TestUtils.resetDb()
    //    val testFromAccount = TestUtils.seedAccount()
    //    val testToAccount = TestUtils.seedAccount()

    //    every { databaseMock.dataSource } returns mockk<HikariDataSource>()
    //    every { databaseMock.dataSource.connection } returns TestUtils.getConnection()
    //    every { databaseMock.dataSource.connection.prepareStatement(any()) } throws SQLException()

    //    val input = TransferBalance(testFromAccount.id, testToAccount.id, 10.0)

    //    val sut = BalanceData(databaseMock)

    //    // Act
    //    val actual = sut.transferBalance(input)

    //    // Assert
    //    assertThat(actual.getError())
    //        .isInstanceOf(SQLException::class.java)
    // }
}
