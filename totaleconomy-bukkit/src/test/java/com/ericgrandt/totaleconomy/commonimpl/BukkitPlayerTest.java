package com.ericgrandt.totaleconomy.commonimpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.UUID;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BukkitPlayerTest {
    @Mock
    private Player playerMock;

    @Test
    @Tag("Unit")
    public void getUniqueId_ShouldReturnPlayerUuid() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        when(playerMock.getUniqueId()).thenReturn(uuid);

        BukkitPlayer sut = new BukkitPlayer(playerMock);

        // Act
        UUID actual = sut.getUniqueId();
        UUID expected = uuid;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void getName_ShouldReturnName() {
        // Arrange
        String name = "NiceName";
        when(playerMock.getName()).thenReturn(name);

        BukkitPlayer sut = new BukkitPlayer(playerMock);

        // Act
        String actual = sut.getName();
        String expected = name;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void isNull_WithNullPlayer_ShouldReturnTrue() {
        // Arrange
        BukkitPlayer sut = new BukkitPlayer(null);

        // Act
        boolean actual = sut.isNull();

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void isNull_WithNonNullPlayer_ShouldReturnFalse() {
        // Arrange
        BukkitPlayer sut = new BukkitPlayer(playerMock);

        // Act
        boolean actual = sut.isNull();

        // Assert
        assertFalse(actual);
    }
}
