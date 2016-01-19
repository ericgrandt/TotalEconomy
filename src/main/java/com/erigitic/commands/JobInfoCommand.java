package com.erigitic.commands;

import com.erigitic.jobs.TEJobs;
import com.erigitic.main.TotalEconomy;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

/**
 * Created by Erigitic on 11/3/2015.
 */
public class JobInfoCommand implements CommandExecutor {
    private TEJobs teJobs;

    private ConfigurationNode jobsConfig;

    public JobInfoCommand(TotalEconomy totalEconomy) {
        teJobs = totalEconomy.getTEJobs();

        jobsConfig = teJobs.getJobsConfig();
    }

    //TODO: Implement this completely. Currently have no idea how to easily grab each item from the config.
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (src instanceof Player) {
            Player sender = ((Player) src).getPlayer().get();
            String jobName = teJobs.getPlayerJob(sender);

            boolean hasBreakNode = (jobsConfig.getNode(jobName, "break").getValue() != null);

            if (hasBreakNode) {
                for (Object value : jobsConfig.getNode(jobName, "break").getChildrenMap().keySet()) {
                    if (value instanceof String) {
                        sender.sendMessage(Text.of(value));
                    }
                }
            }
        }

        return CommandResult.success();
    }
}
