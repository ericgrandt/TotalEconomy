package com.ericgrandt.totaleconomy.jobs.paper.command;

import net.kyori.adventure.text.Component;

public class Messages {
    public static Component activeJob(String activeJob) {
        return Component.text("Current Job: " + activeJob);
    }

    public static Component applyJob(String jobId) {
        return Component.text("You're now a " + jobId + "!");
    }
}
