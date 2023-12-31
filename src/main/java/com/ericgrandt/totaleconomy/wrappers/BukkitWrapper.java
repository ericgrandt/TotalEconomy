package com.ericgrandt.totaleconomy.wrappers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BukkitWrapper {
    public Player getPlayerExact(String name) {
        return Bukkit.getPlayerExact(name);
    }
}
