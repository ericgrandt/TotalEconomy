package com.ericgrandt.totaleconomy.common.jobs;

import com.ericgrandt.totaleconomy.common.game.CommonPlayer;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;

public class ExperienceBar {
    private final CommonPlayer player;
    private final BossBar bossBar;

    private ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(1);
    private ScheduledFuture<?> task;

    public ExperienceBar(final CommonPlayer player) {
        this.player = player;
        this.bossBar = BossBar.bossBar(Component.empty(), 0.0f, BossBar.Color.BLUE, BossBar.Overlay.PROGRESS);
    }

    public void show() {
        if (task != null) {
            task.cancel(true);
        }
        task = scheduler.schedule(this::hide, 5, TimeUnit.SECONDS);
        player.showBossBar(bossBar);
    }

    public void hide() {
        player.hideBossBar(bossBar);
    }
}
