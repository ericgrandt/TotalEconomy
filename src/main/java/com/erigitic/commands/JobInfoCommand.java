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
import org.spongepowered.api.text.format.TextStyles;

/**
 * Created by Eric on 11/3/2015.
 */
public class JobInfoCommand implements CommandExecutor {
    private TEJobs teJobs;

    private ConfigurationNode jobsConfig;

    public JobInfoCommand(TotalEconomy totalEconomy) {
        teJobs = totalEconomy.getTEJobs();

        jobsConfig = teJobs.getJobsConfig();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (src instanceof Player) {
            Player sender = ((Player) src).getPlayer().get();
            String jobName = teJobs.getPlayerJob(sender);

            //TODO: Make this more efficient with a for loop that will loop through each node type.
            boolean hasBreakNode = (jobsConfig.getNode(jobName, "break").getValue() != null);
            boolean hasPlaceNode = (jobsConfig.getNode(jobName, "place").getValue() != null);
            boolean hasCatchNode = (jobsConfig.getNode(jobName, "catch").getValue() != null);

            if (hasBreakNode) {

            }
        }

        return CommandResult.success();
    }

    //TODO: Possibly move to TEJobs.java?
    private void printNodeChildren(Player sender, String jobName, String nodeName) {
        sender.sendMessage(Text.builder("Breakables").style(TextStyles.UNDERLINE).build());
        for (Object value : jobsConfig.getNode(jobName, "break").getChildrenMap().keySet()) {
            if (value instanceof String) {
                sender.sendMessage(Text.of(value));
            }
        }
    }
}
