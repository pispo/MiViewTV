package com.movistar.iptv.platform.stb.sds.dvbstp;

import com.movistar.iptv.util.metadata.MetadataContent;

public class DvbStpContent extends MetadataContent<Integer> {

    public DvbStpContent(Integer id, int fragments, long time) {
        super(id, fragments, time, 0);
    }

    public DvbStpContent(Integer id, int fragments, long time, int version) {
        super(id, fragments, time, version);
    }

    public Integer getId() {
        return super.getId();
    }

    public static Integer generateId(int payload) {
        return payload;
    }

    public static Integer generateId(int payload, int segment) {
        return (payload << 16) | segment;
    }

    public static Integer generateId(int payload, int segment, int version) {
        return (payload << 24) | (segment << 8) | version;
    }
}
