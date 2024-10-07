package com.ericgrandt.totaleconomy.common.command;

import com.ericgrandt.totaleconomy.common.dto.CurrencyDto;
import com.ericgrandt.totaleconomy.common.econ.CommonEconomy;
import com.ericgrandt.totaleconomy.common.game.CommonPlayer;
import com.ericgrandt.totaleconomy.common.game.CommonSender;
import java.math.BigDecimal;
import java.util.Map;
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
    public boolean execute(CommonSender sender, Map<String, CommonParameter<?>> args) {
        if (!(sender instanceof CommonPlayer player)) {
            return false;
        }

        CompletableFuture.runAsync(() -> onCommandHandler(player));

        return true;
    }

    private void onCommandHandler(CommonPlayer player) {
        BigDecimal balance = economy.getBalance(
            player.getUniqueId(),
            currency.id()
        );
        if (balance == null) {
            player.sendMessage(Component.text("No balance found"));
            return;
        }

        player.sendMessage(Component.text("Balance: ").append(
            economy.format(currency, balance)
        ));
    }
}
