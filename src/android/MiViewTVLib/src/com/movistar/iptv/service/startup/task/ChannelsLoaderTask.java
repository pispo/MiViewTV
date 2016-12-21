package com.movistar.iptv.service.startup.task;

import android.util.Log;

import com.movistar.iptv.service.startup.StartupController;
import com.movistar.iptv.util.concurrent.AsyncTask;

import com.movistar.iptv.platform.content.channels.ChannelsManager;
import com.movistar.iptv.platform.content.channels.ChannelsException;

public class ChannelsLoaderTask extends AsyncTask {
    private static final String LOG_TAG = ChannelsLoaderTask.class.getSimpleName();

    @Override
    protected Boolean doInBackground() {
        boolean result = true;

        try {
            if (!isCancelled())
                ChannelsManager.getInstance().load();

        } catch (ChannelsException e)
        {
            Log.e(LOG_TAG, "Channels consolidation error");
            result = false;
        }

        return result;
    }

    @Override
    protected void onSuccessResult() {
        StartupController.getInstance().onChannelsLoaderCompleted();
    }

    @Override
    protected void onFailureResult() {
        StartupController.getInstance().onChannelsLoaderError();
    }
}
