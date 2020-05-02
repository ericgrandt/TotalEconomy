package com.erigitic.shop.data;

import com.google.common.reflect.TypeToken;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.mutable.Value;

public class ShopKeys {
    public static final Key<Value<String>> SHOP_ID;

    public static void initShopKeys() {}

    static {
        SHOP_ID = Key.builder()
            .type(new TypeToken<Value<String>>() {})
            .id("shopid")
            .name("Shop Id")
            .query(DataQuery.of("shop"))
            .build();
    }
}
