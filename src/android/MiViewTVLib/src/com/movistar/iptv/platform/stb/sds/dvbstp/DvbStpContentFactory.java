package com.movistar.iptv.platform.stb.sds.dvbstp;

import com.movistar.iptv.platform.stb.sds.dvbstp.DvbStpBinaryContent;
import com.movistar.iptv.platform.stb.sds.dvbstp.DvbStpXmlContent;

public class DvbStpContentFactory<T extends DvbStpContent> {

    public static <T extends DvbStpContent> T create(Integer id, int fragments, long time) throws DvbStpException {

        if (DvbStpContentFactory.class.isAssignableFrom(DvbStpBinaryContent.class)) {
            return (T) new DvbStpBinaryContent(id, fragments, time);

        } else if (DvbStpContentFactory.class.isAssignableFrom(DvbStpXmlContent.class)) {
            return (T) new DvbStpXmlContent(id, fragments, time);
        }

        throw new DvbStpException("DvbStpContentFactory: Content unknown");
    }

    public static <T extends DvbStpContent> T create(Integer id, int fragments, long time, int version) throws DvbStpException {

        if (DvbStpContentFactory.class.isAssignableFrom(DvbStpBinaryContent.class)) {
            return (T) new DvbStpBinaryContent(id, fragments, time, version);

        } else if (DvbStpContentFactory.class.isAssignableFrom(DvbStpXmlContent.class)) {
            return (T) new DvbStpXmlContent(id, fragments, time, version);
        }

        throw new DvbStpException("DvbStpContentFactory: Content unknown");
    }
}
