package com.movistar.tvservices.cordova.plugin;

import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;

import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;

import android.util.Log;

/**
 * Puts the service in a foreground state, where the system considers it to be
 * something the user is actively aware of and thus not a candidate for killing
 * when low on memory.
 */
public class MiViewTVService extends Service {

    private static final String LOG_TAG = MiViewTVService.class.getSimpleName();

    // Binder given to clients
    private final IBinder binder = new LocalBinder();

    // Partial wake lock to prevent the app from going to sleep when locked
    //private PowerManager.WakeLock wakeLock;

    // Fixed ID for the 'foreground' notification
    public static final int NOTIFICATION_ID = -574543954;


    /**
     * Allow clients to call on to the service.
     */
    @Override
    public IBinder onBind (Intent intent) {
        return binder;
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        MiViewTVService getService() {
            return MiViewTVService.this;
        }
    }

    /**
     * Put the service in a foreground state to prevent app from being killed
     * by the OS.
     */
    @Override
    public void onCreate () {
        super.onCreate();
        keepAwake();

        Log.v(LOG_TAG, "onCreate");
    }

    /**
     * No need to run headless on destroy.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        sleepWell();

        Log.v(LOG_TAG, "onDestroy");
    }

    /**
     * Put the service in a foreground state to prevent app from being killed
     * by the OS.
     */
    private void keepAwake() {

        startForeground(NOTIFICATION_ID, makeNotification());

/*      PowerManager powerMgr = (PowerManager)
                getSystemService(POWER_SERVICE);

        wakeLock = powerMgr.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK, "MiViewTVService");

        wakeLock.acquire(); */
    }

    /**
     * Stop background mode.
     */
    private void sleepWell() {
        stopForeground(true);

/*      if (wakeLock != null) {
            wakeLock.release();
            wakeLock = null;
        } */
    }

    /**
     * Create a notification as the visible part to be able to put the service
     * in a foreground state.
     *
     * @return
     *      A local ongoing notification which pending intent is bound to the
     *      main activity.
     */
    private Notification makeNotification() {
        Context context = getApplicationContext();
        String pkgName  = context.getPackageName();
        Intent intent   = context.getPackageManager()
                .getLaunchIntentForPackage(pkgName);

        Notification.Builder notification = new Notification.Builder(context)
                .setContentTitle("MiViewTV Service")
                .setContentText("MiViewTV Service is running")
                .setTicker("MiViewTV Service is running")
                .setOngoing(true)
                .setSmallIcon(getIconResId());

        return notification.build();
    }

    /**
     * Retrieves the resource ID of the app icon.
     *
     * @return
     *      The resource ID of the app icon
     */
    private int getIconResId() {
        Context context = getApplicationContext();
        Resources res   = context.getResources();
        String pkgName  = context.getPackageName();

        int resId = res.getIdentifier("icon", "drawable", pkgName);

        return resId;
    }

    public void getChannels() {
        Log.v(LOG_TAG, "getChannels");
    }
    
    public void getProgramGuide() {
        Log.v(LOG_TAG, "getProgramGuide");
    }
}
