package com.movistar.iptv.service.startup;

import com.movistar.iptv.service.startup.task.BootLoaderTask;
import com.movistar.iptv.service.startup.task.TimeLoaderTask;
import com.movistar.iptv.service.startup.task.ProfilesLoaderTask;
import com.movistar.iptv.service.startup.task.ServiceDiscoveryTask;
import com.movistar.iptv.service.startup.task.ChannelsLoaderTask;
import com.movistar.iptv.service.startup.task.EPGLoaderTask;

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
        SerialTaskExecutor.execute(new BootLoaderTask());
    }

    public void stop() {
        SerialTaskExecutor.cancelAllTasks();
        ParallelTaskExecutor.cancelAllTasks();
        ScheduleTaskExecutor.cancelAllTasks();
    }

    public void onBootLoaderCompleted() {
        Log.v(LOG_TAG, "Boot config loaded successfully");
        SerialTaskExecutor.execute(new TimeLoaderTask());
    }

    public void onBootLoaderError() {
        Log.v(LOG_TAG, "Boot loader unsuccessfully");
        ScheduleTaskExecutor.schedule(new BootLoaderTask(), 5);
    }

    public void onTimeLoaderCompleted() {
        Log.v(LOG_TAG, "Time loaded successfully");
        SerialTaskExecutor.execute(new ProfilesLoaderTask());
    }

    public void onTimeLoaderError() {
        Log.v(LOG_TAG, "Time loaded unsuccessfully");
        ScheduleTaskExecutor.schedule(new BootLoaderTask(), 5);
    }

    public void onProfilesLoaderCompleted() {
        Log.v(LOG_TAG, "Profiles loaded successfully");
        SerialTaskExecutor.execute(new ServiceDiscoveryTask());
    }

    public void onProfilesLoaderError() {
        Log.v(LOG_TAG, "Profiles loaded unsuccessfully");
        ScheduleTaskExecutor.schedule(new ProfilesLoaderTask(), 5);
    }

    public void onServiceDiscoveryCompleted() {
        Log.v(LOG_TAG, "Services discovered successfully");
        ParallelTaskExecutor.execute(new ChannelsLoaderTask());
        ParallelTaskExecutor.execute(new EPGLoaderTask());
    }

    public void onServiceDiscoveryError() {
        Log.v(LOG_TAG, "Services discovered unsuccessfully");
        ScheduleTaskExecutor.schedule(new ServiceDiscoveryTask(), 5);
    }

    public void onChannelsLoaderCompleted() {
        Log.v(LOG_TAG, "Channels consolidate successfully");
    }

    public void onChannelsLoaderError() {
        Log.v(LOG_TAG, "Channels consolidate unsuccessfully");
        ParallelTaskExecutor.execute(new ChannelsLoaderTask());
    }

    public void onDayEPGLoaderCompleted(int day) {
        Log.v(LOG_TAG, "EPG Day [" + day + "] loaded successfully");
    }

    public void onEPGLoaderCompleted() {
        Log.v(LOG_TAG, "EPG loaded successfully");
    }

    public void onEPGLoaderError() {
        Log.v(LOG_TAG, "EPG loaded unsuccessfully");
        ScheduleTaskExecutor.schedule(new EPGLoaderTask(), 5);
    }
}
