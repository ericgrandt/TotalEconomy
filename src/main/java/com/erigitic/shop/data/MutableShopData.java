package com.erigitic.shop.data;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractSingleData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.mutable.Value;

import java.util.Optional;

public class MutableShopData extends AbstractSingleData<String, MutableShopData, ImmutableShopData> {
    public MutableShopData(String id) {
        super(ShopKeys.SHOP_ID, id);
    }

    @Override
    protected Value<String> getValueGetter() {
        return Sponge.getRegistry().getValueFactory().createValue(ShopKeys.SHOP_ID, getValue());
    }

    @Override
    public Optional<MutableShopData> fill(DataHolder dataHolder, MergeFunction overlap) {
        Optional<MutableShopData> shopDataOpt = dataHolder.get(MutableShopData.class);
        if (shopDataOpt.isPresent()) {
            MutableShopData shopData = shopDataOpt.get();
            MutableShopData mergedShopData = overlap.merge(this, shopData);

            setValue(mergedShopData.getValue());
        }

        return Optional.of(this);
    }

    @Override
    public Optional<MutableShopData> from(DataContainer container) {
        return Optional.of(this);
    }

    @Override
    public MutableShopData copy() {
        return new MutableShopData(getValue());
    }

    @Override
    public ImmutableShopData asImmutable() {
        return new ImmutableShopData(getValue());
    }

    @Override
    public int getContentVersion() {
        return 1;
    }
}
