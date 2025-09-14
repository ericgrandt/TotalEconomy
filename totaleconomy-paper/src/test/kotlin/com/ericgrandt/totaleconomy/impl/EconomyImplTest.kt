package com.ericgrandt.totaleconomy.impl

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.math.exp

class EconomyImplTest {
    private val sut: EconomyImpl = EconomyImpl()

    @Test
    fun isEnabled_ShouldReturnTrue() {
        // Act
        val actual = sut.isEnabled

        // Assert
        assertTrue(actual)
    }

    @Test
    fun getName_ShouldReturnTotalEconomy() {
        // Act
        val actual =  sut.name

        // Assert
        assertEquals("Total Economy", actual)
    }

    @Test
    fun hasBankSupport_ShouldReturnFalse() {
        // Act
        val actual = sut.hasBankSupport()

        // Assert
        assertFalse(actual)
    }

    @Test
    fun fractionalDigits_ShouldReturnTwo() {
        // Act
        val actual = sut.fractionalDigits()

        // Assert
        assertEquals(2, actual)
    }

    @Test
    fun format_WithAmountOfOne_ShouldReturnSingularCurrencyName() {
        // Arrange
        val amount = 1.0

        // Act
        val actual = sut.format(amount)
        val expected = "1.00 Diamond"

        // Arrange
        assertEquals(expected, actual)
    }

    @Test
    fun format_WithAmountLessThanOne_ShouldReturnPluralCurrencyName() {
        // Arrange
        val amount = 0.03

        // Act
        val actual = sut.format(amount)
        val expected = "0.03 Diamonds"

        // Arrange
        assertEquals(expected, actual)
    }

    @Test
    fun format_WithAmountGreaterThanOne_ShouldReturnPluralCurrencyName() {
        // Arrange
        val amount = 12.156

        // Act
        val actual = sut.format(amount)
        val expected = "12.16 Diamonds"

        // Arrange
        assertEquals(expected, actual)
    }

    @Test
    fun currencyNamePlural_ShouldReturnDiamonds() {
        // Act
        val actual = sut.currencyNamePlural()
        val expected = "Diamonds"

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun currencyNameSingular_ShouldReturnDiamond() {
        // Act
        val actual = sut.currencyNameSingular()
        val expected = "Diamond"

        // Assert
        assertEquals(expected, actual)
    }
}