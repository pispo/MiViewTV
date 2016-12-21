package com.movistar.iptv.service.startup.task;

import android.util.Log;

import com.movistar.iptv.service.startup.StartupController;
import com.movistar.iptv.util.concurrent.AsyncTaskWithProgress;

import com.movistar.iptv.platform.content.guide.EPGManager;
import com.movistar.iptv.platform.content.guide.EPGException;

public class EPGLoaderTask extends AsyncTaskWithProgress<Integer> {
    private static final String LOG_TAG = EPGLoaderTask.class.getSimpleName();

    @Override
    protected Boolean doInBackground() {
        boolean result = true;

        try {

            Log.e(LOG_TAG, "EPGLoaderTask on background");

            if (!isCancelled()) {

                Log.e(LOG_TAG, "EPGManager downloading..");

                EPGManager.getInstance().download(this);

                Log.e(LOG_TAG, "EPGManager downloaded");
            }

        } catch (EPGException e)
        {
            Log.e(LOG_TAG, "EPG downloading error");
            result = false;
        }

        return result;
    }

    @Override
    protected void onProgressUpdate(Integer day) {
        StartupController.getInstance().onDayEPGLoaderCompleted(day);
    }

    @Override
    protected void onSuccessResult() {
        StartupController.getInstance().onEPGLoaderCompleted();
    }

    @Override
    protected void onFailureResult() {
        StartupController.getInstance().onEPGLoaderError();
    }
}
