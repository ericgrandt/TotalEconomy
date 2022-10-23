package com.ericgrandt.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ericgrandt.TestUtils;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BalanceDataTest {
    @Test
    @Tag("Unit")
    public void getDefaultBalance_WithDefaultBalanceFound_ShouldReturnDefaultBalance() throws SQLException {
        // Arrange
        Database databaseMock = mock(Database.class);
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);
        when(databaseMock.getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true);
        when(resultSetMock.getBigDecimal("default_balance")).thenReturn(BigDecimal.ONE);

        BalanceData sut = new BalanceData(databaseMock);

        // Act
        BigDecimal actual = sut.getDefaultBalance(1);
        BigDecimal expected = BigDecimal.ONE;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void getDefaultBalance_WithNoDefaultBalanceFound_ShouldReturnZero() throws SQLException {
        // Arrange
        Database databaseMock = mock(Database.class);
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);
        when(databaseMock.getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(false);

        BalanceData sut = new BalanceData(databaseMock);

        // Act
        BigDecimal actual = sut.getDefaultBalance(1);
        BigDecimal expected = BigDecimal.ZERO;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void getBalance_WithBalanceFound_ShouldReturnBalance() throws SQLException {
        // Arrange
        Database databaseMock = mock(Database.class);
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);
        when(databaseMock.getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true);
        when(resultSetMock.getBigDecimal("balance")).thenReturn(BigDecimal.TEN);

        BalanceData sut = new BalanceData(databaseMock);

        // Act
        BigDecimal actual = sut.getBalance("account-id", 1);
        BigDecimal expected = BigDecimal.TEN;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void getBalance_WithNoBalanceFound_ShouldReturnNull() throws SQLException {
        // Arrange
        Database databaseMock = mock(Database.class);
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);
        when(databaseMock.getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(false);

        BalanceData sut = new BalanceData(databaseMock);

        // Act
        BigDecimal actual = sut.getBalance("account-id", 1);

        // Assert
        assertNull(actual);
    }

    @Test
    @Tag("Integration")
    public void getDefaultBalance_ShouldReturnDefaultBalance() throws SQLException {
        // Arrange
        TestUtils.resetDb();
        TestUtils.seedCurrencies();
        TestUtils.seedDefaultBalances();

        Database databaseMock = mock(Database.class);
        when(databaseMock.getConnection()).thenReturn(TestUtils.getConnection());

        BalanceData sut = new BalanceData(databaseMock);

        // Act
        BigDecimal actual = sut.getDefaultBalance(1);
        BigDecimal expected = BigDecimal.valueOf(100.50).setScale(2, RoundingMode.DOWN);

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Integration")
    public void getBalance_ShouldReturnBalance() throws SQLException {
        // Arrange
        TestUtils.resetDb();
        TestUtils.seedCurrencies();
        TestUtils.seedAccounts();

        String accountId = "62694fb0-07cc-4396-8d63-4f70646d75f0";
        int currencyId = 1;

        Database databaseMock = mock(Database.class);
        when(databaseMock.getConnection()).thenReturn(TestUtils.getConnection());

        BalanceData sut = new BalanceData(databaseMock);

        // Act
        BigDecimal actual = sut.getBalance(accountId, currencyId);
        BigDecimal expected = BigDecimal.valueOf(50).setScale(2, RoundingMode.DOWN);

        // Assert
        assertEquals(expected, actual);
    }
}
