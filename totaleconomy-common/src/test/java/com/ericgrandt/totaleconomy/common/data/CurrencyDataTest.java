package com.ericgrandt.totaleconomy.common.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ericgrandt.totaleconomy.common.TestUtils;
import com.ericgrandt.totaleconomy.common.data.dto.CurrencyDto;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CurrencyDataTest {
    @Test
    @Tag("Unit")
    public void getDefaultCurrency_WithSuccess_ShouldReturnCurrencyDto() throws SQLException {
        // Arrange
        Database databaseMock = mock(Database.class);
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);
        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true);
        when(resultSetMock.getInt("id")).thenReturn(1);
        when(resultSetMock.getString("name_singular")).thenReturn("Dollar");
        when(resultSetMock.getString("name_plural")).thenReturn("Dollars");
        when(resultSetMock.getString("symbol")).thenReturn("$");
        when(resultSetMock.getInt("num_fraction_digits")).thenReturn(2);
        when(resultSetMock.getBoolean("is_default")).thenReturn(true);

        CurrencyData sut = new CurrencyData(databaseMock);

        // Act
        CurrencyDto actual = sut.getDefaultCurrency();
        CurrencyDto expected = new CurrencyDto(
            1,
            "Dollar",
            "Dollars",
            "$",
            2,
            true
        );

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void getDefaultCurrency_WithNoDefaultCurrency_ShouldReturnNull() throws SQLException {
        // Arrange
        Database databaseMock = mock(Database.class);
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);
        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(false);

        CurrencyData sut = new CurrencyData(databaseMock);

        // Act
        CurrencyDto actual = sut.getDefaultCurrency();

        // Assert
        assertNull(actual);
    }

    @Test
    @Tag("Integration")
    public void getDefaultCurrency_ShouldReturnDefaultCurrencyDto() throws SQLException {
        // Arrange
        TestUtils.resetDb();
        TestUtils.seedCurrencies();

        Database databaseMock = mock(Database.class);
        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).thenReturn(TestUtils.getConnection());

        CurrencyData sut = new CurrencyData(databaseMock);

        // Act
        CurrencyDto actual = sut.getDefaultCurrency();
        CurrencyDto expected = new CurrencyDto(
            1,
            "Dollar",
            "Dollars",
            "$",
            0,
            true
        );

        // Assert
        assertEquals(expected, actual);
    }
}
