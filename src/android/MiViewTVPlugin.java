package com.movistar.tvservices.cordova.plugin;

import org.apache.cordova.*;

import android.content.Context;
import android.content.Intent;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import android.app.Activity;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import android.util.Log;

public class MiViewTVPlugin extends CordovaPlugin {

    private static final String LOG_TAG = TvServicesPlugin.class.getSimpleName();

    public static final String ACTION_GET_CHANNELS = "getChannels";
    public static final String ACTION_GET_PROGRAM_GUIDE = "getProgramGuide";

    public static final String ACTION_REGISTER_FOR_CHANNEL_UPDATES = "registerForChannelUpdates";
    public static final String ACTION_REGISTER_FOR_PROGRAM_GUIDE_UPDATES = "registerForProgramGuideUpdates";

    public static final String ACTION_DEREGISTER_FOR_CHANNEL_UPDATES = "deregisterForChannelUpdates";
    public static final String ACTION_DEREGISTER_FOR_PROGRAM_GUIDE_UPDATES = "deregisterForProgramGuideUpdates";

    // Flag indicates if the service is bind
    private boolean isBind = false;

    private MiViewTVService miViewTVService;

    // Defines callbacks for service binding, passed to bindService
    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MiViewTVService.LocalBinder binder =
                    (MiViewTVService.LocalBinder) service;
            miViewTVService = binder.getService();

            isBind = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBind = false;
        }
    };

    // Executes the action request
    @Override
    public boolean execute (String action, JSONArray args, CallbackContext callback) throws JSONException {

        if (isServiceRunning()) {

            if (!isBind)
                startService();

            if (ACTION_GET_CHANNELS.equalsIgnoreCase(action)) {
                miViewTVService.getChannels ();
                return true;
            }
            else if(ACTION_GET_PROGRAM_GUIDE.equals(action)) {
                return true;
            }
            else if(ACTION_REGISTER_FOR_CHANNEL_UPDATES.equals(action)) {
                return true;
            }
            else if(ACTION_REGISTER_FOR_PROGRAM_GUIDE_UPDATES.equals(action)) {
                return true;
            }
            else if(ACTION_DEREGISTER_FOR_CHANNEL_UPDATES.equals(action)) {
                return true;
            }
            else if(ACTION_DEREGISTER_FOR_PROGRAM_GUIDE_UPDATES.equals(action)) {
                return true;
            }
        }

        return false;
    }

    private boolean isServiceRunning ()
    {
        try {
            // Return Plugin with ServiceRunning true/ false
            ActivityManager manager = (ActivityManager)this.mContext.getSystemService(Context.ACTIVITY_SERVICE);
            for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (MiViewTVService.class.getCanonicalName().equals(service.service.getClassName())) {
                    return true;
                }
            }

        } catch (Exception ex) {
            Log.d(LOG_TAG, "isServiceRunning failed ", ex);
        }

        return false;
    }

    /**
     * Called when the activity will be destroyed.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopService();
    }

    /**
     * Bind the activity to a background service and put them into foreground
     * state.
     */
    private void startService()
    {
        Activity context = cordova.getActivity();

        Intent intent = new Intent(context, MiViewTVService.class);

        try {
            context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
            context.startService(intent);

            isBind = true;

        } catch (Exception e) {
            Log.d(LOG_TAG, "startService failed ", ex);
        }
    }

    private void stopService()
    {
        Activity context = cordova.getActivity();
        Intent intent = new Intent(context, MiViewTVService.class);

        if (!isBind)
            return;

        context.unbindService(connection);
        context.stopService(intent);

        isBind = false;
    }
}
