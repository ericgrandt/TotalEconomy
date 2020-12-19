package com.erigitic.commands.elements;

import com.erigitic.TotalEconomy;
import com.erigitic.services.TEEconomyService;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.text.Text;

public class CurrencyCommandElement extends CommandElement {
    private final TEEconomyService economyService;

    public CurrencyCommandElement(Text key) {
        super(key);
        economyService = TotalEconomy.getPlugin().getEconomyService();
    }

    @Nullable
    @Override
    protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        String currencyName = args.next();

        Currency currency = economyService.getCurrencies()
            .stream()
            .filter(c -> c.getDisplayName().toPlainSingle().equals(currencyName))
            .findFirst()
            .orElse(null);

        if (currency == null) {
            throw args.createError(Text.of("Invalid currency"));
        }

        return currency;
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
        List<String> currencyNames = new ArrayList<>();

        economyService.getCurrencies()
            .forEach(currency -> currencyNames.add(currency.getDisplayName().toPlainSingle()));

        return currencyNames;
    }
}
