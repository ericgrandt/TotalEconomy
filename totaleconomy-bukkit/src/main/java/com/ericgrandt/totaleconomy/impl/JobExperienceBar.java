package com.ericgrandt.totaleconomy.impl;

import com.ericgrandt.totaleconomy.TotalEconomy;
import com.ericgrandt.totaleconomy.models.JobExperience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class JobExperienceBar {
    private final Player player;
    private final TotalEconomy plugin;

    private final BossBar bossBar;
    private BukkitTask task;

    public JobExperienceBar(Player player, TotalEconomy plugin) {
        this.player = player;
        this.plugin = plugin;
        this.bossBar = BossBar.bossBar(Component.empty(), 0.0f, BossBar.Color.BLUE, BossBar.Overlay.PROGRESS);
    }

    public void show() {
        if (this.task != null) {
            this.task.cancel();
        }

        player.showBossBar(bossBar);

        startHideTask();
    }

    public void hide() {
        player.hideBossBar(bossBar);
    }

    public void setExperienceBarName(JobExperience jobExperience, int expGain) {
        this.bossBar.name(
            Component.text(
                String.format(
                    "%s [+%s EXP]",
                    jobExperience.jobName(),
                    expGain
                )
            )
        );
    }

    public void setProgress(JobExperience jobExperience) {
        float normalizedStart = jobExperience.experience() - jobExperience.levelBaseExperience();
        float normalizedEnd = jobExperience.experienceToNext() - jobExperience.levelBaseExperience();
        this.bossBar.progress(normalizedStart / normalizedEnd);
    }

    public BossBar getBossBar() {
        return this.bossBar;
    }

    private void startHideTask() {
        this.task = new BukkitRunnable() {
            @Override
            public void run() {
                hide();
            }
        }.runTaskLater(this.plugin, 20L * 5L);
    }
}
