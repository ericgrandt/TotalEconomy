package com.ericgrandt.totaleconomy.model;

import net.kyori.adventure.text.Component;

public interface Sender {
    /**
     * Sends a message to the sender
     *
     * @param message the {@link Component} that will be sent to the sender
     */
    void sendMessage(Component message);
}
