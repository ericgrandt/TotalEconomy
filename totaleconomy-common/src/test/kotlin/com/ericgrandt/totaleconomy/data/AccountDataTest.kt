package com.ericgrandt.totaleconomy.data

import com.ericgrandt.totaleconomy.TestUtils
import com.github.michaelbull.result.Err
import com.ericgrandt.totaleconomy.result.Ok as OkOld
import com.ericgrandt.totaleconomy.result.Err as ErrOld
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.asErr
import com.github.michaelbull.result.getError
import com.zaxxer.hikari.HikariDataSource
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.assertAll
import java.sql.SQLException
import java.util.UUID
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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
        val actual = sut.createAccount(uuid)
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
        val actual = sut.createAccount(uuid)

        // Assert
        assertThat(actual.getError())
            .isInstanceOf(SQLException::class.java)
    }

    @Test
    @Tag("Integration")
    fun getAccount_WithSuccess_ShouldReturnAccountOld() {
        // Arrange
        TestUtils.resetDb()
        val testAccount = TestUtils.seedAccount()

        every { databaseMock.dataSource } returns mockk<HikariDataSource>()
        every { databaseMock.dataSource.connection } returns TestUtils.getConnection()

        val sut = AccountData(databaseMock);

        // Act
        val actual = sut.getAccountOld(testAccount.id)
        val expected = OkOld(testAccount)

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    @Tag("Integration")
    fun getAccount_WithNoAccountOldFound_ShouldReturnAnInfoResult() {
        // Arrange
        TestUtils.resetDb()

        every { databaseMock.dataSource } returns mockk<HikariDataSource>()
        every { databaseMock.dataSource.connection } returns TestUtils.getConnection()

        val sut = AccountData(databaseMock);

        // Act
        val actual = sut.getAccountOld(UUID.randomUUID())
        val expected = OkOld(null)

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    @Tag("Integration")
    fun getAccount_Old_WithSQLException_ShouldReturnError() {
        // Arrange
        TestUtils.resetDb()
        val testAccount = TestUtils.seedAccount()

        every { databaseMock.dataSource } returns mockk<HikariDataSource>()
        every { databaseMock.dataSource.connection } returns TestUtils.getConnection()
        every { databaseMock.dataSource.connection.prepareStatement(any()) } throws SQLException("")

        val sut = AccountData(databaseMock);

        // Act
        val actual = sut.getAccountOld(testAccount.id)

        // Assert
        assertThat(actual)
            .isInstanceOf(ErrOld::class.java)
            .extracting { (it as ErrOld).error }
            .isInstanceOf(SQLException::class.java)
    }
}