package com.erigitic.shop;

import com.erigitic.TotalEconomy;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.text.Text;

public class ShopInventory {
    private final TotalEconomy plugin;
    private final String shopName;

    public ShopInventory(String shopName) {
        this.plugin = TotalEconomy.getPlugin();
        this.shopName = shopName;
    }

    public Inventory createInventory() {
        Inventory inventory = Inventory.builder()
            .of(InventoryArchetypes.CHEST)
            .property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of(shopName)))
            .build(plugin);

        return inventory;
    }
}
