/*
 * This file is part of Total Economy, licensed under the MIT License (MIT).
 *
 * Copyright (c) Eric Grandt <https://www.ericgrandt.com>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.erigitic.util;

import com.erigitic.main.TotalEconomy;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

public class MessageManager {

    private TotalEconomy totalEconomy;
    private Logger logger;
    private ConfigurationNode messagesConfig;
    private Locale locale;

    /**
     * Grabs a message from the messages_[lang].conf file and converts it to a usable String/Text object ready for printing. Colors
     * are changed, and value placeholders are changed to their corresponding values which are passed in.
     */
    public MessageManager(TotalEconomy totalEconomy, Logger logger, Locale locale) {
        this.totalEconomy = totalEconomy;
        this.logger = logger;
        this.locale = locale;

        setupConfig(locale);
    }

    /**
     * Setup the messages_[lang].conf file
     */
    private void setupConfig(Locale locale) {
        File messagesFile = new File(totalEconomy.getConfigDir(), "messages_" + locale.getLanguage() + ".conf");
        ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setFile(messagesFile).build();

        try {
            if (!messagesFile.exists()) {
                Asset defaultMessagesAsset = totalEconomy.getPluginContainer().getAsset("messages_en.conf").get();
                Optional<Asset> optMessagesAsset = totalEconomy.getPluginContainer().getAsset("messages_" + locale.getLanguage() + ".conf");

                optMessagesAsset.orElse(defaultMessagesAsset).copyToFile(messagesFile.toPath());
            }

            messagesConfig = loader.load();
        } catch (IOException e) {
            logger.warn("[TE] Error loading/creating the messages configuration file!");
        }
    }

    /**
     * Get a message from the messages_[lang].conf file and deserialize it for printing
     *
     * @param messageKey The key to grab a value from
     * @return Text The deserialized message
     */
    public Text getMessage(String messageKey) {
        String message = messagesConfig.getNode(messageKey).getString("Message not found (" + locale + "): " + messageKey);

        return TextSerializers.FORMATTING_CODE.deserialize(message);
    }

    /**
     * Get a message from the messages_[lang].conf file, replace value placeholders, and deserialize it for printing
     *
     * @param messageKey The key to grab a value from
     * @param values Map of values that will replace value placeholders (ex. {amount}, {name})
     * @return Text The deserialized message
     */
    public Text getMessage(String messageKey, Map<String, String> values) {
        StrSubstitutor sub = new StrSubstitutor(values, "{", "}");
        String message = sub.replace(messagesConfig.getNode(messageKey).getString("Message not found (" + locale + "): " + messageKey));

        return TextSerializers.FORMATTING_CODE.deserialize(message);
    }
}
