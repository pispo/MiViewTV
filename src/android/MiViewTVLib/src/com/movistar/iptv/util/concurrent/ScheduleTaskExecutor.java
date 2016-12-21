package com.movistar.iptv.util.concurrent;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

import com.movistar.iptv.util.concurrent.AsyncTask;
import com.movistar.iptv.util.concurrent.AsyncTaskWithProgress;

public class ScheduleTaskExecutor {
    private static ExecutorService executorService = Executors.newScheduledThreadPool(4);

    private ScheduleTaskExecutor() {}

    public static void schedule(final AsyncTask task, long delay) {
        final Handler handler = new Handler();
        Timer timerAsync = new Timer();
        TimerTask timerTaskAsync = new TimerTask() {

            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            task.executeOnExecutor(executorService);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };

        timerAsync.schedule(timerTaskAsync, delay * 1000);
    }

    public static <T> void scheduleWithFixedDelay(AsyncTaskWithProgress<T> task, long initialDelay, long delay, TimeUnit unit) {
        //task.executeOnExecutor(concurrentExecutor);
    }

    public static void cancelAllTasks() {
        executorService.shutdownNow();
    }
}
