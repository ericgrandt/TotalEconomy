package com.erigitic.shops;

import com.erigitic.config.TECurrency;
import com.erigitic.shops.data.ShopItemData;
import org.spongepowered.api.data.*;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ShopItem implements DataSerializable {

    public static final DataQuery QUANTITY_QUERY = DataQuery.of("Quantity");
    public static final DataQuery PRICE_QUERY = DataQuery.of("Price");

    private int quantity;
    private double price;

    public ShopItem(int quantity, double price) {
        this.quantity = quantity;
        this.price = price;
    }

    @Override
    public DataContainer toContainer() {
        return DataContainer.createNew()
                .set(QUANTITY_QUERY, quantity)
                .set(PRICE_QUERY, price)
                .set(Queries.CONTENT_VERSION, ShopItemData.CONTENT_VERSION);
    }

    @Override
    public int getContentVersion() {
        return ShopItemData.CONTENT_VERSION;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public List<Text> getLore(TECurrency currency) {
        List<Text> lore = new ArrayList<>();
        lore.add(Text.of(TextColors.GRAY, "Price: ", TextColors.GOLD, currency.format(BigDecimal.valueOf(price), 2)));
        lore.add(Text.of(TextColors.GRAY, "Stock: ", TextColors.GOLD, quantity));

        return lore;
    }

    public static class Builder extends AbstractDataBuilder<ShopItem> {

        public Builder() {
            super(ShopItem.class, ShopItemData.CONTENT_VERSION);
        }

        @Override
        public Optional<ShopItem> buildContent(DataView container) throws InvalidDataException {
            if (container.contains(ShopItem.QUANTITY_QUERY, ShopItem.PRICE_QUERY)) {
                int quantity = container.getInt(ShopItem.QUANTITY_QUERY).get();
                double price = container.getDouble(ShopItem.PRICE_QUERY).get();

                return Optional.of(new ShopItem(quantity, price));
            }

            return Optional.empty();
        }
    }
}
