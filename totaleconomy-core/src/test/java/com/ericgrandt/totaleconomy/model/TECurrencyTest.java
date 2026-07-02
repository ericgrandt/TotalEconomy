package com.ericgrandt.totaleconomy.model;

import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TECurrencyTest {
    @Test
    @Tag("Unit")
    public void format_WithSymbol_ShouldFormatUsingSymbol() {
        // Arrange
        var currency = new TECurrency(
            "USD",
            "Dollar",
            "Dollars",
            "$",
            2,
            BigDecimal.TEN,
            true
        );

        // Act
        var actual = currency.format(BigDecimal.TEN);
        var expected = Component.text("$10.00");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void format_WithNoSymbolAndBalanceOfOne_ShouldFormatUsingSingularName() {
        // Arrange
        var currency = new TECurrency(
            "USD",
            "Dollar",
            "Dollars",
            null,
            2,
            BigDecimal.TEN,
            true
        );

        // Act
        var actual = currency.format(BigDecimal.ONE);
        var expected = Component.text("1.00 Dollar");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void format_WithNoSymbolAndBalanceOfTen_ShouldFormatUsingPluralName() {
        // Arrange
        var currency = new TECurrency(
            "USD",
            "Dollar",
            "Dollars",
            null,
            2,
            BigDecimal.TEN,
            true
        );

        // Act
        var actual = currency.format(BigDecimal.TEN);
        var expected = Component.text("10.00 Dollars");

        // Assert
        assertEquals(expected, actual);
    }
}
