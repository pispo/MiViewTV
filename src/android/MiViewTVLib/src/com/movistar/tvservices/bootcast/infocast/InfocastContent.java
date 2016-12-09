package com.movistar.tvservices.bootcast.infocast;

import com.movistar.tvservices.utils.metadata.MetadataContent;

public class InfocastContent extends MetadataContent<String> {

    public InfocastContent(String id, int fragments, int type, long time) {
        super(id, fragments, type, time, 0);
    }

    public InfocastContent(String id, int fragments, int type, long time, int version) {
        super(id, fragments, type, time, version);
    }

    public String getId () {
        return super.getId();
    }
}
