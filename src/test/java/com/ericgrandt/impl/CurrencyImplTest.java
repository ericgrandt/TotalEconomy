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
    public void getId_ShouldReturnId() {
        // Arrange
        CurrencyImpl sut = new CurrencyImpl(
            1,
            "singular",
            "plural",
            "$",
            2,
            true
        );

        // Act
        int actual = sut.getId();
        int expected = 1;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void displayName_ShouldReturnCorrectComponent() {
        // Arrange
        Currency sut = new CurrencyImpl(
            1,
            "singular",
            "plural",
            "$",
            2,
            true
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
            1,
            "singular",
            "plural",
            "$",
            2,
            true
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
            1,
            "singular",
            "plural",
            "$",
            2,
            true
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
            1,
            "singular",
            "plural",
            "$",
            2,
            true
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
            1,
            "singular",
            "plural",
            "$",
            3,
            true
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
            1,
            "singular",
            "plural",
            "$",
            2,
            true
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
            1,
            "singular",
            "plural",
            "$",
            2,
            true
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
            1,
            "singular",
            "plural",
            "$",
            2,
            true
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
            1,
            "singular",
            "plural",
            "$",
            2,
            false
        );

        // Act
        boolean actual = sut.isDefault();

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void equals_WithSameObject_ShouldReturnTrue() {
        // Arrange
        Currency sut = new CurrencyImpl(
            1,
            "singular",
            "plural",
            "$",
            2,
            false
        );

        // Act
        boolean actual = sut.equals(sut);

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void equals_WithNullObject_ShouldReturnFalse() {
        // Arrange
        Currency sut = new CurrencyImpl(
            1,
            "singular",
            "plural",
            "$",
            2,
            false
        );

        // Act
        boolean actual = sut.equals(null);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void equals_WithDifferentClass_ShouldReturnFalse() {
        // Arrange
        Currency sut = new CurrencyImpl(
            1,
            "singular",
            "plural",
            "$",
            2,
            false
        );
        Object obj = new Object();

        // Act
        boolean actual = sut.equals(obj);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void equals_WithDifferentId_ShouldReturnFalse() {
        // Arrange
        Currency sut = new CurrencyImpl(
            1,
            "singular",
            "plural",
            "$",
            2,
            false
        );
        Currency o = new CurrencyImpl(
            2,
            "singular",
            "plural",
            "$",
            2,
            false
        );

        // Act
        boolean actual = sut.equals(o);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void equals_WithDifferentNumFractionDigits_ShouldReturnFalse() {
        // Arrange
        Currency sut = new CurrencyImpl(
            1,
            "singular",
            "plural",
            "$",
            2,
            false
        );
        Currency o = new CurrencyImpl(
            1,
            "singular",
            "plural",
            "$",
            1,
            false
        );

        // Act
        boolean actual = sut.equals(o);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void equals_WithDifferentIsDefault_ShouldReturnFalse() {
        // Arrange
        Currency sut = new CurrencyImpl(
            1,
            "singular",
            "plural",
            "$",
            2,
            false
        );
        Currency o = new CurrencyImpl(
            1,
            "singular",
            "plural",
            "$",
            2,
            true
        );

        // Act
        boolean actual = sut.equals(o);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void equals_WithDifferentNameSingular_ShouldReturnFalse() {
        // Arrange
        Currency sut = new CurrencyImpl(
            1,
            "singular",
            "plural",
            "$",
            2,
            false
        );
        Currency o = new CurrencyImpl(
            1,
            "different",
            "plural",
            "$",
            2,
            false
        );

        // Act
        boolean actual = sut.equals(o);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void equals_WithDifferentNamePlural_ShouldReturnFalse() {
        // Arrange
        Currency sut = new CurrencyImpl(
            1,
            "singular",
            "plural",
            "$",
            2,
            false
        );
        Currency o = new CurrencyImpl(
            1,
            "singular",
            "different",
            "$",
            2,
            false
        );

        // Act
        boolean actual = sut.equals(o);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void equals_WithDifferentSymbol_ShouldReturnFalse() {
        // Arrange
        Currency sut = new CurrencyImpl(
            1,
            "singular",
            "plural",
            "$",
            2,
            false
        );
        Currency o = new CurrencyImpl(
            1,
            "singular",
            "plural",
            "@",
            2,
            false
        );

        // Act
        boolean actual = sut.equals(o);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void equals_WithEqualObjects_ShouldReturnFalse() {
        // Arrange
        Currency sut = new CurrencyImpl(
            1,
            "singular",
            "plural",
            "$",
            2,
            false
        );
        Currency o = new CurrencyImpl(
            1,
            "singular",
            "plural",
            "$",
            2,
            false
        );

        // Act
        boolean actual = sut.equals(o);

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void hashCode_WithEqualObjects_ShouldReturnSameHashCode() {
        // Arrange
        Currency sut = new CurrencyImpl(
            1,
            "singular",
            "plural",
            "$",
            2,
            false
        );
        Currency o = new CurrencyImpl(
            1,
            "singular",
            "plural",
            "$",
            2,
            false
        );

        // Act
        int actual = sut.hashCode();
        int expected = o.hashCode();

        // Assert
        assertEquals(expected, actual);
    }
}
