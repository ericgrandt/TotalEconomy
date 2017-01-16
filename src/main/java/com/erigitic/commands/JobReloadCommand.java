package com.erigitic.commands;

import com.erigitic.jobs.TEJobs;
import com.erigitic.main.TotalEconomy;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

/**
 * Created by Life4YourGames on 16.01.17.
 */
public class JobReloadCommand implements CommandExecutor {

    private TEJobs teJobs;

    public JobReloadCommand(TotalEconomy totalEconomy) {
        this.teJobs = totalEconomy.getTEJobs();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (teJobs.reloadJobsAndSets()) {
            src.sendMessage(Text.of(TextColors.GREEN, "[TE] Sets and jobs reloaded."));
        } else {
            throw new CommandException(Text.of(TextColors.RED, "[TE] Failed to reload sets and/or jobs!"));
        }
        return CommandResult.success();
    }
}
