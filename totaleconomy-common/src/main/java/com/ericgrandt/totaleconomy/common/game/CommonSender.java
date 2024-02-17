package com.ericgrandt.totaleconomy.common.game;

import net.kyori.adventure.text.Component;

public interface CommonSender {
    boolean isPlayer();

    void sendMessage(Component message);
}
