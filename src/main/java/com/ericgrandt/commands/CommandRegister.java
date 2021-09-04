package com.ericgrandt.commands;

import com.ericgrandt.domain.TECommandParameterKey;
import com.ericgrandt.services.AccountService;
import com.ericgrandt.services.TEEconomyService;
import com.ericgrandt.wrappers.CommandBuilder;
import com.ericgrandt.wrappers.ParameterWrapper;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.plugin.PluginContainer;

import java.math.BigDecimal;

public class CommandRegister {
    private final PluginContainer plugin;
    private final TEEconomyService economyService;
    private final AccountService accountService;
    private final CommandBuilder commandBuilder;
    private final ParameterWrapper parameterWrapper;

    public CommandRegister(
        PluginContainer plugin,
        TEEconomyService economyService,
        AccountService accountService,
        CommandBuilder commandBuilder,
        ParameterWrapper parameterWrapper
    ) {
        this.plugin = plugin;
        this.economyService = economyService;
        this.accountService = accountService;
        this.commandBuilder = commandBuilder;
        this.parameterWrapper = parameterWrapper;
    }

    public void registerCommands(final RegisterCommandEvent<Command.Parameterized> event) {
        registerBalanceCommand(event);
        registerPayCommand(event);
    }

    void registerBalanceCommand(final RegisterCommandEvent<Command.Parameterized> event) {
        Command.Parameterized command = commandBuilder.getBuilder()
            .executor(new BalanceCommand(economyService, accountService))
            .permission("totaleconomy.command.balance")
            .build();

        event.register(
            plugin,
            command,
            "balance",
            "money"
        );
    }

    void registerPayCommand(final RegisterCommandEvent<Command.Parameterized> event) {
        Parameter.Value<ServerPlayer> playerParameter = parameterWrapper.player().key("player").build();
        Parameter.Value<BigDecimal> amountParameter = parameterWrapper.bigDecimal().key("amount").build();

        Command.Parameterized command = commandBuilder.getBuilder()
            .executor(new PayCommand(economyService, accountService, parameterWrapper))
            .permission("totaleconomy.command.pay")
            .addParameters(playerParameter, amountParameter)
            .build();

        event.register(
            plugin,
            command,
            "pay"
        );
    }
}
