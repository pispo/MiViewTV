package com.movistar.iptv.service.startup.task;

import android.util.Log;

import com.movistar.iptv.service.startup.StartupController;
import com.movistar.iptv.util.concurrent.AsyncTask;

import com.movistar.iptv.platform.stb.pm.bootup.BootLoader;
import com.movistar.iptv.platform.stb.pm.bootup.BootException;

public class BootLoaderTask extends AsyncTask {
    private static final String LOG_TAG = BootLoaderTask.class.getSimpleName();

    @Override
    protected Boolean doInBackground() {

        boolean result = true;

        try {
            if (!isCancelled())
                BootLoader.download();

        } catch (BootException e)
        {
            Log.e(LOG_TAG, "Boot loading error");
            result = false;
        }

        return result;
    }

    @Override
    protected void onSuccessResult() {
        StartupController.getInstance().onBootLoaderCompleted();
    }

    @Override
    protected void onFailureResult() {
        StartupController.getInstance().onBootLoaderError();
    }
}
