package com.ericgrandt.totaleconomy.commonimpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.UUID;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

@ExtendWith(MockitoExtension.class)
public class SpongePlayerTest {
    @Test
    @Tag("Unit")
    public void getUniqueId_ShouldReturnUuid() {
        // Arrange
        ServerPlayer playerMock = mock(ServerPlayer.class);
        UUID uuid = UUID.randomUUID();
        when(playerMock.uniqueId()).thenReturn(uuid);

        SpongePlayer sut = new SpongePlayer(playerMock);

        // Act
        UUID actual = sut.getUniqueId();
        UUID expected = uuid;

        // Assert
        assertEquals(expected, actual);
    }
}
