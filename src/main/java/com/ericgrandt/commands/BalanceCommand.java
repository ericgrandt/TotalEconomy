package com.ericgrandt.commands;

import com.ericgrandt.domain.Balance;
import com.ericgrandt.domain.TECommandResult;
import com.ericgrandt.domain.TECurrency;
import com.ericgrandt.services.AccountService;
import com.ericgrandt.services.TEEconomyService;
import com.ericgrandt.wrappers.ParameterWrapper;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.Currency;

public class BalanceCommand implements CommandExecutor {
    private final TEEconomyService economyService;
    private final AccountService accountService;
    private final ParameterWrapper parameterWrapper;

    public BalanceCommand(TEEconomyService economyService, AccountService accountService, ParameterWrapper parameterWrapper) {
        this.economyService = economyService;
        this.accountService = accountService;
        this.parameterWrapper = parameterWrapper;
    }

    @Override
    public CommandResult execute(CommandContext context) throws CommandException {
        if (!(context.cause().root() instanceof Player)) {
            throw new CommandException(Component.text("Only players can use this command"));
        }

        Player player = (Player) context.cause().root();
        TECurrency currency = getCurrencyArgument(context);
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

    private TECurrency getCurrencyArgument(CommandContext context) throws CommandException {
        Parameter.Key<String> currencyKey = parameterWrapper.key("currency", String.class);
        Optional<String> currencyParamOpt = context.one(currencyKey);
        if (currencyParamOpt.isPresent()) {
            Optional<Currency> currencyOpt = economyService.currencies().stream().filter(c ->
                PlainTextComponentSerializer.plainText().serialize(c.displayName()).equals(currencyParamOpt.get())
            ).findFirst();

            if (currencyOpt.isPresent()) {
                return (TECurrency) currencyOpt.get();
            } else {
                throw new CommandException(Component.text("That currency does not exist"));
            }
        }

        return (TECurrency) economyService.defaultCurrency();
    }
}