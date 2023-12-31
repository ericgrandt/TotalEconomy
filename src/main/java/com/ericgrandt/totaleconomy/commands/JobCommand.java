package com.ericgrandt.totaleconomy.commands;

import com.ericgrandt.totaleconomy.models.JobExperience;
import com.ericgrandt.totaleconomy.services.JobService;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class JobCommand implements CommandExecutor {
    private final Logger logger;
    private final JobService jobService;

    public JobCommand(Logger logger, JobService jobService) {
        this.logger = logger;
        this.jobService = jobService;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return false;
        }

        CompletableFuture.runAsync(() -> onCommandHandler(player));

        return true;
    }

    public void onCommandHandler(Player player) {
        try {
            List<JobExperience> jobExperienceList = jobService.getExperienceForAllJobs(player.getUniqueId());
            player.sendMessage(buildMessage(jobExperienceList));
        } catch (SQLException e) {
            player.sendMessage(
                Component.text("An error has occurred. Please contact an administrator.", NamedTextColor.RED)
            );
            logger.log(
                Level.SEVERE,
                "An exception occurred during the handling of the job command.",
                e
            );
        }
    }

    private Component buildMessage(List<JobExperience> jobExperienceList) {
        TextComponent.@NotNull Builder message = Component.newline()
            .append(
                Component.text(
                    "Jobs",
                    TextColor.fromHexString("#708090"),
                    TextDecoration.BOLD,
                    TextDecoration.UNDERLINED
                )
            ).append(Component.newline())
            .append(Component.newline())
            .toBuilder();

        for (JobExperience jobExperience : jobExperienceList) {
            message.append(Component.text(jobExperience.jobName(), TextColor.fromHexString("#DADFE1"), TextDecoration.BOLD))
                .append(
                    Component.text(
                        " [LVL",
                        TextColor.fromHexString("#708090"),
                        TextDecoration.BOLD
                    )
                ).append(
                    Component.text(
                        String.format(" %s", jobExperience.level()),
                        TextColor.fromHexString("#DADFE1"),
                        TextDecoration.BOLD
                    )
                ).append(
                    Component.text(
                        "] [",
                        TextColor.fromHexString("#708090"),
                        TextDecoration.BOLD
                    )
                ).append(
                    Component.text(
                        String.format("%s/%s", jobExperience.experience(), jobExperience.experienceToNext()),
                        TextColor.fromHexString("#DADFE1"),
                        TextDecoration.BOLD
                    )
                ).append(
                    Component.text(
                        " EXP]",
                        TextColor.fromHexString("#708090"),
                        TextDecoration.BOLD
                    )
                ).append(Component.newline());
        }

        return message.build();
    }
}
