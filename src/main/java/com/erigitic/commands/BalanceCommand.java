package com.erigitic.commands;

import com.erigitic.domain.Balance;
import com.erigitic.domain.TECurrency;
import com.erigitic.services.AccountService;
import java.util.List;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.ArgumentReader;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.EconomyService;

public class BalanceCommand implements Command {
    private final EconomyService economyService;
    private final AccountService accountService;

    public BalanceCommand(EconomyService economyService, AccountService accountService) {
        this.economyService = economyService;
        this.accountService = accountService;
    }

    @Override
    public CommandResult process(CommandCause cause, ArgumentReader.Mutable args) throws CommandException {
        if (!(cause.root() instanceof Player)) {
            throw new CommandException(Component.text("Only players can use this command"));
        }

        Player player = (Player) cause.root();
        TECurrency currency = (TECurrency) economyService.defaultCurrency();
        Balance balance = accountService.getBalance(
            player.uniqueId(),
            currency.getId()
        );

        player.sendMessage(
            Component.text("Balance", NamedTextColor.GRAY)
                .append(
                    currency.format(balance.getBalance()).color(NamedTextColor.GOLD)
                )
        );

        return CommandResult.success();
    }

    @Override
    public List<String> suggestions(CommandCause cause, ArgumentReader.Mutable arguments) throws CommandException {
        return null;
    }

    @Override
    public boolean canExecute(CommandCause cause) {
        return true;
    }

    @Override
    public Optional<Component> shortDescription(CommandCause cause) {
        return Optional.empty();
    }

    @Override
    public Optional<Component> extendedDescription(CommandCause cause) {
        return Optional.empty();
    }

    @Override
    public Component usage(CommandCause cause) {
        return null;
    }
}