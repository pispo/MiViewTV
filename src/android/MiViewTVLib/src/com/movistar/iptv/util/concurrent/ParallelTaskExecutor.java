package com.movistar.iptv.util.concurrent;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

import com.movistar.iptv.util.concurrent.AsyncTask;
import com.movistar.iptv.util.concurrent.AsyncTaskWithProgress;

public class ParallelTaskExecutor {
    private static ExecutorService executorService = Executors.newFixedThreadPool(4);

    private ParallelTaskExecutor() {}

    public static void execute(AsyncTask task) {
        task.executeOnExecutor(executorService);
    }

    public static <Integer> void execute(AsyncTaskWithProgress<Integer> task) {
        task.executeOnExecutor(executorService);
    }

    public static void cancelAllTasks() {
        executorService.shutdownNow();
    }
}
