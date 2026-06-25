package com.ericgrandt.totaleconomy.command;

import com.ericgrandt.totaleconomy.economy.EconomyProvider;
import com.ericgrandt.totaleconomy.model.Player;
import com.ericgrandt.totaleconomy.model.TEAccount;
import com.ericgrandt.totaleconomy.model.TECurrency;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BalanceCommandTest {
    @Mock
    private EconomyProvider econMock;

    @Mock
    private Player playerMock;

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


//    @Test
//    @Tag("Unit")
//    fun `execute with success and no currencyCode parameter should use default currency and send the correct message to the player`() {
//        runTest {
//            // Arrange
//            val playerId = UUID.randomUUID()
//            val account = TEAccount(playerId, "USD", BigDecimal.valueOf(1.236))
//            val currency = TECurrency("USD", "Dollar", "Dollars", "$", 2, true)
//            every { econMock.getAccount(any(), any()) } returns account
//            every { econMock.getDefaultCurrency() } returns currency
//            every { playerMock.uniqueId } returns UUID.randomUUID()
//
//            val sut = BalanceCommand(econMock)
//
//            // Act
//            val actual = sut.execute(playerMock, mutableMapOf())
//            val expected = CommandResult.SUCCESS
//
//            // Assert
//            assertEquals(expected, actual)
//            verify(exactly = 1) {
//                playerMock.sendMessage(
//                    Component.text("Balance: ").append(Component.text("$1.23")),
//                )
//            }
//        }
//    }
//
//    @Test
//    @Tag("Unit")
//    fun `execute with error should send the correct message to the player`() {
//        runTest {
//            // Arrange
//            val currency = TECurrency("USD", "Dollar", "Dollars", "$", 2, true)
//            every { econMock.getDefaultCurrency() } returns currency
//            every { econMock.getAccount(any(), any()) } throws AccountNotFoundException()
//            every { playerMock.uniqueId } returns UUID.randomUUID()
//
//            val sut = BalanceCommand(econMock)
//
//            // Act
//            val actual = sut.execute(playerMock, mutableMapOf())
//            val expected = CommandResult.FAILURE
//
//            // Assert
//            assertEquals(expected, actual)
//            verify(exactly = 1) {
//                playerMock.sendMessage(
//                    Component.text("You don't have an account for this currency.").color(NamedTextColor.YELLOW),
//                )
//            }
//        }
//    }
}
