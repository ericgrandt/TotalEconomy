package com.ericgrandt.totaleconomy.jobs.paper.command;

import com.ericgrandt.totaleconomy.api.infra.AsyncTaskRunner;
import com.ericgrandt.totaleconomy.jobs.config.Config;
import com.ericgrandt.totaleconomy.jobs.dto.UpsertActiveJobDto;
import com.ericgrandt.totaleconomy.jobs.paper.mapper.ExceptionMapper;
import com.ericgrandt.totaleconomy.jobs.service.JobService;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.CompletableFuture;

// TODO: Test
public class JobCommand {
    private final Plugin plugin;
    private final AsyncTaskRunner taskRunner;
    private final ExceptionMapper exceptionMapper;

    private final Config config;
    private final JobService jobService;

    public JobCommand(Plugin plugin, AsyncTaskRunner taskRunner, ExceptionMapper exceptionMapper, Config config, JobService jobService) {
        this.plugin = plugin;
        this.taskRunner = taskRunner;
        this.exceptionMapper = exceptionMapper;
        this.config = config;
        this.jobService = jobService;
    }

    public LiteralCommandNode<CommandSourceStack> build() {
        return Commands.literal("job")
                .requires(source -> source.getSender() instanceof Player)
                .executes(this::executeGetActiveJob)
                .then(Commands.literal("apply")
                        .then(Commands.argument("job", StringArgumentType.string())
                            .suggests(this::listJobs)
                            .executes(this::executeApplyJob)
                )).build();
    }

    public int executeGetActiveJob(CommandContext<CommandSourceStack> ctx) {
        var player = (Player) ctx.getSource().getSender();

        taskRunner.runAsync(
                () -> {
                    try {
                        var job = jobService.getActiveJob(player.getUniqueId());
                        player.sendMessage(Messages.activeJob(job.jobId()));
                    } catch (Exception e) {
                        player.sendMessage(exceptionMapper.handleException(e));
                    }
                }
        );

        return Command.SINGLE_SUCCESS;
    }

    public int executeApplyJob(CommandContext<CommandSourceStack> ctx) {
        var player = (Player) ctx.getSource().getSender();
        var job = ctx.getArgument("job", String.class);
        var req = new UpsertActiveJobDto(player.getUniqueId(), job);

        taskRunner.runAsync(
                () -> {
                    try {
                        var activeJob = jobService.setActiveJob(req);
                        player.sendMessage(Messages.applyJob(activeJob.jobId()));
                    } catch (Exception e) {
                        player.sendMessage(exceptionMapper.handleException(e));
                    }
                }
        );

        return Command.SINGLE_SUCCESS;
    }

    private CompletableFuture<Suggestions> listJobs(
                CommandContext<CommandSourceStack> ctx,
                SuggestionsBuilder builder
    ) {
        for (var jobId : config.getJobIds()) {
            builder.suggest(jobId);
        }

        return builder.buildFuture();
    }
}
