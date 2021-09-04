package com.ericgrandt.commands;

import com.ericgrandt.domain.Balance;
import com.ericgrandt.domain.TECommandResult;
import com.ericgrandt.domain.TECurrency;
import com.ericgrandt.services.AccountService;
import com.ericgrandt.services.TEEconomyService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

public class BalanceCommand implements CommandExecutor {
    private final TEEconomyService economyService;
    private final AccountService accountService;

    public BalanceCommand(TEEconomyService economyService, AccountService accountService) {
        this.economyService = economyService;
        this.accountService = accountService;
    }

    @Override
    public CommandResult execute(CommandContext context) throws CommandException {
        if (!(context.cause().root() instanceof Player)) {
            throw new CommandException(Component.text("Only players can use this command"));
        }

        Player player = (Player) context.cause().root();
        TECurrency currency = (TECurrency) economyService.defaultCurrency();
        Balance balance = accountService.getBalance(
            player.uniqueId(),
            currency.getId()
        );

        player.sendMessage(
            Component.text("Balance: ", NamedTextColor.GRAY)
                .append(
                    currency.format(balance.getBalance()).color(NamedTextColor.GOLD)
                )
        );

        return new TECommandResult(true);
    }
}