package com.erigitic.shops;

import com.erigitic.shops.data.PlayerShopInfoData;
import org.spongepowered.api.data.*;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.world.Location;

import java.util.Optional;

/**
 * Represents shop related data that'll be offered to a player, such as the location of an opened chest shop.
 */
public class PlayerShopInfo implements DataSerializable {

    private final static DataQuery OPEN_SHOP_LOCATION = DataQuery.of("openshop");

    private Location openShopLocation;

    public PlayerShopInfo(Location openShopLocation) {
        this.openShopLocation = openShopLocation;
    }

    public Location getOpenShopLocation() {
        return openShopLocation;
    }

    @Override
    public DataContainer toContainer() {
        return DataContainer.createNew()
                .set(OPEN_SHOP_LOCATION, getOpenShopLocation())
                .set(Queries.CONTENT_VERSION, PlayerShopInfoData.CONTENT_VERSION);
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    public static class Builder extends AbstractDataBuilder<PlayerShopInfo> {

        public Builder() {
            super(PlayerShopInfo.class, PlayerShopInfoData.CONTENT_VERSION);
        }

        @Override
        protected Optional<PlayerShopInfo> buildContent(DataView container) throws InvalidDataException {
            if (container.contains(OPEN_SHOP_LOCATION)) {
                Location carrier = container.getSerializable(PlayerShopInfo.OPEN_SHOP_LOCATION, Location.class).get();

                return Optional.of(new PlayerShopInfo(carrier));
            }

            return Optional.empty();
        }
    }
}
