package com.erigitic.shops.data;

import com.erigitic.shops.Shop;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableSingleData;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;

import java.util.Optional;

public class ImmutableShopData extends AbstractImmutableSingleData<Shop, ImmutableShopData, ShopData> {

    public ImmutableShopData(Shop shop) {
        super(shop, ShopKeys.SINGLE_SHOP);
    }

    @Override
    public ShopData asMutable() {
        return new ShopData(getValue());
    }

    @Override
    public int getContentVersion() {
        return ShopData.CONTENT_VERSION;
    }

    @Override
    protected ImmutableValue<?> getValueGetter() {
        return Sponge.getRegistry().getValueFactory().createValue(ShopKeys.SINGLE_SHOP, getValue()).asImmutable();
    }

    @Override
    public <E> Optional<ImmutableShopData> with(Key<? extends BaseValue<E>> key, E value) {
        if (this.supports(key)) {
            return Optional.of(asMutable().set(key, value).asImmutable());
        } else {
            return Optional.empty();
        }
    }
}
