package com.erigitic.commands;

import com.erigitic.config.AccountManager;
import com.erigitic.jobs.TEJobs;
import com.erigitic.main.TotalEconomy;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;

/**
 * Created by Erigitic on 5/5/2015.
 */
public class JobCommand implements CommandExecutor {
    private AccountManager accountManager;
    private TEJobs teJobs;

    public JobCommand(TotalEconomy totalEconomy) {
        accountManager = totalEconomy.getAccountManager();
        teJobs = totalEconomy.getTEJobs();
    }

    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (src instanceof Player) {
            Player sender = ((Player) src).getPlayer().get();

            if (args.getOne("jobName").isPresent()) {
                String jobName = args.getOne("jobName").get().toString();

                teJobs.setJob(sender, jobName);
            } else {
                String jobName = teJobs.getPlayerJob(sender);

                sender.sendMessage(Texts.of(TextColors.GRAY, "Your current job is: ", TextColors.GOLD, jobName));
                sender.sendMessage(Texts.of(TextColors.GRAY, jobName, " Level: ", TextColors.GOLD, teJobs.getJobLevel(jobName, sender)));
                sender.sendMessage(Texts.of(TextColors.GRAY, jobName, " Exp: ", TextColors.GOLD, teJobs.getJobExp(jobName, sender), "/", teJobs.getExpToLevel(sender), "\n"));
                sender.sendMessage(Texts.of(TextColors.GRAY, "Available Jobs: ", TextColors.GOLD, teJobs.getJobList()));
            }
        }

        return CommandResult.success();
    }
}