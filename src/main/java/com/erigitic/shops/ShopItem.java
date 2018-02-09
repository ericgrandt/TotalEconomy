/*
 * This file is part of Total Economy, licensed under the MIT License (MIT).
 *
 * Copyright (c) Eric Grandt <https://www.ericgrandt.com>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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

    public static final DataQuery PRICE_QUERY = DataQuery.of("Price");

    private double price;

    public ShopItem(double price) {
        this.price = price;
    }

    @Override
    public DataContainer toContainer() {
        return DataContainer.createNew()
                .set(PRICE_QUERY, price)
                .set(Queries.CONTENT_VERSION, ShopItemData.CONTENT_VERSION);
    }

    @Override
    public int getContentVersion() {
        return ShopItemData.CONTENT_VERSION;
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

        return lore;
    }

    public static class Builder extends AbstractDataBuilder<ShopItem> {

        public Builder() {
            super(ShopItem.class, ShopItemData.CONTENT_VERSION);
        }

        @Override
        public Optional<ShopItem> buildContent(DataView container) throws InvalidDataException {
            if (container.contains(ShopItem.PRICE_QUERY)) {
                double price = container.getDouble(ShopItem.PRICE_QUERY).get();

                return Optional.of(new ShopItem(price));
            }

            return Optional.empty();
        }
    }
}
