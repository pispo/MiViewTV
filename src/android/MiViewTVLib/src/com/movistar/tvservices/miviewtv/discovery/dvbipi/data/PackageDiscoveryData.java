package com.movistar.tvservices.miviewtv.discovery.dvbipi.data;

import java.io.IOException;
import java.util.HashMap;

public class PackageDiscoveryData {
    private static final String LOG_TAG = PackageDiscoveryData.class.getSimpleName();

    private Map<String, Package> packages = new HashMap<String, Package>();

    public class Package {
        private String packageName;
        private String packageId;

        private Map<String, int> services = new HashMap<String, int>();

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

        public HashMap<String, int> getServices() {
            return services;
        }

        public void addService(int serviceName, int logicalChannelNumber) {
            services.put(serviceName, logicalChannelNumber);
        }
    }

    public Package getPackage(String packageName) {
        return packages.get(packageName);
    }

    public void addPackage(Package packagge) {
        packages.put(packagge.getPackageName(), packagge);
    }

    public static PackageDiscoveryData fromMetadata(MetaDataContent metaDataContent) {
        return PackageDiscoveryParser.getInstance().parse(metaDataContent.getByteArrayInputStream());
    }
}
