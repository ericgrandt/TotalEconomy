package com.ericgrandt.totaleconomy.model;

import java.util.UUID;

public interface Player extends Sender {
    /**
     * The player's unique identifier.
     * <p>
     * This {@link UUID} corresponds to the persistent identifier provided by Minecraft.
     * </p>
     *
     * @return the player's unique {@link UUID}
     */
    UUID uniqueId();

    /**
     * The player's username.
     * <p>
     * This name can change over time if the player updates their username. For a permanent identifier, use
     * {@link #uniqueId()}.
     * </p>
     *
     * @return the player's current username
     */
    String name();
}
