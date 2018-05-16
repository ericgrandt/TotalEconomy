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

package com.erigitic.shops.data;

import com.erigitic.shops.Shop;
import com.google.common.base.Preconditions;
import java.util.Optional;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractSingleData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.data.value.mutable.Value;

public class ShopData extends AbstractSingleData<Shop, ShopData, ImmutableShopData> {

    public static final int CONTENT_VERSION = 1;

    public ShopData() {
        super(null, ShopKeys.SINGLE_SHOP);
    }

    public ShopData(Shop shop) {
        super(shop, ShopKeys.SINGLE_SHOP);
    }

    @Override
    public ShopData copy() {
        return new ShopData(getValue());
    }

    @Override
    public ImmutableShopData asImmutable() {
        return new ImmutableShopData(getValue());
    }

    @Override
    public int getContentVersion() {
        return CONTENT_VERSION;
    }

    @Override
    public Optional<ShopData> from(DataContainer container) {
        if (container.contains(ShopKeys.SINGLE_SHOP.getQuery())) {
            Optional<Shop> shopOpt = container.getSerializable(ShopKeys.SINGLE_SHOP.getQuery(), Shop.class);

            if (shopOpt.isPresent()) {
                Optional<ShopData> shopDataOpt = Optional.of(new ShopData(shopOpt.get()));

                return shopDataOpt;
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<ShopData> fill(DataHolder dataHolder, MergeFunction overlap) {
        ShopData shopData = Preconditions.checkNotNull(overlap).merge(copy(), dataHolder.get(ShopData.class).orElse(copy()));
        return Optional.of(set(ShopKeys.SINGLE_SHOP, shopData.get(ShopKeys.SINGLE_SHOP).get()));
    }

    @Override
    protected Value<Shop> getValueGetter() {
        return Sponge.getRegistry().getValueFactory().createValue(ShopKeys.SINGLE_SHOP, getValue(), getValue());
    }

    @Override
    public DataContainer toContainer() {
        return super.toContainer().set(ShopKeys.SINGLE_SHOP, getValue());
    }

    public static class Builder extends AbstractDataBuilder<ShopData> implements DataManipulatorBuilder<ShopData, ImmutableShopData> {

        public Builder() {
            super(ShopData.class, CONTENT_VERSION);
        }

        @Override
        public Optional<ShopData> buildContent(DataView container) throws InvalidDataException {
            if (container.contains(ShopKeys.SINGLE_SHOP.getQuery())) {
                Optional<Shop> shopOpt = container.getSerializable(ShopKeys.SINGLE_SHOP.getQuery(), Shop.class);

                if (shopOpt.isPresent()) {
                    Optional<ShopData> shopDataOpt = Optional.of(new ShopData(shopOpt.get()));

                    return shopDataOpt;
                }
            }

            return Optional.empty();
        }

        @Override
        public ShopData create() {
            return new ShopData();
        }

        @Override
        public Optional<ShopData> createFrom(DataHolder dataHolder) {
            return create().fill(dataHolder);
        }
    }
}
