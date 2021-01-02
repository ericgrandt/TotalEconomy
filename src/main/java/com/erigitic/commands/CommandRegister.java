package com.erigitic.commands;

import com.erigitic.TotalEconomy;
import com.erigitic.commands.elements.CurrencyCommandElement;
import com.erigitic.services.AccountService;
import com.erigitic.services.TEEconomyService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public class CommandRegister {
    private final TotalEconomy plugin;
    private final CommandSpec balanceCommandSpec;
    private final CommandSpec payCommandSpec;

    public CommandRegister(
        TotalEconomy plugin,
        TEEconomyService economyService,
        AccountService accountService
    ) {
        this.plugin = plugin;

        balanceCommandSpec = CommandSpec.builder()
            .description(Text.of("Displays your balance"))
            .permission("totaleconomy.command.balance")
            .executor(new BalanceCommand(economyService, accountService))
            .arguments(
                GenericArguments.optional(new CurrencyCommandElement(Text.of("currencyName")))
            )
            .build();
        payCommandSpec = CommandSpec.builder()
            .description(Text.of("Pay another player"))
            .permission("totaleconomy.command.pay")
            .executor(new PayCommand(economyService, accountService))
            .arguments(
                GenericArguments.player(Text.of("player")),
                GenericArguments.bigDecimal(Text.of("amount"))
            )
            .build();
    }

    public void registerCommands() {
        Sponge.getCommandManager().register(plugin, balanceCommandSpec, "balance");
        Sponge.getCommandManager().register(plugin, payCommandSpec, "pay");
    }
}
