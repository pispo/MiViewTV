package com.movistar.iptv.platform.stb.sds.data;

import java.util.Map;
import java.util.HashMap;

import com.movistar.iptv.platform.stb.sds.dvbstp.DvbStpContent;
import com.movistar.iptv.platform.stb.sds.parser.DiscoveryParserException;
import com.movistar.iptv.platform.stb.sds.parser.PackageDiscoveryParser;

public class PackageDiscoveryData {
    private static final String LOG_TAG = PackageDiscoveryData.class.getSimpleName();

    private Map<String, Package> packages = new HashMap<String, Package>();

    public static class Package {
        private String packageName;
        private String packageId;

        private Map<String, Integer> services = new HashMap<String, Integer>();

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public String getPackageId() {
            return packageId;
        }

        public void setPackageId(String packageId) {
            this.packageId = packageId;
        }

        public Map<String, Integer> getServices() {
            return services;
        }

        public Integer getLogicalChannel(String serviceName)
        {
            if (services.containsKey(serviceName))
                return services.get(serviceName);
            else
                return -1;
        }

        public void addService(String serviceName, int logicalChannelNumber) {
            services.put(serviceName, logicalChannelNumber);
        }
    }

    public boolean hasData() {
        return (packages.size() > 0);
    }
    
    public Package getPackage(String packageName) {
        return packages.get(packageName);
    }

    public void addPackage(Package packagge) {
        if (packagge != null)
            packages.put(packagge.getPackageName(), packagge);
    }

    public static PackageDiscoveryData fromMetadata(DvbStpContent content) throws DiscoveryParserException {
        return PackageDiscoveryParser.parse(content);
    }
}
