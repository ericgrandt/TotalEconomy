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

import com.erigitic.shops.ShopItem;
import com.google.common.base.Preconditions;
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

import java.util.Optional;

public class ShopItemData extends AbstractSingleData<ShopItem, ShopItemData, ImmutableShopItemData> {

    public static final int CONTENT_VERSION = 1;

    public ShopItemData() {
        super(null, ShopKeys.SHOP_ITEM);
    }

    public ShopItemData(ShopItem shopItem) {
        super(shopItem, ShopKeys.SHOP_ITEM);
    }

    @Override
    public ShopItemData copy() {
        return new ShopItemData(getValue());
    }

    @Override
    public ImmutableShopItemData asImmutable() {
        return new ImmutableShopItemData(getValue());
    }

    @Override
    public int getContentVersion() {
        return CONTENT_VERSION;
    }

    @Override
    public Optional<ShopItemData> from(DataContainer container) {
        if (container.contains(ShopKeys.SHOP_ITEM.getQuery())) {
            Optional<ShopItem> shopItemOpt = container.getSerializable(ShopKeys.SHOP_ITEM.getQuery(), ShopItem.class);

            if (shopItemOpt.isPresent()) {
                Optional<ShopItemData> shopItemDataOpt = Optional.of(new ShopItemData(shopItemOpt.get()));

                return shopItemDataOpt;
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<ShopItemData> fill(DataHolder dataHolder, MergeFunction overlap) {
        ShopItemData shopItemData = Preconditions.checkNotNull(overlap).merge(copy(), dataHolder.get(ShopItemData.class).orElse(copy()));
        return Optional.of(set(ShopKeys.SHOP_ITEM, shopItemData.get(ShopKeys.SHOP_ITEM).get()));
    }

    @Override
    protected Value<ShopItem> getValueGetter() {
        return Sponge.getRegistry().getValueFactory().createValue(ShopKeys.SHOP_ITEM, getValue(), getValue());
    }

    @Override
    public DataContainer toContainer() {
        return super.toContainer().set(ShopKeys.SHOP_ITEM, getValue());
    }

    public static class Builder extends AbstractDataBuilder<ShopItemData> implements DataManipulatorBuilder<ShopItemData, ImmutableShopItemData> {

        public Builder() {
            super(ShopItemData.class, CONTENT_VERSION);
        }

        @Override
        public Optional<ShopItemData> buildContent(DataView container) throws InvalidDataException {
            if (container.contains(ShopKeys.SHOP_ITEM.getQuery())) {
                Optional<ShopItem> shopItemOpt = container.getSerializable(ShopKeys.SHOP_ITEM.getQuery(), ShopItem.class);

                if (shopItemOpt.isPresent()) {
                    Optional<ShopItemData> shopItemDataOpt = Optional.of(new ShopItemData(shopItemOpt.get()));

                    return shopItemDataOpt;
                }
            }

            return Optional.empty();
        }

        @Override
        public ShopItemData create() {
            return new ShopItemData();
        }

        @Override
        public Optional<ShopItemData> createFrom(DataHolder dataHolder) {
            return create().fill(dataHolder);
        }

        public ShopItemData createFrom(ShopItem shopItem) {
            return new ShopItemData(shopItem);
        }
    }
}
