package com.movistar.tvservices.miviewtv.discovery.dvbipi.data;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;

public class ServiceProviderDiscoveryData {
    private static final String LOG_TAG = ServiceProviderDiscoveryData.class.getSimpleName();

    private Map<String, ServiceProvider> serviceProviders = new HashMap<String, ServiceProvider>();

    public class ServiceProvider {
        private String domainName;
        private int version;
        private String address;
        private int port;

        public String getDomainName() {
            return domainName;
        }

        public void setDomainName(String domainName) {
            this.domainName = domainName;
        }

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }
    }

    @Override
    public boolean hasData() {
        return (records.size() > 0);
    }

    public void addServiceProvider(ServiceProvider serviceProvider) {
        serviceProviders.put(serviceProvider.getDomainName(), serviceProvider);
    }

    public ServiceProvider getServiceProvider(String domainName) {
        return serviceProviders.get(domainName);
    }

    public static ServiceProviderDiscoveryData decode(MetadataContent metadataContent) {
        return ServiceProviderDiscoveryParser.getInstance().parse(metaDataContent.getByteArrayInputStream());
    }
}
