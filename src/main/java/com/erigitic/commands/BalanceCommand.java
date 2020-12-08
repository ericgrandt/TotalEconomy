package com.erigitic.commands;

import com.erigitic.TotalEconomy;
import com.erigitic.economy.TEAccount;
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

import java.math.BigDecimal;
import java.util.Optional;

public class BalanceCommand implements CommandExecutor {
    private final TEEconomyService economyService;

    public BalanceCommand() {
        TotalEconomy plugin = TotalEconomy.getPlugin();
        this.economyService = plugin.getEconomyService();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            throw new CommandException(Text.of("Only players can use this command"));
        }

        Player player = (Player) src;
        TEAccount account = new TEAccount(player.getUniqueId());
        Optional<Currency> currencyOptional = args.getOne("currencyName");
        Currency currency = currencyOptional.orElseGet(economyService::getDefaultCurrency);
        BigDecimal balance = account.getBalance(currency);

        player.sendMessage(Text.of(TextColors.GRAY, "Balance: ", TextColors.GOLD, currency.format(balance)));

        return CommandResult.success();
    }
}