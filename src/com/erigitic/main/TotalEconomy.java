package com.erigitic.main;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.state.ServerStartedEvent;
import org.spongepowered.api.event.state.ServerStoppingEvent;
import org.spongepowered.api.plugin.Plugin;

@Plugin(id = "TotalEconomy", name = "YTSpongeTest", version = "0.0.1")
public class TotalEconomy {

    @Inject
    private Logger logger;

    @Subscribe
    public void onServerStart(ServerStartedEvent event) {
        logger.info("Test Sponge Plugin Loading");
    }

    @Subscribe
    public void onServerStop(ServerStoppingEvent event) {

    }
}