package com.ericgrandt.totaleconomy.util;

import org.bukkit.plugin.Plugin;

public interface AsyncTaskRunner {
    void runAsync(Plugin plugin, Runnable task);
}
