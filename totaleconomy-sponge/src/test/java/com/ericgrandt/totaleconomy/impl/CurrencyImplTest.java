package com.ericgrandt.totaleconomy.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ericgrandt.totaleconomy.common.data.dto.CurrencyDto;
import java.math.BigDecimal;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class CurrencyImplTest {
    private final CurrencyDto currencyDto = new CurrencyDto(
        1,
        "Dollar",
        "Dollars",
        "$",
        2,
        true
    );

    @Test
    @Tag("Unit")
    public void displayName_ShouldReturnDisplayName() {
        // Arrange
        CurrencyImpl sut = new CurrencyImpl(currencyDto);

        // Act
        Component actual = sut.displayName();
        Component expected = Component.text("Dollar");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void pluralDisplayName_ShouldReturnPluralDisplayName() {
        // Arrange
        CurrencyImpl sut = new CurrencyImpl(currencyDto);

        // Act
        Component actual = sut.pluralDisplayName();
        Component expected = Component.text("Dollars");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void symbol_ShouldReturnSymbol() {
        // Arrange
        CurrencyImpl sut = new CurrencyImpl(currencyDto);

        // Act
        Component actual = sut.symbol();
        Component expected = Component.text("$");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void format_WithNoFractionDigitsPassedIn_ShouldReturnFormattedAmountUsingDefaultFractionDigits() {
        // Arrange
        BigDecimal amount = BigDecimal.TEN;
        CurrencyImpl sut = new CurrencyImpl(currencyDto);

        // Act
        Component actual = sut.format(amount);
        Component expected = Component.text("$10.00");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void format_WithFractionDigitsPassedIn_ShouldReturnFormattedAmountUsingFractionDigitsParam() {
        // Arrange
        BigDecimal amount = BigDecimal.TEN;
        CurrencyImpl sut = new CurrencyImpl(currencyDto);

        // Act
        Component actual = sut.format(amount, 0);
        Component expected = Component.text("$10");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void defaultFractionDigits_ShouldReturnDefaultFractionDigits() {
        // Arrange
        CurrencyImpl sut = new CurrencyImpl(currencyDto);

        // Act
        int actual = sut.defaultFractionDigits();
        int expected = 2;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void isDefault_ShouldReturnIsDefault() {
        // Arrange
        CurrencyImpl sut = new CurrencyImpl(currencyDto);

        // Act
        boolean actual = sut.isDefault();

        // Assert
        assertTrue(actual);
    }
}
