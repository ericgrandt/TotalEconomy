package com.ericgrandt.totaleconomy.jobs.paper.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class PaperAsyncTaskRunner implements AsyncTaskRunner {
    @Override
    public void runAsync(Plugin plugin, Runnable task) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
    }
}
