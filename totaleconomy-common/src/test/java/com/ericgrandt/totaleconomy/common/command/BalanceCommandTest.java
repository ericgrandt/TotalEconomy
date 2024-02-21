package com.ericgrandt.totaleconomy.common.command;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ericgrandt.totaleconomy.common.econ.CommonEconomy;
import com.ericgrandt.totaleconomy.common.game.CommonPlayer;
import com.ericgrandt.totaleconomy.common.game.CommonSender;
import java.math.BigDecimal;
import java.math.RoundingMode;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class BalanceCommandTest {
    @Test
    @Tag("Unit")
    public void execute_WithNonPlayerSender_ShouldReturnFalse() {
        // Arrange
        CommonSender commonSenderMock = mock(CommonSender.class);
        CommonEconomy economyMock = mock(CommonEconomy.class);

        when(commonSenderMock.isPlayer()).thenReturn(false);

        BalanceCommand sut = new BalanceCommand(economyMock);

        // Act
        boolean actual = sut.execute(commonSenderMock, null);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void onCommandHandler_ShouldSendPlayerTheirBalance() {
        // Arrange
        BigDecimal balance = BigDecimal.valueOf(100).setScale(2, RoundingMode.DOWN);

        CommonPlayer commonPlayerMock = mock(CommonPlayer.class);
        CommonEconomy economyMock = mock(CommonEconomy.class);

        when(economyMock.getBalance(commonPlayerMock)).thenReturn(balance.doubleValue());
        when(economyMock.formatBalance(any(Double.class))).thenReturn(
            Component.text("$" + balance)
        );

        BalanceCommand sut = new BalanceCommand(economyMock);

        // Act
        sut.onCommandHandler(commonPlayerMock);

        // Assert
        verify(commonPlayerMock).sendMessage(Component.text("Balance: ").append(Component.text("$100.00")));
    }
}
