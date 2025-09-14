package com.ericgrandt.totaleconomy.common.game;

import java.util.UUID;
import net.kyori.adventure.bossbar.BossBar;

public interface CommonPlayer extends CommonSender {
    UUID getUniqueId();

    String getName();

    boolean isNull();

    void showBossBar(BossBar bossBar);

    void hideBossBar(BossBar bossBar);
}
