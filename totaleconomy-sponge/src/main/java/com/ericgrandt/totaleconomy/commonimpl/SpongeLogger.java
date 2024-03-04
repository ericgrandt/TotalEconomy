package com.ericgrandt.totaleconomy.commonimpl;

import com.ericgrandt.totaleconomy.common.logger.CommonLogger;
import org.apache.logging.log4j.Logger;

public record SpongeLogger(Logger logger) implements CommonLogger {
    @Override
    public void error(String message, Exception e) {
        logger.error(message, e);
    }
}
