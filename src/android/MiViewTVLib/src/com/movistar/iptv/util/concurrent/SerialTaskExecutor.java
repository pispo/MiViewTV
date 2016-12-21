package com.movistar.iptv.util.concurrent;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

import com.movistar.iptv.util.concurrent.AsyncTask;
import com.movistar.iptv.util.concurrent.AsyncTaskWithProgress;

public class SerialTaskExecutor {
    private static ExecutorService executorService = Executors.newSingleThreadExecutor();

    private SerialTaskExecutor() {}

    public static void execute(AsyncTask task) {
        task.executeOnExecutor(executorService);
    }

    public static <T> void execute(AsyncTaskWithProgress<T> task) {
        task.executeOnExecutor(executorService);
    }

    public static void cancelAllTasks() {
        executorService.shutdownNow();
    }
}
