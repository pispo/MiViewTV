package com.movistar.tvservices.miviewtv.discovery;

import com.movistar.tvservices.miviewtv.profiles.*;
import com.movistar.tvservices.miviewtv.discovery.dvbipi.DvbIpiConstants;
import com.movistar.tvservices.miviewtv.discovery.dvbipi.dvbstp.DvbStpReader;

import com.movistar.tvservices.utils.metadata.MetadataContent;

import java.io.IOException;

import java.util.Arrays;
import java.util.List;

/**
 * This class implements the Service Discovery Provider that download all the info related to the Service Discovery & Selection
 * and configuration
 */
public class ServiceDiscoveryManager {

    private static final String LOG_TAG = ServiceDiscoveryManager.class.getSimpleName();

    private static ServiceDiscoveryManager instance = null;

    protected ServiceDiscoveryManager() throws ServiceDiscoveryException {
        initialize();
    }

    public static ServiceDiscoveryManager getInstance() throws ServiceDiscoveryException {
        if (instance == null)
            instance = new ServiceDiscoveryManager();

        return instance;
    }

    public void initialize() throws ServiceDiscoveryException {
        DvbStpReader dvbStpReader = null;

        try {

            PlatformProfile platformProfile = ProfilesManager.getInstance().getPlatformProfile();

            String address = platformProfile.getDvbEntryPoint().split(":")[0];
            int port = Integer.parseInt(platformProfile.getDvbEntryPoint().split(":")[1]);

            dvbStpReader = DvbStpReader.open(address, port);
            List<MetadataContent<Integer>> metadataContents = dvbStpReader.download(
                    Arrays.asList(new Integer[]{DvbIpiConstants.SDS_SERVICE_PROVIDER_DISCOVERY}));

        } catch (IOException e)
        {
            throw new ServiceDiscoveryException("ServiceDiscoveryManager: Initialization error", e);

        } finally {
            if (dvbStpReader != null)
                dvbStpReader.close();
        }
    }
}
