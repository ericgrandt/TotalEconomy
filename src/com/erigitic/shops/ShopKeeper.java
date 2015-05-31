package com.erigitic.shops;

import com.erigitic.main.TotalEconomy;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Villager;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.entity.player.PlayerInteractEntityEvent;

/**
 * Created by Erigitic on 5/29/2015.
 */
public class ShopKeeper {

    private TotalEconomy totalEconomy;

    public ShopKeeper(TotalEconomy totalEconomy) {
        this.totalEconomy = totalEconomy;
    }

    @Subscribe
    public void onShopKeeperInteract(PlayerInteractEntityEvent event) {
        Player player = event.getUser();
        Entity entity = event.getTargetEntity();

        if (entity instanceof Villager) {
            Villager shopKeeper = (Villager) entity;


        }
    }
}
