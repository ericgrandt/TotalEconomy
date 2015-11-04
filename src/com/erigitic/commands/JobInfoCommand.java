package com.erigitic.commands;

import com.erigitic.jobs.TEJobs;
import com.erigitic.main.TotalEconomy;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;

/**
 * Created by Erigitic on 11/3/2015.
 */
public class JobInfoCommand implements CommandExecutor {
    private TEJobs teJobs;

    public JobInfoCommand(TotalEconomy totalEconomy) {
        teJobs = totalEconomy.getTEJobs();
    }

    //TODO: Implement this completely. Currently have no idea how to easily grab each item from the config.
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (src instanceof Player) {
            Player sender = ((Player) src).getPlayer().get();
            String jobName = teJobs.getPlayerJob(sender);

            Text information = Texts.builder("\n" + jobName + " Info\n\n").style(TextStyles.BOLD)
                    .build();

            sender.sendMessage(information);
        }

        return CommandResult.success();
    }
}
