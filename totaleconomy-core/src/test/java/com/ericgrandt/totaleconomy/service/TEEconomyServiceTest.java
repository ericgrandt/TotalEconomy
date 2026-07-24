package com.ericgrandt.totaleconomy.service;

import com.ericgrandt.totaleconomy.data.AccountData;
import com.ericgrandt.totaleconomy.data.CurrencyData;
import com.ericgrandt.totaleconomy.data.TransactionUtil;
import com.ericgrandt.totaleconomy.dto.DepositResult;
import com.ericgrandt.totaleconomy.dto.GetAccountBalanceResult;
import com.ericgrandt.totaleconomy.dto.TransferResult;
import com.ericgrandt.totaleconomy.dto.WithdrawResult;
import com.ericgrandt.totaleconomy.exception.AccountNotFoundException;
import com.ericgrandt.totaleconomy.exception.CurrencyNotFoundException;
import com.ericgrandt.totaleconomy.exception.DatabaseException;
import com.ericgrandt.totaleconomy.exception.InsufficientFundsException;
import com.ericgrandt.totaleconomy.exception.SelfTransferException;
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
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TEEconomyServiceTest {
    @Mock
    private TransactionUtil transactionUtilMock;

    @Mock
    private CurrencyData currencyDataMock;

    @Mock
    private AccountData accountDataMock;

    private TEEconomyService sut;

    @BeforeEach
    public void setUp() throws SQLException {
        lenient().when(transactionUtilMock.runInTransaction(any())).thenAnswer(invocation -> {
            TransactionUtil.Transaction<?> tx = invocation.getArgument(0);
            return tx.execute(mock(Connection.class));
        });
        sut = new TEEconomyService(transactionUtilMock, currencyDataMock, accountDataMock);
    }

    @Test
    @Tag("Unit")
    public void getDefaultCurrency_WithSuccess_ShouldReturnDefaultCurrency() throws SQLException {
        // Arrange
        var currency = new TECurrency("USD", "Dollar", "Dollars", "$", 2, BigDecimal.TEN, true);

        when(currencyDataMock.getDefaultCurrency(any())).thenReturn(currency);

        // Act
        var actual = sut.getDefaultCurrency();

        // Assert
        assertEquals(currency, actual);
    }

    @Test
    @Tag("Unit")
    public void getDefaultCurrency_WithSQLException_ShouldThrowDatabaseException() throws SQLException {
        // Arrange
        when(currencyDataMock.getDefaultCurrency(any())).thenThrow(SQLException.class);

        // Act/Assert
        assertThrows(DatabaseException.class, () -> sut.getDefaultCurrency());
    }

    @Test
    @Tag("Unit")
    public void createAccount_WithSuccess_ShouldReturnCreatedAccount() throws SQLException {
        // Arrange
        var currency = new TECurrency("USD", "Dollar", "Dollars", "$", 2, BigDecimal.TEN, true);
        var account = new TEAccount(UUID.randomUUID(), "USD", BigDecimal.TEN);

        when(currencyDataMock.getCurrency(any(), any())).thenReturn(Optional.of(currency));
        when(accountDataMock.createAccount(any(), any())).thenReturn(account);

        // Act
        var actual = sut.createAccount(UUID.randomUUID(), "USD");

        // Assert
        assertEquals(account, actual);
    }

    @Test
    @Tag("Unit")
    public void createAccount_WithEmptyCurrency_ShouldReturnCurrencyNotFoundException() throws SQLException {
        // Arrange
        when(currencyDataMock.getCurrency(any(), any())).thenReturn(Optional.empty());

        // Act/Assert
        assertThrows(
            CurrencyNotFoundException.class,
            () -> sut.createAccount(UUID.randomUUID(), "USD")
        );
    }

    @Test
    @Tag("Unit")
    public void createAccount_WithSQLException_ShouldThrowDatabaseException() throws SQLException {
        // Arrange
        when(currencyDataMock.getCurrency(any(), any())).thenThrow(SQLException.class);

        // Act/Assert
        assertThrows(DatabaseException.class, () -> sut.createAccount(UUID.randomUUID(), "USD"));
    }

    @Test
    @Tag("Unit")
    public void getAccountBalance_WithCurrencyCodeAndSuccess_ShouldReturnBalanceForCurrency() throws SQLException {
        // Arrange
        var currency = new TECurrency("COIN", "Coin", "Coins", null, 0, BigDecimal.TEN, false);
        var account = new TEAccount(UUID.randomUUID(), "USD", BigDecimal.TEN);

        when(currencyDataMock.getCurrency(any(), any())).thenReturn(Optional.of(currency));
        when(accountDataMock.getAccount(any(), any(), any())).thenReturn(Optional.of(account));

        // Act
        var actual = sut.getAccountBalance(UUID.randomUUID(), currency.code());
        var expected = new GetAccountBalanceResult(currency, BigDecimal.TEN);

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void getAccountBalance_WithCurrencyCodeAndEmptyCurrency_ShouldReturnCurrencyNotFoundException() throws SQLException {
        // Arrange
        when(currencyDataMock.getCurrency(any(), any())).thenReturn(Optional.empty());

        // Act/Assert
        assertThrows(
            CurrencyNotFoundException.class,
            () -> sut.getAccountBalance(UUID.randomUUID(), "USD")
        );
    }

    @Test
    @Tag("Unit")
    public void getAccountBalance_WithCurrencyCodeAndNoAccount_ShouldThrowAccountNotFoundException() throws SQLException {
        // Arrange
        var currency = new TECurrency("COIN", "Coin", "Coins", null, 0, BigDecimal.TEN, false);

        when(currencyDataMock.getCurrency(any(), any())).thenReturn(Optional.of(currency));
        when(accountDataMock.getAccount(any(), any(), any())).thenReturn(Optional.empty());

        // Act/Assert
        assertThrows(AccountNotFoundException.class, () -> sut.getAccountBalance(UUID.randomUUID(), currency.code()));
    }

    @Test
    @Tag("Unit")
    public void getAccountBalance_WithCurrencyCodeAndSQLException_ShouldThrowDatabaseException() throws SQLException {
        // Arrange
        when(currencyDataMock.getCurrency(any(), any())).thenThrow(SQLException.class);

        // Act/Assert
        assertThrows(DatabaseException.class, () -> sut.getAccountBalance(UUID.randomUUID(), "USD"));
    }

    @Test
    @Tag("Unit")
    public void getAccountBalance_WithDefaultCurrencyCodeAndSuccess_ShouldReturnBalanceForDefaultCurrency() throws SQLException {
        // Arrange
        var currency = new TECurrency("USD", "Dollar", "Dollars", "$", 2, BigDecimal.TEN, true);
        var account = new TEAccount(UUID.randomUUID(), "USD", BigDecimal.TEN);

        when(currencyDataMock.getDefaultCurrency(any())).thenReturn(currency);
        when(currencyDataMock.getCurrency(any(), eq(currency.code()))).thenReturn(Optional.of(currency));
        when(accountDataMock.getAccount(any(), any(), any())).thenReturn(Optional.of(account));

        // Act
        var actual = sut.getAccountBalance(UUID.randomUUID());
        var expected = new GetAccountBalanceResult(currency, BigDecimal.TEN);

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void getAccountBalance_WithDefaultCurrencyAndSQLException_ShouldThrowDatabaseException() throws SQLException {
        // Arrange
        when(currencyDataMock.getDefaultCurrency(any())).thenThrow(SQLException.class);

        // Act/Assert
        assertThrows(DatabaseException.class, () -> sut.getAccountBalance(UUID.randomUUID()));
    }

    @Test
    @Tag("Unit")
    public void withdraw_WithCurrencyCodeAndSuccess_ShouldReturnWithdrawResult() throws SQLException {
        // Arrange
        var currency = new TECurrency("USD", "Dollar", "Dollars", "$", 2, BigDecimal.TEN, true);
        var account = new TEAccount(UUID.randomUUID(), "USD", BigDecimal.TEN);
        when(currencyDataMock.getCurrency(any(), any())).thenReturn(Optional.of(currency));
        when(accountDataMock.getAccount(
            any(),
            eq(account.playerId()),
            eq(currency.code())
        )).thenReturn(Optional.of(account));
        when(accountDataMock.withdraw(
            any(),
            eq(account.playerId()),
            eq(currency.code()),
            any(),
            eq(true)
        )).thenReturn(true);

        // Act
        var actual = sut.withdraw(account.playerId(), currency.code(), BigDecimal.TEN);
        var expected = new WithdrawResult(currency, BigDecimal.TEN, BigDecimal.ZERO);

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void withdraw_WithCurrencyCodeAndEmptyCurrency_ShouldReturnCurrencyNotFoundException() throws SQLException {
        // Arrange
        when(currencyDataMock.getCurrency(any(), any())).thenReturn(Optional.empty());

        // Act/Assert
        assertThrows(
            CurrencyNotFoundException.class,
            () -> sut.withdraw(UUID.randomUUID(), "USD", BigDecimal.ONE)
        );
    }

    @Test
    @Tag("Unit")
    public void withdraw_WithCurrencyCodeAndNoAccountFound_ShouldThrowAccountNotFoundException() throws SQLException {
        // Arrange
        var currency = new TECurrency("USD", "Dollar", "Dollars", "$", 2, BigDecimal.TEN, true);
        var account = new TEAccount(UUID.randomUUID(), "USD", BigDecimal.TEN);
        when(currencyDataMock.getCurrency(any(), any())).thenReturn(Optional.of(currency));
        when(accountDataMock.getAccount(
            any(),
            eq(account.playerId()),
            eq(currency.code())
        )).thenReturn(Optional.empty());

        // Act/Assert
        assertThrows(
            AccountNotFoundException.class,
            () -> sut.withdraw(account.playerId(), currency.code(), BigDecimal.TEN)
        );
    }

    @Test
    @Tag("Unit")
    public void withdraw_WithCurrencyCodeAndInsufficientFunds_ShouldThrowInsufficientFundsException() throws SQLException {
        // Arrange
        var currency = new TECurrency("USD", "Dollar", "Dollars", "$", 2, BigDecimal.TEN, true);
        var account = new TEAccount(UUID.randomUUID(), "USD", BigDecimal.TEN);
        when(currencyDataMock.getCurrency(any(), any())).thenReturn(Optional.of(currency));
        when(accountDataMock.getAccount(
            any(),
            eq(account.playerId()),
            eq(currency.code())
        )).thenReturn(Optional.of(account));
        when(accountDataMock.withdraw(
            any(),
            eq(account.playerId()),
            eq(currency.code()),
            any(),
            eq(true)
        )).thenReturn(false);

        // Act/Assert
        assertThrows(
            InsufficientFundsException.class,
            () -> sut.withdraw(account.playerId(), currency.code(), BigDecimal.TEN)
        );
    }

    @Test
    @Tag("Unit")
    public void withdraw_WithCurrencyCodeAndSQLException_ShouldThrowDatabaseException() throws SQLException {
        // Arrange
        var currency = new TECurrency("USD", "Dollar", "Dollars", "$", 2, BigDecimal.TEN, true);
        var account = new TEAccount(UUID.randomUUID(), "USD", BigDecimal.TEN);
        when(currencyDataMock.getCurrency(any(), any())).thenThrow(SQLException.class);

        // Act/Assert
        assertThrows(
            DatabaseException.class,
            () -> sut.withdraw(account.playerId(), currency.code(), BigDecimal.TEN)
        );
    }

    @Test
    @Tag("Unit")
    public void withdraw_WithDefaultCurrencyCodeAndSuccess_ShouldReturnWithdrawResult() throws SQLException {
        // Arrange
        var currency = new TECurrency("USD", "Dollar", "Dollars", "$", 2, BigDecimal.TEN, true);
        var account = new TEAccount(UUID.randomUUID(), "USD", BigDecimal.TEN);
        when(currencyDataMock.getDefaultCurrency(any())).thenReturn(currency);
        when(currencyDataMock.getCurrency(any(), any())).thenReturn(Optional.of(currency));
        when(accountDataMock.getAccount(
            any(),
            eq(account.playerId()),
            eq(currency.code())
        )).thenReturn(Optional.of(account));
        when(accountDataMock.withdraw(
            any(),
            eq(account.playerId()),
            eq(currency.code()),
            any(),
            eq(true)
        )).thenReturn(true);

        // Act
        var actual = sut.withdraw(account.playerId(), BigDecimal.TEN);
        var expected = new WithdrawResult(currency, BigDecimal.TEN, BigDecimal.ZERO);

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void withdraw_WithDefaultCurrencyCodeAndSQLException_ShouldThrowDatabaseException() throws SQLException {
        // Arrange
        var account = new TEAccount(UUID.randomUUID(), "USD", BigDecimal.TEN);
        when(currencyDataMock.getDefaultCurrency(any())).thenThrow(SQLException.class);

        // Act/Assert
        assertThrows(
            DatabaseException.class,
            () -> sut.withdraw(account.playerId(), BigDecimal.TEN)
        );
    }

    @Test
    @Tag("Unit")
    public void deposit_WithCurrencyCodeAndSuccess_ShouldReturnDepositResult() throws SQLException {
        // Arrange
        var currency = new TECurrency("USD", "Dollar", "Dollars", "$", 2, BigDecimal.TEN, true);
        var account = new TEAccount(UUID.randomUUID(), "USD", BigDecimal.TEN);
        when(currencyDataMock.getCurrency(any(), any())).thenReturn(Optional.of(currency));
        when(accountDataMock.getAccount(
            any(),
            eq(account.playerId()),
            eq(currency.code())
        )).thenReturn(Optional.of(account));
        when(accountDataMock.deposit(
            any(),
            eq(account.playerId()),
            eq(currency.code()),
            any()
        )).thenReturn(true);

        // Act
        var actual = sut.deposit(account.playerId(), currency.code(), BigDecimal.TEN);
        var expected = new DepositResult(currency, BigDecimal.TEN, BigDecimal.valueOf(20));

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void deposit_WithCurrencyCodeAndEmptyCurrency_ShouldReturnCurrencyNotFoundException() throws SQLException {
        // Arrange
        when(currencyDataMock.getCurrency(any(), any())).thenReturn(Optional.empty());

        // Act/Assert
        assertThrows(
            CurrencyNotFoundException.class,
            () -> sut.deposit(UUID.randomUUID(), "USD", BigDecimal.ONE)
        );
    }

    @Test
    @Tag("Unit")
    public void deposit_WithCurrencyCodeAndNoAccountFound_ShouldThrowAccountNotFoundException() throws SQLException {
        // Arrange
        var currency = new TECurrency("USD", "Dollar", "Dollars", "$", 2, BigDecimal.TEN, true);
        var account = new TEAccount(UUID.randomUUID(), "USD", BigDecimal.TEN);
        when(currencyDataMock.getCurrency(any(), any())).thenReturn(Optional.of(currency));
        when(accountDataMock.getAccount(
            any(),
            eq(account.playerId()),
            eq(currency.code())
        )).thenReturn(Optional.empty());

        // Act/Assert
        assertThrows(
            AccountNotFoundException.class,
            () -> sut.deposit(account.playerId(), currency.code(), BigDecimal.TEN)
        );
    }

    @Test
    @Tag("Unit")
    public void deposit_WithCurrencyCodeAndUnsuccessfulDeposit_ShouldReturnEmptyDepositResult() throws SQLException {
        // Arrange
        var currency = new TECurrency("USD", "Dollar", "Dollars", "$", 2, BigDecimal.TEN, true);
        var account = new TEAccount(UUID.randomUUID(), "USD", BigDecimal.TEN);
        when(currencyDataMock.getCurrency(any(), any())).thenReturn(Optional.of(currency));
        when(accountDataMock.getAccount(
            any(),
            eq(account.playerId()),
            eq(currency.code())
        )).thenReturn(Optional.of(account));
        when(accountDataMock.deposit(
            any(),
            eq(account.playerId()),
            eq(currency.code()),
            any()
        )).thenReturn(false);

        // Act
        var actual = sut.deposit(account.playerId(), currency.code(), BigDecimal.TEN);
        var expected = new DepositResult(currency, BigDecimal.ZERO, BigDecimal.TEN);

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void deposit_WithCurrencyCodeAndSQLException_ShouldThrowDatabaseException() throws SQLException {
        // Arrange
        var currency = new TECurrency("USD", "Dollar", "Dollars", "$", 2, BigDecimal.TEN, true);
        var account = new TEAccount(UUID.randomUUID(), "USD", BigDecimal.TEN);
        when(currencyDataMock.getCurrency(any(), any())).thenThrow(SQLException.class);

        // Act/Assert
        assertThrows(
            DatabaseException.class,
            () -> sut.deposit(account.playerId(), currency.code(), BigDecimal.TEN)
        );
    }

    @Test
    @Tag("Unit")
    public void deposit_WithDefaultCurrencyCodeAndSuccess_ShouldReturnDepositResult() throws SQLException {
        // Arrange
        var currency = new TECurrency("USD", "Dollar", "Dollars", "$", 2, BigDecimal.TEN, true);
        var account = new TEAccount(UUID.randomUUID(), "USD", BigDecimal.TEN);
        when(currencyDataMock.getDefaultCurrency(any())).thenReturn(currency);
        when(currencyDataMock.getCurrency(any(), any())).thenReturn(Optional.of(currency));
        when(accountDataMock.getAccount(
            any(),
            eq(account.playerId()),
            eq(currency.code())
        )).thenReturn(Optional.of(account));
        when(accountDataMock.deposit(
            any(),
            eq(account.playerId()),
            eq(currency.code()),
            any()
        )).thenReturn(true);

        // Act
        var actual = sut.deposit(account.playerId(), BigDecimal.TEN);
        var expected = new DepositResult(currency, BigDecimal.TEN, BigDecimal.valueOf(20));

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void deposit_WithDefaultCurrencyCodeAndSQLException_ShouldThrowDatabaseException() throws SQLException {
        // Arrange
        var account = new TEAccount(UUID.randomUUID(), "USD", BigDecimal.TEN);
        when(currencyDataMock.getDefaultCurrency(any())).thenThrow(SQLException.class);

        // Act/Assert
        assertThrows(
            DatabaseException.class,
            () -> sut.deposit(account.playerId(), BigDecimal.TEN)
        );
    }

    @Test
    @Tag("Unit")
    public void transfer_WithCurrencyCodeAndSuccess_ShouldReturnTransferResult() throws SQLException {
        // Arrange
        var currency = new TECurrency("USD", "Dollar", "Dollars", "$", 2, BigDecimal.TEN, true);
        var fromAccount = new TEAccount(UUID.randomUUID(), "USD", BigDecimal.TEN);
        var toAccount = new TEAccount(UUID.randomUUID(), "USD", BigDecimal.ONE);
        when(currencyDataMock.getCurrency(any(), any())).thenReturn(Optional.of(currency));
        when(accountDataMock.getAccount(
            any(),
            eq(fromAccount.playerId()),
            eq(currency.code())
        )).thenReturn(Optional.of(fromAccount));
        when(accountDataMock.getAccount(
            any(),
            eq(toAccount.playerId()),
            eq(currency.code())
        )).thenReturn(Optional.of(toAccount));
        when(accountDataMock.withdraw(
            any(),
            eq(fromAccount.playerId()),
            eq(currency.code()),
            any(),
            eq(true)
        )).thenReturn(true);
        when(accountDataMock.deposit(
            any(),
            eq(toAccount.playerId()),
            eq(currency.code()),
            any()
        )).thenReturn(true);

        // Act
        var actual = sut.transfer(fromAccount.playerId(), toAccount.playerId(), currency.code(), BigDecimal.TEN);
        var expected = new TransferResult(currency, BigDecimal.TEN);

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void transfer_WithCurrencyCodeAndEmptyCurrency_ShouldReturnCurrencyNotFoundException() throws SQLException {
        // Arrange
        when(currencyDataMock.getCurrency(any(), any())).thenReturn(Optional.empty());

        // Act/Assert
        assertThrows(
            CurrencyNotFoundException.class,
            () -> sut.transfer(UUID.randomUUID(), UUID.randomUUID(), "USD", BigDecimal.ONE)
        );
    }

    @Test
    @Tag("Unit")
    public void transfer_WithCurrencyCodeAndSameFromAndToPlayerID_ShouldThrowSelfTransferException() {
        // Arrange
        var currency = new TECurrency("USD", "Dollar", "Dollars", "$", 2, BigDecimal.TEN, true);
        var fromAccount = new TEAccount(UUID.randomUUID(), "USD", BigDecimal.TEN);

        // Act/Assert
        assertThrows(
            SelfTransferException.class,
            () -> sut.transfer(fromAccount.playerId(), fromAccount.playerId(), currency.code(), BigDecimal.TEN)
        );
    }

    @Test
    @Tag("Unit")
    public void transfer_WithCurrencyCodeAndNoAccountFound_ShouldReturnThrowAccountNotFoundException() throws SQLException {
        // Arrange
        var currency = new TECurrency("USD", "Dollar", "Dollars", "$", 2, BigDecimal.TEN, true);
        var fromAccount = new TEAccount(UUID.randomUUID(), "USD", BigDecimal.TEN);
        var toAccount = new TEAccount(UUID.randomUUID(), "USD", BigDecimal.ONE);
        when(currencyDataMock.getCurrency(any(), any())).thenReturn(Optional.of(currency));
        when(accountDataMock.getAccount(
            any(),
            eq(fromAccount.playerId()),
            eq(currency.code())
        )).thenReturn(Optional.empty());

        // Act/Assert
        assertThrows(
            AccountNotFoundException.class,
            () -> sut.transfer(fromAccount.playerId(), toAccount.playerId(), currency.code(), BigDecimal.TEN)
        );
    }

    @Test
    @Tag("Unit")
    public void transfer_WithCurrencyCodeAndInsufficientFunds_ShouldThrowInsufficientFundsException() throws SQLException {
        // Arrange
        var currency = new TECurrency("USD", "Dollar", "Dollars", "$", 2, BigDecimal.TEN, true);
        var fromAccount = new TEAccount(UUID.randomUUID(), "USD", BigDecimal.TEN);
        var toAccount = new TEAccount(UUID.randomUUID(), "USD", BigDecimal.ONE);
        when(currencyDataMock.getCurrency(any(), any())).thenReturn(Optional.of(currency));
        when(accountDataMock.getAccount(
            any(),
            eq(fromAccount.playerId()),
            eq(currency.code())
        )).thenReturn(Optional.of(fromAccount));
        when(accountDataMock.getAccount(
            any(),
            eq(toAccount.playerId()),
            eq(currency.code())
        )).thenReturn(Optional.of(toAccount));
        when(accountDataMock.withdraw(
            any(),
            eq(fromAccount.playerId()),
            eq(currency.code()),
            any(),
            eq(true)
        )).thenReturn(false);

        // Act/Assert
        assertThrows(
            InsufficientFundsException.class,
            () -> sut.transfer(fromAccount.playerId(), toAccount.playerId(), currency.code(), BigDecimal.TEN)
        );
    }

    @Test
    @Tag("Unit")
    public void transfer_WithCurrencyCodeAndSQLException_ShouldThrowDatabaseException() throws SQLException {
        // Arrange
        var currency = new TECurrency("USD", "Dollar", "Dollars", "$", 2, BigDecimal.TEN, true);
        var fromAccount = new TEAccount(UUID.randomUUID(), "USD", BigDecimal.TEN);
        var toAccount = new TEAccount(UUID.randomUUID(), "USD", BigDecimal.ONE);
        when(currencyDataMock.getCurrency(any(), any())).thenThrow(SQLException.class);

        // Act/Assert
        assertThrows(
            DatabaseException.class,
            () -> sut.transfer(fromAccount.playerId(), toAccount.playerId(), currency.code(), BigDecimal.TEN)
        );
    }

    @Test
    @Tag("Unit")
    public void transfer_WithDefaultCurrencyCodeAndSuccess_ShouldReturnTransferResult() throws SQLException {
        // Arrange
        var currency = new TECurrency("USD", "Dollar", "Dollars", "$", 2, BigDecimal.TEN, true);
        var fromAccount = new TEAccount(UUID.randomUUID(), "USD", BigDecimal.TEN);
        var toAccount = new TEAccount(UUID.randomUUID(), "USD", BigDecimal.ONE);
        when(currencyDataMock.getDefaultCurrency(any())).thenReturn(currency);
        when(currencyDataMock.getCurrency(any(), any())).thenReturn(Optional.of(currency));
        when(accountDataMock.getAccount(
            any(),
            eq(fromAccount.playerId()),
            eq(currency.code())
        )).thenReturn(Optional.of(fromAccount));
        when(accountDataMock.getAccount(
            any(),
            eq(toAccount.playerId()),
            eq(currency.code())
        )).thenReturn(Optional.of(toAccount));
        when(accountDataMock.withdraw(
            any(),
            eq(fromAccount.playerId()),
            eq(currency.code()),
            any(),
            eq(true)
        )).thenReturn(true);
        when(accountDataMock.deposit(
            any(),
            eq(toAccount.playerId()),
            eq(currency.code()),
            any()
        )).thenReturn(true);

        // Act
        var actual = sut.transfer(fromAccount.playerId(), toAccount.playerId(), BigDecimal.TEN);
        var expected = new TransferResult(currency, BigDecimal.TEN);

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void transfer_WithDefaultCurrencyCodeAndSQLException_ShouldThrowDatabaseException() throws SQLException {
        // Arrange
        var fromAccount = new TEAccount(UUID.randomUUID(), "USD", BigDecimal.TEN);
        var toAccount = new TEAccount(UUID.randomUUID(), "USD", BigDecimal.ONE);
        when(currencyDataMock.getDefaultCurrency(any())).thenThrow(SQLException.class);

        // Act/Assert
        assertThrows(
            DatabaseException.class,
            () -> sut.transfer(
                fromAccount.playerId(),
                toAccount.playerId(),
                BigDecimal.TEN
            )
        );
    }
}
