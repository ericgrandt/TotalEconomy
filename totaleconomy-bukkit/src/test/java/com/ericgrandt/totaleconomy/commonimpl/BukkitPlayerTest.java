package com.ericgrandt.totaleconomy.commonimpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.UUID;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BukkitPlayerTest {
    @Test
    @Tag("Unit")
    public void getUniqueId_ShouldReturnPlayerUuid() {
        // Arrange
        Player playerMock = mock(Player.class);
        UUID uuid = UUID.randomUUID();
        when(playerMock.getUniqueId()).thenReturn(uuid);

        BukkitPlayer sut = new BukkitPlayer(playerMock);

        // Act
        UUID actual = sut.getUniqueId();
        UUID expected = uuid;

        // Assert
        assertEquals(expected, actual);
    }
}
