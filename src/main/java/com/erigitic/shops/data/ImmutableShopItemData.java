package com.erigitic.shops.data;

import com.erigitic.shops.ShopItem;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableSingleData;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;

import java.util.Optional;

public class ImmutableShopItemData extends AbstractImmutableSingleData<ShopItem, ImmutableShopItemData, ShopItemData> {

    public ImmutableShopItemData(ShopItem shopItem) {
        super(shopItem, ShopKeys.SHOP_ITEM);
    }

    @Override
    public ShopItemData asMutable() {
        return new ShopItemData(getValue());
    }

    @Override
    public int getContentVersion() {
        return ShopItemData.CONTENT_VERSION;
    }

    @Override
    protected ImmutableValue<?> getValueGetter() {
        return Sponge.getRegistry().getValueFactory().createValue(ShopKeys.SHOP_ITEM, getValue()).asImmutable();
    }

    @Override
    public <E> Optional<ImmutableShopItemData> with(Key<? extends BaseValue<E>> key, E value) {
        if (this.supports(key)) {
            return Optional.of(asMutable().set(key, value).asImmutable());
        } else {
            return Optional.empty();
        }
    }
}
