/*
 * This file is part of Total Economy, licensed under the MIT License (MIT).
 *
 * Copyright (c) Eric Grandt <https://www.ericgrandt.com>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.erigitic.commands;

import com.erigitic.config.AccountManager;
import com.erigitic.jobs.TEJob;
import com.erigitic.jobs.TEJobManager;
import com.erigitic.jobs.TEJobSet;
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
import java.util.Map;
import java.util.Optional;

public class JobInfoCommand implements CommandExecutor {
    private TEJobManager teJobManager;
    private AccountManager accountManager;

    private ConfigurationNode jobsConfig;

    // Setup pagination
    private PaginationService paginationService = Sponge.getServiceManager().provideUnchecked(PaginationService.class);
    private PaginationList.Builder builder = paginationService.builder();

    public JobInfoCommand(TotalEconomy totalEconomy) {
        teJobManager = totalEconomy.getTEJobManager();
        accountManager = totalEconomy.getAccountManager();

        jobsConfig = teJobManager.getJobsConfig();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Optional<String> optJobName = args.getOne("jobName");
        Optional<TEJob> optJob = Optional.empty();

        if (!optJobName.isPresent() && (src instanceof Player)) {
            optJob = teJobManager.getPlayerTEJob(((Player) src));
        }

        if (optJobName.isPresent()) {
            optJob = teJobManager.getJob(optJobName.get(), false);
        }

        if (!optJob.isPresent()) {
            throw new CommandException(Text.of(TextColors.RED, "Unknown job: \"" + optJobName.orElse("") + "\""));
        }
        src.sendMessage(Text.of(TextColors.YELLOW, "[TE] There may be a long output following now..."));
        List<Text> lines = new ArrayList();

        lines.add(Text.of(TextColors.GREEN, "[TE] Job info about ", TextColors.GOLD, optJobName.isPresent() ? optJobName.get() : teJobManager.getPlayerJob(((Player) src)),"\n"));

        for (String s : optJob.get().getSets()) {
            Optional<TEJobSet> optSet = teJobManager.getJobSet(s);

            if (optSet.isPresent()) {
                lines.add(Text.of(TextColors.GRAY, " * SET ", TextColors.WHITE, s, "\n"));
                Map<String, List<String>> map = optSet.get().getRewardData();
                map.forEach((k, v) -> {
                    //Add event name
                    lines.add(Text.of(TextColors.GRAY, " -> ", TextColors.GOLD, TextStyles.ITALIC, k, "\n"));
                    //Add targets
                    v.forEach(id -> {
                        lines.add(Text.of(TextColors.GRAY, "    ID:",TextColors.DARK_GREEN, id, "\n"));
                    });
                });
            } else {
                lines.add(Text.of(TextColors.RED, " * SET ", TextColors.WHITE, s, TextColors.RED, " UNKNOWN", "\n"));
            }
        }

        if (src instanceof Player) {
            int level = teJobManager.getJobLevel(teJobManager.getPlayerJob(((Player) src)), ((Player) src));
            int exp = teJobManager.getJobExp(teJobManager.getPlayerJob(((Player) src)), ((Player) src));

            lines.add(Text.of(TextColors.GRAY, "Your level: ", TextColors.GOLD, level, " @ ", exp, "\n"));
        }

        src.sendMessage(Text.join(lines.toArray(new Text[lines.size()])));

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
                String expReward = jobsConfig.getNode(jobName, nodeName, value, "expreward").getString();
                String moneyReward = jobsConfig.getNode(jobName, nodeName, value, "pay").getString();
                String valueFormatted;

                if (((String) value).contains(":"))
                    value = ((String) value).split(":")[1];

                valueFormatted = WordUtils.capitalize(((String) value).replaceAll("_", " "));

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
