package com.movistar.iptv.platform.stb.pm.bootup;

import com.movistar.iptv.platform.stb.pm.bootup.infocast.InfocastReader;
import com.movistar.iptv.platform.stb.pm.bootup.infocast.InfocastContent;
import com.movistar.iptv.platform.stb.pm.bootup.infocast.InfocastException;

import android.util.Log;
import java.util.List;

public class BootLoader {
    private static final String LOG_TAG = BootLoader.class.getSimpleName();

    private static final String DEFAULT_OPCH_ADDR = "239.0.2.30"; //239.0.2.30
    private static final int DEFAULT_OPCH_PORT = 22222;

    public static void download() throws BootException {
        InfocastReader infocastReader = null;

        try {

            infocastReader = InfocastReader.open(DEFAULT_OPCH_ADDR, DEFAULT_OPCH_PORT);

            List<InfocastContent> infocastContents = infocastReader.download(BootProperties.getPropertiesName());

            for (InfocastContent content : infocastContents) {
                BootProperties.setPropertyValue (content.getId(), content.getString());
                Log.v(LOG_TAG, "Configured boot property: [" + content.getId() + ", '" + content.getString() + "']");
            }

            Log.v(LOG_TAG, "Finished Boot loader successfully [" + infocastContents.size() + "]");

        } catch (InfocastException e) {
            throw new BootException("Failed to download bootcast information", e);

        } finally {
            if (infocastReader != null)
                infocastReader.close();
        }
    }
}
