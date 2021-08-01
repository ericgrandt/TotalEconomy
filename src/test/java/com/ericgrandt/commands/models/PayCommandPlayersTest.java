package com.ericgrandt.commands.models;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spongepowered.api.entity.living.player.Player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

@Tag("Unit")
@ExtendWith(MockitoExtension.class)
public class PayCommandPlayersTest {
    @Test
    public void getFromPlayer_ShouldReturnPlayer() {
        Player fromPlayer = mock(Player.class);
        PayCommandPlayers sut = new PayCommandPlayers(fromPlayer, null);

        Player result = sut.getFromPlayer();

        assertEquals(fromPlayer, result);
    }

    @Test
    public void getToPlayer_ShouldReturnPlayer() {
        Player toPlayer = mock(Player.class);
        PayCommandPlayers sut = new PayCommandPlayers(null, toPlayer);

        Player result = sut.getToPlayer();

        assertEquals(toPlayer, result);
    }
}
