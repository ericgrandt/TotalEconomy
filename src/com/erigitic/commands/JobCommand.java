package com.erigitic.commands;

import com.erigitic.config.AccountManager;
import com.erigitic.jobs.TEJobs;
import com.erigitic.main.TotalEconomy;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Texts;
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
        Player sender = ((Player) src).getPlayer().get();

        if (args.getOne("jobName").isPresent()) {
            String jobName = args.getOne("jobName").get().toString();

            teJobs.setJob(sender, jobName);
        } else {
            sender.sendMessage(Texts.of("List of jobs will go here."));
        }

        return CommandResult.success();
    }
}
