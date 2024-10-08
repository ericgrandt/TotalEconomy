package com.ericgrandt.totaleconomy.common.econ;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ericgrandt.totaleconomy.common.data.AccountData;
import com.ericgrandt.totaleconomy.common.data.BalanceData;
import com.ericgrandt.totaleconomy.common.data.CurrencyData;
import com.ericgrandt.totaleconomy.common.domain.Account;
import com.ericgrandt.totaleconomy.common.domain.Currency;
import com.ericgrandt.totaleconomy.common.dto.CurrencyDto;
import com.ericgrandt.totaleconomy.common.logger.CommonLogger;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.UUID;
import net.kyori.adventure.text.Component;
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
        Currency defaultCurrency = new Currency(
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
        CurrencyDto expected = new CurrencyDto(
            defaultCurrency.id(),
            defaultCurrency.nameSingular(),
            defaultCurrency.namePlural(),
            defaultCurrency.symbol(),
            defaultCurrency.numFractionDigits(),
            defaultCurrency.isDefault()
        );

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

        when(accountDataMock.createAccount(uuid)).thenReturn(true);

        CommonEconomy sut = new CommonEconomy(loggerMock, accountDataMock, balanceDataMock, currencyDataMock);

        // Act
        boolean actual = sut.createAccount(uuid);

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void createAccount_WithSqlException_ShouldReturnFalse() throws SQLException {
        // Arrange
        UUID uuid = UUID.randomUUID();

        when(accountDataMock.createAccount(uuid)).thenThrow(SQLException.class);

        CommonEconomy sut = new CommonEconomy(loggerMock, accountDataMock, balanceDataMock, currencyDataMock);

        // Act
        boolean actual = sut.createAccount(uuid);

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
    public void withdraw_WithSuccess_ShouldReturnSuccessfulTransactionResult() throws SQLException {
        // Arrange
        UUID uuid = UUID.randomUUID();
        int currencyId = 1;
        BigDecimal amount = BigDecimal.TEN;

        when(balanceDataMock.getBalance(uuid, currencyId)).thenReturn(BigDecimal.TEN);
        when(balanceDataMock.updateBalance(uuid, currencyId, BigDecimal.ZERO)).thenReturn(1);

        CommonEconomy sut = new CommonEconomy(loggerMock, accountDataMock, balanceDataMock, currencyDataMock);

        // Act
        TransactionResult actual = sut.withdraw(uuid, currencyId, amount, false);
        TransactionResult expected = new TransactionResult(
            TransactionResult.ResultType.SUCCESS,
            ""
        );

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void withdraw_WithAmountEqualToZero_ShouldReturnFailedTransactionResult() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        int currencyId = 1;
        BigDecimal amount = BigDecimal.ZERO;

        CommonEconomy sut = new CommonEconomy(loggerMock, accountDataMock, balanceDataMock, currencyDataMock);

        // Act
        TransactionResult actual = sut.withdraw(uuid, currencyId, amount, false);
        TransactionResult expected = new TransactionResult(
            TransactionResult.ResultType.FAILURE,
            "Invalid amount"
        );

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void withdraw_WithAmountLessThanZero_ShouldReturnFailedTransactionResult() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        int currencyId = 1;
        BigDecimal amount = BigDecimal.valueOf(-1);

        CommonEconomy sut = new CommonEconomy(loggerMock, accountDataMock, balanceDataMock, currencyDataMock);

        // Act
        TransactionResult actual = sut.withdraw(uuid, currencyId, amount, false);
        TransactionResult expected = new TransactionResult(
            TransactionResult.ResultType.FAILURE,
            "Invalid amount"
        );

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void withdraw_WithInsufficientFunds_ShouldReturnFailedTransactionResult() throws SQLException {
        // Arrange
        UUID uuid = UUID.randomUUID();
        int currencyId = 1;
        BigDecimal amount = BigDecimal.TEN;

        when(balanceDataMock.getBalance(uuid, currencyId)).thenReturn(BigDecimal.ONE);

        CommonEconomy sut = new CommonEconomy(loggerMock, accountDataMock, balanceDataMock, currencyDataMock);

        // Act
        TransactionResult actual = sut.withdraw(uuid, currencyId, amount, false);
        TransactionResult expected = new TransactionResult(
            TransactionResult.ResultType.FAILURE,
            "Insufficient funds"
        );

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void withdraw_WithSqlException_ShouldReturnFailedTransactionResult() throws SQLException {
        // Arrange
        UUID uuid = UUID.randomUUID();
        int currencyId = 1;
        BigDecimal amount = BigDecimal.TEN;

        when(balanceDataMock.getBalance(uuid, currencyId)).thenReturn(BigDecimal.TEN);
        when(balanceDataMock.updateBalance(uuid, currencyId, BigDecimal.ZERO)).thenThrow(SQLException.class);

        CommonEconomy sut = new CommonEconomy(loggerMock, accountDataMock, balanceDataMock, currencyDataMock);

        // Act
        TransactionResult actual = sut.withdraw(uuid, currencyId, amount, false);
        TransactionResult expected = new TransactionResult(
            TransactionResult.ResultType.FAILURE,
            "An error occurred. Please contact an administrator."
        );

        // Assert
        assertEquals(expected, actual);
        verify(loggerMock, times(1)).error(any(String.class), any(SQLException.class));
    }

    @Test
    @Tag("Unit")
    public void withdraw_WithAmountOfZeroAndAllowZeroTrue_ShouldReturnSuccessfulTransactionResult() throws SQLException {
        // Arrange
        UUID uuid = UUID.randomUUID();
        int currencyId = 1;
        BigDecimal amount = BigDecimal.ZERO;

        when(balanceDataMock.getBalance(uuid, currencyId)).thenReturn(BigDecimal.ZERO);
        when(balanceDataMock.updateBalance(uuid, currencyId, BigDecimal.ZERO)).thenReturn(1);

        CommonEconomy sut = new CommonEconomy(loggerMock, accountDataMock, balanceDataMock, currencyDataMock);

        // Act
        TransactionResult actual = sut.withdraw(uuid, currencyId, amount, true);
        TransactionResult expected = new TransactionResult(
            TransactionResult.ResultType.SUCCESS,
            ""
        );

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void withdraw_WithAmountLessThanZeroAndAllowZeroTrue_ShouldReturnFailedTransactionResult() throws SQLException {
        // Arrange
        UUID uuid = UUID.randomUUID();
        int currencyId = 1;
        BigDecimal amount = BigDecimal.valueOf(-1);

        CommonEconomy sut = new CommonEconomy(loggerMock, accountDataMock, balanceDataMock, currencyDataMock);

        // Act
        TransactionResult actual = sut.withdraw(uuid, currencyId, amount, true);
        TransactionResult expected = new TransactionResult(
            TransactionResult.ResultType.FAILURE,
            "Invalid amount"
        );

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void deposit_WithSuccess_ShouldReturnSuccessfulTransactionResult() throws SQLException {
        // Arrange
        UUID uuid = UUID.randomUUID();
        int currencyId = 1;
        BigDecimal amount = BigDecimal.TEN;

        when(balanceDataMock.getBalance(uuid, currencyId)).thenReturn(BigDecimal.TEN);
        when(balanceDataMock.updateBalance(uuid, currencyId, BigDecimal.valueOf(20))).thenReturn(1);

        CommonEconomy sut = new CommonEconomy(loggerMock, accountDataMock, balanceDataMock, currencyDataMock);

        // Act
        TransactionResult actual = sut.deposit(uuid, currencyId, amount, false);
        TransactionResult expected = new TransactionResult(
            TransactionResult.ResultType.SUCCESS,
            ""
        );

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void deposit_WithAmountEqualToZero_ShouldReturnFailedTransactionResult() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        int currencyId = 1;
        BigDecimal amount = BigDecimal.ZERO;

        CommonEconomy sut = new CommonEconomy(loggerMock, accountDataMock, balanceDataMock, currencyDataMock);

        // Act
        TransactionResult actual = sut.deposit(uuid, currencyId, amount, false);
        TransactionResult expected = new TransactionResult(
            TransactionResult.ResultType.FAILURE,
            "Invalid amount"
        );

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void deposit_WithAmountLessThanZero_ShouldReturnFailedTransactionResult() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        int currencyId = 1;
        BigDecimal amount = BigDecimal.valueOf(-1);

        CommonEconomy sut = new CommonEconomy(loggerMock, accountDataMock, balanceDataMock, currencyDataMock);

        // Act
        TransactionResult actual = sut.deposit(uuid, currencyId, amount, false);
        TransactionResult expected = new TransactionResult(
            TransactionResult.ResultType.FAILURE,
            "Invalid amount"
        );

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void deposit_WithSqlException_ShouldReturnFailedTransactionResult() throws SQLException {
        // Arrange
        UUID uuid = UUID.randomUUID();
        int currencyId = 1;
        BigDecimal amount = BigDecimal.TEN;

        when(balanceDataMock.getBalance(uuid, currencyId)).thenReturn(BigDecimal.TEN);
        when(balanceDataMock.updateBalance(uuid, currencyId, BigDecimal.valueOf(20))).thenThrow(SQLException.class);

        CommonEconomy sut = new CommonEconomy(loggerMock, accountDataMock, balanceDataMock, currencyDataMock);

        // Act
        TransactionResult actual = sut.deposit(uuid, currencyId, amount, false);
        TransactionResult expected = new TransactionResult(
            TransactionResult.ResultType.FAILURE,
            "An error occurred. Please contact an administrator."
        );

        // Assert
        assertEquals(expected, actual);
        verify(loggerMock, times(1)).error(any(String.class), any(SQLException.class));
    }

    @Test
    @Tag("Unit")
    public void deposit_WithAmountOfZeroAndAllowZeroTrue_ShouldReturnSuccessfulTransactionResult() throws SQLException {
        // Arrange
        UUID uuid = UUID.randomUUID();
        int currencyId = 1;
        BigDecimal amount = BigDecimal.ZERO;

        when(balanceDataMock.getBalance(uuid, currencyId)).thenReturn(BigDecimal.ZERO);
        when(balanceDataMock.updateBalance(uuid, currencyId, BigDecimal.ZERO)).thenReturn(1);

        CommonEconomy sut = new CommonEconomy(loggerMock, accountDataMock, balanceDataMock, currencyDataMock);

        // Act
        TransactionResult actual = sut.deposit(uuid, currencyId, amount, true);
        TransactionResult expected = new TransactionResult(
            TransactionResult.ResultType.SUCCESS,
            ""
        );

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void deposit_WithAmountLessThanZeroAndAllowZeroTrue_ShouldReturnFailedTransactionResult() throws SQLException {
        // Arrange
        UUID uuid = UUID.randomUUID();
        int currencyId = 1;
        BigDecimal amount = BigDecimal.valueOf(-1);

        CommonEconomy sut = new CommonEconomy(loggerMock, accountDataMock, balanceDataMock, currencyDataMock);

        // Act
        TransactionResult actual = sut.deposit(uuid, currencyId, amount, true);
        TransactionResult expected = new TransactionResult(
            TransactionResult.ResultType.FAILURE,
            "Invalid amount"
        );

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void transfer_WithSuccess_ShouldReturnTrue() throws SQLException {
        // Arrange
        UUID uuid = UUID.randomUUID();
        UUID toUuid = UUID.randomUUID();
        int currencyId = 1;
        BigDecimal amount = BigDecimal.TEN;

        Account toAccount = new Account(toUuid.toString(), null);

        when(accountDataMock.getAccount(toUuid)).thenReturn(toAccount);
        when(balanceDataMock.getBalance(uuid, currencyId)).thenReturn(BigDecimal.TEN);
        doNothing().when(balanceDataMock).transfer(uuid, toUuid, currencyId, amount);

        CommonEconomy sut = new CommonEconomy(loggerMock, accountDataMock, balanceDataMock, currencyDataMock);

        // Act
        TransactionResult actual = sut.transfer(uuid, toUuid, currencyId, amount);
        TransactionResult expected = new TransactionResult(
            TransactionResult.ResultType.SUCCESS,
            ""
        );

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void transfer_WithAmountEqualToZero_ShouldReturnFailedTransactionResult() throws SQLException {
        // Arrange
        UUID uuid = UUID.randomUUID();
        UUID toUuid = UUID.randomUUID();
        int currencyId = 1;
        BigDecimal amount = BigDecimal.ZERO;

        CommonEconomy sut = new CommonEconomy(loggerMock, accountDataMock, balanceDataMock, currencyDataMock);

        // Act
        TransactionResult actual = sut.transfer(uuid, toUuid, currencyId, amount);
        TransactionResult expected = new TransactionResult(
            TransactionResult.ResultType.FAILURE,
            "Invalid amount"
        );

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void transfer_WithAmountLessThanZero_ShouldReturnFailedTransactionResult() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        UUID toUuid = UUID.randomUUID();
        int currencyId = 1;
        BigDecimal amount = BigDecimal.valueOf(-1);

        CommonEconomy sut = new CommonEconomy(loggerMock, accountDataMock, balanceDataMock, currencyDataMock);

        // Act
        TransactionResult actual = sut.transfer(uuid, toUuid, currencyId, amount);
        TransactionResult expected = new TransactionResult(
            TransactionResult.ResultType.FAILURE,
            "Invalid amount"
        );

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void transfer_WithNoAccountForToUuid_ShouldReturnFailedTransactionResult() throws SQLException {
        // Arrange
        UUID uuid = UUID.randomUUID();
        UUID toUuid = UUID.randomUUID();
        int currencyId = 1;
        BigDecimal amount = BigDecimal.TEN;

        when(accountDataMock.getAccount(toUuid)).thenReturn(null);

        CommonEconomy sut = new CommonEconomy(loggerMock, accountDataMock, balanceDataMock, currencyDataMock);

        // Act
        TransactionResult actual = sut.transfer(uuid, toUuid, currencyId, amount);
        TransactionResult expected = new TransactionResult(
            TransactionResult.ResultType.FAILURE,
            "User not found"
        );

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void transfer_WithInsufficientFunds_ShouldReturnTrue() throws SQLException {
        // Arrange
        UUID uuid = UUID.randomUUID();
        UUID toUuid = UUID.randomUUID();
        int currencyId = 1;
        BigDecimal amount = BigDecimal.TEN;

        Account toAccount = new Account(toUuid.toString(), null);

        when(accountDataMock.getAccount(toUuid)).thenReturn(toAccount);
        when(balanceDataMock.getBalance(uuid, currencyId)).thenReturn(BigDecimal.ONE);

        CommonEconomy sut = new CommonEconomy(loggerMock, accountDataMock, balanceDataMock, currencyDataMock);

        // Act
        TransactionResult actual = sut.transfer(uuid, toUuid, currencyId, amount);
        TransactionResult expected = new TransactionResult(
            TransactionResult.ResultType.FAILURE,
            "Insufficient funds"
        );

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void transfer_WithSqlException_ShouldReturnFailedTransactionResult() throws SQLException {
        // Arrange
        UUID uuid = UUID.randomUUID();
        UUID toUuid = UUID.randomUUID();
        int currencyId = 1;
        BigDecimal amount = BigDecimal.TEN;

        Account toAccount = new Account(toUuid.toString(), null);

        when(accountDataMock.getAccount(toUuid)).thenReturn(toAccount);
        when(balanceDataMock.getBalance(uuid, currencyId)).thenReturn(BigDecimal.TEN);
        doThrow(SQLException.class).when(balanceDataMock).transfer(uuid, toUuid, currencyId, amount);

        CommonEconomy sut = new CommonEconomy(loggerMock, accountDataMock, balanceDataMock, currencyDataMock);

        // Act
        TransactionResult actual = sut.transfer(uuid, toUuid, currencyId, amount);
        TransactionResult expected = new TransactionResult(
            TransactionResult.ResultType.FAILURE,
            "An error occurred. Please contact an administrator."
        );

        // Assert
        assertEquals(expected, actual);
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
        BigDecimal expected = BigDecimal.ZERO;

        // Assert
        assertEquals(expected, actual);
        verify(loggerMock, times(1)).error(any(String.class), any(SQLException.class));
    }

    @Test
    @Tag("Unit")
    public void format_WithFractionalDigits_ShouldReturnFormattedAmountWithCorrectDecimalPlaces() {
        // Arrange
        CurrencyDto currencyDto = new CurrencyDto(
            1,
            "Dollar",
            "Dollars",
            "$",
            2,
            true
        );
        BigDecimal amount = BigDecimal.valueOf(10.1234);

        CommonEconomy sut = new CommonEconomy(loggerMock, accountDataMock, balanceDataMock, currencyDataMock);

        // Act
        Component actual = sut.format(currencyDto, amount);
        Component expected = Component.text("$10.12");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void format_WithNoFractionalDigits_ShouldReturnFormattedAmountWithNoDecimalPlaces() {
        // Arrange
        CurrencyDto currencyDto = new CurrencyDto(
            1,
            "Dollar",
            "Dollars",
            "$",
            0,
            true
        );
        BigDecimal amount = BigDecimal.valueOf(10.1234);

        CommonEconomy sut = new CommonEconomy(loggerMock, accountDataMock, balanceDataMock, currencyDataMock);

        // Act
        Component actual = sut.format(currencyDto, amount);
        Component expected = Component.text("$10");

        // Assert
        assertEquals(expected, actual);
    }
}
