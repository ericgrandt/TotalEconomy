package com.ericgrandt.commands;

import com.ericgrandt.TotalEconomy;
import com.ericgrandt.commands.models.PayCommandAccounts;
import com.ericgrandt.commands.models.PayCommandPlayers;
import com.ericgrandt.config.LocalePaths;
import com.ericgrandt.config.Locales.Placeholders;
import com.ericgrandt.domain.Balance;
import com.ericgrandt.domain.TEAccount;
import com.ericgrandt.domain.TECommandResult;
import com.ericgrandt.domain.TECurrency;
import com.ericgrandt.services.AccountService;
import com.ericgrandt.services.TEEconomyService;
import com.ericgrandt.wrappers.ParameterWrapper;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.util.locale.LocaleSource;

public class PayCommand implements CommandExecutor {
    private final TEEconomyService economyService;
    private final AccountService accountService;
    private final ParameterWrapper parameterWrapper;
    private final TotalEconomy plugin = TotalEconomy.getInstance();

    public PayCommand(TEEconomyService economyService, AccountService accountService, ParameterWrapper parameterWrapper) {
        this.economyService = economyService;
        this.accountService = accountService;
        this.parameterWrapper = parameterWrapper;
    }

    @Override
    public CommandResult execute(CommandContext context) throws CommandException {
        if (!(context.cause().root() instanceof Player)) {
            throw new CommandException(plugin.getLocales().getText("Only players can use this command", ((LocaleSource) context.cause().root()).locale(), LocalePaths.ONLY_PLAYER));
        }

        BigDecimal amount = getAmountArgument(context);
        PayCommandAccounts accounts = getAccounts(context);
        if (!isTransferSuccessful(context, accounts, amount, context.contextCause())) {
            throw new CommandException(plugin.getLocales().getText("Failed to run command: unable to set balances", ((LocaleSource) context.cause().root()).locale(), LocalePaths.PAY_FAIL));
        }
        Currency currency = getCurrencyArgument(context);
        Player sender = (Player) context.cause().root();
        Player recipient = getPlayerArgument(context);
        sender.sendMessage(plugin.getLocales().getTextWhithReplacers("&aYou have sent &6" + Placeholders.CURRENCY + Placeholders.VALUE + " &ato &6" + Placeholders.PLAYER + "&a.", sender.locale(), plugin.getLocales().createReplaceMap(Arrays.asList(Placeholders.CURRENCY, Placeholders.VALUE, Placeholders.PLAYER), Arrays.asList(toPlain(currency.symbol()), amount.doubleValue(), recipient.name())), LocalePaths.PAY_SENDER));
        recipient.sendMessage(plugin.getLocales().getTextWhithReplacers("&aYou have received &6" + Placeholders.CURRENCY + Placeholders.VALUE + " &afrom  &6" + Placeholders.PLAYER + "&a.", recipient.locale(), plugin.getLocales().createReplaceMap(Arrays.asList(Placeholders.CURRENCY, Placeholders.VALUE, Placeholders.PLAYER), Arrays.asList(toPlain(currency.symbol()), amount.doubleValue(), sender.name())), LocalePaths.PAY_RECIPIENT));
        return new TECommandResult(true);
    }

    private BigDecimal getAmountArgument(CommandContext context) throws CommandException {
        Parameter.Key<BigDecimal> amountKey = parameterWrapper.key("amount", BigDecimal.class);
        Optional<BigDecimal> amountOpt = context.one(amountKey);
        if (!amountOpt.isPresent()) {
            throw new CommandException(plugin.getLocales().getText("Amount argument is missing", ((LocaleSource) context.cause().root()).locale(), LocalePaths.PAY_MISSING_AMOUNT));
        } else if (!(amountOpt.get().compareTo(BigDecimal.ZERO) > 0)) {
            throw new CommandException(plugin.getLocales().getText("Amount must be greater than 0", ((LocaleSource) context.cause().root()).locale(), LocalePaths.PAY_ZERO_OR_BELOW));
        }

        return amountOpt.get();
    }

    private PayCommandAccounts getAccounts(CommandContext context) throws CommandException {
        PayCommandPlayers players = getPlayers(context);
        TEAccount fromAccount = (TEAccount) economyService.findOrCreateAccount(players.getFromPlayer().uniqueId()).orElse(null);
        TEAccount toAccount = (TEAccount) economyService.findOrCreateAccount(players.getToPlayer().uniqueId()).orElse(null);
        if (fromAccount == null || toAccount == null) {
            throw new CommandException(plugin.getLocales().getText("Failed to run command: invalid account(s)", ((LocaleSource) context.cause().root()).locale(), LocalePaths.INVALID_ACCOUNT));
        }

        return new PayCommandAccounts(fromAccount, toAccount);
    }

    private PayCommandPlayers getPlayers(CommandContext context) throws CommandException {
        Player fromPlayer = (Player) context.cause().root();
        Player toPlayer = getPlayerArgument(context);
        if (toPlayer.uniqueId() == fromPlayer.uniqueId()) {
            throw new CommandException(plugin.getLocales().getText("You cannot pay yourself", ((LocaleSource) context.cause().root()).locale(), LocalePaths.PAY_SELF));
        }

        return new PayCommandPlayers(fromPlayer, toPlayer);
    }

    private Player getPlayerArgument(CommandContext context) throws CommandException {
        Parameter.Key<ServerPlayer> playerKey = parameterWrapper.key("player", ServerPlayer.class);
        Optional<ServerPlayer> toPlayerOpt = context.one(playerKey);
        if (!toPlayerOpt.isPresent()) {
            throw new CommandException(plugin.getLocales().getText("Player argument is missing", ((LocaleSource) context.cause().root()).locale(), LocalePaths.PLAYER_MISSING));
        }

        return toPlayerOpt.get();
    }

    private boolean isTransferSuccessful(CommandContext context, PayCommandAccounts accounts, BigDecimal amount, Cause cause) {
        TECurrency currency = getCurrencyArgument(context);
        TEAccount fromAccount = accounts.getFromAccount();
        TEAccount toAccount = accounts.getToAccount();
        fromAccount.transfer(toAccount, currency, amount, (Cause) null);

        Balance fromBalance = new Balance(
            fromAccount.uniqueId(),
            currency.getId(),
            fromAccount.balance(currency, cause)
        );
        Balance toBalance = new Balance(
            toAccount.uniqueId(),
            currency.getId(),
            toAccount.balance(currency, cause)
        );

        return accountService.setTransferBalances(fromBalance, toBalance);
    }

    private TECurrency getCurrencyArgument(CommandContext context) {
        Parameter.Key<String> currencyKey = parameterWrapper.key("currency", String.class);
        Optional<String> currencyParamOpt = context.one(currencyKey);
        if (currencyParamOpt.isPresent()) {
            Optional<Currency> currencyOpt = economyService.currencies().stream().filter(c ->
                PlainTextComponentSerializer.plainText().serialize(c.displayName()).equals(currencyParamOpt.get())
            ).findFirst();

            if (currencyOpt.isPresent()) {
                return (TECurrency) currencyOpt.get();
            }
        }

        return (TECurrency) economyService.defaultCurrency();
    }
   
    private String toPlain(Component component) {
    	return LegacyComponentSerializer.legacyAmpersand().serialize(component);
    }
}
