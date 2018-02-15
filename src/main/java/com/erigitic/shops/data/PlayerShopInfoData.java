package com.erigitic.shops.data;

import com.erigitic.shops.PlayerShopInfo;
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

public class PlayerShopInfoData extends AbstractSingleData<PlayerShopInfo, PlayerShopInfoData, ImmutablePlayerShopInfoData> {

    public static final int CONTENT_VERSION = 1;

    public PlayerShopInfoData() {
        super(null, ShopKeys.PLAYER_SHOP_INFO);
    }

    public PlayerShopInfoData(PlayerShopInfo playerShopInfo) {
        super(playerShopInfo, ShopKeys.PLAYER_SHOP_INFO);
    }

    @Override
    public PlayerShopInfoData copy() {
        return new PlayerShopInfoData(getValue());
    }

    @Override
    public ImmutablePlayerShopInfoData asImmutable() {
        return new ImmutablePlayerShopInfoData(getValue());
    }

    @Override
    public int getContentVersion() {
        return CONTENT_VERSION;
    }

    @Override
    public Optional<PlayerShopInfoData> from(DataContainer container) {
        if (container.contains(ShopKeys.SHOP_ITEM.getQuery())) {
            Optional<PlayerShopInfo> playerShopInfoOpt = container.getSerializable(ShopKeys.PLAYER_SHOP_INFO.getQuery(), PlayerShopInfo.class);

            if (playerShopInfoOpt.isPresent()) {
                Optional<PlayerShopInfoData> playerShopInfoDataOpt = Optional.of(new PlayerShopInfoData(playerShopInfoOpt.get()));

                return playerShopInfoDataOpt;
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<PlayerShopInfoData> fill(DataHolder dataHolder, MergeFunction overlap) {
        PlayerShopInfoData playerShopInfoData = Preconditions.checkNotNull(overlap).merge(copy(), dataHolder.get(PlayerShopInfoData.class).orElse(copy()));
        return Optional.of(set(ShopKeys.PLAYER_SHOP_INFO, playerShopInfoData.get(ShopKeys.PLAYER_SHOP_INFO).get()));
    }

    @Override
    protected Value<PlayerShopInfo> getValueGetter() {
        return Sponge.getRegistry().getValueFactory().createValue(ShopKeys.PLAYER_SHOP_INFO, getValue(), getValue());
    }

    @Override
    public DataContainer toContainer() {
        return super.toContainer().set(ShopKeys.PLAYER_SHOP_INFO, getValue());
    }

    public static class Builder extends AbstractDataBuilder<PlayerShopInfoData> implements DataManipulatorBuilder<PlayerShopInfoData, ImmutablePlayerShopInfoData> {

        public Builder() {
            super(PlayerShopInfoData.class, CONTENT_VERSION);
        }

        @Override
        public Optional<PlayerShopInfoData> buildContent(DataView container) throws InvalidDataException {
            if (container.contains(ShopKeys.PLAYER_SHOP_INFO.getQuery())) {
                Optional<PlayerShopInfo> playerShopInfoOpt = container.getSerializable(ShopKeys.PLAYER_SHOP_INFO.getQuery(), PlayerShopInfo.class);

                if (playerShopInfoOpt.isPresent()) {
                    Optional<PlayerShopInfoData> playerShopInfoDataOpt = Optional.of(new PlayerShopInfoData(playerShopInfoOpt.get()));

                    return playerShopInfoDataOpt;
                }
            }

            return Optional.empty();
        }

        @Override
        public PlayerShopInfoData create() {
            return new PlayerShopInfoData();
        }

        @Override
        public Optional<PlayerShopInfoData> createFrom(DataHolder dataHolder) {
            return create().fill(dataHolder);
        }

        public PlayerShopInfoData createFrom(PlayerShopInfo playerShopInfo) {
            return new PlayerShopInfoData(playerShopInfo);
        }
    }
}
