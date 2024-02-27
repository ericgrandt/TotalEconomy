package com.ericgrandt.totaleconomy.common.command;

import com.ericgrandt.totaleconomy.common.econ.ICommonEconomy;
import com.ericgrandt.totaleconomy.common.game.CommonPlayer;
import com.ericgrandt.totaleconomy.common.game.CommonSender;
import java.util.concurrent.CompletableFuture;
import net.kyori.adventure.text.Component;

public class BalanceCommand implements CommonCommand {
    private final ICommonEconomy economy;

    public BalanceCommand(final ICommonEconomy economy) {
        this.economy = economy;
    }

    @Override
    public boolean execute(CommonSender sender, CommonArguments args) {
        if (!sender.isPlayer()) {
            return false;
        }

        CompletableFuture.runAsync(() -> onCommandHandler(sender));

        return true;
    }

    public void onCommandHandler(CommonSender sender) {
        double balance = economy.getBalance((CommonPlayer) sender);
        sender.sendMessage(Component.text("Balance: ").append(economy.formatBalance(balance)));
    }
}
