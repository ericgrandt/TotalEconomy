package com.ericgrandt.commands;

import com.ericgrandt.services.AccountService;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.plugin.PluginContainer;

public class CommandRegister {
    private final PluginContainer plugin;
    private final EconomyService economyService;
    private final AccountService accountService;
    private final CommandBuilder commandBuilder;

    public CommandRegister(
        PluginContainer plugin,
        EconomyService economyService,
        AccountService accountService,
        CommandBuilder commandBuilder
    ) {
        this.plugin = plugin;
        this.economyService = economyService;
        this.accountService = accountService;
        this.commandBuilder = commandBuilder;

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

    public void registerCommands(final RegisterCommandEvent<Command.Parameterized> event) {
        registerBalanceCommand(event);
    }

    void registerBalanceCommand(final RegisterCommandEvent<Command.Parameterized> event) {
        Command.Parameterized command = commandBuilder.getBuilder()
            .executor(new BalanceCommand(economyService, accountService))
            .build();

        event.register(
            plugin,
            command,
            "balance"
        );
    }
}
