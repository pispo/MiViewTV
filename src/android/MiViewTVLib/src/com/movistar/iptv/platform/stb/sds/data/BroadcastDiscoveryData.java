package com.movistar.iptv.platform.stb.sds.data;

import java.util.Map;
import java.util.HashMap;

import java.util.List;
import java.util.Collections;

import com.movistar.iptv.platform.stb.sds.dvbstp.DvbStpContent;
import com.movistar.iptv.platform.stb.sds.parser.BroadcastDiscoveryParser;
import com.movistar.iptv.platform.stb.sds.parser.DiscoveryParserException;

public class BroadcastDiscoveryData {
    private static final String LOG_TAG = BroadcastDiscoveryData.class.getSimpleName();

    private Map<String, Service> services = new HashMap<String, Service>();

    public static class Service {
        private String address;
        private int port;
        private String serviceName;
        private String logoURI;
        private String name;
        private String shortName;
        private String description;
        private int genre;
        private int subgenre;
        private int parentalRating;
        private String replacementService;
        private boolean HD;
        private int flags;
        private boolean dolby;
        private boolean subscribed;
        private int logicalChannel;

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

        public String getServiceName() {
            return serviceName;
        }

        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }

        public String getLogoURI() {
            return logoURI;
        }

        public void setLogoURI(String logoURI) {
            this.logoURI = logoURI;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getShortName() {
            return shortName;
        }

        public void setShortName(String shortName) {
            this.shortName = shortName;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getGenre() {
            return genre;
        }

        public void setGenre(int genre) {
            this.genre = genre;
        }

        public int getSubgenre() {
            return subgenre;
        }

        public void setSubgenre(int subgenre) {
            this.subgenre = subgenre;
        }

        public int getParentalRating() {
            return parentalRating;
        }

        public void setParentalRating(int parentalRating) {
            this.parentalRating = parentalRating;
        }

        public String getReplacementService() {
            return replacementService;
        }

        public void setReplacementService(String replacementService) {
            this.replacementService = replacementService;
        }

        public boolean isHD() {
            return HD;
        }

        public void setHD(boolean HD) {
            this.HD = HD;
        }

        public int getFlags() {
            return flags;
        }

        public void setFlags(int flags) {
            this.flags = flags;
        }

        public boolean isDolby() {
            return dolby;
        }

        public void setDolby(boolean dolby) {
            this.dolby = dolby;
        }

        public boolean isSubscribed() {
            return subscribed;
        }

        public void setSubscribed(boolean subscribed) {
            this.subscribed = subscribed;
        }

        public int getLogicalChannel() {
            return logicalChannel;
        }

        public void setLogicalChannel(int logicalChannel) {
            this.logicalChannel = logicalChannel;
        }
    }

    public boolean hasData() {
        return (services.size() > 0);
    }

    public void addService(Service service) {
        if (service != null)
            services.put(service.getServiceName(), service);
    }

    public Service getService(String serviceName) {
        return services.get(serviceName);
    }

    public List<Service> getServices() {
        return Collections.list(Collections.enumeration(services.values()));
    }

    public static BroadcastDiscoveryData fromMetadata(DvbStpContent content) throws DiscoveryParserException {
        return BroadcastDiscoveryParser.parse(content);
    }
}
