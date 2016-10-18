package com.movistar.tvservices.cordova.plugin;

import org.apache.cordova.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;

import android.content.Context;
import android.content.Intent;
import android.content.ComponentName;
import android.content.ServiceConnection;

import android.os.IBinder;

import android.util.Log;

public class MiViewTVPlugin extends CordovaPlugin {

    private static final String LOG_TAG = MiViewTVPlugin.class.getSimpleName();

    public static final String ACTION_GET_CHANNELS = "getChannels";
    public static final String ACTION_GET_PROGRAM_GUIDE = "getProgramGuide";

    public static final String ACTION_REGISTER_FOR_CHANNEL_UPDATES = "registerForChannelUpdates";
    public static final String ACTION_REGISTER_FOR_PROGRAM_GUIDE_UPDATES = "registerForProgramGuideUpdates";

    public static final String ACTION_DEREGISTER_FOR_CHANNEL_UPDATES = "deregisterForChannelUpdates";
    public static final String ACTION_DEREGISTER_FOR_PROGRAM_GUIDE_UPDATES = "deregisterForProgramGuideUpdates";

    // Flag indicates if the service is bind
    private boolean serviceConnected = false;
    private Object serviceConnectedLock = new Object();
    
    private MiViewTVService miViewTVService = null;

    // Defines callbacks for service binding, passed to bindService
    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MiViewTVService.LocalBinder binder =
                    (MiViewTVService.LocalBinder) service;

            synchronized(serviceConnectedLock) {
		miViewTVService = binder.getService();
		serviceConnected = true;
		serviceConnectedLock.notify();
	    }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
	    synchronized(serviceConnectedLock) {
		serviceConnected = false;
		miViewTVService = null;
		serviceConnectedLock.notify();
	    }
        }
    };

    // Executes the action request
    @Override
    public boolean execute (String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {

        if (isServiceRunning()) {

            if (!serviceConnected)
                startService();

            Log.d(LOG_TAG, "action: " + action);

            if (ACTION_GET_CHANNELS.equalsIgnoreCase(action)) {

                cordova.getThreadPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        miViewTVService.getChannels ();
                        callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK));
                    }
                });

                return true;
            }
            else if(ACTION_GET_PROGRAM_GUIDE.equals(action)) {

                cordova.getThreadPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        miViewTVService.getProgramGuide ();
                        callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK));
                    }
                });

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
        else {
            Log.d(LOG_TAG, "The service is not running");
        }

        callbackContext.error ("Invalid action");

        return false;
    }

    private boolean isServiceRunning ()
    {
        try {
            // Return Plugin with ServiceRunning true/ false            
            Activity context = cordova.getActivity();
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            
            for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (MiViewTVService.class.getCanonicalName().equals(service.service.getClassName())) {
                    return true;
                }
            }

        } catch (Exception e) {
            Log.d(LOG_TAG, "isServiceRunning failed ", e);
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
        try {

            Activity context = cordova.getActivity();
            Intent intent = new Intent(context, MiViewTVService.class);
            context.startService(intent);
            
            if (context.bindService(intent, connection, Context.BIND_AUTO_CREATE)) {    
                Log.d(LOG_TAG, "Waiting for service connected lock");
				
                synchronized(serviceConnectedLock) {
			while (miViewTVService==null) {
				try {
					serviceConnectedLock.wait();
				} catch (InterruptedException e) {
					Log.d(LOG_TAG, "Interrupt occurred while waiting for connection", e);
				}
			}
                     
			serviceConnected = true;
		}
	    }

        } catch (Exception e) {
            Log.d(LOG_TAG, "startService failed ", e);
        }
    }

    private void stopService()
    {
        Activity context = cordova.getActivity();
        Intent intent = new Intent(context, MiViewTVService.class);

        if (!serviceConnected)
            return;

        context.unbindService(connection);
        context.stopService(intent);
    }
}
