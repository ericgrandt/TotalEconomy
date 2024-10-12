package com.ericgrandt.totaleconomy.common.game;

import net.kyori.adventure.bossbar.BossBar;

import java.util.UUID;

public interface CommonPlayer extends CommonSender {
    UUID getUniqueId();

    String getName();

    boolean isNull();

    void showBossBar(BossBar bossBar);

    void hideBossBar(BossBar bossBar);
}
