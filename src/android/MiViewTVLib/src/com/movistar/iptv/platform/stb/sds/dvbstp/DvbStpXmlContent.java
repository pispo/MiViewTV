package com.movistar.iptv.platform.stb.sds.dvbstp;


public class DvbStpXmlContent extends DvbStpContent {

    public DvbStpXmlContent(Integer id, int fragments, long time) {
        super(id, fragments, TYPE_XML, time, 0);
    }

    public DvbStpXmlContent(Integer id, int fragments, long time, int version) {
        super(id, fragments, TYPE_XML, time, version);
    }
}