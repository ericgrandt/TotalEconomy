package com.erigitic.player;

import com.erigitic.TotalEconomy;
import com.erigitic.economy.TEEconomyService;
import com.erigitic.shop.data.ShopKeys;
import org.slf4j.Logger;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.text.Text;

public class PlayerListener {
    private final TotalEconomy plugin;
    private final Logger logger;
    private final TEEconomyService economyService;

    public PlayerListener() {
        plugin = TotalEconomy.getPlugin();
        logger = plugin.getLogger();
        economyService = plugin.getEconomyService();
    }

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event) {
        Player player = event.getTargetEntity();

        economyService.getOrCreateAccount(player.getUniqueId());
    }

    @Listener
    public void onBlockHit(InteractBlockEvent event) {
        Player player = (Player) event.getSource();

        player.sendMessage(Text.of(event.getTargetBlock().getState().getType().getName()));
        player.sendMessage(Text.of(event.getTargetBlock().getState().getName()));
        player.sendMessage(Text.of(event.getTargetBlock().getState().getId()));
    }

    @Listener
    public void onVillageTouch(InteractEntityEvent.Secondary event) {
        event.setCancelled(true);
        Player player = (Player) event.getSource();
        player.sendMessage(Text.of(event.getTargetEntity().get(ShopKeys.SHOP_ID).get()));

//        Inventory inventory = Inventory.builder()
//            .of(InventoryArchetypes.CHEST)
//            .property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of("MY SHOP")))
//            .build(plugin);
//
//        player.openInventory(inventory);
    }
}