package com.ericgrandt.domain;

import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("Unit")
public class TECurrencyTest {
    @Test
    public void displayName_ShouldReturnDisplayName() {
        TECurrency sut = new TECurrency(1, "Dollar", "Dollars", "$", 0, true);

        Component result = sut.displayName();
        Component expectedResult = Component.text("Dollar");

        assertEquals(expectedResult, result);
    }

    @Test
    public void pluralDisplayName_ShouldReturnPluralDisplayName() {
        TECurrency sut = new TECurrency(1, "Dollar", "Dollars", "$", 0, true);

        Component result = sut.pluralDisplayName();
        Component expectedResult = Component.text("Dollars");

        assertEquals(expectedResult, result);
    }

    @Test
    public void symbol_ShouldReturnSymbol() {
        TECurrency sut = new TECurrency(1, "Dollar", "Dollars", "$", 0, true);

        Component result = sut.symbol();
        Component expectedResult = Component.text("$");

        assertEquals(expectedResult, result);
    }

    @Test
    public void format_WithZeroFractionDigits_ShouldReturnCorrectFormattedAmount() {
        TECurrency sut = new TECurrency(1, "Dollar", "Dollars", "$", 0, true);

        Component result = sut.format(BigDecimal.TEN, 0);
        Component expectedResult = Component.text("$10");

        assertEquals(expectedResult, result);
    }

    @Test
    public void format_WithTwoFractionDigits_ShouldReturnCorrectFormattedAmount() {
        TECurrency sut = new TECurrency(1, "Dollar", "Dollars", "$", 2, true);

        Component result = sut.format(BigDecimal.TEN, 2);
        Component expectedResult = Component.text("$10.00");

        assertEquals(expectedResult, result);
    }

    @Test
    public void defaultFractionDigits_ShouldReturnNumberOfFractionDigits() {
        TECurrency sut = new TECurrency(1, "Dollar", "Dollars", "$", 4, true);

        int result = sut.defaultFractionDigits();
        int expectedResult = 4;

        assertEquals(expectedResult, result);
    }

    @Test
    public void isDefault_WithTrue_ShouldReturnTrue() {
        TECurrency sut = new TECurrency(1, "Dollar", "Dollars", "$", 9, true);

        boolean result = sut.isDefault();

        assertTrue(result);
    }

    @Test
    public void isDefault_WithFalse_ShouldReturnFalse() {
        TECurrency sut = new TECurrency(1, "Dollar", "Dollars", "$", 0, false);

        boolean result = sut.isDefault();

        assertFalse(result);
    }

    @Test
    public void getId_ShouldReturnId() {
        TECurrency sut = new TECurrency(1, "Dollar", "Dollars", "$", 0, true);

        int result = sut.getId();
        int expectedResult = 1;

        assertEquals(expectedResult, result);
    }

    @Test
    public void equals_WithSameObject_ShouldReturnTrue() {
        TECurrency sut = new TECurrency(1, "Dollar", "Dollars", "$", 0, true);

        boolean result = sut.equals(sut);

        assertTrue(result);
    }

    @Test
    public void equals_WithEqualObjects_ShouldReturnTrue() {
        TECurrency sut = new TECurrency(1, "Dollar", "Dollars", "$", 0, true);
        TECurrency currency = new TECurrency(1, "Dollar", "Dollars", "$", 0, true);

        boolean result = sut.equals(currency);

        assertTrue(result);
    }

    @Test
    public void equals_WithNullObject_ShouldReturnFalse() {
        TECurrency sut = new TECurrency(1, "Dollar", "Dollars", "$", 0, true);

        boolean result = sut.equals(null);

        assertFalse(result);
    }

    @Test
    public void equals_WithDifferentObjectClass_ShouldReturnFalse() {
        TECurrency sut = new TECurrency(1, "Dollar", "Dollars", "$", 0, true);

        boolean result = sut.equals("123");

        assertFalse(result);
    }

    @Test
    public void equals_WithDifferentId_ShouldReturnFalse() {
        TECurrency sut = new TECurrency(1, "Dollar", "Dollars", "$", 0, true);
        TECurrency currency = new TECurrency(2, "Dollar", "Dollars", "$", 0, true);

        boolean result = sut.equals(currency);

        assertFalse(result);
    }

    @Test
    public void equals_WithDifferentSingularName_ShouldReturnFalse() {
        TECurrency sut = new TECurrency(1, "Dollar", "Dollars", "$", 0, true);
        TECurrency currency = new TECurrency(1, "Euro", "Dollars", "$", 0, true);

        boolean result = sut.equals(currency);

        assertFalse(result);
    }

    @Test
    public void equals_WithDifferentPluralName_ShouldReturnFalse() {
        TECurrency sut = new TECurrency(1, "Dollar", "Dollars", "$", 0, true);
        TECurrency currency = new TECurrency(1, "Dollar", "Euros", "$", 0, true);

        boolean result = sut.equals(currency);

        assertFalse(result);
    }

    @Test
    public void equals_WithDifferentSymbol_ShouldReturnFalse() {
        TECurrency sut = new TECurrency(1, "Dollar", "Dollars", "$", 0, true);
        TECurrency currency = new TECurrency(1, "Dollar", "Dollars", "E", 0, true);

        boolean result = sut.equals(currency);

        assertFalse(result);
    }

    @Test
    public void equals_WithDifferentNumFractionDigits_ShouldReturnFalse() {
        TECurrency sut = new TECurrency(1, "Dollar", "Dollars", "$", 0, true);
        TECurrency currency = new TECurrency(1, "Dollar", "Dollars", "$", 1, true);

        boolean result = sut.equals(currency);

        assertFalse(result);
    }

    @Test
    public void equals_WithDifferentDefault_ShouldReturnFalse() {
        TECurrency sut = new TECurrency(1, "Dollar", "Dollars", "$", 0, true);
        TECurrency currency = new TECurrency(1, "Dollar", "Dollars", "$", 0, false);

        boolean result = sut.equals(currency);

        assertFalse(result);
    }

    @Test
    public void hashCode_ShouldReturnHashCode() {
        TECurrency sut = new TECurrency(1, "Dollar", "Dollars", "$", 0, true);

        int result = sut.hashCode();
        int expectedResult = 822424632;

        assertEquals(expectedResult, result);
    }
}
