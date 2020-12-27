package com.erigitic.commands;

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
import org.spongepowered.api.service.economy.transaction.TransferResult;
import org.spongepowered.api.text.Text;

public class PayCommand implements CommandExecutor {
    private final EconomyService economyService;

    public PayCommand(EconomyService economyService) {
        this.economyService = economyService;
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

        // TODO: Get balances
        // TODO: Call AccountService.transfer()

        // Optional<UniqueAccount> fromAccount = economyService.getOrCreateAccount(fromPlayer.getUniqueId());
        // Optional<UniqueAccount> toAccount = economyService.getOrCreateAccount(toPlayer.getUniqueId());
        // if (!fromAccount.isPresent() || !toAccount.isPresent()) {
        //     throw new CommandException(Text.of("Failed to run command"));
        // }

        // EventContext context = EventContext.builder()
        //     .add(EventContextKeys.PLAYER, fromPlayer)
        //     .build();
        // TransferResult result = fromAccount.get().transfer(
        //     toAccount.get(),
        //     economyService.getDefaultCurrency(),
        //     amount,
        //     Cause.of(context, fromPlayer)
        // );
        // if (isTransferSuccessful(result)) {
        //     sendMessages(result, fromPlayer, toPlayer);
        // }

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
