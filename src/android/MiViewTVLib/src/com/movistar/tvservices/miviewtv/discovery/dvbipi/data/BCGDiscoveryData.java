package com.movistar.tvservices.miviewtv.discovery.dvbipi.data;

import java.util.Map;
import java.util.HashMap;

import com.movistar.tvservices.utils.metadata.MetadataContent;
import com.movistar.tvservices.miviewtv.discovery.dvbipi.parser.BCGDiscoveryParser;


public class BCGDiscoveryData {
    private static final String LOG_TAG = BCGDiscoveryData.class.getSimpleName();

    private Map<String, BCG> guides = new HashMap<String, BCG>();

    public final class BCG {
        private String id;
        private int version;
        private HashMap<String, TransportMode> transportModes;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }

        public HashMap<String, TransportMode> getLocations() {
            return locations;
        }

        public void addTransportMode(String source, TransportMode transportMode) {
            transportModes.put(source, transportMode);
        }

        public TransportMode getTransportMode(String source) {
            return transportModes.get(source);
        }
    }

    public final class TransportMode {
        private String source;
        private String address;
        private int port;
        private HashMap<int, int> segments;

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

        public HashMap<int, int> getSegments() {
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

    public Service getBroadContentGuide(String id) {
        return guides.get(id);
    }

    public static BCGDiscoveryData fromMetadata(MetadataContent metadataContent) {
        return BCGDiscoveryParser.getInstance().parse(metadataContent.getByteArrayInputStream());
    }
}
