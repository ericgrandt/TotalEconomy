package com.erigitic.commands;

import com.erigitic.TotalEconomy;
import com.erigitic.services.TEEconomyService;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.text.Text;

import java.math.BigDecimal;
import java.util.Optional;

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

        Optional<BigDecimal> amountOpt = args.getOne("amount");
        if (!amountOpt.isPresent()) {
            throw new CommandException(Text.of("Amount argument is missing"));
        } else if (!(amountOpt.get().compareTo(BigDecimal.ZERO) > 0)) {
            throw new CommandException(Text.of("Amount must be greater than 0"));
        }


        // BigDecimal amount = args.<BigDecimal>getOne(Text.of("amount")).get();
        // if (amount.compareTo(BigDecimal.ONE) < 0) {
        //     throw new CommandException(Text.of("Amount must be greater than 0"));
        // }
        //
        // Player fromPlayer = (Player) src;
        // Player toPlayer = args.<Player>getOne(Text.of("player")).get();
        // if (fromPlayer.getUniqueId() == toPlayer.getUniqueId()) {
        //     throw new CommandException(Text.of("You can't pay yourself"));
        // }
        //
        // TEAccount fromAccount = new TEAccount(fromPlayer.getUniqueId());
        // TEAccount toAccount = new TEAccount(toPlayer.getUniqueId());
        // Optional<Currency> currencyOptional = args.getOne("currencyName");
        // Currency currency = currencyOptional.isPresent() ? currencyOptional.get() : economyService.getDefaultCurrency();
        //
        // TransferResult result = fromAccount.transfer(toAccount, currency, amount, Cause.of(plugin.getEventContext(), plugin));
        // if (isTransferSuccessful(result)) {
        //     sendMessages(result, fromPlayer, toPlayer);
        // }

        return CommandResult.success();
    }

    // private boolean isTransferSuccessful(TransferResult result) throws CommandException {
    //     ResultType resultType = result.getResult();
    //
    //     if (resultType == ResultType.FAILED) {
    //         throw new CommandException(Text.of("Failed to pay user"));
    //     } else if (resultType == ResultType.ACCOUNT_NO_FUNDS) {
    //         throw new CommandException(Text.of("Insufficient funds"));
    //     }
    //
    //     return true;
    // }
    //
    // private void sendMessages(TransferResult result, Player fromPlayer, Player toPlayer) {
    //     Currency currency = result.getCurrency();
    //
    //     Text amount = Text.of(TextColors.GOLD, currency.format(result.getAmount()));
    //     Text toPlayerName = Text.of(TextColors.GOLD, result.getAccountTo().getDisplayName());
    //     Text fromPlayerName = Text.of(TextColors.GOLD, fromPlayer.getDisplayNameData().displayName().get());
    //
    //     fromPlayer.sendMessage(Text.of(TextColors.GRAY, "Paid ", amount, TextColors.GRAY, " to ", toPlayerName));
    //     toPlayer.sendMessage(Text.of(TextColors.GRAY, "Received ", amount, TextColors.GRAY, " from ", fromPlayerName));
    // }
}
