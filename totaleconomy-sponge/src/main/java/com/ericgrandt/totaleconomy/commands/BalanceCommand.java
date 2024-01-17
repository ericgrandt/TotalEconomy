package com.ericgrandt.totaleconomy.commands;

import com.ericgrandt.totaleconomy.impl.EconomyImpl;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.UniqueAccount;

public class BalanceCommand implements CommandExecutor {
    private final EconomyImpl economy;
    private final Currency currency;

    public BalanceCommand(EconomyImpl economy, Currency currency) {
        this.economy = economy;
        this.currency = currency;
    }

    // TODO: Integration test
    @Override
    public CommandResult execute(CommandContext context) throws CommandException {
        if (!(context.cause().root() instanceof ServerPlayer player)) {
            return CommandResult.error(Component.text("This command can only be used by a player"));
        }

        CompletableFuture.runAsync(() -> onCommandHandler(player));

        return CommandResult.success();
    }

    public void onCommandHandler(ServerPlayer player) {
        UniqueAccount account = economy.findOrCreateAccount(player.uniqueId()).orElseThrow();
        BigDecimal balance = account.balance(currency, new HashSet<>());
        player.sendMessage(Component.text("Balance: ").append(currency.format(balance)));
    }
}
