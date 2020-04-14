package com.erigitic.commands.elements;

import com.erigitic.TotalEconomy;
import com.erigitic.economy.TEEconomyService;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CurrencyCommandElement extends CommandElement {
    private TotalEconomy plugin;
    private TEEconomyService economyService;

    public CurrencyCommandElement(Text key) {
        super(key);

        plugin = TotalEconomy.getPlugin();
        economyService = plugin.getEconomyService();
    }

    @Nullable
    @Override
    protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        String currencyName = args.next();

        Currency currency = economyService.getCurrency(currencyName);
        if (currency == null) {
            throw args.createError(Text.of("Invalid currency name"));
        }

        return currency;
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
        List<String> currencyNames = new ArrayList<>();

        economyService.getCurrencies().forEach(currency -> {
            currencyNames.add(currency.getDisplayName().toPlainSingle());
        });

        return currencyNames;
    }
}
