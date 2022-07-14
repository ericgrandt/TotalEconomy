package com.ericgrandt.commands;

import com.ericgrandt.domain.TECurrency;
import com.ericgrandt.services.TEEconomyService;
import com.ericgrandt.wrappers.ParameterWrapper;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.Player;

import java.math.BigDecimal;
import java.util.Optional;

public class ParameterHandler {
    private final CommandContext context;
    private final ParameterWrapper parameterWrapper;

    public ParameterHandler(CommandContext context, ParameterWrapper parameterWrapper) {
        this.context = context;
        this.parameterWrapper = parameterWrapper;
    }

    public Player getPlayerParameter() {
        Object rootCause = context.cause().root();
        if (rootCause instanceof Player) {
            return (Player) rootCause;
        }

        return null;
    }

    public TECurrency getCurrencyParameter(TEEconomyService service) {
        Parameter.Key<String> currencyKey = parameterWrapper.key("currency", String.class);
        Optional<String> currencyParamOpt = context.one(currencyKey);

        if (currencyParamOpt.isPresent()) {
            return (TECurrency) service.getCurrency(currencyParamOpt.get());
        }

        return (TECurrency) service.defaultCurrency();
    }

    public BigDecimal getBigDecimalParameter(String key) {
        Parameter.Key<BigDecimal> bigDecimalKey = parameterWrapper.key(key, BigDecimal.class);
        Optional<BigDecimal> paramOpt = context.one(bigDecimalKey);

        return paramOpt.orElse(null);
    }
}