package com.movistar.iptv.cordova.plugin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.util.Log;

public class MiViewTVBootReceiver extends BroadcastReceiver {  
    private static final String LOG_TAG = MiViewTVBootReceiver.class.getSimpleName();	

    @Override  
    public void onReceive(Context context, Intent intent) {
	    Intent serviceIntent = new Intent(context, MiViewTVService.class);
	    context.startService(serviceIntent);

	    Log.v(LOG_TAG, "MiViewTVService boot");
    } 
} 
