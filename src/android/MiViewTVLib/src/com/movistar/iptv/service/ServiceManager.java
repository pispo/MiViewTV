package com.movistar.iptv.service;

import com.movistar.iptv.service.startup.StartupController;

import android.util.Log;
import android.os.SystemClock;

public class ServiceManager {

    enum Status { STOPPED, STARTED, START_PENDING, STOP_PENDING };

    private static final String LOG_TAG = ServiceManager.class.getSimpleName();

    private static ServiceManager instance = new ServiceManager();

    private Status status;

    private long utcTime = 0;
    private long firstElapsedTime = 0;

    protected ServiceManager() {
        status = Status.STOPPED;
    }

    public static ServiceManager getInstance() {
        return instance;
    }

    public void start() {
        status = Status.START_PENDING;
        StartupController.getInstance().start();
    }

    public void stop() {
        status = Status.STOP_PENDING;
        StartupController.getInstance().stop();
    }

    public void onStartCompleted() {
        status = Status.STARTED;
    }

    public void onStartError() {
    }

    public void onStop() {
        status = Status.STOPPED;
    }

    public Status getStatus() { return status; }
}
