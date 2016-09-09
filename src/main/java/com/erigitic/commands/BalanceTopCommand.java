package com.erigitic.commands;

import com.erigitic.config.AccountManager;
import com.erigitic.config.TEAccount;
import com.erigitic.config.TECurrency;
import com.erigitic.main.TotalEconomy;
import ninja.leaping.configurate.ConfigurationNode;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Eric on 6/2/2016.
 */
public class BalanceTopCommand implements CommandExecutor {
    private Logger logger;
    private TotalEconomy totalEconomy;
    private AccountManager accountManager;

    private PaginationService paginationService = Sponge.getServiceManager().provideUnchecked(PaginationService.class);
    private PaginationList.Builder builder = paginationService.builder();

    public BalanceTopCommand(TotalEconomy totalEconomy) {
        this.totalEconomy = totalEconomy;
        logger = totalEconomy.getLogger();

        accountManager = totalEconomy.getAccountManager();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        ConfigurationNode accountNode = accountManager.getAccountConfig();
        List<Text> accountBalances = new ArrayList<>();

        // TODO: Add customization to this (amount of accounts to show).
        accountNode.getChildrenMap().keySet().forEach(accountUUID -> {
            TEAccount playerAccount = (TEAccount) accountManager.getOrCreateAccount(UUID.fromString(accountUUID.toString())).get();
            Currency defaultCurrency = accountManager.getDefaultCurrency();
            Text playerName = playerAccount.getDisplayName();
            Text playerBalance = defaultCurrency.format(playerAccount.getBalance(defaultCurrency));

            accountBalances.add(Text.of(TextColors.GRAY, playerName.toPlain(), ": ", TextColors.GOLD, playerBalance.toPlain()));
        });

        builder.reset().title(Text.of(TextColors.GOLD, "Top Balances"))
                .contents(accountBalances)
                .padding(Text.of(TextColors.GRAY, "-"))
                .sendTo(src);

        return CommandResult.success();
    }
}
