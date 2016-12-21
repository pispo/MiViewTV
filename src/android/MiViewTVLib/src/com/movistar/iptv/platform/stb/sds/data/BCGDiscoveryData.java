package com.movistar.iptv.platform.stb.sds.data;

import android.util.Log;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.LinkedHashMap;

import com.movistar.iptv.platform.stb.sds.dvbstp.DvbStpContent;
import com.movistar.iptv.platform.stb.sds.parser.BCGDiscoveryParser;
import com.movistar.iptv.platform.stb.sds.parser.DiscoveryParserException;


public class BCGDiscoveryData {
    private static final String LOG_TAG = BCGDiscoveryData.class.getSimpleName();

    private Map<String, BCG> guides = new LinkedHashMap<String, BCG>();

    public static class BCG {
        private String id;
        private String name;
        private int version;
        
        private Map<String, TransportMode> transportModes =  new LinkedHashMap<String, TransportMode>();

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

        public void addTransportMode(TransportMode transportMode) {
            transportModes.put(transportMode.getSource(), transportMode);
        }

        public TransportMode getTransportMode(String source) {
            return transportModes.get(source);
        }

        public List<TransportMode> getTransportModes() {
            return Collections.list(Collections.enumeration(transportModes.values()));
        }
    }

    public static class TransportMode {
        private String source;
        private String address;
        private int port;
        private Map<Integer, Integer> segments = new LinkedHashMap<Integer, Integer>();

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

        public List<Integer> getSegmentIds() {
            return Arrays.asList(segments.keySet().toArray(new Integer[0]));
        }

        public void addSegment(int id, int version) {
            segments.put(id, version);
        }
    }

    public boolean hasData() {
        return (guides.size() > 0);
    }

    public void addBroadContentGuide(BCG broadContentGuide) {
        if (broadContentGuide != null)
            guides.put(broadContentGuide.getId(), broadContentGuide);
    }

    public BCG getBroadContentGuide(String id) {
        return guides.get(id);
    }

    public List<BCG> getBroadContentGuides() {
        return Collections.list(Collections.enumeration(guides.values()));
    }

    public static BCGDiscoveryData fromMetadata(DvbStpContent content) throws DiscoveryParserException {
        return BCGDiscoveryParser.parse(content);
    }
}
