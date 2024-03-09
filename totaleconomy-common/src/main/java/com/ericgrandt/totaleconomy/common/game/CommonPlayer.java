package com.ericgrandt.totaleconomy.common.game;

import java.util.UUID;

public interface CommonPlayer extends CommonSender {
    UUID getUniqueId();

    String getName();
}
