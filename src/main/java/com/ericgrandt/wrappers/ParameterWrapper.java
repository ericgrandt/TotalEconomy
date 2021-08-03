package com.ericgrandt.wrappers;

import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import java.math.BigDecimal;

public class ParameterWrapper {
    public Parameter.Value.Builder<ServerPlayer> player() {
        return Parameter.player();
    }

    public Parameter.Value.Builder<BigDecimal> bigDecimal() {
        return Parameter.bigDecimal();
    }

    public <T> Parameter.Key<T> key(String key, Class<T> type) {
        return Parameter.key(key, type);
    }
}
