package com.erigitic.shops.data;

import com.erigitic.shops.Shop;
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

public class ShopData extends AbstractSingleData<Shop, ShopData, ImmutableShopData> {

    public static final int CONTENT_VERSION = 1;

    public ShopData() {
        super(null, ShopKeys.SINGLE_SHOP);
    }

    public ShopData(Shop shop) {
        super(shop, ShopKeys.SINGLE_SHOP);
    }

    @Override
    public ShopData copy() {
        return new ShopData(getValue());
    }

    @Override
    public ImmutableShopData asImmutable() {
        return new ImmutableShopData(getValue());
    }

    @Override
    public int getContentVersion() {
        return CONTENT_VERSION;
    }

    @Override
    public Optional<ShopData> from(DataContainer container) {
        if (container.contains(ShopKeys.SINGLE_SHOP.getQuery())) {
            Optional<Shop> shopOpt = container.getSerializable(ShopKeys.SINGLE_SHOP.getQuery(), Shop.class);

            if (shopOpt.isPresent()) {
                Optional<ShopData> shopDataOpt = Optional.of(new ShopData(shopOpt.get()));

                return shopDataOpt;
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<ShopData> fill(DataHolder dataHolder, MergeFunction overlap) {
        ShopData shopData = Preconditions.checkNotNull(overlap).merge(copy(), dataHolder.get(ShopData.class).orElse(copy()));
        return Optional.of(set(ShopKeys.SINGLE_SHOP, shopData.get(ShopKeys.SINGLE_SHOP).get()));
    }

    @Override
    protected Value<Shop> getValueGetter() {
        return Sponge.getRegistry().getValueFactory().createValue(ShopKeys.SINGLE_SHOP, getValue(), getValue());
    }

    @Override
    public DataContainer toContainer() {
        return super.toContainer().set(ShopKeys.SINGLE_SHOP, getValue());
    }

    public static class Builder extends AbstractDataBuilder<ShopData> implements DataManipulatorBuilder<ShopData, ImmutableShopData> {

        public Builder() {
            super(ShopData.class, CONTENT_VERSION);
        }

        @Override
        public Optional<ShopData> buildContent(DataView container) throws InvalidDataException {
            if (container.contains(ShopKeys.SINGLE_SHOP.getQuery())) {
                Optional<Shop> shopOpt = container.getSerializable(ShopKeys.SINGLE_SHOP.getQuery(), Shop.class);

                if (shopOpt.isPresent()) {
                    Optional<ShopData> shopDataOpt = Optional.of(new ShopData(shopOpt.get()));

                    return shopDataOpt;
                }
            }

            return Optional.empty();
        }

        @Override
        public ShopData create() {
            return new ShopData();
        }

        @Override
        public Optional<ShopData> createFrom(DataHolder dataHolder) {
            return create().fill(dataHolder);
        }

        public ShopData createFrom(Shop shop) {
            return new ShopData(shop);
        }
    }
}
