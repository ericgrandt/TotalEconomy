package com.ericgrandt.totaleconomy.paper.util;

import com.ericgrandt.totaleconomy.api.infra.AsyncTaskRunner;

public class TestTaskRunner implements AsyncTaskRunner {
    @Override
    public void runAsync(Runnable task) {
        task.run();
    }
}
