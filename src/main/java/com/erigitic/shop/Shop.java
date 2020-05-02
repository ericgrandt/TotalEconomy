package com.erigitic.shop;

import java.math.BigDecimal;
import java.util.Map;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

public class Shop {
    private final int id;
    private final Text name;
    private final Map<ItemStack, BigDecimal> stock;

    public Shop(int id, Text name, Map<ItemStack, BigDecimal> stock) {
        this.id = id;
        this.name = name;
        this.stock = stock;
    }

    public int getId() {
        return id;
    }

    public Text getName() {
        return name;
    }

    public Map<ItemStack, BigDecimal> getStock() {
        return stock;
    }
}
