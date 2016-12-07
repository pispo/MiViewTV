package com.movistar.tvservices.bootcast;

import com.movistar.tvservices.utils.metadata.MetadataContent;
import com.movistar.tvservices.bootcast.infocast.InfocastReader;
import com.movistar.tvservices.bootcast.infocast.InfocastException;

import android.util.Log;
import java.util.List;

public class BootLoader {
    private static final String LOG_TAG = BootLoader.class.getSimpleName();

    private static final String DEFAULT_OPCH_ADDR = "239.0.2.10"; //239.0.2.30
    private static final int DEFAULT_OPCH_PORT = 22222;

    public static void download() throws BootException {
        InfocastReader infocastReader = null;

        try {

            infocastReader = InfocastReader.open(DEFAULT_OPCH_ADDR, DEFAULT_OPCH_PORT);

            List<MetadataContent<String>> metadataContents = infocastReader.download(BootProperties.getPropertiesName());

            for (MetadataContent<String> metadataContent : metadataContents) {
                BootProperties.setPropertyValue (metadataContent.getId(), metadataContent.getString());
            }

        } catch (InfocastException e) {
            throw new BootException("Failed to download bootcast information", e);

        } finally {
            if (infocastReader != null)
                infocastReader.close();
        }
    }
}
