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
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.FormattingCodeTextSerializer;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageManager {

    private TotalEconomy totalEconomy;
    private Logger logger;

    private File messagesFile;
    private ConfigurationLoader<CommentedConfigurationNode> loader;
    private ConfigurationNode messagesConfig;

    /**
     * Grabs a message from the messages_[lang].conf file and converts it to a usable String/Text object ready for printing. Colors
     * are changed, and aliases are changed to their corresponding values which are passed in.
     */
    public MessageManager(TotalEconomy totalEconomy, Locale locale) {
        this.totalEconomy = totalEconomy;

        logger = totalEconomy.getLogger();

        setupConfig(locale);
    }

    // TODO: If no messages_{lang} file is found, default to messages_en
    /**
     * Setup the config file that will contain the messages
     */
    private void setupConfig(Locale locale) {
        messagesFile = new File(totalEconomy.getConfigDir(), "messages_" + locale.getLanguage() + ".conf");
        loader = HoconConfigurationLoader.builder().setFile(messagesFile).build();

        try {
            messagesConfig = loader.load();

            if (!messagesFile.exists()) {
                ResourceBundle rb = ResourceBundle.getBundle("messages", locale);
                rb.keySet().forEach(key -> {
                    String value = rb.getString(key);

                    messagesConfig.getNode(key.toString()).setValue(value);
                });

                loader.save(messagesConfig);
            }
        } catch (IOException e) {
            logger.warn("[TE] Error loading/creating the messages configuration file!");
        }
    }

    public Text getMessage(String messageKey) {
        String message = messagesConfig.getNode(messageKey).getString();

        return TextSerializers.FORMATTING_CODE.deserialize(message);
    }

    public Text getMessage(String messageKey, Map<String, String> values) {
        StrSubstitutor sub = new StrSubstitutor(values, "{", "}");
        String message = sub.replace(messagesConfig.getNode(messageKey).getString());

        return TextSerializers.FORMATTING_CODE.deserialize(message);
    }
}
