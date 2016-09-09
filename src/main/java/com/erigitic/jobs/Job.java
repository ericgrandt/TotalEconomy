package com.erigitic.jobs;

import ninja.leaping.configurate.ConfigurationNode;

/**
 * Created by Eric on 10/29/2015.
 */
public interface Job {
    void setupJobValues(ConfigurationNode jobsConfig);
}
