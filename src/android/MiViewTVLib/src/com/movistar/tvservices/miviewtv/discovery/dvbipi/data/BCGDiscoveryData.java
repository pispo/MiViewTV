package com.movistar.tvservices.miviewtv.discovery.dvbipi.data;

import java.util.Map;
import java.util.HashMap;

import com.movistar.tvservices.utils.metadata.MetadataContent;
import com.movistar.tvservices.miviewtv.discovery.dvbipi.parser.BCGDiscoveryParser;


public class BCGDiscoveryData {
    private static final String LOG_TAG = BCGDiscoveryData.class.getSimpleName();

    private Map<String, BCG> guides = new HashMap<String, BCG>();

    public static class BCG {
        private String id;
        private String name;
        private int version;
        
        
        private Map<String, TransportMode> transportModes =  new HashMap<String, TransportMode>();

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
        
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }

        public Map<String, TransportMode> getTransportModes() {
            return transportModes;
        }

        public void addTransportMode(TransportMode transportMode) {
            transportModes.put(transportMode.getSource(), transportMode);
        }

        public TransportMode getTransportMode(String source) {
            return transportModes.get(source);
        }
    }

    public static class TransportMode {
        private String source;
        private String address;
        private int port;
        private Map<Integer, Integer> segments = new HashMap<Integer, Integer>();

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
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

        public Map<Integer, Integer> getSegments() {
            return segments;
        }

        public void addSegment(int id, int version) {
            segments.put(id, version);
        }
    }

    public boolean hasData() {
        return (guides.size() > 0);
    }

    public void addBroadContentGuide(BCG broadContentGuide) {
        guides.put(broadContentGuide.getId(), broadContentGuide);
    }

    public BCG getBroadContentGuide(String id) {
        return guides.get(id);
    }

    public static BCGDiscoveryData fromMetadata(MetadataContent<Integer> metadataContent) {
        return BCGDiscoveryParser.getInstance().parse(metadataContent.getByteArrayInputStream());
    }
}
