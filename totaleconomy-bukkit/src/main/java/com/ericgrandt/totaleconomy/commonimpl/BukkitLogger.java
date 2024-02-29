package com.ericgrandt.totaleconomy.commonimpl;

import com.ericgrandt.totaleconomy.common.logger.CommonLogger;
import java.util.logging.Level;
import java.util.logging.Logger;

public record BukkitLogger(Logger logger) implements CommonLogger {
    @Override
    public void error(String message, Exception e) {
        logger.log(Level.SEVERE, message, e);
    }
}
