package com.ericgrandt.totaleconomy.economy;

import com.ericgrandt.totaleconomy.data.AccountData;
import com.ericgrandt.totaleconomy.data.CurrencyData;
import com.ericgrandt.totaleconomy.data.TransactionUtil;
import com.ericgrandt.totaleconomy.exception.AccountNotFoundException;
import com.ericgrandt.totaleconomy.exception.CurrencyNotFoundException;
import com.ericgrandt.totaleconomy.exception.DatabaseException;
import com.ericgrandt.totaleconomy.exception.EntityNotFoundException;
import com.ericgrandt.totaleconomy.exception.MissingDefaultCurrencyException;
import com.ericgrandt.totaleconomy.model.TEAccount;
import com.ericgrandt.totaleconomy.model.TECurrency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EconomyProviderTest {
    @Mock
    private Logger loggerMock;

    @Mock
    private TransactionUtil transactionUtilMock;

    @Mock
    private CurrencyData currencyDataMock;

    @Mock
    private AccountData accountDataMock;

    private EconomyProvider sut;

    @BeforeEach
    public void setUp() throws SQLException {
        when(transactionUtilMock.runInTransaction(any())).thenAnswer(invocation -> {
            TransactionUtil.Transaction<?> tx = invocation.getArgument(0);
            return tx.execute(mock(Connection.class));
        });
        sut = new EconomyProvider(loggerMock, transactionUtilMock, currencyDataMock, accountDataMock);
    }

    @Test
    @Tag("Unit")
    public void getDefaultCurrency_WithSuccess_ShouldReturnDefaultCurrency() throws SQLException {
        // Arrange
        var currency = new TECurrency("USD", "Dollar", "Dollars", "$", 2, true);

        when(currencyDataMock.getDefaultCurrency(any())).thenReturn(currency);

        // Act
        var actual = sut.getDefaultCurrency();

        // Assert
        assertEquals(currency, actual);
    }

    @Test
    @Tag("Unit")
    public void getDefaultCurrency_WithEntityNotFoundException_ShouldThrowMissingDefaultCurrencyException() throws SQLException {
        // Arrange
        when(currencyDataMock.getDefaultCurrency(any())).thenThrow(EntityNotFoundException.class);

        // Act/Assert
        assertThrows(MissingDefaultCurrencyException.class, () -> sut.getDefaultCurrency());

        verify(loggerMock, times(1)).error(
            eq("default currency not found"),
            any(EntityNotFoundException.class)
        );
    }

    @Test
    @Tag("Unit")
    public void getDefaultCurrency_WithSQLException_ShouldThrowDatabaseException() throws SQLException {
        // Arrange
        when(currencyDataMock.getDefaultCurrency(any())).thenThrow(SQLException.class);

        // Act/Assert
        assertThrows(DatabaseException.class, () -> sut.getDefaultCurrency());

        verify(loggerMock, times(1)).error(
            eq("database exception when getting default currency"),
            any(SQLException.class)
        );
    }

    @Test
    @Tag("Unit")
    public void getCurrency_WithSuccess_ShouldReturnCurrency() throws SQLException {
        // Arrange
        var currency = new TECurrency("USD", "Dollar", "Dollars", "$", 2, true);

        when(currencyDataMock.getCurrency(any(), any())).thenReturn(currency);

        // Act
        var actual = sut.getCurrency("USD");

        // Assert
        assertEquals(currency, actual);
    }

    @Test
    @Tag("Unit")
    public void getCurrency_WithEntityNotFoundException_ShouldThrowCurrencyNotFoundException() throws SQLException {
        // Arrange
        when(currencyDataMock.getCurrency(any(), any())).thenThrow(EntityNotFoundException.class);

        // Act/Assert
        assertThrows(CurrencyNotFoundException.class, () -> sut.getCurrency("USD"));

        verify(loggerMock, times(1)).error(
            eq("currency not found"),
            any(EntityNotFoundException.class)
        );
    }

    @Test
    @Tag("Unit")
    public void getCurrency_WithSQLException_ShouldThrowDatabaseException() throws SQLException {
        // Arrange
        when(currencyDataMock.getCurrency(any(), any())).thenThrow(SQLException.class);

        // Act/Assert
        assertThrows(DatabaseException.class, () -> sut.getCurrency("USD"));

        verify(loggerMock, times(1)).error(
            eq("database exception when getting currency"),
            any(SQLException.class)
        );
    }

    @Test
    @Tag("Unit")
    public void createAccount_WithSuccess_ShouldReturnAccount() throws SQLException {
        // Arrange
        var account = new TEAccount(UUID.randomUUID(), "USD", BigDecimal.TEN);

        when(accountDataMock.createAccount(any(), any())).thenReturn(account);

        // Act
        var actual = sut.createAccount(account.playerId(), account.currencyCode(), account.balance());

        // Assert
        assertEquals(account, actual);
    }

    @Test
    @Tag("Unit")
    public void createAccount_WithSQLException_ShouldThrowDatabaseException() throws SQLException {
        // Arrange
        when(accountDataMock.createAccount(any(), any())).thenThrow(SQLException.class);

        // Act/Assert
        assertThrows(DatabaseException.class, () -> sut.createAccount(UUID.randomUUID(), "USD", BigDecimal.TEN));

        verify(loggerMock, times(1)).error(
            eq("database exception when creating account"),
            any(SQLException.class)
        );
    }

    @Test
    @Tag("Unit")
    public void getAccount_WithSuccess_ShouldReturnAccount() throws SQLException {
        // Arrange
        var account = new TEAccount(UUID.randomUUID(), "USD", BigDecimal.TEN);

        when(accountDataMock.getAccount(any(), any())).thenReturn(account);

        // Act
        var actual = sut.getAccount(account.playerId(), account.currencyCode());

        // Assert
        assertEquals(account, actual);
    }

    @Test
    @Tag("Unit")
    public void getAccount_WithEntityNotFoundException_ShouldThrowAccountNotFoundException() throws SQLException {
        // Arrange
        when(accountDataMock.getAccount(any(), any())).thenThrow(EntityNotFoundException.class);

        // Act/Assert
        assertThrows(AccountNotFoundException.class, () -> sut.getAccount(UUID.randomUUID(), "USD"));

        verify(loggerMock, times(1)).error(
            eq("account not found"),
            any(EntityNotFoundException.class)
        );
    }

    @Test
    @Tag("Unit")
    public void getAccount_WithSQLException_ShouldThrowDatabaseException() throws SQLException {
        // Arrange
        when(accountDataMock.getAccount(any(), any())).thenThrow(SQLException.class);

        // Act/Assert
        assertThrows(DatabaseException.class, () -> sut.getAccount(UUID.randomUUID(), "USD"));

        verify(loggerMock, times(1)).error(
            eq("database exception when getting account"),
            any(SQLException.class)
        );
    }
}