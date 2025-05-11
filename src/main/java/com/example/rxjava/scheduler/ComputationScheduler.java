package com.example.rxjava.scheduler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ComputationScheduler implements Scheduler {
    private final ExecutorService executor;
    private static final int THREAD_COUNT = Runtime.getRuntime().availableProcessors();

    public ComputationScheduler() {
        this.executor = Executors.newFixedThreadPool(THREAD_COUNT);
    }

    @Override
    public void execute(Runnable task) {
        executor.execute(task);
    }
}
