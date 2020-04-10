package com.erigitic.commands;

import com.erigitic.TotalEconomy;
import com.erigitic.economy.TEEconomyService;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class BalanceCommand implements CommandExecutor {
    private final TotalEconomy plugin;
    private final TEEconomyService economyService;

    public BalanceCommand() {
        plugin = TotalEconomy.getPlugin();
        economyService = plugin.getEconomyService();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            return CommandResult.empty();
        }

        String currencyName = getCurrencyNameArgument(args.getOne("currencyName"));
        Currency currency = economyService.getCurrency(currencyName);

        if (currency == null) {
            src.sendMessage(Text.of(TextColors.RED, "Invalid currency name"));

            return CommandResult.empty();
        }

        src.sendMessage(currency.getPluralDisplayName());

        return CommandResult.success();
    }

    private String getCurrencyNameArgument(Optional<String> currencyNameOpt) {
        String currencyName = "";
        if (currencyNameOpt.isPresent()) {
            currencyName = currencyNameOpt.get();
        }

        return currencyName;
    }
}
