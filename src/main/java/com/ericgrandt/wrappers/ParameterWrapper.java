package com.ericgrandt.wrappers;

import java.math.BigDecimal;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

public class ParameterWrapper {
    public Parameter.Value.Builder<ServerPlayer> player() {
        return Parameter.player();
    }

    public Parameter.Value.Builder<BigDecimal> bigDecimal() {
        return Parameter.bigDecimal();
    }

    public Parameter.Value.Builder<String> currency() {
        return Parameter.string();
    }

    public <T> Parameter.Key<T> key(String key, Class<T> type) {
        return Parameter.key(key, type);
    }
}
