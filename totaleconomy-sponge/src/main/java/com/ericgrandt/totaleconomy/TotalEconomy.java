package com.ericgrandt.totaleconomy;

import com.ericgrandt.totaleconomy.config.PluginConfig;
import com.google.inject.Inject;
import org.apache.logging.log4j.Logger;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.ConstructPluginEvent;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.reference.ConfigurationReference;
import org.spongepowered.configurate.reference.ValueReference;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

@Plugin("totaleconomy")
public class TotalEconomy {
    private final PluginContainer container;
    private final Logger logger;
    private final ConfigurationReference<CommentedConfigurationNode> configurationReference;

    private PluginConfig pluginConfig;

    @Inject
    private TotalEconomy(
        final PluginContainer container,
        final Logger logger,
        final @DefaultConfig(sharedRoot = true) ConfigurationReference<CommentedConfigurationNode> configurationReference
    ) {
        this.container = container;
        this.logger = logger;
        this.configurationReference = configurationReference;
    }

    @Listener
    public void onConstructPlugin(final ConstructPluginEvent event) {
        try {
            ValueReference<PluginConfig, CommentedConfigurationNode> valueReference = configurationReference
                .referenceTo(PluginConfig.class);
            configurationReference.save();

            pluginConfig = valueReference.get();
        } catch (final ConfigurateException ex) {
            logger.error("[TotalEconomy] Error loading configuration", ex);
        }
    }
}
