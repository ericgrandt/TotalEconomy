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

import com.erigitic.shops.data.ShopData;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.Queries;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Optional;
import java.util.UUID;

public class SingleItemShop extends Shop {

    private ItemStack stock;

    public SingleItemShop(UUID owner, String title, ItemStack stock) {
        super(owner, title);

        this.stock = stock;
    }

    public ItemStack getStock() {
        return stock;
    }

    public void setStock(ItemStack stock) {
        this.stock = stock;
    }

    @Override
    public DataContainer toContainer() {
        return DataContainer.createNew()
                .set(OWNER_QUERY, getOwner())
                .set(TITLE_QUERY, getTitle())
                .set(STOCK_QUERY, stock)
                .set(Queries.CONTENT_VERSION, ShopData.CONTENT_VERSION);
    }

    public static class Builder extends AbstractDataBuilder<SingleItemShop> {

        public Builder() {
            super(SingleItemShop.class, ShopData.CONTENT_VERSION);
        }

        @Override
        public Optional<SingleItemShop> buildContent(DataView container) throws InvalidDataException {
            if (container.contains(Shop.OWNER_QUERY, Shop.TITLE_QUERY, Shop.STOCK_QUERY)) {
                UUID owner = container.getObject(Shop.OWNER_QUERY, UUID.class).get();
                String title = container.getString(Shop.TITLE_QUERY).get();
                ItemStack stock = container.getObject(Shop.STOCK_QUERY, ItemStack.class).get();

                return Optional.of(new SingleItemShop(owner, title, stock));
            }

            return Optional.empty();
        }
    }
}
