package com.ericgrandt.commands;

import com.ericgrandt.commands.models.PayCommandAccounts;
import com.ericgrandt.commands.models.PayCommandPlayers;
import com.ericgrandt.domain.Balance;
import com.ericgrandt.domain.TEAccount;
import com.ericgrandt.domain.TECommandParameterKey;
import com.ericgrandt.domain.TECommandResult;
import com.ericgrandt.domain.TECurrency;
import com.ericgrandt.services.AccountService;
import com.ericgrandt.services.TEEconomyService;
import java.math.BigDecimal;
import java.util.Optional;

import com.ericgrandt.wrappers.ParameterWrapper;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cause;

// TODO: Use requireOne
public class PayCommand implements CommandExecutor {
    private final TEEconomyService economyService;
    private final AccountService accountService;
    private final ParameterWrapper parameterWrapper;

    public PayCommand(TEEconomyService economyService, AccountService accountService, ParameterWrapper parameterWrapper) {
        this.economyService = economyService;
        this.accountService = accountService;
        this.parameterWrapper = parameterWrapper;
    }

    @Override
    public CommandResult execute(CommandContext context) throws CommandException {
        if (!(context.cause().root() instanceof Player)) {
            throw new CommandException(Component.text("Only players can use this command"));
        }

        BigDecimal amount = getAmountArgument(context);
        PayCommandAccounts accounts = getAccounts(context);
        if (!isTransferSuccessful(accounts, amount, context.contextCause())) {
            throw new CommandException(Component.text("Failed to run command: unable to set balances"));
        }

        return new TECommandResult(true);
    }

    private BigDecimal getAmountArgument(CommandContext context) throws CommandException {
        Parameter.Key<BigDecimal> amountKey = parameterWrapper.key("amount", BigDecimal.class);
        Optional<BigDecimal> amountOpt = context.one(amountKey);
        if (!amountOpt.isPresent()) {
            throw new CommandException(Component.text("Amount argument is missing"));
        } else if (!(amountOpt.get().compareTo(BigDecimal.ZERO) > 0)) {
            throw new CommandException(Component.text("Amount must be greater than 0"));
        }

        return amountOpt.get();
    }

    private PayCommandAccounts getAccounts(CommandContext context) throws CommandException {
        PayCommandPlayers players = getPlayers(context);
        TEAccount fromAccount = (TEAccount) economyService.findOrCreateAccount(players.getFromPlayer().uniqueId()).orElse(null);
        TEAccount toAccount = (TEAccount) economyService.findOrCreateAccount(players.getToPlayer().uniqueId()).orElse(null);
        if (fromAccount == null || toAccount == null) {
            throw new CommandException(Component.text("Failed to run command: invalid account(s)"));
        }

        return new PayCommandAccounts(fromAccount, toAccount);
    }

    private PayCommandPlayers getPlayers(CommandContext context) throws CommandException {
        Player fromPlayer = (Player) context.cause().root();
        Player toPlayer = getPlayerArgument(context);
        if (toPlayer.uniqueId() == fromPlayer.uniqueId()) {
            throw new CommandException(Component.text("You cannot pay yourself"));
        }

        return new PayCommandPlayers(fromPlayer, toPlayer);
    }

    private Player getPlayerArgument(CommandContext context) throws CommandException {
        Parameter.Key<ServerPlayer> playerKey = parameterWrapper.key("player", ServerPlayer.class);
        Optional<ServerPlayer> toPlayerOpt = context.one(playerKey);
        if (!toPlayerOpt.isPresent()) {
            throw new CommandException(Component.text("Player argument is missing"));
        }

        return toPlayerOpt.get();
    }

    private boolean isTransferSuccessful(PayCommandAccounts accounts, BigDecimal amount, Cause cause) {
        TECurrency defaultCurrency = (TECurrency) economyService.defaultCurrency();
        TEAccount fromAccount = accounts.getFromAccount();
        TEAccount toAccount = accounts.getToAccount();
        fromAccount.transfer(toAccount, defaultCurrency, amount, (Cause) null);

        Balance fromBalance = new Balance(
            fromAccount.uniqueId(),
            defaultCurrency.getId(),
            fromAccount.balance(defaultCurrency, cause)
        );
        Balance toBalance = new Balance(
            toAccount.uniqueId(),
            defaultCurrency.getId(),
            toAccount.balance(defaultCurrency, cause)
        );

        return accountService.setTransferBalances(fromBalance, toBalance);
    }
}
