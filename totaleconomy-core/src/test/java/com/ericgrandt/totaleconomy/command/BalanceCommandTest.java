package com.ericgrandt.totaleconomy.command;

import com.ericgrandt.totaleconomy.economy.EconomyProvider;
import com.ericgrandt.totaleconomy.exception.AccountNotFoundException;
import com.ericgrandt.totaleconomy.model.Sender;
import com.ericgrandt.totaleconomy.model.TEAccount;
import com.ericgrandt.totaleconomy.model.TECurrency;
import com.ericgrandt.totaleconomy.model.TEPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BalanceCommandTest {
    @Mock
    private EconomyProvider econMock;

    @Mock
    private TEPlayer playerMock;

    @Test
    @Tag("Unit")
    public void execute_WithSuccessAndCurrencyCodeParameter_ShouldSendCorrectMessageToPlayer() {
        // Arrange
        var currency = new TECurrency("COIN", "Coin", "Coins", null, 0, false);
        var account = new TEAccount(UUID.randomUUID(), "COIN", BigDecimal.TEN);
        when(playerMock.uniqueId()).thenReturn(account.playerId());
        when(econMock.getCurrency("COIN")).thenReturn(currency);
        when(econMock.getAccount(account.playerId(), account.currencyCode())).thenReturn(account);

        var sut = new BalanceCommand(econMock);

        // Act
        var actual = sut.execute(playerMock, Map.of("currencyCode", new StringArg("COIN")));
        var expected = CommandResult.SUCCESS;

        // Assert
        assertEquals(expected, actual);
        verify(playerMock, times(1)).sendMessage(
            Component.text("Balance: ").append(Component.text("10 Coins"))
        );
    }

    @Test
    @Tag("Unit")
    public void execute_WithSuccessAndNoCurrencyCodeParameter_ShouldUseDefaultCurrencyAndSendCorrectMessageToPlayer() {
        // Arrange
        var currency = new TECurrency("USD", "Dollar", "Dollars", "$", 2, true);
        var account = new TEAccount(UUID.randomUUID(), "USD", BigDecimal.TEN);
        when(playerMock.uniqueId()).thenReturn(account.playerId());
        when(econMock.getDefaultCurrency()).thenReturn(currency);
        when(econMock.getAccount(account.playerId(), account.currencyCode())).thenReturn(account);

        var sut = new BalanceCommand(econMock);

        // Act
        var actual = sut.execute(playerMock, Map.of());
        var expected = CommandResult.SUCCESS;

        // Assert
        assertEquals(expected, actual);
        verify(playerMock, times(1)).sendMessage(
            Component.text("Balance: ").append(Component.text("$10.00"))
        );
    }

    @Test
    @Tag("Unit")
    public void execute_WithNonPlayerSender_ShouldReturnFailure() {
        // Arrange
        var sut = new BalanceCommand(econMock);

        // Act
        var actual = sut.execute(mock(Sender.class), Map.of());
        var expected = CommandResult.FAILURE;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void execute_WithException_ShouldSendCorrectMessageToPlayer() {
        // Arrange
        var currency = new TECurrency("USD", "Dollar", "Dollars", "$", 2, true);
        when(playerMock.uniqueId()).thenReturn(UUID.randomUUID());
        when(econMock.getDefaultCurrency()).thenReturn(currency);
        when(econMock.getAccount(any(), any())).thenThrow(AccountNotFoundException.class);

        var sut = new BalanceCommand(econMock);

        // Act
        var actual = sut.execute(playerMock, Map.of());
        var expected = CommandResult.FAILURE;

        // Assert
        assertEquals(expected, actual);
        verify(playerMock, times(1)).sendMessage(
            Component.text("You don't have an account for this currency.").color(NamedTextColor.YELLOW)
        );
    }
}
