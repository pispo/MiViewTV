package com.movistar.iptv.platform.stb.sds.dvbstp;

public class DvbStpBinaryContent extends DvbStpContent {

    public DvbStpBinaryContent(Integer id, int fragments, long time) {
        super(id, fragments, TYPE_BINARY, time, 0);
    }

    public DvbStpBinaryContent(Integer id, int fragments, long time, int version) {
        super(id, fragments, TYPE_BINARY, time, version);
    }
}
