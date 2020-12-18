package com.erigitic.commands;

import com.erigitic.services.AccountService;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

public class BalanceCommand implements CommandExecutor {
    // private final TEEconomyService economyService;
    // private final AccountService accountService;

    public BalanceCommand(AccountService accountService) {
        // TotalEconomy plugin = TotalEconomy.getPlugin();
        // this.economyService = plugin.getEconomyService();
        // this.accountService = accountService;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        // if (!(src instanceof Player)) {
        //     throw new CommandException(Text.of("Only players can use this command"));
        // }
        //
        // Player player = (Player) src;
        // Optional<Currency> currencyOptional = args.getOne("currencyName");
        // Currency currency = currencyOptional.orElseGet(economyService::getDefaultCurrency);
        // Balance balance = accountService.getBalance(
        //     player.getUniqueId(),
        //     Integer.parseInt(currency.getId())
        // );
        //
        // player.sendMessage(Text.of(TextColors.GRAY, "Balance: ", TextColors.GOLD, currency.format(balance.getBalance())));

        return CommandResult.success();
    }
}