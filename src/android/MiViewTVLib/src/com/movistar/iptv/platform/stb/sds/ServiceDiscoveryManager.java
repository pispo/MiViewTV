package com.movistar.iptv.platform.stb.sds;

import com.movistar.iptv.platform.stb.pm.profiles.*;

import com.movistar.iptv.platform.stb.sds.SDSConstants;

import com.movistar.iptv.platform.stb.sds.dvbstp.DvbStpReader;
import com.movistar.iptv.platform.stb.sds.dvbstp.DvbStpContent;
import com.movistar.iptv.platform.stb.sds.dvbstp.DvbStpXmlContent;
import com.movistar.iptv.platform.stb.sds.dvbstp.DvbStpException;

import com.movistar.iptv.platform.stb.sds.data.BCGDiscoveryData;
import com.movistar.iptv.platform.stb.sds.data.BroadcastDiscoveryData;
import com.movistar.iptv.platform.stb.sds.data.PackageDiscoveryData;
import com.movistar.iptv.platform.stb.sds.data.ServiceProviderDiscoveryData;
import com.movistar.iptv.platform.stb.sds.data.ServiceProviderDiscoveryData.ServiceProvider;

import com.movistar.iptv.platform.stb.sds.parser.DiscoveryParserException;

import java.io.IOException;

import java.util.Arrays;
import java.util.List;
import java.util.LinkedList;

import android.util.Log;

/**
 * This class implements the Service Discovery Manager that download all the info related to the Service Discovery & Selection
 */
public class ServiceDiscoveryManager {

    private static final String LOG_TAG = ServiceDiscoveryManager.class.getSimpleName();

    private static ServiceDiscoveryManager instance = new ServiceDiscoveryManager();

    private static final int SERVICE_PROVIDER_DISCOVERY_ID = DvbStpXmlContent.generateId(SDSConstants.SDS_SERVICE_PROVIDER_DISCOVERY,
            SDSConstants.SDS_DEFAULT_SECTION_ID);

    private static final int BROADCAST_DISCOVERY_ID = DvbStpXmlContent.generateId(SDSConstants.SDS_BROADCAST_DISCOVERY,
            SDSConstants.SDS_DEFAULT_SECTION_ID);

    private static final int PACKAGE_DISCOVERY_ID = DvbStpXmlContent.generateId(SDSConstants.SDS_PACKAGE_DISCOVERY,
            SDSConstants.SDS_DEFAULT_SECTION_ID);

    private static final int BCG_DISCOVERY_ID = DvbStpXmlContent.generateId(SDSConstants.SDS_BCG_DISCOVERY,
            SDSConstants.SDS_DEFAULT_SECTION_ID);


    private ServiceProviderDiscoveryData serviceProviderDiscoveryData;
    private BroadcastDiscoveryData broadcastDiscoveryData;
    private PackageDiscoveryData packageDiscoveryData;
    private BCGDiscoveryData bcgDiscoveryData;

    private boolean downloaded = false;

    protected ServiceDiscoveryManager() {
    }

    public static ServiceDiscoveryManager getInstance() { return instance; }


    public void discover() throws ServiceDiscoveryException {
        DvbStpReader<DvbStpXmlContent> dvbStpReader = null;
        ServiceProvider serviceProvider = null;

        try {

            downloaded = false;

            PlatformProfile platformProfile = ProfilesManager.getInstance().getPlatformProfile();

            String address = platformProfile.getDvbEntryPoint().split(":")[0];
            int port = Integer.parseInt(platformProfile.getDvbEntryPoint().split(":")[1]);

            ClientProfile clientProfile = ProfilesManager.getInstance().getClientProfile();
            int demarcation = clientProfile.getDemarcation();
            String domainServer = platformProfile.getDvbServiceProvider();
            String domainName = "DEM_" + demarcation + "." + domainServer;

            try {

                dvbStpReader = DvbStpReader.open(address, port);

                List<? extends DvbStpContent> serviceProviderContents = dvbStpReader.download(
                        Arrays.asList(new Integer[]{SERVICE_PROVIDER_DISCOVERY_ID}));

                for (DvbStpContent content : serviceProviderContents) {
                    serviceProviderDiscoveryData = ServiceProviderDiscoveryData.fromMetadata(content);

                    if ((serviceProvider = serviceProviderDiscoveryData.getServiceProvider(domainName)) == null)
                        throw new ServiceDiscoveryException("ServiceDiscoveryManager: Domain name [" + domainName +"] not found");

                    break;
                }

                Log.v(LOG_TAG, "Found service provider [" + serviceProvider.getAddress() + ":" + serviceProvider.getPort() +
                        "] for domain name [" + domainName + "]");

            } catch (DvbStpException e) {
                throw new ServiceDiscoveryException("ServiceDiscoveryManager: Error on discovering services", e);

            } catch (DiscoveryParserException e) {
                throw new ServiceDiscoveryException("ServiceDiscoveryManager: Error on data parsing", e);

            } finally {

                if (dvbStpReader != null) {
                    dvbStpReader.close();
                    dvbStpReader = null;
                }
            }

            if (serviceProvider == null)
                throw new ServiceDiscoveryException("ServiceDiscoveryManager: Service provider unknown");

            try {

                dvbStpReader = DvbStpReader.open(serviceProvider.getAddress(), serviceProvider.getPort());

                List<DvbStpXmlContent> serviceDataContents = dvbStpReader.download(
                        Arrays.asList(new Integer[]{BROADCAST_DISCOVERY_ID, PACKAGE_DISCOVERY_ID, BCG_DISCOVERY_ID}));

                for (DvbStpXmlContent content : serviceDataContents) {
                    if (content.getId() == BROADCAST_DISCOVERY_ID) {
                        Log.v(LOG_TAG, "Donwloaded metadata for Broadcast Discovery");
                        broadcastDiscoveryData = BroadcastDiscoveryData.fromMetadata(content);

                    } else if (content.getId() == PACKAGE_DISCOVERY_ID) {
                        Log.v(LOG_TAG, "Donwloaded metadata for Package Discovery");
                        packageDiscoveryData = PackageDiscoveryData.fromMetadata(content);

                    } else if (content.getId() == BCG_DISCOVERY_ID) {
                        Log.v(LOG_TAG, "Donwloaded metadata for BCG Discovery");
                        bcgDiscoveryData = BCGDiscoveryData.fromMetadata(content);
                    }
                }

                Log.v(LOG_TAG, "Donwloaded metadata for all services discovered");

            } catch (DvbStpException e) {
                throw new ServiceDiscoveryException("ServiceDiscoveryManager: Error on discovering services", e);

            } catch (DiscoveryParserException e) {
                throw new ServiceDiscoveryException("ServiceDiscoveryManager: Error on data parsing", e);

            } finally {

                if (dvbStpReader != null) {
                    dvbStpReader.close();
                    dvbStpReader = null;
                }
            }

            downloaded = true;

        } catch (ProfileException e) {
            throw new ServiceDiscoveryException("ServiceDiscoveryManager: Platform profile not available", e);
        }
    }


    private void assertDownloaded() throws ServiceDiscoveryException {
        if (!downloaded) {
            throw new ServiceDiscoveryException("ServiceDiscoveryManager: The services have not been downloaded yet");
        }
    }

    public ServiceProviderDiscoveryData getServiceProviderDiscoveryData() throws ServiceDiscoveryException {
        assertDownloaded();
        return serviceProviderDiscoveryData;
    }

    public BroadcastDiscoveryData getBroadcastDiscoveryData() throws ServiceDiscoveryException {
        assertDownloaded();
        return broadcastDiscoveryData;
    }

    public PackageDiscoveryData getPackageDiscoveryData() throws ServiceDiscoveryException {
        assertDownloaded();
        return packageDiscoveryData;
    }

    public BCGDiscoveryData getBCGDiscoveryData() throws ServiceDiscoveryException {
        assertDownloaded();
        return bcgDiscoveryData;
    }
}
