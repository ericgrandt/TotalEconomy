package com.erigitic.shop;

import com.erigitic.economy.TECurrency;
import java.math.BigDecimal;
import org.spongepowered.api.item.inventory.ItemStack;

public class ShopItem {
    private final TECurrency currency;
    private final ItemStack item;
    private final BigDecimal price;

    public ShopItem(TECurrency currency, ItemStack item, BigDecimal price) {
        this.currency = currency;
        this.item = item;
        this.price = price;
    }

    public TECurrency getCurrency() {
        return currency;
    }

    public ItemStack getItem() {
        return item;
    }

    public BigDecimal getPrice() {
        return price;
    }
}
