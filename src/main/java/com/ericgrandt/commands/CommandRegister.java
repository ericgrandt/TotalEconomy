package com.ericgrandt.commands;

import com.ericgrandt.services.AccountService;
import com.ericgrandt.services.TEEconomyService;
import com.ericgrandt.wrappers.CommandBuilder;
import com.ericgrandt.wrappers.ParameterWrapper;
import java.math.BigDecimal;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;
import org.spongepowered.plugin.PluginContainer;

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
        registerAddMoney(event);
        registerRemoveMoney(event);
        registerSetMoney(event);
    }

    void registerBalanceCommand(final RegisterCommandEvent<Command.Parameterized> event) {
        Parameter.Value<String> currencyParameter = parameterWrapper.currency().key("currency").optional().build();

        Command.Parameterized command = commandBuilder.getBuilder()
            .executor(new BalanceCommand(economyService, accountService, parameterWrapper))
            .permission("totaleconomy.command.balance")
            .addParameter(currencyParameter)
            .build();

        event.register(
            plugin,
            command,
            "balance", "money"
        );
    }

    void registerPayCommand(final RegisterCommandEvent<Command.Parameterized> event) {
        Parameter.Value<ServerPlayer> playerParameter = parameterWrapper.player().key("player").build();
        Parameter.Value<BigDecimal> amountParameter = parameterWrapper.bigDecimal().key("amount").build();
        Parameter.Value<String> currencyParameter = parameterWrapper.currency().key("currency").optional().build();

        Command.Parameterized command = commandBuilder.getBuilder()
            .executor(new PayCommand(economyService, accountService, parameterWrapper))
            .permission("totaleconomy.command.pay")
            .addParameters(playerParameter, amountParameter, currencyParameter)
            .build();

        event.register(
            plugin,
            command,
            "pay"
        );
    }

    void registerAddMoney(final RegisterCommandEvent<Command.Parameterized> event) {
        Parameter.Value<String> playerParameter = parameterWrapper.user().key("player").build();
        Parameter.Value<String> currencyParameter = parameterWrapper.currency().key("currency").optional().build();
        Parameter.Value<BigDecimal> amountParameter = parameterWrapper.bigDecimal().key("amount").build();

        Command.Parameterized command = commandBuilder.getBuilder()
                .executor(new BalanceAddCommand(economyService, accountService, parameterWrapper))
                .permission("totaleconomy.command.addmoney")
                .addParameters(playerParameter, amountParameter, currencyParameter)
                .build();

        event.register(
                plugin,
                command,
                "addmoney", "givemoney", "balanceadd", "balancegive"
        );
    }

    void registerRemoveMoney(final RegisterCommandEvent<Command.Parameterized> event) {
        Parameter.Value<String> playerParameter = parameterWrapper.user().key("player").build();
        Parameter.Value<String> currencyParameter = parameterWrapper.currency().key("currency").optional().build();
        Parameter.Value<BigDecimal> amountParameter = parameterWrapper.bigDecimal().key("amount").build();

        Command.Parameterized command = commandBuilder.getBuilder()
                .executor(new BalanceRemoveCommand(economyService, accountService, parameterWrapper))
                .permission("totaleconomy.command.removemoney")
                .addParameters(playerParameter, amountParameter, currencyParameter)
                .build();

        event.register(
                plugin,
                command,
                "removemoney", "takemoney", "balanceremove", "balancetake"
        );
    }

    void registerSetMoney(final RegisterCommandEvent<Command.Parameterized> event) {
        Parameter.Value<String> playerParameter = parameterWrapper.user().key("player").build();
        Parameter.Value<String> currencyParameter = parameterWrapper.currency().key("currency").optional().build();
        Parameter.Value<BigDecimal> amountParameter = parameterWrapper.bigDecimal().key("amount").build();

        Command.Parameterized command = commandBuilder.getBuilder()
                .executor(new BalanceSetCommand(economyService, accountService, parameterWrapper))
                .permission("totaleconomy.command.setmoney")
                .addParameters(playerParameter, amountParameter, currencyParameter)
                .build();

        event.register(
                plugin,
                command,
                "setmoney", "setbalance"
        );
    }
}
