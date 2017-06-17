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

import com.erigitic.config.TECurrency;
import com.erigitic.jobs.*;
import com.erigitic.main.TotalEconomy;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class JobCommand implements CommandExecutor {

    private TotalEconomy totalEconomy;

    public JobCommand(TotalEconomy totalEconomy) {
        this.totalEconomy = totalEconomy;
    }

    public static CommandSpec commandSpec(TotalEconomy totalEconomy) {
        return CommandSpec.builder()
                .child(Set.commandSpec(totalEconomy), "set", "s")
                .child(Toggle.commandSpec(totalEconomy), "toggle", "t")
                .child(Info.commandSpec(totalEconomy), "info", "i")
                .child(Reload.commandSpec(totalEconomy), "reload")
                .description(Text.of("Display list of jobs."))
                .permission("totaleconomy.command.job")
                .arguments(GenericArguments.none())
                .executor(new JobCommand(totalEconomy))
                .build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (src instanceof Player) {
            Player player = ((Player) src).getPlayer().get();
            String jobName = totalEconomy.getTEJobManager().getPlayerJob(player);

            player.sendMessage(Text.of(TextColors.GRAY, "Current Job: ", TextColors.GOLD, totalEconomy.getTEJobManager().titleize(jobName)));
            player.sendMessage(Text.of(TextColors.GRAY, totalEconomy.getTEJobManager().titleize(jobName),
                    " Level: ", TextColors.GOLD, totalEconomy.getTEJobManager().getJobLevel(jobName, player)));
            player.sendMessage(Text.of(TextColors.GRAY, totalEconomy.getTEJobManager().titleize(jobName),
                    " Exp: ", TextColors.GOLD, totalEconomy.getTEJobManager().getJobExp(jobName, player), "/", totalEconomy.getTEJobManager().getExpToLevel(player), " exp\n"));
            player.sendMessage(Text.of(TextColors.GRAY, "Available Jobs: ", TextColors.GOLD, totalEconomy.getTEJobManager().getJobList()));

            return CommandResult.success();
        } else {
            throw new CommandException(Text.of("You can't have a job!"));
        }
    }

    public static class Set implements CommandExecutor {

        private TotalEconomy totalEconomy;

        public Set(TotalEconomy totalEconomy) {
            this.totalEconomy = totalEconomy;
        }

        public static CommandSpec commandSpec(TotalEconomy totalEconomy) {
            return CommandSpec.builder()
                    .description(Text.of("Set your job"))
                    .permission(TEPermissions.JOB_SET)
                    .executor(new Set(totalEconomy))
                    .arguments(
                            GenericArguments.string(Text.of("jobName")),
                            GenericArguments.requiringPermission(
                                    GenericArguments.userOrSource(Text.of("user")),
                                    TEPermissions.JOB_SET_OTHERS
                            )
                    )
                    .build();
        }

        @Override
        public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
            // TODO: Do checks here, in case we or other plugins want to bypass them in the future

            String jobName = args.getOne("jobName").get().toString().toLowerCase();
            User user = args.<User>getOne("user").get();

            Optional<TEJob> optJob = totalEconomy.getTEJobManager().getJob(jobName, false);
            if (!optJob.isPresent()) throw new CommandException(Text.of("Job " + jobName + " does not exist!"));

            TEJob job = optJob.get();

            if (job.getRequirement().isPresent()) {
                JobBasedRequirement req = job.getRequirement().get();

                if (req.permissionNeeded() != null && !user.hasPermission(req.permissionNeeded())) {
                    throw new CommandException(Text.of("Not permitted to join job \"", TextColors.GOLD, jobName, TextColors.RED, "\""));
                }

                if (req.jobNeeded() != null && req.jobLevelNeeded() > totalEconomy.getTEJobManager().getJobLevel(req.jobNeeded().toLowerCase(), user)) {
                     throw new CommandException(Text.of("Insufficient level! Level ",
                             TextColors.GOLD, req.jobLevelNeeded(), TextColors.RED," as a ",
                             TextColors.GOLD, req.jobNeeded(), TextColors.RED, " required!"));
                }
            }

            if (!totalEconomy.getTEJobManager().setJob(user, jobName)) {
                throw new CommandException(Text.of("Failed to set job. Contact your administrator."));
            } else if (user.getPlayer().isPresent()) {
                user.getPlayer().get().sendMessage(Text.of(TextColors.GRAY, "Job set to: ", TextColors.GOLD, totalEconomy.getTEJobManager().titleize(jobName)));
            }

            // Only send additional feedback if CommandSource isn't the target.
            if (!(src instanceof User) || !((User) src).getUniqueId().equals(user.getUniqueId())) {
                src.sendMessage(Text.of(TextColors.GREEN, "Job set."));
            }

            return CommandResult.success();
        }
    }

    public static class Info implements CommandExecutor {

        private TotalEconomy totalEconomy;

        public Info(TotalEconomy totalEconomy) {
            this.totalEconomy = totalEconomy;
        }

        public static CommandSpec commandSpec(TotalEconomy totalEconomy) {
            return CommandSpec.builder()
                    .description(Text.of("Prints out a list of items that reward exp and money for the current job"))
                    .permission("totaleconomy.command.job.info")
                    .executor(new Info(totalEconomy))
                    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("jobName"))))
                    .build();
        }

        // Setup pagination
        private PaginationService paginationService = Sponge.getServiceManager().provideUnchecked(PaginationService.class);
        private PaginationList.Builder pageBuilder = paginationService.builder();

        @Override
        public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
            TEJobManager teJobManager = totalEconomy.getTEJobManager();

            Optional<String> optJobName = args.getOne("jobName");
            Optional<TEJob> optJob = Optional.empty();

            if (!optJobName.isPresent() && (src instanceof Player)) {
                optJob = teJobManager.getJob(teJobManager.getPlayerJob((Player) src), true);
            }

            if (optJobName.isPresent()) {
                optJob = teJobManager.getJob(optJobName.get(), false);
            }

            if (!optJob.isPresent()) {
                throw new CommandException(Text.of(TextColors.RED, "Unknown job: \"" + optJobName.orElse("") + "\""));
            }

            List<Text> lines = new ArrayList();

            for (String s : optJob.get().getSets()) {
                Optional<TEJobSet> optSet = teJobManager.getJobSet(s);

                if (optSet.isPresent()) {
                    TEJobSet jobSet = optSet.get();
                    Currency defaultCurrency = totalEconomy.getDefaultCurrency();

                    for (TEActionReward actionReward : jobSet.getActionRewards()) {
                        lines.add(Text.of(TextColors.GOLD, "[", teJobManager.titleize(actionReward.getAction()), "] ", TextColors.GRAY, actionReward.getTargetID(), TextColors.GOLD, " (", actionReward.getExpReward(), " EXP) (", defaultCurrency.format(actionReward.getMoneyReward()), ")"));
                    }
                }
            }

            pageBuilder.reset()
                    .header(Text.of(TextColors.GRAY, "Job information for ", TextColors.GOLD, optJobName.orElseGet(() -> teJobManager.getPlayerJob(((Player) src))),"\n"))
                    .contents(lines.toArray(new Text[lines.size()]))
                    .sendTo(src);

            return CommandResult.success();
        }
    }

    public static class Reload implements CommandExecutor {

        private TotalEconomy totalEconomy;

        public Reload(TotalEconomy totalEconomy) {
            this.totalEconomy = totalEconomy;
        }

        public static CommandSpec commandSpec(TotalEconomy totalEconomy) {
            return CommandSpec.builder()
                    .description(Text.of("Reloads sets and jobs"))
                    .permission("totaleconomy.command.job.reload")
                    .executor(new Reload(totalEconomy))
                    .arguments(GenericArguments.none())
                    .build();
        }

        @Override
        public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
            if (totalEconomy.getTEJobManager().reloadJobsAndSets()) {
                src.sendMessage(Text.of(TextColors.GRAY, "[TE] Sets and jobs reloaded."));
            } else {
                throw new CommandException(Text.of(TextColors.RED, "[TE] Failed to reload sets and/or jobs!"));
            }

            return CommandResult.success();
        }
    }

    public static class Toggle implements CommandExecutor {

        private TotalEconomy totalEconomy;

        public Toggle(TotalEconomy totalEconomy) {
            this.totalEconomy = totalEconomy;
        }

        public static CommandSpec commandSpec(TotalEconomy totalEconomy) {
            return CommandSpec.builder()
                    .description(Text.of("Toggle job notifications on/off"))
                    .permission("totaleconomy.command.job.toggle")
                    .executor(new Toggle(totalEconomy))
                    .build();
        }

        @Override
        public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
            if (src instanceof Player) {
                Player sender = ((Player) src).getPlayer().get();

                totalEconomy.getAccountManager().toggleNotifications(sender);

                return CommandResult.success();
            }

            return CommandResult.empty();
        }
    }
}