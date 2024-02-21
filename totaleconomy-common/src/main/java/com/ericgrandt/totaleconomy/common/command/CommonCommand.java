package com.ericgrandt.totaleconomy.common.command;

import com.ericgrandt.totaleconomy.common.game.CommonSender;

public interface CommonCommand {
    boolean execute(CommonSender sender, CommonArguments args);
}
