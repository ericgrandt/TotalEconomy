package com.ericgrandt.totaleconomy.paper.util;

import com.ericgrandt.totaleconomy.api.infra.AsyncTaskRunner;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class PaperAsyncTaskRunner implements AsyncTaskRunner {
    private final Plugin plugin;

    public PaperAsyncTaskRunner(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void runAsync(Runnable task) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
    }
}
