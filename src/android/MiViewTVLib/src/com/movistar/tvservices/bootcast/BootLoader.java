package com.movistar.tvservices.bootcast;

import com.movistar.tvservices.utils.metadata.MetadataContent;
import com.movistar.tvservices.bootcast.infocast.InfocastReader;
import com.movistar.tvservices.bootcast.infocast.InfocastException;

import android.util.Log;

import java.io.IOException;
import java.util.Map;

public class BootLoader {
    private static final String LOG_TAG = BootLoader.class.getSimpleName();

    private static final String DEFAULT_OPCH_ADDR = "239.0.2.10";
    private static final int DEFAULT_POCH_PORT = 22222;

    public static void download() throws BootException {
        InfocastReader infocastReader = null;

        try {

            infocastReader = InfocastReader.open(DEFAULT_OPCH_ADDR, DEFAULT_POCH_PORT);

            Map<String, MetadataContent> metadataContents = infocastLoader.download(BootProperties.getPropertiesName());

            for (MetadataContent metadataContent : metadataContents)
                BootProperties.setPropertyValue (metadataContent.getName(), metadataContent.getString());

        } catch (InfocastException e) {
            throw new BootException("Failed to download bootcast information", e);

        } finally {
            if (infocastLoader)
                infocastLoader.close();
        }
    }
}
