package com.ericgrandt.data.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class CurrencyDtoTest {
    @Test
    @Tag("Unit")
    public void getId_ShouldReturnId() {
        // Arrange
        CurrencyDto sut = new CurrencyDto(
            1,
            "nameSingular",
            "namePlural",
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
    public void getNameSingular_ShouldReturnNameSingular() {
        // Arrange
        CurrencyDto sut = new CurrencyDto(
            1,
            "nameSingular",
            "namePlural",
            "$",
            2,
            true
        );

        // Act
        String actual = sut.getNameSingular();
        String expected = "nameSingular";

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void getNamePlural_ShouldReturnNamePlural() {
        // Arrange
        CurrencyDto sut = new CurrencyDto(
            1,
            "nameSingular",
            "namePlural",
            "$",
            2,
            true
        );

        // Act
        String actual = sut.getNamePlural();
        String expected = "namePlural";

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void getSymbol_ShouldReturnSymbol() {
        // Arrange
        CurrencyDto sut = new CurrencyDto(
            1,
            "nameSingular",
            "namePlural",
            "$",
            2,
            true
        );

        // Act
        String actual = sut.getSymbol();
        String expected = "$";

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void getNumFractionDigits_ShouldReturnNumFractionDigits() {
        // Arrange
        CurrencyDto sut = new CurrencyDto(
            1,
            "nameSingular",
            "namePlural",
            "$",
            2,
            true
        );

        // Act
        int actual = sut.getNumFractionDigits();
        int expected = 2;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void isDefault_ShouldReturnIsDefault() {
        // Arrange
        CurrencyDto sut = new CurrencyDto(
            1,
            "nameSingular",
            "namePlural",
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
    public void equals_WithSameObject_ShouldReturnTrue() {
        // Arrange
        CurrencyDto sut = new CurrencyDto(
            1,
            "singular",
            "plural",
            "$",
            2,
            true
        );

        // Act
        boolean actual = sut.equals(sut);

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void equals_WithEqualObjects_ShouldReturnTrue() {
        // Arrange
        CurrencyDto sut = new CurrencyDto(
            1,
            "singular",
            "plural",
            "$",
            2,
            true
        );
        CurrencyDto currencyDto = new CurrencyDto(
            1,
            "singular",
            "plural",
            "$",
            2,
            true
        );

        // Act
        boolean actual = sut.equals(currencyDto);

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void equals_WithNullObject_ShouldReturnFalse() {
        // Arrange
        CurrencyDto sut = new CurrencyDto(
            1,
            "singular",
            "plural",
            "$",
            2,
            true
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
        CurrencyDto sut = new CurrencyDto(
            1,
            "singular",
            "plural",
            "$",
            2,
            true
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
        CurrencyDto sut = new CurrencyDto(
            1,
            "singular",
            "plural",
            "$",
            2,
            true
        );
        CurrencyDto currencyDto = new CurrencyDto(
            2,
            "singular",
            "plural",
            "$",
            2,
            true
        );

        // Act
        boolean actual = sut.equals(currencyDto);

        // Assert
        assertFalse(actual);
    }

    @Tag("Unit")
    public void equals_WithDifferentNumFractionDigits_ShouldReturnFalse() {
        // Arrange
        CurrencyDto sut = new CurrencyDto(
            1,
            "singular",
            "plural",
            "$",
            2,
            true
        );
        CurrencyDto currencyDto = new CurrencyDto(
            1,
            "singular",
            "plural",
            "$",
            3,
            true
        );

        // Act
        boolean actual = sut.equals(currencyDto);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void equals_WithDifferentIsDefault_ShouldReturnFalse() {
        // Arrange
        CurrencyDto sut = new CurrencyDto(
            1,
            "singular",
            "plural",
            "$",
            2,
            true
        );
        CurrencyDto currencyDto = new CurrencyDto(
            1,
            "singular",
            "plural",
            "$",
            2,
            false
        );

        // Act
        boolean actual = sut.equals(currencyDto);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void equals_WithDifferentNameSingular_ShouldReturnFalse() {
        // Arrange
        CurrencyDto sut = new CurrencyDto(
            1,
            "singular",
            "plural",
            "$",
            2,
            true
        );
        CurrencyDto currencyDto = new CurrencyDto(
            1,
            "singular2",
            "plural",
            "$",
            2,
            true
        );

        // Act
        boolean actual = sut.equals(currencyDto);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void equals_WithDifferentNamePlural_ShouldReturnFalse() {
        // Arrange
        CurrencyDto sut = new CurrencyDto(
            1,
            "singular",
            "plural",
            "$",
            2,
            true
        );
        CurrencyDto currencyDto = new CurrencyDto(
            1,
            "singular",
            "plural2",
            "$",
            2,
            true
        );

        // Act
        boolean actual = sut.equals(currencyDto);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void equals_WithDifferentSymbol_ShouldReturnFalse() {
        // Arrange
        CurrencyDto sut = new CurrencyDto(
            1,
            "singular",
            "plural",
            "$",
            2,
            true
        );
        CurrencyDto currencyDto = new CurrencyDto(
            1,
            "singular",
            "plural",
            "&",
            2,
            true
        );

        // Act
        boolean actual = sut.equals(currencyDto);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void hashCode_WithSameObjects_ShouldReturnSameHashCode() {
        // Arrange
        CurrencyDto sut1 = new CurrencyDto(
            1,
            "singular",
            "plural",
            "$",
            2,
            true
        );
        CurrencyDto sut2 = new CurrencyDto(
            1,
            "singular",
            "plural",
            "$",
            2,
            true
        );

        // Act
        int actual1 = sut1.hashCode();
        int actual2 = sut2.hashCode();

        // Assert
        assertEquals(actual1, actual2);
    }
}
