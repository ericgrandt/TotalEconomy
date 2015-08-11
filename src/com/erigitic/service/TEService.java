package com.erigitic.service;

import org.spongepowered.api.entity.player.Player;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by Erigitic on 5/5/2015.
 */
public interface TEService {
//    void createAccount(Player player);
//    boolean hasAccount(Player player);
//    void addToBalance(Player player, BigDecimal amount, boolean notify);
//    void removeFromBalance(Player player, BigDecimal amount);
//    void setBalance(Player player, BigDecimal amount);
//    boolean hasMoney(Player player, BigDecimal amount);
//    BigDecimal getBalance(Player player);

    void createAccount(UUID uuid);
    boolean hasAccount(UUID uuid);
    void addToBalance(UUID uuid, BigDecimal amount, boolean notify);
    void removeFromBalance(UUID uuid, BigDecimal amount);
    void setBalance(UUID uuid, BigDecimal amount);
    boolean hasMoney(UUID uuid, BigDecimal amount);
    BigDecimal getBalance(UUID uuid);
}
