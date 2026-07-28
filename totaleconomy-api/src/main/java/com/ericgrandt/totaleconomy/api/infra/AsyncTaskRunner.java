package com.ericgrandt.totaleconomy.api.infra;

/**
 * Executes tasks asynchronously, abstracting away the platform-specific scheduler. Primarily for first-party add-ons to
 * avoid depending on a specific platform API. Third-party plugins should use their own scheduler.
 */
public interface AsyncTaskRunner {
    void runAsync(Runnable task);
}
