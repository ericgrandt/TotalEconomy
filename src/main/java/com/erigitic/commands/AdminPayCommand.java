package com.erigitic.commands;

import com.erigitic.config.AccountManager;
import com.erigitic.main.TotalEconomy;
import org.slf4j.Logger;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;

import java.math.BigDecimal;

/**
 * Created by Erigitic on 9/7/2015.
 */
public class AdminPayCommand implements CommandExecutor {
    private Logger logger;
    private TotalEconomy totalEconomy;
    private AccountManager accountManager;

    public AdminPayCommand(TotalEconomy totalEconomy) {
        this.totalEconomy = totalEconomy;
        logger = totalEconomy.getLogger();

        accountManager = totalEconomy.getAccountManager();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (src instanceof Player) {
            Player sender = ((Player) src).getPlayer().get();
            Object playerArg = args.getOne("player").get();
            String strAmount = (String) args.getOne("amount").get();
            BigDecimal amount;

            if (totalEconomy.isNumeric(strAmount)) {
                if (!strAmount.contains("-")) {
                    amount = new BigDecimal((String) args.getOne("amount").get()).setScale(2, BigDecimal.ROUND_DOWN);

                    //TODO: Possibly allow people to send money to offline players? Might be possible with the way I have this implemented?
                    if (playerArg instanceof Player) {
                        Player recipient = (Player) playerArg;

                        if (recipient.isOnline()) {
                            accountManager.addToBalance(recipient.getUniqueId(), amount, true);
                            sender.sendMessage(Texts.of(TextColors.GRAY, "You have sent ", TextColors.GOLD, totalEconomy.getCurrencySymbol(), amount,
                                    TextColors.GRAY, " to ", TextColors.GOLD, recipient.getName()));
                        } else {
                            sender.sendMessage(Texts.of(TextColors.RED, "Player is not online."));
                        }
                    }
                } else {
                    sender.sendMessage(Texts.of(TextColors.RED, "The amount must be positive."));
                }
            } else {
                sender.sendMessage(Texts.of(TextColors.RED, "The amount must only contain numbers and a single decimal point if needed."));
            }
        }

        return CommandResult.success();
    }
}
