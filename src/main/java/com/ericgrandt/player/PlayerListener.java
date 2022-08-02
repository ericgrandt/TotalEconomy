package com.ericgrandt.player;

import com.ericgrandt.TotalEconomy;
import com.ericgrandt.data.TempData;
import com.ericgrandt.services.TEEconomyService;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;

import java.util.List;
import java.util.Optional;

public class PlayerListener {
    private final TEEconomyService economyService;

    public PlayerListener(TEEconomyService economyService) {
        this.economyService = economyService;
    }

    @Listener
    public void onPlayerJoin(ServerSideConnectionEvent.Join event) {
        ServerPlayer player = event.player();

        economyService.findOrCreateAccount(player.uniqueId());
        if(player.hasPlayedBefore()) return;
        Optional<List<TempData>> tempdata = TotalEconomy.getInstance().getDefaultConfiguration().get().getTempData(player.name());
        if(!tempdata.isPresent()) return;
        tempdata.get().forEach(temp -> {
            economyService.findOrCreateAccount(player.uniqueId()).ifPresent(account -> {
                account.setBalance(temp.getCurrency(), account.balance(temp.getCurrency()).add(temp.getAmount()));
            });
        });
    }
}