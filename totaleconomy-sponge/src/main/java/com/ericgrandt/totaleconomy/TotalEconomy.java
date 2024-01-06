package com.ericgrandt.totaleconomy;

import com.google.inject.Inject;
import java.net.URL;
import org.apache.logging.log4j.Logger;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.ConstructPluginEvent;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

@Plugin("totaleconomy")
public class TotalEconomy {
    private final PluginContainer container;
    private final Logger logger;

    private ConfigurationLoader<CommentedConfigurationNode> loader;

    @Inject
    private TotalEconomy(final PluginContainer container, final Logger logger) {
        this.container = container;
        this.logger = logger;
    }

    @Listener
    public void onConstructPlugin(final ConstructPluginEvent event) {
        final URL configUrl = this.getClass().getResource("/config.yml");
        loader = YamlConfigurationLoader
            .builder()
            .url(configUrl)
            .build();
        try {
            ConfigurationNode rootNode = loader.load();
            logger.info(rootNode.node("database", "url").getString());
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }
        this.logger.info("Constructing totaleconomy");
    }
}
