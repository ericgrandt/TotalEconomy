package com.ericgrandt.totaleconomy.common.command;

import com.ericgrandt.totaleconomy.common.game.CommonSender;
import java.util.Map;

public interface CommonCommand {
    boolean execute(CommonSender sender, Map<String, CommonParameter<?>> args);
}
