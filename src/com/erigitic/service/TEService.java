package com.erigitic.service;

import org.spongepowered.api.entity.player.Player;

import java.math.BigDecimal;

/**
 * Created by Erigitic on 5/5/2015.
 */
public interface TEService {
    boolean hasAccount(Player player);
    void addToBalance(Player player, BigDecimal amount);
    void removeFromBalance(Player player, BigDecimal amount);
    boolean hasMoney(Player player, BigDecimal amount);
    BigDecimal getBalance(Player player);
}
