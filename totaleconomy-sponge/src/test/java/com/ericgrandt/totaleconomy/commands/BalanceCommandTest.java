package com.ericgrandt.totaleconomy.commands;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ericgrandt.totaleconomy.common.data.dto.CurrencyDto;
import com.ericgrandt.totaleconomy.impl.CurrencyImpl;
import com.ericgrandt.totaleconomy.impl.EconomyImpl;
import com.ericgrandt.totaleconomy.impl.UniqueAccountImpl;
import java.math.BigDecimal;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.service.economy.Currency;

@ExtendWith(MockitoExtension.class)
public class BalanceCommandTest {
    @Mock
    private EconomyImpl economyMock;

    private final Currency currency = new CurrencyImpl(
        new CurrencyDto(1, "Dollar", "Dollars", "$", 2, true)
    );

    @Test
    @Tag("Unit")
    public void onCommandHandler_WithAccount_ShouldSendMessageWithBalance() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        Map<Currency, BigDecimal> balances = Map.of(currency, BigDecimal.TEN);
        when(economyMock.findOrCreateAccount(any(UUID.class))).thenReturn(
            Optional.of(new UniqueAccountImpl(null, uuid, balances, null, null))
        );

        ServerPlayer playerMock = mock(ServerPlayer.class);
        when(playerMock.uniqueId()).thenReturn(uuid);

        BalanceCommand sut = new BalanceCommand(economyMock, currency);

        // Act
        sut.onCommandHandler(playerMock);

        // Assert
        verify(playerMock).sendMessage(Component.text("Balance: ").append(Component.text("$10.00")));
    }

    @Test
    @Tag("Unit")
    public void onCommandHandler_WithNoAccount_ShouldThrowException() {
        // Arrange
        when(economyMock.findOrCreateAccount(any(UUID.class))).thenReturn(Optional.empty());

        ServerPlayer playerMock = mock(ServerPlayer.class);
        when(playerMock.uniqueId()).thenReturn(UUID.randomUUID());

        BalanceCommand sut = new BalanceCommand(economyMock, currency);

        // Act/Assert
        assertThrows(
            NoSuchElementException.class,
            () -> sut.onCommandHandler(playerMock)
        );
    }

    @Test
    @Tag("Integration")
    public void execute_ShouldReturnPlayerBalanceAndReturnCommandResultSuccess() {
        // Arrange

        // Act

        // Assert
    }
}
