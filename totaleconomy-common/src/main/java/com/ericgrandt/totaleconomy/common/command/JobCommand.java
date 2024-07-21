package com.ericgrandt.totaleconomy.common.command;

import com.ericgrandt.totaleconomy.common.game.CommonPlayer;
import com.ericgrandt.totaleconomy.common.game.CommonSender;
import com.ericgrandt.totaleconomy.common.models.GetAllJobExperienceRequest;
import com.ericgrandt.totaleconomy.common.models.GetJobExperienceResponse;
import com.ericgrandt.totaleconomy.common.services.JobService;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class JobCommand implements CommonCommand {
    private final JobService service;

    public JobCommand(final JobService service) {
        this.service = service;
    }

    @Override
    public boolean execute(CommonSender sender, Map<String, CommonParameter<?>> args) {
        if (!(sender instanceof CommonPlayer player)) {
            return false;
        }

        CompletableFuture.runAsync(() -> onCommandHandler(player));

        return true;
    }

    private void onCommandHandler(CommonPlayer player) {
        GetAllJobExperienceRequest request = new GetAllJobExperienceRequest(player.getUniqueId());
        List<GetJobExperienceResponse> jobExperienceResponses = service.getAllJobExperience(request);
        player.sendMessage(buildMessage(jobExperienceResponses));
    }

    private Component buildMessage(List<GetJobExperienceResponse> jobExperienceResponses) {
        TextComponent.Builder message = Component.newline()
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

        for (GetJobExperienceResponse jobExperienceResponse : jobExperienceResponses) {
            message.append(Component.text(jobExperienceResponse.jobName(), TextColor.fromHexString("#DADFE1"), TextDecoration.BOLD))
                .append(
                    Component.text(
                        " [LVL",
                        TextColor.fromHexString("#708090"),
                        TextDecoration.BOLD
                    )
                ).append(
                    Component.text(
                        String.format(" %s", jobExperienceResponse.level()),
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
                        String.format("%s/%s", jobExperienceResponse.experience(), jobExperienceResponse.nextLevelExperience()),
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
