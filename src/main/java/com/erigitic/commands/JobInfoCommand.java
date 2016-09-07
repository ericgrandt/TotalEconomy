package com.erigitic.commands;

import com.erigitic.config.AccountManager;
import com.erigitic.jobs.TEJobs;
import com.erigitic.main.TotalEconomy;
import ninja.leaping.configurate.ConfigurationNode;
import org.apache.commons.lang3.text.WordUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eric on 11/3/2015.
 */
public class JobInfoCommand implements CommandExecutor {
    private TEJobs teJobs;
    private AccountManager accountManager;

    private ConfigurationNode jobsConfig;

    // Setup pagination
    private PaginationService paginationService = Sponge.getServiceManager().provideUnchecked(PaginationService.class);
    private PaginationList.Builder builder = paginationService.builder();

    public JobInfoCommand(TotalEconomy totalEconomy) {
        teJobs = totalEconomy.getTEJobs();
        accountManager = totalEconomy.getAccountManager();

        jobsConfig = teJobs.getJobsConfig();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (src instanceof Player) {
            Player sender = ((Player) src).getPlayer().get();
            String jobName = teJobs.getPlayerJob(sender);
            List<Text> jobValues = new ArrayList<>();

            // TODO: There is probably a much better way of doing this.
            boolean hasBreakNode = (jobsConfig.getNode(jobName, "break").getValue() != null);
            boolean hasPlaceNode = (jobsConfig.getNode(jobName, "place").getValue() != null);
            boolean hasCatchNode = (jobsConfig.getNode(jobName, "catch").getValue() != null);
            boolean hasKillNode = (jobsConfig.getNode(jobName, "kill").getValue() != null);

            // TODO: Same with this, probably a much better way of doing this.
            if (hasBreakNode) { jobValues.addAll(getJobValues(jobName, "break", "Breakables")); }
            if (hasPlaceNode) { jobValues.addAll(getJobValues(jobName, "place", "Placeables")); }
            if (hasCatchNode) { jobValues.addAll(getJobValues(jobName, "catch", "Catchables")); }
            if (hasKillNode) { jobValues.addAll(getJobValues(jobName, "kill", "Killables")); }

            printNodeChildren(sender, jobValues);
        }

        return CommandResult.success();
    }

    /**
     * Gets a list of items that reward the player for doing a certain job
     *
     * @param jobName players current job
     * @param nodeName node type (break, catch, etc.)
     * @param title
     * @return List<Text> formatted text containing job values
     */
    private List<Text> getJobValues(String jobName, String nodeName, String title) {
        List<Text> jobValues = new ArrayList<>();

        jobsConfig.getNode(jobName, nodeName).getChildrenMap().keySet().forEach(value -> {
            if (value instanceof String) {
                String valueFormatted = WordUtils.capitalize(((String) value).replaceAll("_", " "));
                String expReward = jobsConfig.getNode(jobName, nodeName, value, "expreward").getString();
                String moneyReward = jobsConfig.getNode(jobName, nodeName, value, "pay").getString();

                jobValues.add(Text.of(TextColors.LIGHT_PURPLE, WordUtils.capitalize(nodeName + ": "), TextColors.GRAY,
                        valueFormatted, " | ", TextColors.GREEN, expReward, " exp", TextColors.GRAY, " | ", TextColors.GOLD,
                        accountManager.getDefaultCurrency().getSymbol(), moneyReward));
            }
        });

        return jobValues;
    }

    /**
     * Print the job values in a paginated list
     *
     * @param sender player who sent the command
     * @param jobValues list of the formatted job values
     */
    private void printNodeChildren(Player sender, List<Text> jobValues) {
        builder.reset().title(Text.of(TextColors.GOLD, TextStyles.BOLD, "Job Information"))
                .contents(jobValues)
                .padding(Text.of(TextColors.GRAY, "-"))
                .sendTo(sender);
    }
}
