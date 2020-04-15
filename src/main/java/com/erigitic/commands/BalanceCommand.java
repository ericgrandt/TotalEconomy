package com.erigitic.commands;

import com.erigitic.TotalEconomy;
import com.erigitic.economy.TEAccount;
import com.erigitic.economy.TEEconomyService;
import java.math.BigDecimal;
import java.util.Optional;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

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

        TEAccount account = new TEAccount(((Player) src).getUniqueId());
        Currency currency = getCurrencyArgOrDefault(args.getOne("currencyName"));

        BigDecimal balance = account.getBalance(currency);
        if (balance == null) {
            src.sendMessage(Text.of(TextColors.RED, "Error retrieving balance"));

            return CommandResult.empty();
        }

        src.sendMessage(Text.of(TextColors.GRAY, "Balance: ", TextColors.GOLD, currency.getSymbol(), balance.toString()));

        return CommandResult.success();
    }

    private Currency getCurrencyArgOrDefault(Optional<Currency> currencyNameOpt) {
        Currency currency = economyService.getDefaultCurrency();
        if (currencyNameOpt.isPresent()) {
            currency = currencyNameOpt.get();
        }

        return currency;
    }
}
