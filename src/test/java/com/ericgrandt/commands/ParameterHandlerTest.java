package com.ericgrandt.commands;

import com.ericgrandt.domain.TECommandParameterKey;
import com.ericgrandt.domain.TECurrency;
import com.ericgrandt.services.TEEconomyService;
import com.ericgrandt.wrappers.ParameterWrapper;
import org.junit.jupiter.api.Test;
import org.spongepowered.api.block.entity.CommandBlock;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ParameterHandlerTest {
    @Test
    public void getPlayerParameter_WithPlayerKey_ShouldReturnPlayer() {
        // Arrange
        CommandContext contextMock = mock(CommandContext.class);
        when(contextMock.cause()).thenReturn(mock(CommandCause.class));

        Player player = mock(Player.class);
        when(contextMock.cause().root()).thenReturn(player);

        ParameterHandler sut = new ParameterHandler(contextMock, null);

        // Act
        Player actual = sut.getPlayerParameter();

        // Assert
        assertEquals(player, actual);
    }

    @Test
    public void getPlayerParameter_WithNonPlayerRoot_ShouldReturnNull() {
        // Arrange
        CommandContext contextMock = mock(CommandContext.class);
        when(contextMock.cause()).thenReturn(mock(CommandCause.class));
        when(contextMock.cause().root()).thenReturn(mock(CommandBlock.class));

        ParameterHandler sut = new ParameterHandler(contextMock, null);

        // Act
        Player actual = sut.getPlayerParameter();

        // Assert
        assertNull(actual);
    }

    @Test
    public void getCurrencyParameter_WithValidKey_ShouldReturnTECurrencyObject() {
        // Arrange
        TECommandParameterKey<String> stringKey = new TECommandParameterKey<>("currency", String.class);

        CommandContext contextMock = mock(CommandContext.class);
        ParameterWrapper parameterWrapperMock = mock(ParameterWrapper.class);
        ParameterHandler sut = new ParameterHandler(contextMock, parameterWrapperMock);

        TEEconomyService economyServiceMock = mock(TEEconomyService.class);
        TECurrency expected = new TECurrency(
            1,
            "ValidCurrency",
            "ValidCurrency",
            "V",
            0,
            true
        );

        when(parameterWrapperMock.key(stringKey.key(), String.class)).thenReturn(stringKey);
        when(contextMock.one(stringKey)).thenReturn(Optional.of("ValidCurrency"));
        when(economyServiceMock.getCurrency("ValidCurrency")).thenReturn(expected);

        // Act
        TECurrency actual = sut.getCurrencyParameter(economyServiceMock);

        // Assert
        verify(economyServiceMock, times(1)).getCurrency("ValidCurrency");
        assertEquals(expected, actual);
    }

    @Test
    public void getCurrencyParameter_WithMissingParam_ShouldReturnTheDefaultCurrency() {
        // Arrange
        CommandContext contextMock = mock(CommandContext.class);
        ParameterWrapper parameterWrapperMock = mock(ParameterWrapper.class);
        ParameterHandler sut = new ParameterHandler(contextMock, parameterWrapperMock);

        TEEconomyService economyServiceMock = mock(TEEconomyService.class);
        TECurrency expected = new TECurrency(
            1,
            "DefaultCurrency",
            "DefaultCurrency",
            "V",
            0,
            true
        );

        when(economyServiceMock.defaultCurrency()).thenReturn(expected);

        // Act
        TECurrency actual = sut.getCurrencyParameter(economyServiceMock);

        // Assert
        verify(economyServiceMock, times(1)).defaultCurrency();
        assertEquals(expected, actual);
    }

    @Test
    public void getCurrencyParameter_WithInvalidCurrency_ShouldReturnNull() {
        // Arrange
        TECommandParameterKey<String> stringKey = new TECommandParameterKey<>("currency", String.class);

        CommandContext contextMock = mock(CommandContext.class);
        ParameterWrapper parameterWrapperMock = mock(ParameterWrapper.class);
        ParameterHandler sut = new ParameterHandler(contextMock, parameterWrapperMock);

        TEEconomyService economyServiceMock = mock(TEEconomyService.class);

        when(parameterWrapperMock.key(stringKey.key(), String.class)).thenReturn(stringKey);
        when(contextMock.one(stringKey)).thenReturn(Optional.of("InvalidCurrency"));
        when(economyServiceMock.getCurrency("InvalidCurrency")).thenReturn(null);

        // Act
        TECurrency actual = sut.getCurrencyParameter(economyServiceMock);

        // Assert
        verify(economyServiceMock, times(1)).getCurrency("InvalidCurrency");
        assertNull(actual);
    }

    @Test
    public void getBigDecimalParameter_WithValidKey_ShouldReturnBigDecimal() {
        // Arrange
        TECommandParameterKey<BigDecimal> bigDecimalKey = new TECommandParameterKey<>("amount", BigDecimal.class);

        CommandContext contextMock = mock(CommandContext.class);
        ParameterWrapper parameterWrapperMock = mock(ParameterWrapper.class);
        ParameterHandler sut = new ParameterHandler(contextMock, parameterWrapperMock);

        when(parameterWrapperMock.key(bigDecimalKey.key(), BigDecimal.class)).thenReturn(bigDecimalKey);
        when(contextMock.one(bigDecimalKey)).thenReturn(Optional.of(BigDecimal.ONE));

        // Act
        BigDecimal actual = sut.getBigDecimalParameter("amount");
        BigDecimal expected = BigDecimal.ONE;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void getBigDecimalParameter_WithNoAmountParameter_ShouldReturnNull() {
        // Arrange
        CommandContext contextMock = mock(CommandContext.class);
        ParameterWrapper parameterWrapperMock = mock(ParameterWrapper.class);
        ParameterHandler sut = new ParameterHandler(contextMock, parameterWrapperMock);

        // Act
        BigDecimal actual = sut.getBigDecimalParameter("amount");

        // Assert
        assertNull(actual);
    }
}
