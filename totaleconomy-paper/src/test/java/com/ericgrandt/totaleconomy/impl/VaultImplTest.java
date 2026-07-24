package com.ericgrandt.totaleconomy.impl;

import com.ericgrandt.totaleconomy.dto.DepositResult;
import com.ericgrandt.totaleconomy.dto.GetAccountBalanceResult;
import com.ericgrandt.totaleconomy.dto.WithdrawResult;
import com.ericgrandt.totaleconomy.exception.AccountNotFoundException;
import com.ericgrandt.totaleconomy.exception.CurrencyNotFoundException;
import com.ericgrandt.totaleconomy.exception.DatabaseException;
import com.ericgrandt.totaleconomy.exception.InsufficientFundsException;
import com.ericgrandt.totaleconomy.model.Account;
import com.ericgrandt.totaleconomy.model.Currency;
import com.ericgrandt.totaleconomy.model.TECurrency;
import com.ericgrandt.totaleconomy.service.EconomyService;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VaultImplTest {
    @Mock
    private Logger loggerMock;

    @Mock
    private EconomyService economyServiceMock;

    private Economy sut;

    private final Currency currency = new TECurrency("USD", "Dollar", "Dollars", "$", 2, BigDecimal.ONE, true);

    @BeforeEach
    public void setup() {
        when(economyServiceMock.getDefaultCurrency()).thenReturn(currency);
        sut = new VaultImpl(loggerMock, economyServiceMock);
    }

    @Test
    @Tag("Unit")
    public void isEnabled_ShouldReturnTrue() {
        // Arrange
        var sut = new VaultImpl(loggerMock, economyServiceMock);

        // Act
        var actual = sut.isEnabled();

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void getName_ShouldReturnName() {
        // Act
        var actual = sut.getName();
        var expected = "Total Economy";

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void hasBankSupport_ShouldReturnFalse() {
        // Act
        var actual = sut.hasBankSupport();

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void fractionalDigits_ShouldReturnCorrectNumber() {
        // Act
        var actual = sut.fractionalDigits();
        var expected = 2;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void currencyNamePlural_ShouldReturnName() {
        // Act
        var actual = sut.currencyNamePlural();
        var expected = "Dollars";

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void currencyNameSingular_ShouldReturnName() {
        // Act
        var actual = sut.currencyNameSingular();
        var expected = "Dollar";

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void hasAccount_WithAccount_ShouldReturnTrue() {
        // Arrange
        var playerMock = mock(Player.class);
        when(economyServiceMock.getAccountBalance(any())).thenReturn(new GetAccountBalanceResult(
            currency,
            BigDecimal.ONE
        ));

        // Act
        var actual = sut.hasAccount(playerMock);

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void hasAccount_WithCurrencyNotFoundException_ShouldReturnFalse() {
        // Arrange
        var playerMock = mock(Player.class);
        when(economyServiceMock.getAccountBalance(any())).thenThrow(CurrencyNotFoundException.class);

        // Act
        var actual = sut.hasAccount(playerMock);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void hasAccount_WithAccountNotFoundException_ShouldReturnFalse() {
        // Arrange
        var playerMock = mock(Player.class);
        when(economyServiceMock.getAccountBalance(any())).thenThrow(AccountNotFoundException.class);

        // Act
        var actual = sut.hasAccount(playerMock);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void hasAccount_WithDatabaseException_ShouldLogAndReturnFalse() {
        // Arrange
        var playerMock = mock(Player.class);
        when(economyServiceMock.getAccountBalance(any())).thenThrow(DatabaseException.class);

        // Act
        var actual = sut.hasAccount(playerMock);

        // Assert
        verify(loggerMock, times(1)).error(any(), any(DatabaseException.class));
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void getBalance_WithBalance_ShouldReturnAmountInBalance() {
        // Arrange
        var playerMock = mock(Player.class);
        when(economyServiceMock.getAccountBalance(any())).thenReturn(new GetAccountBalanceResult(
            currency,
            BigDecimal.ONE
        ));

        // Act
        var actual = sut.getBalance(playerMock);
        var expected = 1;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void getBalance_WithCurrencyNotFoundException_ShouldReturnZero() {
        // Arrange
        var playerMock = mock(Player.class);
        when(economyServiceMock.getAccountBalance(any())).thenThrow(CurrencyNotFoundException.class);

        // Act
        var actual = sut.getBalance(playerMock);
        var expected = 0;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void getBalance_WithAccountNotFoundException_ShouldReturnZero() {
        // Arrange
        var playerMock = mock(Player.class);
        when(economyServiceMock.getAccountBalance(any())).thenThrow(AccountNotFoundException.class);

        // Act
        var actual = sut.getBalance(playerMock);
        var expected = 0;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void getBalance_WithDatabaseException_ShouldLogAndReturnZero() {
        // Arrange
        var playerMock = mock(Player.class);
        when(economyServiceMock.getAccountBalance(any())).thenThrow(DatabaseException.class);

        // Act
        var actual = sut.getBalance(playerMock);
        var expected = 0;

        // Assert
        verify(loggerMock, times(1)).error(any(), any(DatabaseException.class));
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void has_WithSufficientFunds_ShouldReturnTrue() {
        // Arrange
        var playerMock = mock(Player.class);
        when(economyServiceMock.getAccountBalance(any())).thenReturn(new GetAccountBalanceResult(
            currency,
            BigDecimal.TEN
        ));

        // Act
        var actual = sut.has(playerMock, 1);

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void has_WithExactBalance_ShouldReturnTrue() {
        // Arrange
        var playerMock = mock(Player.class);
        when(economyServiceMock.getAccountBalance(any())).thenReturn(new GetAccountBalanceResult(
            currency,
            BigDecimal.TEN
        ));

        // Act
        var actual = sut.has(playerMock, 10);

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void has_WithInsufficientFunds_ShouldReturnFalse() {
        // Arrange
        var playerMock = mock(Player.class);
        when(economyServiceMock.getAccountBalance(any())).thenReturn(new GetAccountBalanceResult(
            currency,
            BigDecimal.TEN
        ));

        // Act
        var actual = sut.has(playerMock, 20);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void has_WithCurrencyNotFoundException_ShouldReturnFalse() {
        // Arrange
        var playerMock = mock(Player.class);
        when(economyServiceMock.getAccountBalance(any())).thenThrow(CurrencyNotFoundException.class);

        // Act
        var actual = sut.has(playerMock, 10);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void has_WithAccountNotFoundException_ShouldReturnFalse() {
        // Arrange
        var playerMock = mock(Player.class);
        when(economyServiceMock.getAccountBalance(any())).thenThrow(AccountNotFoundException.class);

        // Act
        var actual = sut.has(playerMock, 10);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void has_WithDatabaseException_ShouldLogAndReturnFalse() {
        // Arrange
        var playerMock = mock(Player.class);
        when(economyServiceMock.getAccountBalance(any())).thenThrow(DatabaseException.class);

        // Act
        var actual = sut.has(playerMock, 10);

        // Assert
        verify(loggerMock, times(1)).error(any(), any(DatabaseException.class));
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void withdrawPlayer_WithSuccess_ShouldReturnCorrectEconomyResponse() {
        // Arrange
        var playerMock = mock(Player.class);
        when(economyServiceMock.withdraw(any(), any())).thenReturn(new WithdrawResult(
            currency,
            BigDecimal.ONE,
            BigDecimal.TEN
        ));

        // Act
        var actual = sut.withdrawPlayer(playerMock, 1);
        var expected = new EconomyResponse(1, 10, EconomyResponse.ResponseType.SUCCESS, "");

        // Assert
        assertEquals(expected.amount, actual.amount);
        assertEquals(expected.balance, actual.balance);
        assertEquals(expected.type, actual.type);
        assertEquals(expected.errorMessage, actual.errorMessage);
    }

    @Test
    @Tag("Unit")
    public void withdrawPlayer_WithCurrencyNotFound_ShouldReturnCorrectEconomyResponse() {
        // Arrange
        var playerMock = mock(Player.class);
        when(economyServiceMock.withdraw(any(), any())).thenThrow(CurrencyNotFoundException.class);

        // Act
        var actual = sut.withdrawPlayer(playerMock, 1);
        var expected = new EconomyResponse(1, 0, EconomyResponse.ResponseType.FAILURE, "currency not found");

        // Assert
        assertEquals(expected.amount, actual.amount);
        assertEquals(expected.balance, actual.balance);
        assertEquals(expected.type, actual.type);
        assertEquals(expected.errorMessage, actual.errorMessage);
    }

    @Test
    @Tag("Unit")
    public void withdrawPlayer_WithAccountNotFound_ShouldReturnCorrectEconomyResponse() {
        // Arrange
        var playerMock = mock(Player.class);
        when(economyServiceMock.withdraw(any(), any())).thenThrow(AccountNotFoundException.class);

        // Act
        var actual = sut.withdrawPlayer(playerMock, 1);
        var expected = new EconomyResponse(1, 0, EconomyResponse.ResponseType.FAILURE, "account not found");

        // Assert
        assertEquals(expected.amount, actual.amount);
        assertEquals(expected.balance, actual.balance);
        assertEquals(expected.type, actual.type);
        assertEquals(expected.errorMessage, actual.errorMessage);
    }

    @Test
    @Tag("Unit")
    public void withdrawPlayer_WithInsufficientFunds_ShouldReturnCorrectEconomyResponse() {
        // Arrange
        var playerMock = mock(Player.class);
        when(economyServiceMock.withdraw(any(), any())).thenThrow(InsufficientFundsException.class);

        // Act
        var actual = sut.withdrawPlayer(playerMock, 1);
        var expected = new EconomyResponse(1, 0, EconomyResponse.ResponseType.FAILURE, "insufficient funds");

        // Assert
        assertEquals(expected.amount, actual.amount);
        assertEquals(expected.balance, actual.balance);
        assertEquals(expected.type, actual.type);
        assertEquals(expected.errorMessage, actual.errorMessage);
    }

    @Test
    @Tag("Unit")
    public void withdrawPlayer_WithDatabaseException_ShouldLogAndReturnCorrectEconomyResponse() {
        // Arrange
        var playerMock = mock(Player.class);
        when(economyServiceMock.withdraw(any(), any())).thenThrow(DatabaseException.class);

        // Act
        var actual = sut.withdrawPlayer(playerMock, 1);
        var expected = new EconomyResponse(1, 0, EconomyResponse.ResponseType.FAILURE, "internal server error");

        // Assert
        verify(loggerMock, times(1)).error(any(), any(DatabaseException.class));
        assertEquals(expected.amount, actual.amount);
        assertEquals(expected.balance, actual.balance);
        assertEquals(expected.type, actual.type);
        assertEquals(expected.errorMessage, actual.errorMessage);
    }

    @Test
    @Tag("Unit")
    public void depositPlayer_WithSuccess_ShouldReturnCorrectEconomyResponse() {
        // Arrange
        var playerMock = mock(Player.class);
        when(economyServiceMock.deposit(any(), any())).thenReturn(new DepositResult(
            currency,
            BigDecimal.ONE,
            BigDecimal.TEN
        ));

        // Act
        var actual = sut.depositPlayer(playerMock, 1);
        var expected = new EconomyResponse(1, 10, EconomyResponse.ResponseType.SUCCESS, "");

        // Assert
        assertEquals(expected.amount, actual.amount);
        assertEquals(expected.balance, actual.balance);
        assertEquals(expected.type, actual.type);
        assertEquals(expected.errorMessage, actual.errorMessage);
    }

    @Test
    @Tag("Unit")
    public void depositPlayer_WithAccountNotFound_ShouldReturnCorrectEconomyResponse() {
        // Arrange
        var playerMock = mock(Player.class);
        when(economyServiceMock.deposit(any(), any())).thenThrow(AccountNotFoundException.class);

        // Act
        var actual = sut.depositPlayer(playerMock, 1);
        var expected = new EconomyResponse(1, 0, EconomyResponse.ResponseType.FAILURE, "account not found");

        // Assert
        assertEquals(expected.amount, actual.amount);
        assertEquals(expected.balance, actual.balance);
        assertEquals(expected.type, actual.type);
        assertEquals(expected.errorMessage, actual.errorMessage);
    }

    @Test
    @Tag("Unit")
    public void depositPlayer_WithCurrencyNotFound_ShouldReturnCorrectEconomyResponse() {
        // Arrange
        var playerMock = mock(Player.class);
        when(economyServiceMock.deposit(any(), any())).thenThrow(CurrencyNotFoundException.class);

        // Act
        var actual = sut.depositPlayer(playerMock, 1);
        var expected = new EconomyResponse(1, 0, EconomyResponse.ResponseType.FAILURE, "currency not found");

        // Assert
        assertEquals(expected.amount, actual.amount);
        assertEquals(expected.balance, actual.balance);
        assertEquals(expected.type, actual.type);
        assertEquals(expected.errorMessage, actual.errorMessage);
    }

    @Test
    @Tag("Unit")
    public void depositPlayer_WithDatabaseException_ShouldLogAndReturnCorrectEconomyResponse() {
        // Arrange
        var playerMock = mock(Player.class);
        when(economyServiceMock.deposit(any(), any())).thenThrow(DatabaseException.class);

        // Act
        var actual = sut.depositPlayer(playerMock, 1);
        var expected = new EconomyResponse(1, 0, EconomyResponse.ResponseType.FAILURE, "internal server error");

        // Assert
        verify(loggerMock, times(1)).error(any(), any(DatabaseException.class));
        assertEquals(expected.amount, actual.amount);
        assertEquals(expected.balance, actual.balance);
        assertEquals(expected.type, actual.type);
        assertEquals(expected.errorMessage, actual.errorMessage);
    }

    @Test
    @Tag("Unit")
    public void createPlayerAccount_WithSuccess_ShouldReturnTrue() {
        // Arrange
        var playerMock = mock(Player.class);
        when(economyServiceMock.createAccount(any(), any())).thenReturn(mock(Account.class));

        // Act
        var actual = sut.createPlayerAccount(playerMock);

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void createPlayerAccount_WithCurrencyNotFound_ShouldReturnFalse() {
        // Arrange
        var playerMock = mock(Player.class);
        when(economyServiceMock.createAccount(any(), any())).thenThrow(CurrencyNotFoundException.class);

        // Act/Assert
        var actual = sut.createPlayerAccount(playerMock);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void createPlayerAccount_WithDatabaseException_ShouldLogAndReturnFalse() {
        // Arrange
        var playerMock = mock(Player.class);
        when(economyServiceMock.createAccount(any(), any())).thenThrow(DatabaseException.class);

        // Act/Assert
        var actual = sut.createPlayerAccount(playerMock);

        // Assert
        verify(loggerMock, times(1)).error(any(), any(DatabaseException.class));
        assertFalse(actual);
    }
}
