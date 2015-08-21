package com.erigitic.service;

import org.spongepowered.api.entity.player.Player;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by Erigitic on 5/5/2015.
 */
public interface TEService {
    void createAccount(UUID uuid);
    boolean hasAccount(UUID uuid);
    void addToBalance(UUID uuid, BigDecimal amount, boolean notify);
    void removeFromBalance(UUID uuid, BigDecimal amount);
    void setBalance(UUID uuid, BigDecimal amount);
    boolean hasMoney(UUID uuid, BigDecimal amount);
    BigDecimal getBalance(UUID uuid);
}
