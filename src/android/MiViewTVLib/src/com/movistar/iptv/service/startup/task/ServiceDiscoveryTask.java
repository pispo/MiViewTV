package com.movistar.iptv.service.startup.task;

import android.util.Log;

import com.movistar.iptv.service.startup.StartupController;
import com.movistar.iptv.util.concurrent.AsyncTask;

import com.movistar.iptv.platform.stb.sds.ServiceDiscoveryManager;
import com.movistar.iptv.platform.stb.sds.ServiceDiscoveryException;

public class ServiceDiscoveryTask extends AsyncTask {
    private static final String LOG_TAG = ServiceDiscoveryTask.class.getSimpleName();

    @Override
    protected Boolean doInBackground() {
        boolean result = true;

        try {
            if (!isCancelled())
                ServiceDiscoveryManager.getInstance().discover();

        } catch (ServiceDiscoveryException e)
        {
            e.printStackTrace();
            Log.e(LOG_TAG, "Service discovering error");
            result = false;
        }

        return result;
    }

    @Override
    protected void onSuccessResult() {
        StartupController.getInstance().onServiceDiscoveryCompleted();
    }

    @Override
    protected void onFailureResult() {
        StartupController.getInstance().onServiceDiscoveryError();
    }
}
