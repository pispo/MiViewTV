package com.movistar.tvservices.cordova.plugin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.util.Log;

public class MiViewTVInstallReceiver extends BroadcastReceiver {  
    private static final String LOG_TAG = MiViewTVInstallReceiver.class.getSimpleName();	

    @Override  
    public void onReceive(Context context, Intent intent) {
        String action = null;

        if (intent != null) {
            action = intent.getAction();
        }

        if (action != null && Intent.ACTION_PACKAGE_ADDED.equals(action)) {
            String dataString = intent.getDataString();
            
            Log.v(LOG_TAG, "PackageName: " + dataString);
            
            if (dataString != null
                    && dataString.equals("AAA")) {

	              Intent serviceIntent = new Intent(context, MiViewTVService.class);
	              context.startService(serviceIntent);
            }
        }
    } 
}
