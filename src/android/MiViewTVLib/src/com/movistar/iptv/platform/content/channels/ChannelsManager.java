package com.movistar.iptv.platform.content.channels;

import android.util.Log;

import java.util.List;
import java.util.LinkedList;

import com.movistar.iptv.platform.content.channels.ChannelsException;

import com.movistar.iptv.platform.stb.pm.profiles.ProfileException;
import com.movistar.iptv.platform.stb.pm.profiles.ProfilesManager;
import com.movistar.iptv.platform.stb.pm.profiles.ClientProfile;

import com.movistar.iptv.platform.stb.sds.ServiceDiscoveryManager;
import com.movistar.iptv.platform.stb.sds.ServiceDiscoveryException;

import com.movistar.iptv.platform.stb.sds.data.BroadcastDiscoveryData;
import com.movistar.iptv.platform.stb.sds.data.BroadcastDiscoveryData.Service;
import com.movistar.iptv.platform.stb.sds.data.PackageDiscoveryData;
import com.movistar.iptv.platform.stb.sds.data.PackageDiscoveryData.Package;

public class ChannelsManager {
    private static final String LOG_TAG = ChannelsManager.class.getSimpleName();

    private static ChannelsManager instance = new ChannelsManager();

    private List<Service> channels = new LinkedList<Service>();

    private boolean loaded = false;

    public static ChannelsManager getInstance() { return instance; }

    public void load() throws ChannelsException {

        loaded = false;

        try {

            BroadcastDiscoveryData broadcastDiscoveryData = ServiceDiscoveryManager.getInstance().getBroadcastDiscoveryData();
            PackageDiscoveryData packageDiscoveryData = ServiceDiscoveryManager.getInstance().getPackageDiscoveryData();

            ClientProfile clientProfile = ProfilesManager.getInstance().getClientProfile();
            String[] tvPackages = clientProfile.getTvPackages().split("\\|");

            for (Service service: broadcastDiscoveryData.getServices()) {

                String serviceName = service.getServiceName();
                Integer channelKey = Integer.parseInt(serviceName);

                int logicalChannel = -1;
                boolean subscribed = false;

                for (String packageId: tvPackages)
                {
                    if (packageDiscoveryData.getPackage(packageId) != null) {

                        logicalChannel = packageDiscoveryData.getPackage(packageId).getLogicalChannel(serviceName);

                        if (logicalChannel != -1) {
                            subscribed = true;
                            break;
                        }
                    }
                }

                service.setSubscribed(subscribed);
                service.setLogicalChannel(logicalChannel);

                channels.add(service);

                Log.v(LOG_TAG, "Channel={ " + serviceName + " }, subscribed=[" + subscribed + "]");
            }

        } catch (ProfileException e) {
            throw new ChannelsException("ChannelsManager: Consolidating the data failed", e);

        } catch (ServiceDiscoveryException e) {
            throw new ChannelsException("ChannelsManager: Consolidating the data failed", e);
        }

        loaded = true;
    }

    private void assertConsolidated() throws ChannelsException {
        if (!loaded) {
            throw new ChannelsException("ChannelsManager: The data have not been consolidated yet");
        }
    }
}
