package com.movistar.tvservices.miviewtv.discovery.dvbipi.dvbstp;

import com.movistar.tvservices.utils.metadata.MetadataContent;

public class DvbStpContent extends MetadataContent<Integer> {

    public DvbStpContent(Integer id, int fragments, int type, long time) {
        super(id, fragments, type, time, 0);
    }

    public DvbStpContent(Integer id, int fragments, int type, long time, int version) {
        super(id, fragments, type, time, version);
    }

    public Integer getId () {
        return super.getId();
    }
}
