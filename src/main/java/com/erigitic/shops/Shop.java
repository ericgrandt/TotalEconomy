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
import org.spongepowered.api.data.*;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

import java.util.Optional;
import java.util.UUID;

public class Shop implements DataSerializable {

    public static final DataQuery OWNER_QUERY = DataQuery.of("Owner");
    public static final DataQuery TITLE_QUERY = DataQuery.of("Title");

    private UUID owner;
    private String title;

    public Shop(UUID owner, String title) {
        this.owner = owner;
        this.title = title;
    }

    public UUID getOwner() {
        return owner;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int getContentVersion() {
        return ShopData.CONTENT_VERSION;
    }

    @Override
    public DataContainer toContainer() {
        return DataContainer.createNew()
                .set(OWNER_QUERY, getOwner())
                .set(TITLE_QUERY, getTitle())
                .set(Queries.CONTENT_VERSION, ShopData.CONTENT_VERSION);
    }

    public static class Builder extends AbstractDataBuilder<Shop> {

        public Builder() {
            super(Shop.class, ShopData.CONTENT_VERSION);
        }

        @Override
        public Optional<Shop> buildContent(DataView container) throws InvalidDataException {
            if (container.contains(Shop.OWNER_QUERY, Shop.TITLE_QUERY)) {
                UUID owner = container.getObject(Shop.OWNER_QUERY, UUID.class).get();
                String title = container.getString(Shop.TITLE_QUERY).get();

                return Optional.of(new Shop(owner, title));
            }

            return Optional.empty();
        }
    }
}
