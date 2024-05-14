package com.ericgrandt.totaleconomy.common.event;

import com.ericgrandt.totaleconomy.common.game.CommonPlayer;

public record JobEvent(CommonPlayer player, String action, String material) {
}
