package com.movistar.iptv.service.startup;

import com.movistar.iptv.service.startup.scheduler.TaskHelper;

import com.movistar.iptv.util.concurrent.SerialTaskExecutor;
import com.movistar.iptv.util.concurrent.ParallelTaskExecutor;
import com.movistar.iptv.util.concurrent.ScheduleTaskExecutor;

import android.util.Log;

public class StartupController {

    private static final String LOG_TAG = StartupController.class.getSimpleName();

    private static StartupController instance = new StartupController();

    protected StartupController() {
    }

    public static StartupController getInstance() {
        return instance;
    }

    public void start() {
        SerialTaskExecutor.execute(TaskHelper.createBootLoaderTask());
    }

    public void stop() {
        SerialTaskExecutor.cancelAllTasks();
        ParallelTaskExecutor.cancelAllTasks();
        ScheduleTaskExecutor.cancelAllTasks();
    }

    public void onBootLoaderCompleted() {
        Log.v(LOG_TAG, "Boot config loaded successfully");
        SerialTaskExecutor.execute(TaskHelper.createTimeLoaderTask());
    }

    public void onBootLoaderError() {
        Log.v(LOG_TAG, "Boot loader unsuccessfully");
        ScheduleTaskExecutor.schedule(TaskHelper.createBootLoaderTask(), 5);
    }

    public void onTimeLoaderCompleted() {
        Log.v(LOG_TAG, "Time loaded successfully");
        SerialTaskExecutor.execute(TaskHelper.createProfilesLoaderTask());
    }

    public void onTimeLoaderError() {
        Log.v(LOG_TAG, "Time loaded unsuccessfully");
        ScheduleTaskExecutor.schedule(TaskHelper.createBootLoaderTask(), 5);
    }

    public void onProfilesLoaderCompleted() {
        Log.v(LOG_TAG, "Profiles loaded successfully");
        SerialTaskExecutor.execute(TaskHelper.createServiceDiscoveryTask());
    }

    public void onProfilesLoaderError() {
        Log.v(LOG_TAG, "Profiles loaded unsuccessfully");
        ScheduleTaskExecutor.schedule(TaskHelper.createProfilesLoaderTask(), 5);
    }

    public void onServiceDiscoveryCompleted() {
        Log.v(LOG_TAG, "Services discovered successfully");
        //ParallelTaskExecutor.execute(TaskHelper.createChannelsLoaderTask());
        //ParallelTaskExecutor.execute(TaskHelper.createEPGLoaderTask());
    }

    public void onServiceDiscoveryError() {
        Log.v(LOG_TAG, "Services discovered unsuccessfully");
        ScheduleTaskExecutor.schedule(TaskHelper.createServiceDiscoveryTask(), 5);
    }

    public void onChannelsLoaderCompleted() {
        Log.v(LOG_TAG, "Channels consolidate successfully");
    }

    public void onChannelsLoaderError() {
        Log.v(LOG_TAG, "Channels consolidate unsuccessfully");
        //ParallelTaskExecutor.execute(TaskHelper.createChannelsLoaderTask());
    }

    public void onDayEPGLoaderCompleted(int day) {
        Log.v(LOG_TAG, "EPG Day [" + day + "] loaded successfully");
    }

    public void onEPGLoaderCompleted() {
        Log.v(LOG_TAG, "EPG loaded successfully");
    }

    public void onEPGLoaderError() {
        Log.v(LOG_TAG, "EPG loaded unsuccessfully");
        //ParallelTaskExecutor.execute(TaskHelper.createEPGLoaderTask());
    }
}
