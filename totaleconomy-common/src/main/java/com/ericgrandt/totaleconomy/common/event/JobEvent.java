package com.ericgrandt.totaleconomy.common.event;

import com.ericgrandt.totaleconomy.common.game.CommonPlayer;

public record JobEvent(CommonPlayer player, String material, String action) {
}
