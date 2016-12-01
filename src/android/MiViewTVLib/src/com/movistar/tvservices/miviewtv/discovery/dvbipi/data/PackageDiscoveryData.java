package com.movistar.tvservices.miviewtv.discovery.dvbipi.data;

import java.util.Map;
import java.util.HashMap;
import com.movistar.tvservices.utils.metadata.MetadataContent;
import com.movistar.tvservices.miviewtv.discovery.dvbipi.parser.PackageDiscoveryParser;

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
        packages.put(packagge.getPackageName(), packagge);
    }

    public static PackageDiscoveryData fromMetadata(MetadataContent<Integer> metadataContent) {
        return (PackageDiscoveryData) PackageDiscoveryParser.getInstance().parse(metadataContent.getByteArrayInputStream());
    }
}
