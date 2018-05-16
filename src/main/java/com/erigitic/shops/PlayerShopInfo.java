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

import com.erigitic.shops.data.PlayerShopInfoData;
import java.util.Optional;

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.Queries;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.world.Location;

/**
 * Represents shop related data that'll be offered to a player, such as the location of an opened chest shop.
 */
public class PlayerShopInfo implements DataSerializable {

    private static final DataQuery OPEN_SHOP_LOCATION = DataQuery.of("openshop");

    private Location openShopLocation;

    public PlayerShopInfo(Location openShopLocation) {
        this.openShopLocation = openShopLocation;
    }

    public Location getOpenShopLocation() {
        return openShopLocation;
    }

    @Override
    public DataContainer toContainer() {
        return DataContainer.createNew()
                .set(OPEN_SHOP_LOCATION, getOpenShopLocation())
                .set(Queries.CONTENT_VERSION, PlayerShopInfoData.CONTENT_VERSION);
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    public static class Builder extends AbstractDataBuilder<PlayerShopInfo> {

        public Builder() {
            super(PlayerShopInfo.class, PlayerShopInfoData.CONTENT_VERSION);
        }

        @Override
        protected Optional<PlayerShopInfo> buildContent(DataView container) throws InvalidDataException {
            if (container.contains(OPEN_SHOP_LOCATION)) {
                Location carrier = container.getSerializable(PlayerShopInfo.OPEN_SHOP_LOCATION, Location.class).get();

                return Optional.of(new PlayerShopInfo(carrier));
            }

            return Optional.empty();
        }
    }
}
