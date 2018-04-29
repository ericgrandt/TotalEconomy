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

import com.erigitic.shops.PlayerShopInfo;
import com.erigitic.shops.Shop;
import com.erigitic.shops.ShopItem;
import com.google.common.reflect.TypeToken;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.KeyFactory;
import org.spongepowered.api.data.value.mutable.Value;

public class ShopKeys {

    public static Key<Value<Shop>> SINGLE_SHOP = Key.builder()
            .type(new TypeToken<Value<Shop>>() {})
            .id("shop")
            .name("shop")
            .query(DataQuery.of("shop"))
            .build();

    public static Key<Value<ShopItem>> SHOP_ITEM = Key.builder()
            .type(new TypeToken<Value<ShopItem>>() {})
            .id("shopitem")
            .name("shopitem")
            .query(DataQuery.of("shopitem"))
            .build();

    public static Key<Value<PlayerShopInfo>> PLAYER_SHOP_INFO = Key.builder()
            .type(new TypeToken<Value<PlayerShopInfo>>() {})
            .id("playershopinfo")
            .name("playershopinfo")
            .query(DataQuery.of("playershopinfo"))
            .build();
}
