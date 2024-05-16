//package com.ericgrandt.totaleconomy.commands;
//
//import com.ericgrandt.totaleconomy.common.models.GetAllJobExperienceRequest;
//import com.ericgrandt.totaleconomy.common.models.GetJobExperienceResponse;
//import com.ericgrandt.totaleconomy.common.services.JobService;
//import com.ericgrandt.totaleconomy.models.JobExperience;
//import java.sql.SQLException;
//import java.util.List;
//import java.util.concurrent.CompletableFuture;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import net.kyori.adventure.text.Component;
//import net.kyori.adventure.text.TextComponent;
//import net.kyori.adventure.text.format.NamedTextColor;
//import net.kyori.adventure.text.format.TextColor;
//import net.kyori.adventure.text.format.TextDecoration;
//import org.bukkit.command.Command;
//import org.bukkit.command.CommandExecutor;
//import org.bukkit.command.CommandSender;
//import org.bukkit.entity.Player;
//import org.jetbrains.annotations.NotNull;
//
//public class JobCommand implements CommandExecutor {
//    private final Logger logger;
//    private final JobService jobService;
//
//    public JobCommand(Logger logger, JobService jobService) {
//        this.logger = logger;
//        this.jobService = jobService;
//    }
//
//    @Override
//    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
//        if (!(sender instanceof Player player)) {
//            return false;
//        }
//
//        CompletableFuture.runAsync(() -> onCommandHandler(player));
//
//        return true;
//    }
//
//    public void onCommandHandler(Player player) {
//        GetAllJobExperienceRequest request = new GetAllJobExperienceRequest(player.getUniqueId());
//        List<GetJobExperienceResponse> jobExperienceResponses = jobService.getAllJobExperience(request);
//        player.sendMessage(buildMessage(jobExperienceResponses));
//    }
//
//    private Component buildMessage(List<GetJobExperienceResponse> jobExperienceResponses) {
//        TextComponent.@NotNull Builder message = Component.newline()
//            .append(
//                Component.text(
//                    "Jobs",
//                    TextColor.fromHexString("#708090"),
//                    TextDecoration.BOLD,
//                    TextDecoration.UNDERLINED
//                )
//            ).append(Component.newline())
//            .append(Component.newline())
//            .toBuilder();
//
//        for (GetJobExperienceResponse jobExperienceResponse : jobExperienceResponses) {
//            message.append(Component.text(jobExperienceResponse.jobName(), TextColor.fromHexString("#DADFE1"), TextDecoration.BOLD))
//                .append(
//                    Component.text(
//                        " [LVL",
//                        TextColor.fromHexString("#708090"),
//                        TextDecoration.BOLD
//                    )
//                ).append(
//                    Component.text(
//                        String.format(" %s", jobExperienceResponse.level()),
//                        TextColor.fromHexString("#DADFE1"),
//                        TextDecoration.BOLD
//                    )
//                ).append(
//                    Component.text(
//                        "] [",
//                        TextColor.fromHexString("#708090"),
//                        TextDecoration.BOLD
//                    )
//                ).append(
//                    Component.text(
//                        String.format("%s/%s", jobExperienceResponse.experience(), jobExperienceResponse.experienceToNext()),
//                        TextColor.fromHexString("#DADFE1"),
//                        TextDecoration.BOLD
//                    )
//                ).append(
//                    Component.text(
//                        " EXP]",
//                        TextColor.fromHexString("#708090"),
//                        TextDecoration.BOLD
//                    )
//                ).append(Component.newline());
//        }
//
//        return message.build();
//    }
//}
