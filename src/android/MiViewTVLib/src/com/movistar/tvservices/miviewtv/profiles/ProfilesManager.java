package com.movistar.tvservices.miviewtv.profiles;

import android.util.Log;
import android.util.JsonReader;

import java.io.IOException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.movistar.tvservices.utils.http.HTTPHelper;
import com.movistar.tvservices.utils.dns.DNSHelper;

import com.movistar.tvservices.bootcast.BootProperties;

public class ProfilesManager {
    private static final String LOG_TAG = ProfilesManager.class.getSimpleName();

    private static final String DEFAULT_SERVICE_ADDR = DNSHelper.resolve("www.svc.imagenio.telefonica.net");
    private static final int DEFAULT_SERVICE_PORT = 2001;
        
    private String serviceAddress = DEFAULT_SERVICE_ADDR;
    private int servicePort = DEFAULT_SERVICE_PORT;

    private static ProfilesManager instance = null;

    private ClientProfile clientProfile;
    private PlatformProfile platformProfile;

    protected ProfilesManager() throws IOException {
        Pattern pattern = Pattern.compile("(https?)://([^:^/]*)(:(\\d*))?(.*)?");
        String mvtvWsUrl = BootProperties.getPropertyValue(BootProperties.CFG_RESOURCES_URL_ID);                
        
        if (mvtvWsUrl != null) {
            Matcher matcher = pattern.matcher(mvtvWsUrl);
            if (false == matcher.matches()) {
                Log.d(LOG_TAG, "ProfilesManager: No valid MiviewTV web services URL found.");
                return;
            }
            
            serviceAddress = DNSHelper.resolve(matcher.group(2));
            servicePort = Integer.valueOf(matcher.group(4));
        }
        
        load();
    }

    public static ProfilesManager getInstance() { 
        if (instance == null)
            instance = new ProfilesManager();
        
        return instance;
    }

    private void load() throws IOException {
        clientProfile = ClientProfile.fromJSONStream(HTTPHelper.GET(serviceAddress, servicePort, "/appserver/mvtv.do?action=getClientProfile"));
        platformProfile = PlatformProfile.fromJSONStream(HTTPHelper.GET(serviceAddress, servicePort, "/appserver/mvtv.do?action=getPlatformProfile"));
    }

    public ClientProfile getClientProfile() { return clientProfile; }

    public PlatformProfile getPlatformProfile() { return platformProfile; }
}
