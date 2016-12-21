package com.movistar.iptv.service.startup.task;

import android.util.Log;

import com.movistar.iptv.service.startup.StartupController;
import com.movistar.iptv.util.concurrent.AsyncTask;

import com.movistar.iptv.platform.stb.pm.profiles.ProfilesManager;
import com.movistar.iptv.platform.stb.pm.profiles.ProfileException;

public class ProfilesLoaderTask extends AsyncTask {
    private static final String LOG_TAG = ProfilesLoaderTask.class.getSimpleName();

    @Override
    protected Boolean doInBackground() {
        boolean result = true;

        try {
            if (!isCancelled())
                ProfilesManager.getInstance().download();

        } catch (ProfileException e)
        {
            Log.e(LOG_TAG, "Profile loading error: " + e);
            result = false;
        }

        return result;
    }

    @Override
    protected void onSuccessResult() {
        StartupController.getInstance().onProfilesLoaderCompleted();
    }

    @Override
    protected void onFailureResult() {
        StartupController.getInstance().onProfilesLoaderError();
    }
}
