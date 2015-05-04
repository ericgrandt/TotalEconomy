package com.erigitic.commands;

import com.erigitic.config.AccountManager;
import com.erigitic.main.TotalEconomy;
import org.slf4j.Logger;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;

import java.math.BigDecimal;

/**
 * Created by Erigitic on 5/3/2015.
 */
public class PayCommand implements CommandExecutor {

    private Logger logger;
    private TotalEconomy totalEconomy;
    private AccountManager accountManager;

    public PayCommand(TotalEconomy totalEconomy) {
        this.totalEconomy = totalEconomy;
        logger = totalEconomy.getLogger();

        accountManager = totalEconomy.getAccountManager();
    }

    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player sender = ((Player) src).getPlayer().get();
        Player recipitent = (Player) args.getOne("player").get();
        BigDecimal amount = new BigDecimal((String) args.getOne("amount").get()).setScale(2, BigDecimal.ROUND_UNNECESSARY);

        if (recipitent.isOnline()) {
            if (accountManager.hasMoney(sender, amount)) {
                accountManager.removeFromBalance(sender, amount);
                accountManager.addToBalance(recipitent, amount);

                logger.info("" + accountManager.getBalance(sender));
            }
        } else {
            sender.sendMessage(Texts.of("Player is not online."));
        }

        return CommandResult.success();
    }
}
