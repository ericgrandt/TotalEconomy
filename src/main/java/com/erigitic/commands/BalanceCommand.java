package com.erigitic.commands;

import com.erigitic.config.AccountManager;
import com.erigitic.config.TEAccount;
import com.erigitic.main.TotalEconomy;
import org.slf4j.Logger;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

/**
 * Created by Erigitic on 5/4/2015.
 */
public class BalanceCommand implements CommandExecutor {
    private Logger logger;
    private TotalEconomy totalEconomy;
    private AccountManager accountManager;

    public BalanceCommand(TotalEconomy totalEconomy) {
        this.totalEconomy = totalEconomy;
        logger = totalEconomy.getLogger();

        accountManager = totalEconomy.getAccountManager();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (src instanceof Player) {
            Player sender = ((Player) src).getPlayer().get();
            TEAccount playerAccount = (TEAccount) accountManager.getAccount(sender.getUniqueId()).get();
            Currency defaultCurrency = accountManager.getDefaultCurrency();
            Text playerBalance = defaultCurrency.format(playerAccount.getBalance(defaultCurrency));

            sender.sendMessage(Text.of(TextColors.GRAY, "Balance: ", TextColors.GOLD, defaultCurrency.getSymbol(), playerBalance));
        }

        return CommandResult.success();
    }
}
