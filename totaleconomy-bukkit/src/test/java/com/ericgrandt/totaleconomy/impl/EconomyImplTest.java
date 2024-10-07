package com.ericgrandt.totaleconomy.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ericgrandt.totaleconomy.common.dto.CurrencyDto;
import com.ericgrandt.totaleconomy.common.econ.CommonEconomy;
import com.ericgrandt.totaleconomy.common.econ.TransactionResult;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class EconomyImplTest {
    @Mock
    private CommonEconomy economyMock;

    private final CurrencyDto defaultCurrency = new CurrencyDto(
        1,
        "singular",
        "plural",
        "$",
        2,
        true
    );

    @Test
    @Tag("Unit")
    public void isEnabled_WhenTrue_ShouldReturnTrue() {
        // Arrange
        EconomyImpl sut = new EconomyImpl(true, null, null);

        // Act
        boolean actual = sut.isEnabled();

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void isEnabled_WhenFalse_ShouldReturnFalse() {
        // Arrange
        EconomyImpl sut = new EconomyImpl(false, null, null);

        // Act
        boolean actual = sut.isEnabled();

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void getName_ShouldReturnName() {
        // Arrange
        EconomyImpl sut = new EconomyImpl(true, null, null);

        // Act
        String actual = sut.getName();
        String expected = "Total Economy";

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void hasBankSupport_ShouldReturnFalse() {
        // Arrange
        EconomyImpl sut = new EconomyImpl(true, null, null);

        // Act
        boolean actual = sut.hasBankSupport();

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void fractionalDigits_WithDefaultCurrency_ShouldReturnFractionalDigits() {
        // Arrange
        EconomyImpl sut = new EconomyImpl(true, defaultCurrency, null);

        // Act
        int actual = sut.fractionalDigits();
        int expected = 2;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void currencyNamePlural_WithDefaultCurrency_ShouldReturnNamePlural() {
        // Arrange
        EconomyImpl sut = new EconomyImpl(true, defaultCurrency, null);

        // Act
        String actual = sut.currencyNamePlural();
        String expected = "plural";

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void currencyNameSingular_WithDefaultCurrency_ShouldReturnNameSingular() {
        // Arrange
        EconomyImpl sut = new EconomyImpl(true, defaultCurrency, null);

        // Act
        String actual = sut.currencyNameSingular();
        String expected = "singular";

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void format_ShouldReturnFormattedAmount() {
        // Arrange
        when(economyMock.format(defaultCurrency, BigDecimal.valueOf(123.45))).thenReturn(
            Component.text("$123.45")
        );

        EconomyImpl sut = new EconomyImpl(true, defaultCurrency, economyMock);

        // Act
        String actual = sut.format(123.45);
        String expected = "$123.45";

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void hasAccount_WithPlayerHavingAnAccount_ShouldReturnTrue() {
        // Arrange
        UUID playerUUID = UUID.randomUUID();
        OfflinePlayer playerMock = mock(OfflinePlayer.class);
        when(playerMock.getUniqueId()).thenReturn(playerUUID);
        when(economyMock.hasAccount(playerUUID)).thenReturn(true);

        EconomyImpl sut = new EconomyImpl(true, defaultCurrency, economyMock);

        // Act
        boolean actual = sut.hasAccount(playerMock);

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void hasAccount_World_WithPlayerHavingAnAccount_ShouldReturnTrue() {
        // Arrange
        UUID playerUUID = UUID.randomUUID();
        OfflinePlayer playerMock = mock(OfflinePlayer.class);
        when(playerMock.getUniqueId()).thenReturn(playerUUID);
        when(economyMock.hasAccount(playerUUID)).thenReturn(true);

        EconomyImpl sut = new EconomyImpl(true, defaultCurrency, economyMock);

        // Act
        boolean actual = sut.hasAccount(playerMock, "randomWorld");

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void createPlayerAccount_WithSuccessfulCallToCreateAccount_ShouldReturnTrue() throws SQLException {
        // Arrange
        UUID playerUUID = UUID.randomUUID();
        OfflinePlayer playerMock = mock(OfflinePlayer.class);
        when(playerMock.getUniqueId()).thenReturn(playerUUID);
        when(economyMock.createAccount(playerUUID)).thenReturn(true);

        EconomyImpl sut = new EconomyImpl(true, defaultCurrency, economyMock);

        // Act
        boolean actual = sut.createPlayerAccount(playerMock);

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void createPlayerAccount_World_WithSuccessfulCallToCreateAccount_ShouldReturnTrue() throws SQLException {
        // Arrange
        UUID playerUUID = UUID.randomUUID();
        OfflinePlayer playerMock = mock(OfflinePlayer.class);
        when(playerMock.getUniqueId()).thenReturn(playerUUID);
        when(economyMock.createAccount(playerUUID)).thenReturn(true);

        EconomyImpl sut = new EconomyImpl(true, defaultCurrency, economyMock);

        // Act
        boolean actual = sut.createPlayerAccount(playerMock, "randomWorld");

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void getBalance_WithBalanceFound_ShouldReturnBalance() throws SQLException {
        // Arrange
        UUID playerUUID = UUID.randomUUID();
        OfflinePlayer playerMock = mock(OfflinePlayer.class);
        when(playerMock.getUniqueId()).thenReturn(playerUUID);
        when(economyMock.getBalance(playerUUID, 1)).thenReturn(BigDecimal.TEN);

        EconomyImpl sut = new EconomyImpl(true, defaultCurrency, economyMock);

        // Act
        double actual = sut.getBalance(playerMock);
        double expected = 10;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void getBalance_World_WithBalanceFound_ShouldReturnBalance() throws SQLException {
        // Arrange
        UUID playerUUID = UUID.randomUUID();
        OfflinePlayer playerMock = mock(OfflinePlayer.class);
        when(playerMock.getUniqueId()).thenReturn(playerUUID);
        when(economyMock.getBalance(playerUUID, 1)).thenReturn(BigDecimal.TEN);

        EconomyImpl sut = new EconomyImpl(true, defaultCurrency, economyMock);

        // Act
        double actual = sut.getBalance(playerMock, "randomWorld");
        double expected = 10;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void has_WithAmountLessThanBalance_ShouldReturnTrue() {
        // Arrange
        UUID playerUUID = UUID.randomUUID();
        OfflinePlayer playerMock = mock(OfflinePlayer.class);
        when(playerMock.getUniqueId()).thenReturn(playerUUID);
        when(economyMock.getBalance(playerUUID, 1)).thenReturn(BigDecimal.valueOf(100));

        EconomyImpl sut = new EconomyImpl(true, defaultCurrency, economyMock);

        // Act
        boolean actual = sut.has(playerMock, 10);

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void has_WithAmountEqualToBalance_ShouldReturnTrue() {
        // Arrange
        UUID playerUUID = UUID.randomUUID();
        OfflinePlayer playerMock = mock(OfflinePlayer.class);
        when(playerMock.getUniqueId()).thenReturn(playerUUID);
        when(economyMock.getBalance(playerUUID, 1)).thenReturn(BigDecimal.TEN);

        EconomyImpl sut = new EconomyImpl(true, defaultCurrency, economyMock);

        // Act
        boolean actual = sut.has(playerMock, 10);

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void has_WithAmountLessThanBalance_ShouldReturnFalse() {
        // Arrange
        UUID playerUUID = UUID.randomUUID();
        OfflinePlayer playerMock = mock(OfflinePlayer.class);
        when(playerMock.getUniqueId()).thenReturn(playerUUID);
        when(economyMock.getBalance(playerUUID, 1)).thenReturn(BigDecimal.TEN);

        EconomyImpl sut = new EconomyImpl(true, defaultCurrency, economyMock);

        // Act
        boolean actual = sut.has(playerMock, 11);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void has_World_WithAmountLessThanBalance_ShouldReturnTrue() {
        // Arrange
        UUID playerUUID = UUID.randomUUID();
        OfflinePlayer playerMock = mock(OfflinePlayer.class);
        when(playerMock.getUniqueId()).thenReturn(playerUUID);
        when(economyMock.getBalance(playerUUID, 1)).thenReturn(BigDecimal.valueOf(100));

        EconomyImpl sut = new EconomyImpl(true, defaultCurrency, economyMock);

        // Act
        boolean actual = sut.has(playerMock, "randomWorld", 10);

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void has_World_WithAmountEqualToBalance_ShouldReturnTrue() {
        // Arrange
        UUID playerUUID = UUID.randomUUID();
        OfflinePlayer playerMock = mock(OfflinePlayer.class);
        when(playerMock.getUniqueId()).thenReturn(playerUUID);
        when(economyMock.getBalance(playerUUID, 1)).thenReturn(BigDecimal.TEN);

        EconomyImpl sut = new EconomyImpl(true, defaultCurrency, economyMock);

        // Act
        boolean actual = sut.has(playerMock, "randomWorld", 10);

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void has_World_WithAmountLessThanBalance_ShouldReturnFalse() {
        // Arrange
        UUID playerUUID = UUID.randomUUID();
        OfflinePlayer playerMock = mock(OfflinePlayer.class);
        when(playerMock.getUniqueId()).thenReturn(playerUUID);
        when(economyMock.getBalance(playerUUID, 1)).thenReturn(BigDecimal.TEN);

        EconomyImpl sut = new EconomyImpl(true, defaultCurrency, economyMock);

        // Act
        boolean actual = sut.has(playerMock, "randomWorld", 11);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void withdrawPlayer_WithSuccessfulWithdraw_ShouldReturnCorrectEconomyResponse() {
        // Arrange
        UUID playerUUID = UUID.randomUUID();
        OfflinePlayer playerMock = mock(OfflinePlayer.class);
        when(playerMock.getUniqueId()).thenReturn(playerUUID);
        when(economyMock.withdraw(playerUUID, 1, BigDecimal.valueOf(10D), true)).thenReturn(
            new TransactionResult(TransactionResult.ResultType.SUCCESS, "")
        );

        EconomyImpl sut = new EconomyImpl(true, defaultCurrency, economyMock);

        // Act
        EconomyResponse actual = sut.withdrawPlayer(playerMock, 10);
        EconomyResponse expected = new EconomyResponse(
            10,
            0,
            EconomyResponse.ResponseType.SUCCESS,
            ""
        );

        // Assert
        assertEquals(expected.amount, actual.amount);
        assertEquals(expected.balance, actual.balance);
        assertEquals(expected.type, actual.type);
        assertEquals(expected.errorMessage, actual.errorMessage);
    }

    @Test
    @Tag("Unit")
    public void withdrawPlayer_WithFailedWithdraw_ShouldReturnCorrectEconomyResponse() {
        // Arrange
        UUID playerUUID = UUID.randomUUID();
        OfflinePlayer playerMock = mock(OfflinePlayer.class);
        when(playerMock.getUniqueId()).thenReturn(playerUUID);
        when(economyMock.withdraw(playerUUID, 1, BigDecimal.valueOf(10D), true)).thenReturn(
            new TransactionResult(TransactionResult.ResultType.FAILURE, "Failed")
        );

        EconomyImpl sut = new EconomyImpl(true, defaultCurrency, economyMock);

        // Act
        EconomyResponse actual = sut.withdrawPlayer(playerMock, 10);
        EconomyResponse expected = new EconomyResponse(
            10,
            0,
            EconomyResponse.ResponseType.FAILURE,
            "Failed"
        );

        // Assert
        assertEquals(expected.amount, actual.amount);
        assertEquals(expected.balance, actual.balance);
        assertEquals(expected.type, actual.type);
        assertEquals(expected.errorMessage, actual.errorMessage);
    }

    @Test
    @Tag("Unit")
    public void withdrawPlayer_World_WithSuccessfulWithdraw_ShouldReturnCorrectEconomyResponse() {
        // Arrange
        UUID playerUUID = UUID.randomUUID();
        OfflinePlayer playerMock = mock(OfflinePlayer.class);
        when(playerMock.getUniqueId()).thenReturn(playerUUID);
        when(economyMock.withdraw(playerUUID, 1, BigDecimal.valueOf(10D), true)).thenReturn(
            new TransactionResult(TransactionResult.ResultType.SUCCESS, "")
        );

        EconomyImpl sut = new EconomyImpl(true, defaultCurrency, economyMock);

        // Act
        EconomyResponse actual = sut.withdrawPlayer(playerMock, "randomWorld", 10);
        EconomyResponse expected = new EconomyResponse(
            10,
            0,
            EconomyResponse.ResponseType.SUCCESS,
            ""
        );

        // Assert
        assertEquals(expected.amount, actual.amount);
        assertEquals(expected.balance, actual.balance);
        assertEquals(expected.type, actual.type);
        assertEquals(expected.errorMessage, actual.errorMessage);
    }

    @Test
    @Tag("Unit")
    public void withdrawPlayer_World_WithFailedWithdraw_ShouldReturnCorrectEconomyResponse() {
        // Arrange
        UUID playerUUID = UUID.randomUUID();
        OfflinePlayer playerMock = mock(OfflinePlayer.class);
        when(playerMock.getUniqueId()).thenReturn(playerUUID);
        when(economyMock.withdraw(playerUUID, 1, BigDecimal.valueOf(10D), true)).thenReturn(
            new TransactionResult(TransactionResult.ResultType.FAILURE, "Failed")
        );

        EconomyImpl sut = new EconomyImpl(true, defaultCurrency, economyMock);

        // Act
        EconomyResponse actual = sut.withdrawPlayer(playerMock, "randomWorld", 10);
        EconomyResponse expected = new EconomyResponse(
            10,
            0,
            EconomyResponse.ResponseType.FAILURE,
            "Failed"
        );

        // Assert
        assertEquals(expected.amount, actual.amount);
        assertEquals(expected.balance, actual.balance);
        assertEquals(expected.type, actual.type);
        assertEquals(expected.errorMessage, actual.errorMessage);
    }

    @Test
    @Tag("Unit")
    public void depositPlayer_WithSuccessfulDeposit_ShouldReturnCorrectEconomyResponse() {
        // Arrange
        UUID playerUUID = UUID.randomUUID();
        OfflinePlayer playerMock = mock(OfflinePlayer.class);
        when(playerMock.getUniqueId()).thenReturn(playerUUID);
        when(economyMock.deposit(playerUUID, 1, BigDecimal.valueOf(10D), true)).thenReturn(
            new TransactionResult(TransactionResult.ResultType.SUCCESS, "")
        );

        EconomyImpl sut = new EconomyImpl(true, defaultCurrency, economyMock);

        // Act
        EconomyResponse actual = sut.depositPlayer(playerMock, 10);
        EconomyResponse expected = new EconomyResponse(
            10,
            0,
            EconomyResponse.ResponseType.SUCCESS,
            ""
        );

        // Assert
        assertEquals(expected.amount, actual.amount);
        assertEquals(expected.balance, actual.balance);
        assertEquals(expected.type, actual.type);
        assertEquals(expected.errorMessage, actual.errorMessage);
    }

    @Test
    @Tag("Unit")
    public void depositPlayer_WithFailedDeposit_ShouldReturnCorrectEconomyResponse() {
        // Arrange
        UUID playerUUID = UUID.randomUUID();
        OfflinePlayer playerMock = mock(OfflinePlayer.class);
        when(playerMock.getUniqueId()).thenReturn(playerUUID);
        when(economyMock.deposit(playerUUID, 1, BigDecimal.valueOf(10D), true)).thenReturn(
            new TransactionResult(TransactionResult.ResultType.FAILURE, "Failed")
        );

        EconomyImpl sut = new EconomyImpl(true, defaultCurrency, economyMock);

        // Act
        EconomyResponse actual = sut.depositPlayer(playerMock, 10);
        EconomyResponse expected = new EconomyResponse(
            10,
            0,
            EconomyResponse.ResponseType.FAILURE,
            "Failed"
        );

        // Assert
        assertEquals(expected.amount, actual.amount);
        assertEquals(expected.balance, actual.balance);
        assertEquals(expected.type, actual.type);
        assertEquals(expected.errorMessage, actual.errorMessage);
    }

    @Test
    @Tag("Unit")
    public void depositPlayer_World_WithSuccessfulDeposit_ShouldReturnCorrectEconomyResponse() {
        // Arrange
        UUID playerUUID = UUID.randomUUID();
        OfflinePlayer playerMock = mock(OfflinePlayer.class);
        when(playerMock.getUniqueId()).thenReturn(playerUUID);
        when(economyMock.deposit(playerUUID, 1, BigDecimal.valueOf(10D), true)).thenReturn(
            new TransactionResult(TransactionResult.ResultType.SUCCESS, "")
        );

        EconomyImpl sut = new EconomyImpl(true, defaultCurrency, economyMock);

        // Act
        EconomyResponse actual = sut.depositPlayer(playerMock, "randomWorld", 10);
        EconomyResponse expected = new EconomyResponse(
            10,
            0,
            EconomyResponse.ResponseType.SUCCESS,
            ""
        );

        // Assert
        assertEquals(expected.amount, actual.amount);
        assertEquals(expected.balance, actual.balance);
        assertEquals(expected.type, actual.type);
        assertEquals(expected.errorMessage, actual.errorMessage);
    }

    @Test
    @Tag("Unit")
    public void depositPlayer_World_WithFailedDeposit_ShouldReturnCorrectEconomyResponse() {
        // Arrange
        UUID playerUUID = UUID.randomUUID();
        OfflinePlayer playerMock = mock(OfflinePlayer.class);
        when(playerMock.getUniqueId()).thenReturn(playerUUID);
        when(economyMock.deposit(playerUUID, 1, BigDecimal.valueOf(10D), true)).thenReturn(
            new TransactionResult(TransactionResult.ResultType.FAILURE, "Failed")
        );

        EconomyImpl sut = new EconomyImpl(true, defaultCurrency, economyMock);

        // Act
        EconomyResponse actual = sut.depositPlayer(playerMock, "randomWorld", 10);
        EconomyResponse expected = new EconomyResponse(
            10,
            0,
            EconomyResponse.ResponseType.FAILURE,
            "Failed"
        );

        // Assert
        assertEquals(expected.amount, actual.amount);
        assertEquals(expected.balance, actual.balance);
        assertEquals(expected.type, actual.type);
        assertEquals(expected.errorMessage, actual.errorMessage);
    }
}
