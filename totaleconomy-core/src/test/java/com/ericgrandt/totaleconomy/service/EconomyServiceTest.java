package com.ericgrandt.totaleconomy.service;

import com.ericgrandt.totaleconomy.data.AccountData;
import com.ericgrandt.totaleconomy.data.CurrencyData;
import com.ericgrandt.totaleconomy.data.TransactionUtil;
import com.ericgrandt.totaleconomy.dto.GetAccountBalanceResult;
import com.ericgrandt.totaleconomy.exception.DatabaseException;
import com.ericgrandt.totaleconomy.model.TEAccount;
import com.ericgrandt.totaleconomy.model.TECurrency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EconomyServiceTest {
    @Mock
    private TransactionUtil transactionUtilMock;

    @Mock
    private CurrencyData currencyDataMock;

    @Mock
    private AccountData accountDataMock;

    private EconomyService sut;

    @BeforeEach
    public void setUp() throws SQLException {
        when(transactionUtilMock.runInTransaction(any())).thenAnswer(invocation -> {
            TransactionUtil.Transaction<?> tx = invocation.getArgument(0);
            return tx.execute(mock(Connection.class));
        });
        sut = new EconomyService(transactionUtilMock, currencyDataMock, accountDataMock);
    }

    @Test
    @Tag("Unit")
    public void getAccountBalance_WithNoCurrencyCodeAndSuccess_ShouldReturnBalanceForDefaultCurrency() throws SQLException {
        // Arrange
        var currency = new TECurrency("USD", "Dollar", "Dollars", "$", 2, true);
        var account = new TEAccount(UUID.randomUUID(), "USD", BigDecimal.TEN);

        when(currencyDataMock.getDefaultCurrency(any())).thenReturn(currency);
        when(accountDataMock.getAccount(any(), any(), any())).thenReturn(account);

        // Act
        var actual = sut.getAccountBalance(UUID.randomUUID(), null);
        var expected = new GetAccountBalanceResult(currency, BigDecimal.TEN);

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void getAccountBalance_WithCurrencyCodeAndSuccess_ShouldReturnBalanceForCurrency() throws SQLException {
        // Arrange
        var currency = new TECurrency("COIN", "Coin", "Coins", null, 0, false);
        var account = new TEAccount(UUID.randomUUID(), "USD", BigDecimal.TEN);

        when(currencyDataMock.getCurrency(any(), any())).thenReturn(currency);
        when(accountDataMock.getAccount(any(), any(), any())).thenReturn(account);

        // Act
        var actual = sut.getAccountBalance(UUID.randomUUID(), currency.code());
        var expected = new GetAccountBalanceResult(currency, BigDecimal.TEN);

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void getAccountBalance_WithSQLException_ShouldThrowDatabaseException() throws SQLException {
        // Arrange
        when(currencyDataMock.getCurrency(any(), any())).thenThrow(SQLException.class);

        // Act/Assert
        assertThrows(DatabaseException.class, () -> sut.getAccountBalance(UUID.randomUUID(), "USD"));
    }
}
