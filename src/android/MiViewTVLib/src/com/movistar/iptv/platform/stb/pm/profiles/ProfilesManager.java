package com.movistar.iptv.platform.stb.pm.profiles;

import android.util.Log;
import android.util.JsonReader;

import java.io.IOException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.movistar.iptv.util.http.HTTPHelper;
import com.movistar.iptv.util.dns.DNSHelper;

import com.movistar.iptv.platform.stb.pm.bootup.BootProperties;

public class ProfilesManager {
    private static final String LOG_TAG = ProfilesManager.class.getSimpleName();

    private static final String DEFAULT_SERVICE_ADDR = DNSHelper.resolve("www-60.svc.imagenio.telefonica.net");
    private static final int DEFAULT_SERVICE_PORT = 2001;
        
    private String serviceAddress = DEFAULT_SERVICE_ADDR;
    private int servicePort = DEFAULT_SERVICE_PORT;

    private static ProfilesManager instance = new ProfilesManager();

    private ClientProfile clientProfile = null;
    private PlatformProfile platformProfile = null;

    private boolean downloaded = false;

    public static ProfilesManager getInstance() { return instance; }

    private void checkRemoteService() throws ProfileException {

        Pattern pattern = Pattern.compile("(https?)://([^:^/]*)(:(\\d*))?(.*)?");
        String mvtvWsUrl = BootProperties.getPropertyValue(BootProperties.CFG_RESOURCES_URL_ID);

        if (mvtvWsUrl == null) {
            Log.e(LOG_TAG, "Error: MiviewTV web service URL not found");
            throw new ProfileException("Error: MiviewTV web service URL not found");

        } else {

            Matcher matcher = pattern.matcher(mvtvWsUrl);

            if (false == matcher.matches()) {
                Log.e(LOG_TAG, "Error: No valid MiviewTV web services URL found");
                throw new ProfileException("Error: No valid MiviewTV web services URL found");
            }

            serviceAddress = DNSHelper.resolve(matcher.group(2));
            servicePort = Integer.valueOf(matcher.group(4));

            if (serviceAddress == null) {
                Log.e(LOG_TAG, "Error: Failed to resolve the MiviewTV server");
                throw new ProfileException("Error: Failed to resolve the MiviewTV server");
            }
        }
    }

    public void download() throws ProfileException {

        downloaded = false;

        checkRemoteService();

        try {

            clientProfile = ClientProfile.fromJSONStream(HTTPHelper.doGet(serviceAddress, servicePort, "/appserver/mvtv.do?action=getClientProfile"));
            platformProfile = PlatformProfile.fromJSONStream(HTTPHelper.doGet(serviceAddress, servicePort, "/appserver/mvtv.do?action=getPlatformProfile"));

        } catch (IOException e) {
            e.printStackTrace();
            throw new ProfileException("Error: Failed to download the profiles", e);
        }

        downloaded = true;
    }

    private void assertDownloaded() throws ProfileException {
        if (!downloaded) {
            throw new ProfileException("ProfilesManager: The profiles have not been downloaded yet");
        }
    }

    public ClientProfile getClientProfile() throws ProfileException {
        assertDownloaded();
        return clientProfile;
    }

    public PlatformProfile getPlatformProfile() throws ProfileException {
        assertDownloaded();
        return platformProfile;
    }
}
