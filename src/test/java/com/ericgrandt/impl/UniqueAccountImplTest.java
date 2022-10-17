package com.ericgrandt.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spongepowered.api.service.economy.account.UniqueAccount;

@ExtendWith(MockitoExtension.class)
public class UniqueAccountImplTest {
    @Test
    @Tag("Unit")
    public void displayName_ShouldReturnDisplayName() {
        // Arrange
        UUID playerUUID = UUID.randomUUID();
        UniqueAccount sut = new UniqueAccountImpl(playerUUID);

        // Act
        Component actual = sut.displayName();
        Component expected = Component.text(playerUUID.toString());

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void identifier_ShouldReturnUuidAsString() {
        // Arrange
        UUID playerUUID = UUID.randomUUID();
        UniqueAccount sut = new UniqueAccountImpl(playerUUID);

        // Act
        String actual = sut.identifier();
        String expected = playerUUID.toString();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void uniqueId_ShouldReturnUniqueAccountId() {
        // Arrange
        UUID playerUUID = UUID.randomUUID();
        UniqueAccount sut = new UniqueAccountImpl(playerUUID);

        // Act
        UUID actual = sut.uniqueId();

        // Assert
        assertEquals(playerUUID, actual);
    }

    @Test
    @Tag("Unit")
    public void equals_WithEqualObjects_ShouldReturnTrue() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        UniqueAccount uniqueAccount1 = new UniqueAccountImpl(uuid);
        UniqueAccount uniqueAccount2 = new UniqueAccountImpl(uuid);

        // Act
        boolean actual = uniqueAccount1.equals(uniqueAccount2);

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void equals_WithSameObject_ShouldReturnTrue() {
        // Arrange
        UniqueAccount uniqueAccount = new UniqueAccountImpl(UUID.randomUUID());

        // Act
        boolean actual = uniqueAccount.equals(uniqueAccount);

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void equals_WithNullObject_ShouldReturnFalse() {
        // Arrange
        UniqueAccount uniqueAccount = new UniqueAccountImpl(UUID.randomUUID());

        // Act
        boolean actual = uniqueAccount.equals(null);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void equals_WithWrongClass_ShouldReturnFalse() {
        // Arrange
        UniqueAccount uniqueAccount1 = new UniqueAccountImpl(UUID.randomUUID());
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
        UniqueAccount uniqueAccount1 = new UniqueAccountImpl(UUID.randomUUID());
        UniqueAccount uniqueAccount2 = new UniqueAccountImpl(UUID.randomUUID());

        // Act
        boolean actual = uniqueAccount1.equals(uniqueAccount2);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void hashCode_ShouldReturnCorrectHashCode() {
        // Arrange
        UniqueAccount sut1 = new UniqueAccountImpl(UUID.fromString("051cfed0-9046-4e50-a7b4-6dcba5ccaa23"));
        UniqueAccount sut2 = new UniqueAccountImpl(UUID.fromString("051cfed0-9046-4e50-a7b4-6dcba5ccaa23"));

        // Act
        int actual1 = sut1.hashCode();
        int actual2 = sut2.hashCode();

        // Assert
        assertEquals(actual1, actual2);
    }
}
