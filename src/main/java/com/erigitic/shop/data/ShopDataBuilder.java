package com.erigitic.shop.data;

import java.util.Optional;
import java.util.UUID;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

public class ShopDataBuilder extends AbstractDataBuilder<MutableShopData> implements DataManipulatorBuilder<MutableShopData, ImmutableShopData> {
    public ShopDataBuilder() {
        super(MutableShopData.class, 1);
    }

    @Override
    public MutableShopData create() {
        return new MutableShopData(UUID.randomUUID().toString());
    }

    @Override
    public Optional<MutableShopData> createFrom(DataHolder dataHolder) {
        return create().fill(dataHolder);
    }

    @Override
    protected Optional<MutableShopData> buildContent(DataView container) throws InvalidDataException {
        if (container.contains(ShopKeys.SHOP_ID)) {
            String id = container.getString(ShopKeys.SHOP_ID.getQuery()).get();

            return Optional.of(new MutableShopData(id));
        }

        return Optional.empty();
    }
}
