package com.ericgrandt.totaleconomy.common.econ;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ericgrandt.totaleconomy.common.data.AccountData;
import com.ericgrandt.totaleconomy.common.data.BalanceData;
import com.ericgrandt.totaleconomy.common.data.CurrencyData;
import com.ericgrandt.totaleconomy.common.data.dto.CurrencyDto;
import com.ericgrandt.totaleconomy.common.logger.CommonLogger;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.UUID;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CommonEconomyTest {
    @Mock
    private CommonLogger loggerMock;

    @Mock
    private AccountData accountDataMock;

    @Mock
    private BalanceData balanceDataMock;

    @Mock
    private CurrencyData currencyDataMock;

    @Test
    @Tag("Unit")
    public void getDefaultCurrency_WithDefaultCurrency_ShouldReturnCurrency() throws SQLException {
        // Arrange
        CurrencyDto defaultCurrency = new CurrencyDto(
            1,
            "singular",
            "plural",
            "$",
            2,
            true
        );

        when(currencyDataMock.getDefaultCurrency()).thenReturn(defaultCurrency);

        CommonEconomy sut = new CommonEconomy(loggerMock, accountDataMock, balanceDataMock, currencyDataMock);

        // Act
        CurrencyDto actual = sut.getDefaultCurrency();
        CurrencyDto expected = defaultCurrency;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void getDefaultCurrency_WithSqlException_ShouldReturnNull() throws SQLException {
        // Arrange
        when(currencyDataMock.getDefaultCurrency()).thenThrow(SQLException.class);

        CommonEconomy sut = new CommonEconomy(loggerMock, accountDataMock, balanceDataMock, currencyDataMock);

        // Act
        CurrencyDto actual = sut.getDefaultCurrency();

        // Assert
        assertNull(actual);
        verify(loggerMock, times(1)).error(any(String.class), any(SQLException.class));
    }

    @Test
    @Tag("Unit")
    public void createAccount_WithSuccess_ShouldReturnTrue() throws SQLException {
        // Arrange
        UUID uuid = UUID.randomUUID();
        int currencyId = 1;

        when(accountDataMock.createAccount(uuid, currencyId)).thenReturn(true);

        CommonEconomy sut = new CommonEconomy(loggerMock, accountDataMock, balanceDataMock, currencyDataMock);

        // Act
        boolean actual = sut.createAccount(uuid, currencyId);

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void createAccount_WithSqlException_ShouldReturnFalse() throws SQLException {
        // Arrange
        UUID uuid = UUID.randomUUID();
        int currencyId = 1;

        when(accountDataMock.createAccount(uuid, currencyId)).thenThrow(SQLException.class);

        CommonEconomy sut = new CommonEconomy(loggerMock, accountDataMock, balanceDataMock, currencyDataMock);

        // Act
        boolean actual = sut.createAccount(uuid, currencyId);

        // Assert
        assertFalse(actual);
        verify(loggerMock, times(1)).error(any(String.class), any(SQLException.class));
    }

    @Test
    @Tag("Unit")
    public void hasAccount_WithNoAccount_ShouldReturnFalse() throws SQLException {
        // Arrange
        UUID uuid = UUID.randomUUID();

        when(accountDataMock.getAccount(uuid)).thenReturn(null);

        CommonEconomy sut = new CommonEconomy(loggerMock, accountDataMock, balanceDataMock, currencyDataMock);

        // Act
        boolean actual = sut.hasAccount(uuid);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void hasAccount_WithSqlException_ShouldReturnFalse() throws SQLException {
        // Arrange
        UUID uuid = UUID.randomUUID();

        when(accountDataMock.getAccount(uuid)).thenThrow(SQLException.class);

        CommonEconomy sut = new CommonEconomy(loggerMock, accountDataMock, balanceDataMock, currencyDataMock);

        // Act
        boolean actual = sut.hasAccount(uuid);

        // Assert
        assertFalse(actual);
        verify(loggerMock, times(1)).error(any(String.class), any(SQLException.class));
    }

    @Test
    @Tag("Unit")
    public void withdraw_WithSuccess_ShouldReturnTrue() throws SQLException {
        // Arrange
        UUID uuid = UUID.randomUUID();
        int currencyId = 1;
        BigDecimal amount = BigDecimal.TEN;

        when(balanceDataMock.getBalance(uuid, currencyId)).thenReturn(BigDecimal.TEN);
        when(balanceDataMock.updateBalance(uuid, currencyId, BigDecimal.ZERO)).thenReturn(1);

        CommonEconomy sut = new CommonEconomy(loggerMock, accountDataMock, balanceDataMock, currencyDataMock);

        // Act
        boolean actual = sut.withdraw(uuid, currencyId, amount);

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void withdraw_WithNullCurrentBalance_ShouldReturnFalse() throws SQLException {
        // Arrange
        UUID uuid = UUID.randomUUID();
        int currencyId = 1;
        BigDecimal amount = BigDecimal.TEN;

        when(balanceDataMock.getBalance(uuid, currencyId)).thenReturn(null);

        CommonEconomy sut = new CommonEconomy(loggerMock, accountDataMock, balanceDataMock, currencyDataMock);

        // Act
        boolean actual = sut.withdraw(uuid, currencyId, amount);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void withdraw_WithBalanceNotUpdated_ShouldReturnFalse() throws SQLException {
        // Arrange
        UUID uuid = UUID.randomUUID();
        int currencyId = 1;
        BigDecimal amount = BigDecimal.TEN;

        when(balanceDataMock.getBalance(uuid, currencyId)).thenReturn(BigDecimal.TEN);
        when(balanceDataMock.updateBalance(uuid, currencyId, BigDecimal.ZERO)).thenReturn(0);

        CommonEconomy sut = new CommonEconomy(loggerMock, accountDataMock, balanceDataMock, currencyDataMock);

        // Act
        boolean actual = sut.withdraw(uuid, currencyId, amount);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void withdraw_WithSqlException_ShouldReturnFalse() throws SQLException {
        // Arrange
        UUID uuid = UUID.randomUUID();
        int currencyId = 1;
        BigDecimal amount = BigDecimal.TEN;

        when(balanceDataMock.getBalance(uuid, currencyId)).thenReturn(BigDecimal.TEN);
        when(balanceDataMock.updateBalance(uuid, currencyId, BigDecimal.ZERO)).thenThrow(SQLException.class);

        CommonEconomy sut = new CommonEconomy(loggerMock, accountDataMock, balanceDataMock, currencyDataMock);

        // Act
        boolean actual = sut.withdraw(uuid, currencyId, amount);

        // Assert
        assertFalse(actual);
        verify(loggerMock, times(1)).error(any(String.class), any(SQLException.class));
    }

    @Test
    @Tag("Unit")
    public void getBalance_WithBalance_ShouldReturnBalance() throws SQLException {
        // Arrange
        UUID uuid = UUID.randomUUID();
        int currencyId = 1;

        when(balanceDataMock.getBalance(uuid, currencyId)).thenReturn(BigDecimal.TEN);

        CommonEconomy sut = new CommonEconomy(loggerMock, accountDataMock, balanceDataMock, currencyDataMock);

        // Act
        BigDecimal actual = sut.getBalance(uuid, currencyId);
        BigDecimal expected = BigDecimal.TEN;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void getBalance_WithSqlException_ShouldReturnNull() throws SQLException {
        // Arrange
        UUID uuid = UUID.randomUUID();
        int currencyId = 1;

        when(balanceDataMock.getBalance(uuid, currencyId)).thenThrow(SQLException.class);

        CommonEconomy sut = new CommonEconomy(loggerMock, accountDataMock, balanceDataMock, currencyDataMock);

        // Act
        BigDecimal actual = sut.getBalance(uuid, currencyId);

        // Assert
        assertNull(actual);
        verify(loggerMock, times(1)).error(any(String.class), any(SQLException.class));
    }
}
