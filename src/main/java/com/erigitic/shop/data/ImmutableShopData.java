package com.erigitic.shop.data;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableSingleData;
import org.spongepowered.api.data.value.immutable.ImmutableValue;

public class ImmutableShopData extends AbstractImmutableSingleData<String, ImmutableShopData, MutableShopData> {
    public ImmutableShopData(String id) {
        super(ShopKeys.SHOP_ID, id);
    }

    @Override
    protected ImmutableValue<String> getValueGetter() {
        return Sponge.getRegistry().getValueFactory().createValue(ShopKeys.SHOP_ID, getValue()).asImmutable();
    }

    @Override
    public MutableShopData asMutable() {
        return new MutableShopData(getValue());
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    @Override
    protected DataContainer fillContainer(DataContainer dataContainer) {
        return null;
    }
}
