package com.movistar.tvservices.miviewtv.profiles;

import android.util.JsonReader;

import com.movistar.tvservices.utils.http.HTTPHelper;
import com.movistar.tvservices.utils.dns.DNSHelper;

public class ProfilesManager {
    private static final String LOG_TAG = ProfileManager.class.getSimpleName();

    private static final String serviceAddress = DNSHelper.resolve("www.svc.imagenio.telefonica.net");
    private static final int servicePort = 2001;
    private static final String defaultDnsAddress = "172.26.23.3";

    private static final ProfilesManager instance = new ProfilesManager();

    private ClientProfile clientProfile;
    private PlatformProfile platformProfile;

    protected ProfilesManager() throws IOException {
        load();
    }

    public ProfilesManager getInstance() { return instance; }

    private void load() throws IOException
    {
        clientProfile = ClientProfile.fromJSONStream(HTTPHelper.GET(serviceAddress, servicePort, "/appserver/mvtv.do?action=getClientProfile"));
        platformProfile = PlatformProfile.fromJSONStream(HTTPHelper.GET(serviceAddress, servicePort, "/appserver/mvtv.do?action=getPlatformProfile"));
    }

    public ClientProfile getClientProfile() { return clientProfile; }

    public PlatformProfile getPlatformProfile() { return platformProfile; }
}
