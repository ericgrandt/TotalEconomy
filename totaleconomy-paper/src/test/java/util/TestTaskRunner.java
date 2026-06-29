package util;

import com.ericgrandt.totaleconomy.util.AsyncTaskRunner;
import org.bukkit.plugin.Plugin;

public class TestTaskRunner implements AsyncTaskRunner {
    @Override
    public void runAsync(Plugin plugin, Runnable task) {
        task.run();
    }
}
