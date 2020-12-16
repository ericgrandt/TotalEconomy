package com.erigitic.commands;

import com.erigitic.TotalEconomy;
import com.erigitic.domain.Balance;
import com.erigitic.economy.TEEconomyService;
import com.erigitic.services.AccountService;
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
    private final TEEconomyService economyService;
    private final AccountService accountService;

    public BalanceCommand(AccountService accountService) {
        TotalEconomy plugin = TotalEconomy.getPlugin();
        this.economyService = plugin.getEconomyService();
        this.accountService = accountService;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            throw new CommandException(Text.of("Only players can use this command"));
        }

        Player player = (Player) src;
        Optional<Currency> currencyOptional = args.getOne("currencyName");
        Currency currency = currencyOptional.orElseGet(economyService::getDefaultCurrency);
        Balance balance = accountService.getBalance(player.getUniqueId().toString(), Integer.parseInt(currency.getId()));

        player.sendMessage(Text.of(TextColors.GRAY, "Balance: ", TextColors.GOLD, currency.format(balance.getBalance())));

        return CommandResult.success();
    }
}