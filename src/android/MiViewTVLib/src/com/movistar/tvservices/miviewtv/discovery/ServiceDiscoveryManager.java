package com.movistar.tvservices.miviewtv.discovery;

import com.movistar.tvservices.miviewtv.profiles.*;
import com.movistar.tvservices.miviewtv.discovery.dvbipi.DvbIpiConstants;
import com.movistar.tvservices.miviewtv.discovery.dvbipi.dvbstp.DvbStpReader;

/**
 * This class implements the Service Provider that download all the info related to the Service Discovery & Selection
 * and configuration of the Infocast service
 */
public class ServiceDiscoveryManager {

    private static final String LOG_TAG = ServiceDiscoveryManager.class.getSimpleName();
    private static final ServiceDiscoveryManager serviceDiscoveryManager = new ServiceDiscoveryManager();

    protected ServiceDiscoveryManager() {
    }

    public ServiceDiscoveryManager getInstance() { return serviceDiscoveryManager; }

    public void initialize()
    {
        PlatformProfile platformProfile = ProfileManager.getInstance().getPlatformProfile();

        String address = platformProfile.getDvbEntryPoint().split(":")[0];
        int port = Integer.parseInt(platformProfile.getDvbEntryPoint().split(":")[1]);

        DvbStpReader dvbStpReader = DvbStpReader.open(address, port);
        Hashmap<int, MetadataContent> metadataContents = dvbStpReader.download (DvbIpiConstants.SDS_SERVICE_PROVIDER_DISCOVERY);
        dvbStpReader.close();

    }
}
