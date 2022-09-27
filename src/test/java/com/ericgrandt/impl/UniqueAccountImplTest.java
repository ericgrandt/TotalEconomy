package com.ericgrandt.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.UniqueAccount;

@ExtendWith(MockitoExtension.class)
public class UniqueAccountImplTest {
    @Test
    @Tag("Unit")
    public void displayName_ShouldReturnDisplayName() {
        // Arrange
        UUID playerUUID = UUID.randomUUID();
        Map<Currency, BigDecimal> balances = new HashMap<>();
        UniqueAccountImpl sut = new UniqueAccountImpl(playerUUID, balances);

        // Act
        Component actual = sut.displayName();
        Component expected = Component.text(playerUUID.toString());

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void equals_WithEqualObjects_ShouldReturnTrue() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        UniqueAccount uniqueAccount1 = new UniqueAccountImpl(
            uuid,
            new HashMap<>()
        );
        UniqueAccount uniqueAccount2 = new UniqueAccountImpl(
            uuid,
            new HashMap<>()
        );

        // Act
        boolean actual = uniqueAccount1.equals(uniqueAccount2);

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void equals_WithSameObject_ShouldReturnTrue() {
        // Arrange
        UniqueAccount uniqueAccount = new UniqueAccountImpl(
            UUID.randomUUID(),
            new HashMap<>()
        );

        // Act
        boolean actual = uniqueAccount.equals(uniqueAccount);

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void equals_WithNullObject_ShouldReturnFalse() {
        // Arrange
        UniqueAccount uniqueAccount = new UniqueAccountImpl(
            UUID.randomUUID(),
            new HashMap<>()
        );

        // Act
        boolean actual = uniqueAccount.equals(null);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void equals_WithWrongClass_ShouldReturnFalse() {
        // Arrange
        UniqueAccount uniqueAccount1 = new UniqueAccountImpl(
            UUID.randomUUID(),
            new HashMap<>()
        );
        Object uniqueAccount2 = new Object();

        // Act
        boolean actual = uniqueAccount1.equals(uniqueAccount2);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void equals_WithDifferentPlayerUuid_ShouldReturnFalse() {
        // Arrange
        UniqueAccount uniqueAccount1 = new UniqueAccountImpl(
            UUID.randomUUID(),
            new HashMap<>()
        );
        UniqueAccount uniqueAccount2 = new UniqueAccountImpl(
            UUID.randomUUID(),
            new HashMap<>()
        );

        // Act
        boolean actual = uniqueAccount1.equals(uniqueAccount2);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void equals_WithDifferentBalances_ShouldReturnFalse() {
        // Arrange
        UniqueAccount uniqueAccount1 = new UniqueAccountImpl(
            UUID.randomUUID(),
            Collections.singletonMap(mock(Currency.class), BigDecimal.ZERO)
        );
        UniqueAccount uniqueAccount2 = new UniqueAccountImpl(
            UUID.randomUUID(),
            Collections.singletonMap(mock(Currency.class), BigDecimal.TEN)
        );

        // Act
        boolean actual = uniqueAccount1.equals(uniqueAccount2);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void hashCode_ShouldReturnCorrectHashCode() {
        // Arrange
        UniqueAccount sut = new UniqueAccountImpl(
            UUID.fromString("051cfed0-9046-4e50-a7b4-6dcba5ccaa23"),
            new HashMap<>()
        );

        // Act
        int actual = sut.hashCode();
        int expected = 1294759320;

        // Assert
        assertEquals(expected, actual);
    }
}
