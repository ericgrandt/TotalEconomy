package com.erigitic.commands;

import com.erigitic.services.AccountService;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.plugin.PluginContainer;

public class CommandRegister {
    private final PluginContainer plugin;
    private final EconomyService economyService;
    private final AccountService accountService;

    public CommandRegister(
        PluginContainer plugin,
        EconomyService economyService,
        AccountService accountService
    ) {
        this.plugin = plugin;
        this.economyService = economyService;
        this.accountService = accountService;

        // payCommandSpec = CommandSpec.builder()
        //     .description(Text.of("Pay another player"))
        //     .permission("totaleconomy.command.pay")
        //     .executor(new PayCommand(economyService, accountService))
        //     .arguments(
        //         GenericArguments.player(Text.of("player")),
        //         GenericArguments.bigDecimal(Text.of("amount"))
        //     )
        //     .build();
    }

    // public void registerCommands() {
    //     Sponge.getCommandManager().register(plugin, balanceCommandSpec, "balance");
    //     Sponge.getCommandManager().register(plugin, payCommandSpec, "pay");
    // }

    @Listener
    public void onRegisterCommands(final RegisterCommandEvent<Command> event) {
        registerBalanceCommand(event);
    }

    private void registerBalanceCommand(final RegisterCommandEvent<Command> event) {
        event.register(
            plugin,
            new BalanceCommand(economyService, accountService),
            "balance"
        );
    }
}
