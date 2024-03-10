package com.ericgrandt.totaleconomy.commonimpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.UUID;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

@ExtendWith(MockitoExtension.class)
public class SpongePlayerTest {
    @Mock
    private ServerPlayer playerMock;

    @Test
    @Tag("Unit")
    public void getUniqueId_ShouldReturnUuid() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        when(playerMock.uniqueId()).thenReturn(uuid);

        SpongePlayer sut = new SpongePlayer(playerMock);

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
        when(playerMock.name()).thenReturn(name);

        SpongePlayer sut = new SpongePlayer(playerMock);

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
        SpongePlayer sut = new SpongePlayer(null);

        // Act
        boolean actual = sut.isNull();

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void isNull_WithNonNullPlayer_ShouldReturnFalse() {
        // Arrange
        SpongePlayer sut = new SpongePlayer(playerMock);

        // Act
        boolean actual = sut.isNull();

        // Assert
        assertFalse(actual);
    }
}
