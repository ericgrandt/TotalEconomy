package com.ericgrandt.totaleconomy.data

import com.ericgrandt.totaleconomy.TestUtils
import com.ericgrandt.totaleconomy.result.Err
import com.ericgrandt.totaleconomy.result.Ok
import com.zaxxer.hikari.HikariDataSource
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Tag
import java.sql.SQLException
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class BalanceDataTest {
    @MockK
    lateinit var databaseMock: Database

    @BeforeTest
    fun setUp() = MockKAnnotations.init(this, relaxUnitFun = true)

    @Test
    @Tag("Integration")
    fun getBalance_WithBalance_ShouldReturnBalance() {
        // Arrange
        TestUtils.resetDb()
        val testAccount = TestUtils.seedAccount(null)
        val testBalance = TestUtils.seedBalance(testAccount.id, null)

        every { databaseMock.dataSource } returns mockk<HikariDataSource>()
        every { databaseMock.dataSource.connection } returns TestUtils.getConnection()

        val sut = BalanceData(databaseMock);

        // Act
        val actual = sut.getBalance(testAccount.id)
        val expected = Ok(testBalance)

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    @Tag("Integration")
    fun getBalance_WithNoBalance_ShouldReturnNull() {
        // Arrange
        TestUtils.resetDb()
        val testAccount = TestUtils.seedAccount(null)

        every { databaseMock.dataSource } returns mockk<HikariDataSource>()
        every { databaseMock.dataSource.connection } returns TestUtils.getConnection()

        val sut = BalanceData(databaseMock);

        // Act
        val actual = sut.getBalance(testAccount.id)
        val expected = Ok(null)

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    @Tag("Integration")
    fun getBalance_WithSqlException_ShouldReturnErrResult() {
        // Arrange
        TestUtils.resetDb()
        val testAccount = TestUtils.seedAccount(null)

        every { databaseMock.dataSource } returns mockk<HikariDataSource>()
        every { databaseMock.dataSource.connection } returns TestUtils.getConnection()
        every { databaseMock.dataSource.connection.prepareStatement(any()) } throws SQLException()

        val sut = BalanceData(databaseMock);

        // Act
        val actual = sut.getBalance(testAccount.id)
        val expected = Err(SQLException())

        // Assert
        assertThat(actual)
            .isInstanceOf(Err::class.java)
            .extracting { (it as Err).error }
            .isInstanceOf(SQLException::class.java)
    }
}