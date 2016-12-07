package com.movistar.tvservices.miviewtv.discovery;

import com.movistar.tvservices.miviewtv.profiles.*;
import com.movistar.tvservices.miviewtv.discovery.dvbipi.DvbIpiConstants;
import com.movistar.tvservices.miviewtv.discovery.dvbipi.dvbstp.DvbStpReader;

import com.movistar.tvservices.utils.metadata.MetadataContent;

import java.util.Arrays;
import java.util.List;

/**
 * This class implements the Service Discovery Provider that download all the info related to the Service Discovery & Selection
 * and configuration
 */
public class ServiceDiscoveryManager {

    private static final String LOG_TAG = ServiceDiscoveryManager.class.getSimpleName();
    private static final ServiceDiscoveryManager serviceDiscoveryManager = new ServiceDiscoveryManager();

    protected ServiceDiscoveryManager() {
    }

    public ServiceDiscoveryManager getInstance() { return serviceDiscoveryManager; }

    public void initialize()
    {
        PlatformProfile platformProfile = ProfilesManager.getInstance().getPlatformProfile();

        String address = platformProfile.getDvbEntryPoint().split(":")[0];
        int port = Integer.parseInt(platformProfile.getDvbEntryPoint().split(":")[1]);

        DvbStpReader dvbStpReader = DvbStpReader.open(address, port);
        List<MetadataContent<Integer>> metadataContents = dvbStpReader.download(
                Arrays.asList(new Integer[]{DvbIpiConstants.SDS_SERVICE_PROVIDER_DISCOVERY}));
        dvbStpReader.close();
    }
}
