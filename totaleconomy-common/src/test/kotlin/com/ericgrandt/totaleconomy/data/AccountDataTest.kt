package com.ericgrandt.totaleconomy.data

import com.ericgrandt.totaleconomy.TestUtils
import com.ericgrandt.totaleconomy.model.Result
import com.zaxxer.hikari.HikariDataSource
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import java.sql.SQLException
import java.util.UUID
import kotlin.test.BeforeTest

class AccountDataTest {
    @MockK
    lateinit var databaseMock: Database

    @BeforeTest
    fun setUp() = MockKAnnotations.init(this, relaxUnitFun = true)

    @Test
    @Tag("Integration")
    fun createAccount_WithSuccess() {
        // Arrange
        TestUtils.resetDb()

        every { databaseMock.dataSource } returns mockk<HikariDataSource>()
        every { databaseMock.dataSource.connection } returns TestUtils.getConnection()

        val uuid = UUID.randomUUID()

        val sut = AccountData(databaseMock);

        // Act
        val actual = sut.createAccount(uuid)

        // Assert
        assertThat(actual)
            .isInstanceOf(Result.Success::class.java)
            .extracting { (it as Result.Success).data }
            .isEqualTo(true)
    }

    @Test
    @Tag("Integration")
    fun getAccount_WithSuccess_ShouldReturnAccount() {
        // Arrange
        TestUtils.resetDb()
        val testAccount = TestUtils.seedAccount(null)

        every { databaseMock.dataSource } returns mockk<HikariDataSource>()
        every { databaseMock.dataSource.connection } returns TestUtils.getConnection()

        val sut = AccountData(databaseMock);

        // Act
        val actual = sut.getAccount(testAccount.id)

        // Assert
        assertThat(actual)
            .isInstanceOf(Result.Success::class.java)
            .extracting { (it as Result.Success).data }
            .isEqualTo(testAccount)
    }

    @Test
    @Tag("Integration")
    fun getAccount_WithNoAccountFound_ShouldReturnAnInfoResult() {
        // Arrange
        TestUtils.resetDb()

        every { databaseMock.dataSource } returns mockk<HikariDataSource>()
        every { databaseMock.dataSource.connection } returns TestUtils.getConnection()

        val sut = AccountData(databaseMock);

        // Act
        val actual = sut.getAccount(UUID.randomUUID())

        // Assert
        assertThat(actual)
            .isInstanceOf(Result.Info::class.java)
            .extracting { (it as Result.Info).message }
            .isEqualTo("account not found")
    }

    @Test
    @Tag("Integration")
    fun getAccount_WithSQLException_ShouldReturnError() {
        // Arrange
        TestUtils.resetDb()
        val testAccount = TestUtils.seedAccount(null)

        every { databaseMock.dataSource } returns mockk<HikariDataSource>()
        every { databaseMock.dataSource.connection } returns TestUtils.getConnection()
        every { databaseMock.dataSource.connection.prepareStatement(any()) } throws SQLException("")

        val sut = AccountData(databaseMock);

        // Act
        val actual = sut.getAccount(testAccount.id)

        // Assert
        assertThat(actual)
            .isInstanceOf(Result.Error::class.java)
            .extracting { (it as Result.Error).cause }
            .isInstanceOf(SQLException::class.java)
    }
}