package com.ericgrandt.data.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BalanceDtoTest {
    @Test
    @Tag("Unit")
    public void getId_ShouldReturnId() {
        // Arrange
        BalanceDto sut = new BalanceDto(
            "id",
            "account-id",
            1,
            BigDecimal.ONE
        );

        // Act
        String actual = sut.getId();
        String expected = "id";

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void getAccountId_ShouldReturnAccountId() {
        // Arrange
        BalanceDto sut = new BalanceDto(
            "id",
            "account-id",
            1,
            BigDecimal.ONE
        );

        // Act
        String actual = sut.getAccountId();
        String expected = "account-id";

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void getCurrencyId_ShouldReturnCurrencyId() {
        // Arrange
        BalanceDto sut = new BalanceDto(
            "id",
            "account-id",
            1,
            BigDecimal.ONE
        );

        // Act
        int actual = sut.getCurrencyId();
        int expected = 1;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void getBalance_ShouldReturnBalance() {
        // Arrange
        BalanceDto sut = new BalanceDto(
            "id",
            "account-id",
            1,
            BigDecimal.ONE
        );

        // Act
        BigDecimal actual = sut.getBalance();
        BigDecimal expected = BigDecimal.ONE;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void equals_WithSameObject_ShouldReturnTrue() {
        // Arrange
        BalanceDto sut = new BalanceDto(
            "id",
            "account-id",
            1,
            BigDecimal.ONE
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
        BalanceDto sut = new BalanceDto(
            "id",
            "account-id",
            1,
            BigDecimal.ONE
        );
        BalanceDto o = new BalanceDto(
            "id",
            "account-id",
            1,
            BigDecimal.ONE
        );

        // Act
        boolean actual = sut.equals(o);

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void equals_WithNullObject_ShouldReturnFalse() {
        // Arrange
        BalanceDto sut = new BalanceDto(
            "id",
            "account-id",
            1,
            BigDecimal.ONE
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
        BalanceDto sut = new BalanceDto(
            "id",
            "account-id",
            1,
            BigDecimal.ONE
        );
        Object o = new Object();

        // Act
        boolean actual = sut.equals(o);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void equals_WithDifferentCurrencyId_ShouldReturnFalse() {
        // Arrange
        BalanceDto sut = new BalanceDto(
            "id",
            "account-id",
            1,
            BigDecimal.ONE
        );
        BalanceDto o = new BalanceDto(
            "id",
            "account-id",
            2,
            BigDecimal.ONE
        );

        // Act
        boolean actual = sut.equals(o);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void equals_WithDifferentId_ShouldReturnFalse() {
        // Arrange
        BalanceDto sut = new BalanceDto(
            "id",
            "account-id",
            1,
            BigDecimal.ONE
        );
        BalanceDto o = new BalanceDto(
            "different-id",
            "account-id",
            1,
            BigDecimal.ONE
        );

        // Act
        boolean actual = sut.equals(o);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void equals_WithDifferentAccountId_ShouldReturnFalse() {
        // Arrange
        BalanceDto sut = new BalanceDto(
            "id",
            "account-id",
            1,
            BigDecimal.ONE
        );
        BalanceDto o = new BalanceDto(
            "id",
            "different-account-id",
            1,
            BigDecimal.ONE
        );

        // Act
        boolean actual = sut.equals(o);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void equals_WithDifferentBalance_ShouldReturnFalse() {
        // Arrange
        BalanceDto sut = new BalanceDto(
            "id",
            "account-id",
            1,
            BigDecimal.ONE
        );
        BalanceDto o = new BalanceDto(
            "id",
            "account-id",
            1,
            BigDecimal.TEN
        );

        // Act
        boolean actual = sut.equals(o);

        // Assert
        assertFalse(actual);
    }
}
