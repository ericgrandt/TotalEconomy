package com.erigitic.shops.data;

import com.erigitic.shops.Shop;
import com.erigitic.shops.ShopItem;
import com.google.common.reflect.TypeToken;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.KeyFactory;
import org.spongepowered.api.data.value.mutable.Value;

public class ShopKeys {

    public static Key<Value<Shop>> SINGLE_SHOP = KeyFactory.makeSingleKey(
            TypeToken.of(Shop.class),
            new TypeToken<Value<Shop>>() {},
            DataQuery.of("shop"),
            "totaleconomy:shop",
            "shop"
    );

    public static Key<Value<ShopItem>> SHOP_ITEM = KeyFactory.makeSingleKey(
            TypeToken.of(ShopItem.class),
            new TypeToken<Value<ShopItem>>() {},
            DataQuery.of("shopitem"),
            "totaleconomy:shopitem",
            "shopitem"
    );
}
