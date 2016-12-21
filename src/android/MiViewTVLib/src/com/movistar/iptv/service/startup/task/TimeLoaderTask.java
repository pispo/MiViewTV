package com.movistar.iptv.service.startup.task;

import android.util.Log;

import com.movistar.iptv.service.startup.StartupController;
import com.movistar.iptv.util.concurrent.AsyncTask;

import com.movistar.iptv.platform.stb.pm.time.UTCTimeManager;
import com.movistar.iptv.platform.stb.pm.time.UTCTimeException;

public class TimeLoaderTask extends AsyncTask {
    private static final String LOG_TAG = TimeLoaderTask.class.getSimpleName();

    @Override
    protected Boolean doInBackground() {

        boolean result = true;

        try {
            if (!isCancelled())
                UTCTimeManager.loadSeedTime();

        } catch (UTCTimeException e)
        {
            Log.e(LOG_TAG, "Time loading error");
            result = false;
        }

        return result;
    }

    @Override
    protected void onSuccessResult() {
        StartupController.getInstance().onTimeLoaderCompleted();
    }

    @Override
    protected void onFailureResult() {
        StartupController.getInstance().onTimeLoaderError();
    }
}