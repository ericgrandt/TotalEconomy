package com.ericgrandt.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.spongepowered.api.service.economy.Currency;

public class CurrencyImplTest {
    @Test
    @Tag("Unit")
    public void displayName_ShouldReturnCorrectComponent() {
        // Arrange
        Currency sut = new CurrencyImpl(
            "singular",
            "plural",
            "$",
            2,
            true,
            BigDecimal.TEN
        );

        // Act
        Component actual = sut.displayName();
        Component expected = Component.text("singular");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void pluralDisplayName_ShouldReturnCorrectComponent() {
        // Arrange
        Currency sut = new CurrencyImpl(
            "singular",
            "plural",
            "$",
            2,
            true,
            BigDecimal.TEN
        );

        // Act
        Component actual = sut.pluralDisplayName();
        Component expected = Component.text("plural");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void symbol_ShouldReturnCorrectComponent() {
        // Arrange
        Currency sut = new CurrencyImpl(
            "singular",
            "plural",
            "$",
            2,
            true,
            BigDecimal.TEN
        );

        // Act
        Component actual = sut.symbol();
        Component expected = Component.text("$");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void format_WithTwoFractionDigits_ShouldReturnCorrectComponent() {
        // Arrange
        Currency sut = new CurrencyImpl(
            "singular",
            "plural",
            "$",
            2,
            true,
            BigDecimal.TEN
        );
        BigDecimal value = BigDecimal.valueOf(10.1252);

        // Act
        Component actual = sut.format(value);
        Component expected = Component.text("10.12");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void format_WithThreeFractionDigits_ShouldReturnCorrectComponent() {
        // Arrange
        Currency sut = new CurrencyImpl(
            "singular",
            "plural",
            "$",
            3,
            true,
            BigDecimal.TEN
        );
        BigDecimal value = BigDecimal.valueOf(10.1252);

        // Act
        Component actual = sut.format(value);
        Component expected = Component.text("10.125");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void format_WithOverriddenFractionDigits_ShouldReturnCorrectComponent() {
        // Arrange
        Currency sut = new CurrencyImpl(
            "singular",
            "plural",
            "$",
            2,
            true,
            BigDecimal.TEN
        );
        BigDecimal value = BigDecimal.valueOf(10.1252);

        // Act
        Component actual = sut.format(value, 3);
        Component expected = Component.text("10.125");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void defaultFractionDigits_ShouldReturnNumFractionDigits() {
        // Arrange
        Currency sut = new CurrencyImpl(
            "singular",
            "plural",
            "$",
            2,
            true,
            BigDecimal.TEN
        );

        // Act
        int actual = sut.defaultFractionDigits();
        int expected = 2;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void isDefault_WithValueOfTrue_ShouldReturnTrue() {
        // Arrange
        Currency sut = new CurrencyImpl(
            "singular",
            "plural",
            "$",
            2,
            true,
            BigDecimal.TEN
        );

        // Act
        boolean actual = sut.isDefault();

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void isDefault_WithValueOfFalse_ShouldReturnFalse() {
        // Arrange
        Currency sut = new CurrencyImpl(
            "singular",
            "plural",
            "$",
            2,
            false,
            BigDecimal.TEN
        );

        // Act
        boolean actual = sut.isDefault();

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void defaultBalance_WithValueOfTen_ShouldReturnTen() {
        // Arrange
        CurrencyImpl sut = new CurrencyImpl(
            "singular",
            "plural",
            "$",
            2,
            true,
            BigDecimal.TEN
        );

        // Act
        BigDecimal actual = sut.defaultBalance();
        BigDecimal expected = BigDecimal.TEN;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void defaultBalance_WithValueOfOne_ShouldReturnOne() {
        // Arrange
        CurrencyImpl sut = new CurrencyImpl(
            "singular",
            "plural",
            "$",
            2,
            true,
            BigDecimal.ONE
        );

        // Act
        BigDecimal actual = sut.defaultBalance();
        BigDecimal expected = BigDecimal.ONE;

        // Assert
        assertEquals(expected, actual);
    }
}
