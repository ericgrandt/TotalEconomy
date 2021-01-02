package com.erigitic.commands;

import com.erigitic.domain.Balance;
import com.erigitic.domain.TECurrency;
import com.erigitic.services.AccountService;
import java.math.BigDecimal;
import java.util.Optional;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.text.Text;

public class PayCommand implements CommandExecutor {
    private final EconomyService economyService;
    private final AccountService accountService;

    public PayCommand(EconomyService economyService, AccountService accountService) {
        this.economyService = economyService;
        this.accountService = accountService;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            throw new CommandException(Text.of("Only players can use this command"));
        }

        BigDecimal amount = getAmountArgument(args);
        Player fromPlayer = (Player) src;
        Player toPlayer = getPlayerArgument(args);
        if (toPlayer.getUniqueId() == fromPlayer.getUniqueId()) {
            throw new CommandException(Text.of("You cannot pay yourself"));
        }

        UniqueAccount fromAccount = economyService.getOrCreateAccount(fromPlayer.getUniqueId()).orElse(null);
        UniqueAccount toAccount = economyService.getOrCreateAccount(toPlayer.getUniqueId()).orElse(null);
        if (fromAccount == null || toAccount == null) {
            throw new CommandException(Text.of("Failed to run command: invalid account(s)"));
        }

        TECurrency defaultCurrency = (TECurrency) economyService.getDefaultCurrency();
        EventContext context = EventContext.builder()
            .add(EventContextKeys.PLAYER, fromPlayer)
            .build();
        fromAccount.transfer(toAccount, defaultCurrency, amount, Cause.of(context, fromPlayer));

        Balance fromBalance = new Balance(
            fromAccount.getUniqueId(),
            defaultCurrency.getIntId(),
            fromAccount.getBalance(defaultCurrency)
        );
        Balance toBalance = new Balance(
            toAccount.getUniqueId(),
            defaultCurrency.getIntId(),
            toAccount.getBalance(defaultCurrency)
        );

        boolean isSuccessful = accountService.setTransferBalances(fromBalance, toBalance);

        if (!isSuccessful) {
            throw new CommandException(Text.of("Failed to run command: unable to set balances"));
        }

        return CommandResult.success();
    }

    private BigDecimal getAmountArgument(CommandContext args) throws CommandException {
        Optional<BigDecimal> amountOpt = args.getOne("amount");
        if (!amountOpt.isPresent()) {
            throw new CommandException(Text.of("Amount argument is missing"));
        } else if (!(amountOpt.get().compareTo(BigDecimal.ZERO) > 0)) {
            throw new CommandException(Text.of("Amount must be greater than 0"));
        }

        return amountOpt.get();
    }

    private Player getPlayerArgument(CommandContext args) throws CommandException {
        Optional<Player> toPlayerOpt = args.getOne("player");
        if (!toPlayerOpt.isPresent()) {
            throw new CommandException(Text.of("Player argument is missing"));
        }

        return toPlayerOpt.get();
    }
}
