package com.movistar.iptv.platform.stb.pm.bootup.infocast;

import com.movistar.iptv.util.metadata.MetadataContent;

public class InfocastContent extends MetadataContent<String> {

    public InfocastContent(String id, int fragments, long time) {
        super(id, fragments,  time, 0);
    }

    public InfocastContent(String id, int fragments, long time, int version) {
        super(id, fragments, time, version);
    }

    public String getId () {
        return super.getId();
    }
}
