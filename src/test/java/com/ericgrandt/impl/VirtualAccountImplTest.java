package com.ericgrandt.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;

@ExtendWith(MockitoExtension.class)
public class VirtualAccountImplTest {
    @Test
    @Tag("Unit")
    public void displayName_ShouldReturnDisplayName() {
        // Arrange
        String identifier = "identifier";
        Map<Currency, BigDecimal> balances = new HashMap<>();
        Account sut = new VirtualAccountImpl(identifier, balances);

        // Act
        Component actual = sut.displayName();
        Component expected = Component.text(identifier);

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void equals_WithEqualObjects_ShouldReturnTrue() {
        // Arrange
        String identifier = "identifier";
        Account account1 = new VirtualAccountImpl(
            identifier,
            new HashMap<>()
        );
        Account account2 = new VirtualAccountImpl(
            identifier,
            new HashMap<>()
        );

        // Act
        boolean actual = account1.equals(account2);

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void equals_WithSameObject_ShouldReturnTrue() {
        // Arrange
        Account account = new VirtualAccountImpl(
            "identifier",
            new HashMap<>()
        );

        // Act
        boolean actual = account.equals(account);

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void equals_WithNullObject_ShouldReturnFalse() {
        // Arrange
        Account account = new VirtualAccountImpl(
            "identifier",
            new HashMap<>()
        );

        // Act
        boolean actual = account.equals(null);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void equals_WithWrongClass_ShouldReturnFalse() {
        // Arrange
        Account account1 = new VirtualAccountImpl(
            "identifier",
            new HashMap<>()
        );
        Object account2 = new Object();

        // Act
        boolean actual = account1.equals(account2);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void equals_WithDifferentPlayerUuid_ShouldReturnFalse() {
        // Arrange
        Account account1 = new VirtualAccountImpl(
            "identifier",
            new HashMap<>()
        );
        Account account2 = new VirtualAccountImpl(
            "identifier2",
            new HashMap<>()
        );

        // Act
        boolean actual = account1.equals(account2);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void equals_WithDifferentBalances_ShouldReturnFalse() {
        // Arrange
        Account account1 = new VirtualAccountImpl(
            "identifier",
            Collections.singletonMap(mock(Currency.class), BigDecimal.ZERO)

        );
        Account account2 = new VirtualAccountImpl(
            "identifier2",
            Collections.singletonMap(mock(Currency.class), BigDecimal.TEN)
        );

        // Act
        boolean actual = account1.equals(account2);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void hashCode_ShouldReturnCorrectHashCode() {
        // Arrange
        Account account1 = new VirtualAccountImpl(
            "identifier",
            new HashMap<>()
        );
        Account account2 = new VirtualAccountImpl(
            "identifier",
            new HashMap<>()
        );

        // Act
        int actual1 = account1.hashCode();
        int actual2 = account2.hashCode();

        // Assert
        assertEquals(actual1, actual2);
    }
}
