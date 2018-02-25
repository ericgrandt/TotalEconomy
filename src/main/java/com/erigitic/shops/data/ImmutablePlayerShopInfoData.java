package com.erigitic.shops.data;

import com.erigitic.shops.PlayerShopInfo;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableSingleData;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;

import java.util.Optional;

public class ImmutablePlayerShopInfoData extends AbstractImmutableSingleData<PlayerShopInfo, ImmutablePlayerShopInfoData, PlayerShopInfoData> {

    public ImmutablePlayerShopInfoData(PlayerShopInfo playerShopInfo) {
        super(playerShopInfo, ShopKeys.PLAYER_SHOP_INFO);
    }

    @Override
    public PlayerShopInfoData asMutable() {
        return new PlayerShopInfoData(getValue());
    }

    @Override
    public int getContentVersion() {
        return PlayerShopInfoData.CONTENT_VERSION;
    }

    @Override
    protected ImmutableValue<?> getValueGetter() {
        return Sponge.getRegistry().getValueFactory().createValue(ShopKeys.PLAYER_SHOP_INFO, getValue()).asImmutable();
    }

    @Override
    public <E> Optional<ImmutablePlayerShopInfoData> with(Key<? extends BaseValue<E>> key, E value) {
        if (this.supports(key)) {
            return Optional.of(asMutable().set(key, value).asImmutable());
        } else {
            return Optional.empty();
        }
    }
}
