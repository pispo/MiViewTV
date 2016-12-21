package com.movistar.iptv.platform.stb.sds.dvbstp;

import com.movistar.iptv.util.metadata.MetadataContent;

public abstract class DvbStpContent extends MetadataContent<Integer> {

    public DvbStpContent(Integer id, int fragments, int type, long time) {
        super(id, fragments, type, time, 0);
    }

    public DvbStpContent(Integer id, int fragments, int type, long time, int version) {
        super(id, fragments, type, time, version);
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
