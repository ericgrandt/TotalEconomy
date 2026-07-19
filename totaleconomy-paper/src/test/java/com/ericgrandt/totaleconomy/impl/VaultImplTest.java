package com.ericgrandt.totaleconomy.impl;

import com.ericgrandt.totaleconomy.dto.GetAccountBalanceResult;
import com.ericgrandt.totaleconomy.exception.AccountNotFoundException;
import com.ericgrandt.totaleconomy.exception.DatabaseException;
import com.ericgrandt.totaleconomy.model.Currency;
import com.ericgrandt.totaleconomy.model.TECurrency;
import com.ericgrandt.totaleconomy.service.EconomyService;
import net.milkbowl.vault.economy.Economy;
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
    public void has_WithAccountNotFoundException_ShouldReturnZero() {
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
    public void has_WithDatabaseException_ShouldLogAndReturnZero() {
        // Arrange
        var playerMock = mock(Player.class);
        when(economyServiceMock.getAccountBalance(any())).thenThrow(DatabaseException.class);

        // Act
        var actual = sut.has(playerMock, 10);

        // Assert
        verify(loggerMock, times(1)).error(any(), any(DatabaseException.class));
        assertFalse(actual);
    }
}
