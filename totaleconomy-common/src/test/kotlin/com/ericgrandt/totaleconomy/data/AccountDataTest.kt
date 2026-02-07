package com.ericgrandt.totaleconomy.data

import com.ericgrandt.totaleconomy.TestUtils
import com.ericgrandt.totaleconomy.result.Ok
import com.ericgrandt.totaleconomy.result.Err
import com.zaxxer.hikari.HikariDataSource
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Tag
import java.sql.SQLException
import java.util.UUID
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AccountDataTest {
    @MockK
    lateinit var databaseMock: Database

    @BeforeTest
    fun setUp() = MockKAnnotations.init(this, relaxUnitFun = true)

    @Test
    @Tag("Integration")
    fun createAccount_WithSuccess_ShouldReturnBoolean() {
        // Arrange
        TestUtils.resetDb()

        every { databaseMock.dataSource } returns mockk<HikariDataSource>()
        every { databaseMock.dataSource.connection } returns TestUtils.getConnection()

        val uuid = UUID.randomUUID()

        val sut = AccountData(databaseMock);

        // Act
        val actual = sut.createAccountOld(uuid)
        val expected = Ok(1)

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    @Tag("Integration")
    fun createAccount_WithSQLException_ShouldReturnErrorResult() {
        // Arrange
        TestUtils.resetDb()

        every { databaseMock.dataSource } returns mockk<HikariDataSource>()
        every { databaseMock.dataSource.connection } returns TestUtils.getConnection()
        every { databaseMock.dataSource.connection.prepareStatement(any()) } throws SQLException()

        val uuid = UUID.randomUUID()

        val sut = AccountData(databaseMock);

        // Act
        val actual = sut.createAccountOld(uuid)

        // Assert
        assertThat(actual)
            .isInstanceOf(Err::class.java)
            .extracting { (it as Err).error }
            .isInstanceOf(SQLException::class.java)
    }

    @Test
    @Tag("Integration")
    fun getAccount_WithSuccess_ShouldReturnAccount() {
        // Arrange
        TestUtils.resetDb()
        val testAccount = TestUtils.seedAccount()

        every { databaseMock.dataSource } returns mockk<HikariDataSource>()
        every { databaseMock.dataSource.connection } returns TestUtils.getConnection()

        val sut = AccountData(databaseMock);

        // Act
        val actual = sut.getAccount(testAccount.id)
        val expected = Ok(testAccount)

        // Assert
        assertEquals(expected, actual)
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
        val expected = Ok(null)

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    @Tag("Integration")
    fun getAccount_WithSQLException_ShouldReturnError() {
        // Arrange
        TestUtils.resetDb()
        val testAccount = TestUtils.seedAccount()

        every { databaseMock.dataSource } returns mockk<HikariDataSource>()
        every { databaseMock.dataSource.connection } returns TestUtils.getConnection()
        every { databaseMock.dataSource.connection.prepareStatement(any()) } throws SQLException("")

        val sut = AccountData(databaseMock);

        // Act
        val actual = sut.getAccount(testAccount.id)

        // Assert
        assertThat(actual)
            .isInstanceOf(Err::class.java)
            .extracting { (it as Err).error }
            .isInstanceOf(SQLException::class.java)
    }
}