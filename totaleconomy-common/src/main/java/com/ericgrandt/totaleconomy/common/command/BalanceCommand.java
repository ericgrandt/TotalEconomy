package com.ericgrandt.totaleconomy.common.command;

import com.ericgrandt.totaleconomy.common.data.dto.CurrencyDto;
import com.ericgrandt.totaleconomy.common.econ.CommonEconomy;
import com.ericgrandt.totaleconomy.common.game.CommonPlayer;
import com.ericgrandt.totaleconomy.common.game.CommonSender;
import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import net.kyori.adventure.text.Component;

public class BalanceCommand implements CommonCommand {
    private final CommonEconomy economy;
    private final CurrencyDto currency;

    public BalanceCommand(final CommonEconomy economy, final CurrencyDto currency) {
        this.economy = economy;
        this.currency = currency;
    }

    @Override
    public boolean execute(CommonSender sender, CommonArguments args) {
        if (!sender.isPlayer()) {
            return false;
        }

        CompletableFuture.runAsync(() -> onCommandHandler(sender));

        return true;
    }

    private void onCommandHandler(CommonSender sender) {
        CommonPlayer player = (CommonPlayer) sender;
        BigDecimal balance = economy.getBalance(
            player.getUniqueId(),
            currency.id()
        );
        if (balance == null) {
            sender.sendMessage(Component.text("No balance found"));
            return;
        }

        sender.sendMessage(Component.text("Balance: ").append(
            economy.format(currency, balance)
        ));
    }
}
