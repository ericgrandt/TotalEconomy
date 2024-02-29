package com.ericgrandt.totaleconomy.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ericgrandt.totaleconomy.common.data.AccountData;
import com.ericgrandt.totaleconomy.common.data.BalanceData;
import com.ericgrandt.totaleconomy.common.data.dto.AccountDto;
import com.ericgrandt.totaleconomy.common.data.dto.CurrencyDto;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private Logger loggerMock;

    @Mock
    private AccountData accountDataMock;

    @Mock
    private BalanceData balanceDataMock;

    @Test
    @Tag("Unit")
    public void isEnabled_WhenTrue_ShouldReturnTrue() {
        // Arrange
        EconomyImpl sut = new EconomyImpl(loggerMock, true, null, accountDataMock, balanceDataMock);

        // Act
        boolean actual = sut.isEnabled();

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void isEnabled_WhenFalse_ShouldReturnFalse() {
        // Arrange
        EconomyImpl sut = new EconomyImpl(loggerMock, false, null, accountDataMock, balanceDataMock);

        // Act
        boolean actual = sut.isEnabled();

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void getName_ShouldReturnName() {
        // Arrange
        EconomyImpl sut = new EconomyImpl(loggerMock, true, null, accountDataMock, balanceDataMock);

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
        EconomyImpl sut = new EconomyImpl(loggerMock, true, null, accountDataMock, balanceDataMock);

        // Act
        boolean actual = sut.hasBankSupport();

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void fractionalDigits_WithDefaultCurrency_ShouldReturnFractionalDigits() {
        // Arrange
        CurrencyDto defaultCurrency = new CurrencyDto(
            1,
            "singular",
            "plural",
            "$",
            1,
            true
        );

        EconomyImpl sut = new EconomyImpl(loggerMock, true, defaultCurrency, accountDataMock, balanceDataMock);

        // Act
        int actual = sut.fractionalDigits();
        int expected = 1;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void format_WithDefaultCurrency_ShouldReturnFormattedAmountWithSymbol() {
        // Arrange
        CurrencyDto defaultCurrency = new CurrencyDto(
            1,
            "singular",
            "plural",
            "$",
            2,
            true
        );

        EconomyImpl sut = new EconomyImpl(loggerMock, true, defaultCurrency, accountDataMock, balanceDataMock);

        // Act
        String actual = sut.format(123.45);
        String expected = "$123.45";

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void format_WithDefaultCurrencyHavingOneFractionalDigit_ShouldReturnFormattedAmountWithOneDigit() {
        // Arrange
        CurrencyDto defaultCurrency = new CurrencyDto(
            1,
            "singular",
            "plural",
            "$",
            1,
            true
        );

        EconomyImpl sut = new EconomyImpl(loggerMock, true, defaultCurrency, accountDataMock, balanceDataMock);

        // Act
        String actual = sut.format(123.45);
        String expected = "$123.4";

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void currencyNamePlural_WithDefaultCurrency_ShouldReturnNamePlural() {
        // Arrange
        CurrencyDto defaultCurrency = new CurrencyDto(
            1,
            "singular",
            "plural",
            "$",
            1,
            true
        );

        EconomyImpl sut = new EconomyImpl(loggerMock, true, defaultCurrency, accountDataMock, balanceDataMock);

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
        CurrencyDto defaultCurrency = new CurrencyDto(
            1,
            "singular",
            "plural",
            "$",
            1,
            true
        );

        EconomyImpl sut = new EconomyImpl(loggerMock, true, defaultCurrency, accountDataMock, balanceDataMock);

        // Act
        String actual = sut.currencyNameSingular();
        String expected = "singular";

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void hasAccount_WithPlayerHavingAnAccount_ShouldReturnTrue() throws SQLException {
        // Arrange
        UUID playerUUID = UUID.randomUUID();
        OfflinePlayer playerMock = mock(OfflinePlayer.class);
        AccountDto accountDto = new AccountDto("", null);
        when(playerMock.getUniqueId()).thenReturn(playerUUID);
        when(accountDataMock.getAccount(playerUUID)).thenReturn(accountDto);

        EconomyImpl sut = new EconomyImpl(loggerMock, true, null, accountDataMock, balanceDataMock);

        // Act
        boolean actual = sut.hasAccount(playerMock);

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void hasAccount_WithPlayerNotHavingAnAccount_ShouldReturnFalse() throws SQLException {
        // Arrange
        UUID playerUUID = UUID.randomUUID();
        OfflinePlayer playerMock = mock(OfflinePlayer.class);
        when(playerMock.getUniqueId()).thenReturn(playerUUID);

        when(accountDataMock.getAccount(playerUUID)).thenReturn(null);

        EconomyImpl sut = new EconomyImpl(loggerMock, true, null, accountDataMock, balanceDataMock);

        // Act
        boolean actual = sut.hasAccount(playerMock);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void hasAccount_WithSqlException_ShouldReturnFalse() throws SQLException {
        // Arrange
        UUID playerUUID = UUID.randomUUID();
        OfflinePlayer playerMock = mock(OfflinePlayer.class);
        when(playerMock.getUniqueId()).thenReturn(playerUUID);

        when(accountDataMock.getAccount(playerUUID)).thenThrow(SQLException.class);

        EconomyImpl sut = new EconomyImpl(loggerMock, true, null, accountDataMock, balanceDataMock);

        // Act
        boolean actual = sut.hasAccount(playerMock);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void hasAccount_WithSqlException_ShouldLogException() throws SQLException {
        // Arrange
        UUID playerUUID = UUID.randomUUID();
        OfflinePlayer playerMock = mock(OfflinePlayer.class);
        when(playerMock.getUniqueId()).thenReturn(playerUUID);

        when(accountDataMock.getAccount(playerUUID)).thenThrow(SQLException.class);

        EconomyImpl sut = new EconomyImpl(loggerMock, true, null, accountDataMock, balanceDataMock);

        // Act
        sut.hasAccount(playerMock);

        // Assert
        verify(loggerMock, times(1)).log(
            eq(Level.SEVERE),
            eq(String.format("[Total Economy] Error calling getAccount (accountId: %s)", playerUUID)),
            any(SQLException.class)
        );
    }

    @Test
    @Tag("Unit")
    public void createPlayerAccount_WithSuccessfulCallToCreateAccount_ShouldReturnTrue() throws SQLException {
        // Arrange
        UUID playerUUID = UUID.randomUUID();
        OfflinePlayer playerMock = mock(OfflinePlayer.class);
        when(playerMock.getUniqueId()).thenReturn(playerUUID);

        when(accountDataMock.createAccount(playerUUID, 1)).thenReturn(true);

        EconomyImpl sut = new EconomyImpl(loggerMock, true, null, accountDataMock, balanceDataMock);

        // Act
        boolean actual = sut.createPlayerAccount(playerMock);

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void createPlayerAccount_WithSqlException_ShouldReturnFalse() throws SQLException {
        // Arrange
        UUID playerUUID = UUID.randomUUID();
        OfflinePlayer playerMock = mock(OfflinePlayer.class);
        when(playerMock.getUniqueId()).thenReturn(playerUUID);

        when(accountDataMock.createAccount(playerUUID, 1)).thenThrow(SQLException.class);

        EconomyImpl sut = new EconomyImpl(loggerMock, true, null, accountDataMock, balanceDataMock);

        // Act
        boolean actual = sut.createPlayerAccount(playerMock);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void createPlayerAccount_WithSqlException_ShouldLogException() throws SQLException {
        // Arrange
        UUID playerUUID = UUID.randomUUID();
        OfflinePlayer playerMock = mock(OfflinePlayer.class);
        when(playerMock.getUniqueId()).thenReturn(playerUUID);

        when(accountDataMock.createAccount(playerUUID, 1)).thenThrow(SQLException.class);

        EconomyImpl sut = new EconomyImpl(loggerMock, true, null, accountDataMock, balanceDataMock);

        // Act
        sut.createPlayerAccount(playerMock);

        // Assert
        verify(loggerMock, times(1)).log(
            eq(Level.SEVERE),
            eq(String.format(
                "[Total Economy] Error calling createAccount (accountId: %s, currencyId: %s)",
                playerUUID,
                1
            )),
            any(SQLException.class)
        );
    }

    @Test
    @Tag("Unit")
    public void getBalance_WithBalanceFound_ShouldReturnBalance() throws SQLException {
        // Arrange
        UUID playerUUID = UUID.randomUUID();
        OfflinePlayer playerMock = mock(OfflinePlayer.class);
        when(playerMock.getUniqueId()).thenReturn(playerUUID);

        when(balanceDataMock.getBalance(playerUUID, 1)).thenReturn(BigDecimal.TEN);

        EconomyImpl sut = new EconomyImpl(loggerMock, true, null, accountDataMock, balanceDataMock);

        // Act
        double actual = sut.getBalance(playerMock);
        double expected = 10;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void getBalance_WithNullValue_ShouldReturnZero() throws SQLException {
        // Arrange
        UUID playerUUID = UUID.randomUUID();
        OfflinePlayer playerMock = mock(OfflinePlayer.class);
        when(playerMock.getUniqueId()).thenReturn(playerUUID);

        when(balanceDataMock.getBalance(playerUUID, 1)).thenReturn(null);

        EconomyImpl sut = new EconomyImpl(loggerMock, true, null, accountDataMock, balanceDataMock);

        // Act
        double actual = sut.getBalance(playerMock);
        double expected = 0;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void getBalance_WithNullBalance_ShouldReturnZero() throws SQLException {
        // Arrange
        UUID playerUUID = UUID.randomUUID();
        OfflinePlayer playerMock = mock(OfflinePlayer.class);
        when(playerMock.getUniqueId()).thenReturn(playerUUID);

        when(balanceDataMock.getBalance(playerUUID, 1)).thenThrow(SQLException.class);

        EconomyImpl sut = new EconomyImpl(loggerMock, true, null, accountDataMock, balanceDataMock);

        // Act
        double actual = sut.getBalance(playerMock);
        double expected = 0;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void getBalance_WithSqlException_ShouldLogException() throws SQLException {
        // Arrange
        UUID playerUUID = UUID.randomUUID();
        OfflinePlayer playerMock = mock(OfflinePlayer.class);
        when(playerMock.getUniqueId()).thenReturn(playerUUID);

        when(balanceDataMock.getBalance(playerUUID, 1)).thenThrow(SQLException.class);

        EconomyImpl sut = new EconomyImpl(loggerMock, true, null, accountDataMock, balanceDataMock);

        // Act
        sut.getBalance(playerMock);

        // Assert
        verify(loggerMock, times(1)).log(
            eq(Level.SEVERE),
            eq(String.format(
                "[Total Economy] Error calling getBalance (accountId: %s, currencyId: %s)",
                playerUUID,
                1
            )),
            any(SQLException.class)
        );
    }

    @Test
    @Tag("Unit")
    public void has_WithAmountGreaterThanBalance_ShouldReturnTrue() {
        // Arrange
        EconomyImpl sut = spy(
            new EconomyImpl(loggerMock, true, null, accountDataMock, balanceDataMock)
        );

        OfflinePlayer playerMock = mock(OfflinePlayer.class);
        doReturn(100.0).when(sut).getBalance(playerMock);

        // Act
        boolean actual = sut.has(playerMock, 10);

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void has_WithAmountEqualToBalance_ShouldReturnTrue() {
        // Arrange
        EconomyImpl sut = spy(
            new EconomyImpl(loggerMock, true, null, accountDataMock, balanceDataMock)
        );

        OfflinePlayer playerMock = mock(OfflinePlayer.class);
        doReturn(100.0).when(sut).getBalance(playerMock);

        // Act
        boolean actual = sut.has(playerMock, 100.0);

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void has_WithAmountLessThanBalance_ShouldReturnFalse() {
        // Arrange
        EconomyImpl sut = spy(
            new EconomyImpl(loggerMock, true, null, accountDataMock, balanceDataMock)
        );

        OfflinePlayer playerMock = mock(OfflinePlayer.class);
        doReturn(100.0).when(sut).getBalance(playerMock);

        // Act
        boolean actual = sut.has(playerMock, 105.5);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void withdrawPlayer_WithSuccessfulWithdraw_ShouldReturnCorrectEconomyResponse() throws SQLException {
        // Arrange
        UUID playerUUID = UUID.randomUUID();
        OfflinePlayer playerMock = mock(OfflinePlayer.class);
        when(playerMock.getUniqueId()).thenReturn(playerUUID);
        when(balanceDataMock.getBalance(playerUUID, 1)).thenReturn(BigDecimal.valueOf(100));
        when(balanceDataMock.updateBalance(playerUUID, 1, 90)).thenReturn(1);

        EconomyImpl sut = new EconomyImpl(loggerMock, true, null, accountDataMock, balanceDataMock);

        // Act
        EconomyResponse actual = sut.withdrawPlayer(playerMock, 10);
        EconomyResponse expected = new EconomyResponse(
            10,
            90,
            EconomyResponse.ResponseType.SUCCESS,
            null
        );

        // Assert
        assertEquals(expected.amount, actual.amount);
        assertEquals(expected.balance, actual.balance);
        assertEquals(expected.type, actual.type);
        assertEquals(expected.errorMessage, actual.errorMessage);
    }

    @Test
    @Tag("Unit")
    public void withdrawPlayer_WithNullBalance_ShouldReturnCorrectEconomyResponse() throws SQLException {
        // Arrange
        UUID playerUUID = UUID.randomUUID();
        OfflinePlayer playerMock = mock(OfflinePlayer.class);
        when(playerMock.getUniqueId()).thenReturn(playerUUID);
        when(balanceDataMock.getBalance(playerUUID, 1)).thenReturn(null);

        EconomyImpl sut = new EconomyImpl(loggerMock, true, null, accountDataMock, balanceDataMock);

        // Act
        EconomyResponse actual = sut.withdrawPlayer(playerMock, 10);
        EconomyResponse expected = new EconomyResponse(
            10,
            0,
            EconomyResponse.ResponseType.FAILURE,
            "No balance found for user"
        );

        // Assert
        assertEquals(expected.amount, actual.amount);
        assertEquals(expected.balance, actual.balance);
        assertEquals(expected.type, actual.type);
        assertEquals(expected.errorMessage, actual.errorMessage);
    }

    @Test
    @Tag("Unit")
    public void withdrawPlayer_WithSqlExceptionFromGetBalance_ShouldLogException() throws SQLException {
        // Arrange
        UUID playerUUID = UUID.randomUUID();
        OfflinePlayer playerMock = mock(OfflinePlayer.class);
        when(playerMock.getUniqueId()).thenReturn(playerUUID);
        when(balanceDataMock.getBalance(playerUUID, 1)).thenThrow(SQLException.class);

        EconomyImpl sut = new EconomyImpl(loggerMock, true, null, accountDataMock, balanceDataMock);

        // Act
        sut.withdrawPlayer(playerMock, 10);

        // Assert
        verify(loggerMock, times(1)).log(
            eq(Level.SEVERE),
            eq(String.format(
                "[Total Economy] Error calling getBalance (accountId: %s, currencyId: %s)",
                playerUUID,
                1
            )),
            any(SQLException.class)
        );
    }

    @Test
    @Tag("Unit")
    public void withdrawPlayer_WithFalseResultFromUpdateBalance_ShouldReturnCorrectEconomyResponse() throws SQLException {
        // Arrange
        UUID playerUUID = UUID.randomUUID();
        OfflinePlayer playerMock = mock(OfflinePlayer.class);
        when(playerMock.getUniqueId()).thenReturn(playerUUID);
        when(balanceDataMock.getBalance(playerUUID, 1)).thenReturn(BigDecimal.valueOf(100));
        when(balanceDataMock.updateBalance(playerUUID, 1, 90)).thenThrow(SQLException.class);

        EconomyImpl sut = new EconomyImpl(loggerMock, true, null, accountDataMock, balanceDataMock);

        // Act
        EconomyResponse actual = sut.withdrawPlayer(playerMock, 10);
        EconomyResponse expected = new EconomyResponse(
            10,
            100,
            EconomyResponse.ResponseType.FAILURE,
            "Error updating balance"
        );

        // Assert
        assertEquals(expected.amount, actual.amount);
        assertEquals(expected.balance, actual.balance);
        assertEquals(expected.type, actual.type);
        assertEquals(expected.errorMessage, actual.errorMessage);
    }

    @Test
    @Tag("Unit")
    public void withdrawPlayer_WithSqlExceptionFromUpdateBalance_ShouldLogException() throws SQLException {
        // Arrange
        UUID playerUUID = UUID.randomUUID();
        OfflinePlayer playerMock = mock(OfflinePlayer.class);
        when(playerMock.getUniqueId()).thenReturn(playerUUID);
        when(balanceDataMock.getBalance(playerUUID, 1)).thenReturn(BigDecimal.valueOf(100));
        when(balanceDataMock.updateBalance(playerUUID, 1, 90)).thenThrow(SQLException.class);

        EconomyImpl sut = new EconomyImpl(loggerMock, true, null, accountDataMock, balanceDataMock);

        // Act
        sut.withdrawPlayer(playerMock, 10);

        // Assert
        verify(loggerMock, times(1)).log(
            eq(Level.SEVERE),
            eq(String.format(
                "[Total Economy] Error calling updateBalance (accountId: %s, currencyId: %s, balance: %s)",
                playerUUID,
                1,
                90.0
            )),
            any(SQLException.class)
        );
    }

    @Test
    @Tag("Unit")
    public void depositPlayer_WithSuccessfulDeposit_ShouldReturnCorrectEconomyResponse() throws SQLException {
        // Arrange
        UUID playerUUID = UUID.randomUUID();
        OfflinePlayer playerMock = mock(OfflinePlayer.class);
        when(playerMock.getUniqueId()).thenReturn(playerUUID);
        when(balanceDataMock.getBalance(playerUUID, 1)).thenReturn(BigDecimal.valueOf(100));
        when(balanceDataMock.updateBalance(playerUUID, 1, 110)).thenReturn(1);

        EconomyImpl sut = new EconomyImpl(loggerMock, true, null, accountDataMock, balanceDataMock);

        // Act
        EconomyResponse actual = sut.depositPlayer(playerMock, 10);
        EconomyResponse expected = new EconomyResponse(
            10,
            110,
            EconomyResponse.ResponseType.SUCCESS,
            null
        );

        // Assert
        assertEquals(expected.amount, actual.amount);
        assertEquals(expected.balance, actual.balance);
        assertEquals(expected.type, actual.type);
        assertEquals(expected.errorMessage, actual.errorMessage);
    }

    @Test
    @Tag("Unit")
    public void depositPlayer_WithNullBalance_ShouldReturnCorrectEconomyResponse() throws SQLException {
        // Arrange
        UUID playerUUID = UUID.randomUUID();
        OfflinePlayer playerMock = mock(OfflinePlayer.class);
        when(playerMock.getUniqueId()).thenReturn(playerUUID);
        when(balanceDataMock.getBalance(playerUUID, 1)).thenReturn(null);

        EconomyImpl sut = new EconomyImpl(loggerMock, true, null, accountDataMock, balanceDataMock);

        // Act
        EconomyResponse actual = sut.depositPlayer(playerMock, 10);
        EconomyResponse expected = new EconomyResponse(
            10,
            0,
            EconomyResponse.ResponseType.FAILURE,
            "No balance found for user"
        );

        // Assert
        assertEquals(expected.amount, actual.amount);
        assertEquals(expected.balance, actual.balance);
        assertEquals(expected.type, actual.type);
        assertEquals(expected.errorMessage, actual.errorMessage);
    }

    @Test
    @Tag("Unit")
    public void depositPlayer_WithSqlExceptionFromGetBalance_ShouldLogException() throws SQLException {
        // Arrange
        UUID playerUUID = UUID.randomUUID();
        OfflinePlayer playerMock = mock(OfflinePlayer.class);
        when(playerMock.getUniqueId()).thenReturn(playerUUID);
        when(balanceDataMock.getBalance(playerUUID, 1)).thenThrow(SQLException.class);

        EconomyImpl sut = new EconomyImpl(loggerMock, true, null, accountDataMock, balanceDataMock);

        // Act
        sut.depositPlayer(playerMock, 10);

        // Assert
        verify(loggerMock, times(1)).log(
            eq(Level.SEVERE),
            eq(String.format(
                "[Total Economy] Error calling getBalance (accountId: %s, currencyId: %s)",
                playerUUID,
                1
            )),
            any(SQLException.class)
        );
    }

    @Test
    @Tag("Unit")
    public void depositPlayer_WithFalseResultFromUpdateBalance_ShouldReturnCorrectEconomyResponse() throws SQLException {
        // Arrange
        UUID playerUUID = UUID.randomUUID();
        OfflinePlayer playerMock = mock(OfflinePlayer.class);
        when(playerMock.getUniqueId()).thenReturn(playerUUID);
        when(balanceDataMock.getBalance(playerUUID, 1)).thenReturn(BigDecimal.valueOf(100));
        when(balanceDataMock.updateBalance(playerUUID, 1, 110)).thenThrow(SQLException.class);

        EconomyImpl sut = new EconomyImpl(loggerMock, true, null, accountDataMock, balanceDataMock);

        // Act
        EconomyResponse actual = sut.depositPlayer(playerMock, 10);
        EconomyResponse expected = new EconomyResponse(
            10,
            100,
            EconomyResponse.ResponseType.FAILURE,
            "Error updating balance"
        );

        // Assert
        assertEquals(expected.amount, actual.amount);
        assertEquals(expected.balance, actual.balance);
        assertEquals(expected.type, actual.type);
        assertEquals(expected.errorMessage, actual.errorMessage);
    }

    @Test
    @Tag("Unit")
    public void depositPlayer_WithSqlExceptionFromUpdateBalance_ShouldLogException() throws SQLException {
        // Arrange
        UUID playerUUID = UUID.randomUUID();
        OfflinePlayer playerMock = mock(OfflinePlayer.class);
        when(playerMock.getUniqueId()).thenReturn(playerUUID);
        when(balanceDataMock.getBalance(playerUUID, 1)).thenReturn(BigDecimal.valueOf(100));
        when(balanceDataMock.updateBalance(playerUUID, 1, 110)).thenThrow(SQLException.class);

        EconomyImpl sut = new EconomyImpl(loggerMock, true, null, accountDataMock, balanceDataMock);

        // Act
        sut.depositPlayer(playerMock, 10);

        // Assert
        verify(loggerMock, times(1)).log(
            eq(Level.SEVERE),
            eq(String.format(
                "[Total Economy] Error calling updateBalance (accountId: %s, currencyId: %s, balance: %s)",
                playerUUID,
                1,
                110.0
            )),
            any(SQLException.class)
        );
    }
}
