package com.movistar.iptv.platform.stb.sds.data;

import java.util.Map;
import java.util.HashMap;

import com.movistar.iptv.platform.stb.sds.dvbstp.DvbStpContent;
import com.movistar.iptv.platform.stb.sds.parser.DiscoveryParserException;
import com.movistar.iptv.platform.stb.sds.parser.ServiceProviderDiscoveryParser;

public class ServiceProviderDiscoveryData {
    private static final String LOG_TAG = ServiceProviderDiscoveryData.class.getSimpleName();

    private Map<String, ServiceProvider> serviceProviders = new HashMap<String, ServiceProvider>();

    public static class ServiceProvider {
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

    public boolean hasData() {
        return (serviceProviders.size() > 0);
    }

    public void addServiceProvider(ServiceProvider serviceProvider) {
        if (serviceProvider != null)
            serviceProviders.put(serviceProvider.getDomainName(), serviceProvider);
    }

    public ServiceProvider getServiceProvider(String domainName) {
        return serviceProviders.get(domainName);
    }

    public static ServiceProviderDiscoveryData fromMetadata(DvbStpContent content) throws DiscoveryParserException {
        return ServiceProviderDiscoveryParser.parse(content);
    }
}
