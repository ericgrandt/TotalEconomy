package com.ericgrandt.totaleconomy.common.game;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface CommonSender {
    void sendMessage(@NotNull Component message);
}
