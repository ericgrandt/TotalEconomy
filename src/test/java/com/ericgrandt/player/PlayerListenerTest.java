package com.ericgrandt.player;

import com.ericgrandt.TestUtils;
import com.ericgrandt.data.AccountData;
import com.ericgrandt.data.CurrencyData;
import com.ericgrandt.data.Database;
import com.ericgrandt.services.TEEconomyService;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;

import java.sql.SQLException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@Tag("Integration")
@ExtendWith(MockitoExtension.class)
public class PlayerListenerTest {
    private PlayerListener sut;

    @Mock
    private Database databaseMock;

    @Mock
    private ServerSideConnectionEvent.Join eventMock;

    @Mock
    private ServerPlayer playerMock;

    @Test
    public void onPlayerJoin_ForNewPlayer_ShouldCreateAccount() throws SQLException {
        // Arrange
        TestUtils.resetDb();
        TestUtils.seedCurrencies();

        when(databaseMock.getConnection())
            .thenReturn(TestUtils.getConnection())
            .thenReturn(TestUtils.getConnection())
            .thenReturn(TestUtils.getConnection())
            .thenReturn(TestUtils.getConnection())
            .thenReturn(TestUtils.getConnection());
        when(eventMock.player()).thenReturn(playerMock);
        when(playerMock.uniqueId()).thenReturn(UUID.fromString("62694fb0-07cc-4396-8d63-4f70646d75f0"));

        AccountData accountData = new AccountData(null, databaseMock);
        CurrencyData currencyData = new CurrencyData(null, databaseMock);
        TEEconomyService economyService = new TEEconomyService(accountData, currencyData);
        sut = new PlayerListener(economyService);

        // Act
        sut.onPlayerJoin(eventMock);

        boolean result = accountData.hasAccount(playerMock.uniqueId());

        // Assert
        assertTrue(result);
    }
}
