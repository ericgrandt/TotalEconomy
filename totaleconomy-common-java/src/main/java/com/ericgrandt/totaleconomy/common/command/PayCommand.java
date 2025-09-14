package com.ericgrandt.totaleconomy.common.command;

import com.ericgrandt.totaleconomy.common.dto.CurrencyDto;
import com.ericgrandt.totaleconomy.common.econ.CommonEconomy;
import com.ericgrandt.totaleconomy.common.econ.TransactionResult;
import com.ericgrandt.totaleconomy.common.game.CommonPlayer;
import com.ericgrandt.totaleconomy.common.game.CommonSender;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.kyori.adventure.text.Component;

public class PayCommand implements CommonCommand {
    private final CommonEconomy economy;
    private final CurrencyDto currency;

    public PayCommand(final CommonEconomy economy, final CurrencyDto currency) {
        this.economy = economy;
        this.currency = currency;
    }

    @Override
    public boolean execute(CommonSender sender, Map<String, CommonParameter<?>> args) {
        if (!(sender instanceof CommonPlayer player)) {
            return false;
        }

        if (!args.containsKey("toPlayer") || !args.containsKey("amount")) {
            return false;
        }

        CommonPlayer toPlayer = (CommonPlayer) args.get("toPlayer").value();
        BigDecimal amount = ((BigDecimal) args.get("amount").value()).setScale(
            currency.numFractionDigits(),
            RoundingMode.DOWN
        );

        if (toPlayer.isNull()) {
            player.sendMessage(Component.text("Invalid player specified"));
            return false;
        }
        if (player.getUniqueId() == toPlayer.getUniqueId()) {
            player.sendMessage(Component.text("You cannot pay yourself"));
            return false;
        }

        CompletableFuture.runAsync(() -> onCommandHandler(player, toPlayer, amount));

        return true;
    }

    private void onCommandHandler(CommonPlayer player, CommonPlayer toPlayer, BigDecimal amount) {
        TransactionResult result = economy.transfer(
            player.getUniqueId(),
            toPlayer.getUniqueId(),
            currency.id(),
            amount
        );
        if (result.resultType() == TransactionResult.ResultType.FAILURE) {
            player.sendMessage(Component.text(result.message()));
            return;
        }

        Component formattedAmount = economy.format(currency, amount);
        player.sendMessage(buildFromPlayerMessage(formattedAmount, toPlayer));
        toPlayer.sendMessage(buildToPlayerMessage(formattedAmount, player));
    }

    private Component buildFromPlayerMessage(Component formattedAmount, CommonPlayer toPlayer) {
        Component toPlayerName = Component.text(toPlayer.getName());
        return Component.text(
            "You sent "
        ).append(
            formattedAmount
        ).append(
            Component.text(" to ")
        ).append(
            toPlayerName
        );
    }

    private Component buildToPlayerMessage(Component formattedAmount, CommonPlayer fromPlayer) {
        Component fromPlayerName = Component.text(fromPlayer.getName());
        return Component.text(
            "You received "
        ).append(
            formattedAmount
        ).append(
            Component.text(" from ")
        ).append(
            fromPlayerName
        );
    }
}
