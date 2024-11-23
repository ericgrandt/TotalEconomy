package com.ericgrandt.totaleconomy.common.jobs;

import com.ericgrandt.totaleconomy.common.dto.ExperienceBarDto;
import com.ericgrandt.totaleconomy.common.game.CommonPlayer;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;

public class ExperienceBar {
    private final CommonPlayer player;
    private final ScheduledThreadPoolExecutor scheduler;
    private final BossBar bossBar;

    private ScheduledFuture<?> task;

    public ExperienceBar(final CommonPlayer player, final ScheduledThreadPoolExecutor scheduler) {
        this.player = player;
        this.scheduler = scheduler;
        this.bossBar = BossBar.bossBar(Component.empty(), 0.0f, BossBar.Color.BLUE, BossBar.Overlay.PROGRESS);
    }

    public void show(ExperienceBarDto expBarDto, int expGain) {
        if (task != null) {
            task.cancel(true);
        }

        setExpBarTitle(expBarDto, expGain);
        setProgress(expBarDto);
        player.showBossBar(bossBar);
        task = scheduler.schedule(this::hide, 5, TimeUnit.SECONDS);
    }

    public void hide() {
        player.hideBossBar(bossBar);
    }

    private void setExpBarTitle(ExperienceBarDto expBarDto, int expGain) {
        bossBar.name(
            Component.text(
                String.format(
                    "%s [+%s EXP] [%s/%s]",
                    expBarDto.jobName(),
                    expGain,
                    expBarDto.exp(),
                    expBarDto.expToNext()
                )
            )
        );
    }

    private void setProgress(ExperienceBarDto expBarDto) {
        float normalizedStart = expBarDto.exp() - expBarDto.levelBaseExp();
        float normalizedEnd = expBarDto.expToNext() - expBarDto.levelBaseExp();
        this.bossBar.progress(normalizedStart / normalizedEnd);
    }
}
