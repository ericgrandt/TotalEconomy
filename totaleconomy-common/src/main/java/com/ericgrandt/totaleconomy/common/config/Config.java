package com.ericgrandt.totaleconomy.common.config;

import java.util.Map;

public interface Config {
    String getDatabaseUrl();

    String getDatabaseUser();

    String getDatabasePassword();

    Map<String, Boolean> getFeatures();
}
