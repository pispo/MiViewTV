package com.movistar.iptv.platform.content.guide;

import android.util.Log;

import com.movistar.iptv.platform.stb.sds.SDSConstants;

import com.movistar.iptv.platform.stb.sds.dvbstp.DvbStpReader;
import com.movistar.iptv.platform.stb.sds.dvbstp.DvbStpContent;
import com.movistar.iptv.platform.stb.sds.dvbstp.DvbStpBinaryContent;
import com.movistar.iptv.platform.stb.sds.dvbstp.DvbStpException;

import com.movistar.iptv.platform.stb.sds.ServiceDiscoveryManager;
import com.movistar.iptv.platform.stb.sds.ServiceDiscoveryException;

import com.movistar.iptv.platform.stb.sds.data.BCGDiscoveryData;
import com.movistar.iptv.platform.stb.sds.data.BCGDiscoveryData.BCG;
import com.movistar.iptv.platform.stb.sds.data.BCGDiscoveryData.TransportMode;

import com.movistar.iptv.platform.stb.pm.time.UTCTimeException;
import com.movistar.iptv.platform.stb.pm.time.UTCTimeManager;

import com.movistar.iptv.util.concurrent.AsyncTaskProgressCallback;

import java.util.List;
import java.util.LinkedList;

public class EPGManager {
    private static final String LOG_TAG = EPGManager.class.getSimpleName();

    private static EPGManager instance = new EPGManager();

    private boolean loaded = false;

    public static EPGManager getInstance() { return instance; }

    public void download(AsyncTaskProgressCallback progressCallback) throws EPGException {
        DvbStpReader<DvbStpBinaryContent> dvbStpReader = null;
        int[] days = new int[] {0, 1, 2, -1, -2, 3, -3, 4};
        int numDay = 0;

        loaded = false;

        try {

            Log.v(LOG_TAG, "Estoy en el EPGManager");

            BCGDiscoveryData bcgDiscoveryData = ServiceDiscoveryManager.getInstance().getBCGDiscoveryData();

            Log.v(LOG_TAG, "Estoy en el EPGManager 2");

            BCG bcg = bcgDiscoveryData.getBroadContentGuide("EPG");

            Log.v(LOG_TAG, "Estoy en el EPGManager 3");

            Log.v(LOG_TAG, "BCG EPG " + bcg.getTransportModes().size());

            for (TransportMode transportMode : bcg.getTransportModes()) {

                Log.v(LOG_TAG, "EPG Transport mode: address = [" + transportMode.getAddress() + "], port=[" + transportMode.getPort() + "]");

                List<Integer> contentIds = new LinkedList<Integer>();

                for (Integer segmentId : transportMode.getSegmentIds()) {
                    contentIds.add(DvbStpBinaryContent.generateId(
                            SDSConstants.SDS_CONTENT_GUIDE_DISCOVERY, segmentId));
                }

                dvbStpReader = DvbStpReader.<DvbStpBinaryContent>open(transportMode.getAddress(), transportMode.getPort());
                List<DvbStpBinaryContent> epgContents = dvbStpReader.download(contentIds);
                dvbStpReader.close();

                for (DvbStpBinaryContent content : epgContents) {
                    Log.v(LOG_TAG, "EPG Content type: " + content.getType());
                }

                progressCallback.publishProgress(days[numDay++]);
            }

        } catch (DvbStpException e) {
            throw new EPGException("EPGManager: Downloading the program guide failed", e);

        } catch (ServiceDiscoveryException e) {
            throw new EPGException("EPGManager: Downloading the program guide failed", e);
        }

        loaded = true;
    }

    private int decodeDay (byte[] buffer, int length) throws UTCTimeException
    {
        final int SERVICE_HEAD_PACKET_SIZE = 7;

        int name_length;
        long timestamp;

        if (length > SERVICE_HEAD_PACKET_SIZE) {
            int sUID = buffer[3] << 8 + buffer[4];
            int bytes = buffer[1] << 8 + buffer[2];

            Log.d(LOG_TAG, "Decoding service info: " + sUID);

            if (bytes <= 0)
                return 0xff;

            name_length = buffer[SERVICE_HEAD_PACKET_SIZE - 1];

            if (name_length > 0) {
                if (buffer[SERVICE_HEAD_PACKET_SIZE] == 0)
                    return 0xff;

                int offset = SERVICE_HEAD_PACKET_SIZE + name_length + 4;
                timestamp = (buffer[offset] & 0xff) << 24 | (buffer[offset + 1] & 0xff) << 16 |
                        (buffer[offset + 2] & 0xff) << 8 | (buffer[offset + 3] & 0xff);

                return (int)((timestamp / 86400) - (UTCTimeManager.getCurrentTime() / 86400));
            }
        }
        return 0xff;
    }

    private void assertConsolidated() throws EPGException {
        if (!loaded) {
            throw new EPGException("EPGManager: The program guide has not been downloaded yet");
        }
    }
}
