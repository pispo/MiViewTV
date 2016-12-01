package com.movistar.tvservices.miviewtv.discovery.dvbipi.dvbstp;

import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.UnknownHostException;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.movistar.tvservices.utils.metadata.MetadataContent;
import com.movistar.tvservices.utils.net.MulticastSocketHandler;

/**
 * This class implements an Infocast loader that download all the info related to the booting
 * and configuration of the Infocast service
 */
public class DvbStpReader {

    private static final String LOG_TAG = DvbStpReader.class.getSimpleName();

    public static final int TYPE_DVBSTP    = 0x00;
    public static final int TYPE_DVBBINSTP = 0x01;

    public static final int PAYLOAD_ID      = 0x01;
    public static final int SEGMENT_ID      = 0x02;
    public static final int SEGMENT_VERSION = 0x04;
    public static final int ALL_FIELDS      = 0xFF;

    public static final int IO_ERROR             = -1;
    public static final int PARSE_ERROR          = -2;
    public static final int EMPTY_FILELIST_ERROR = -3;

    private static final int TIMEOUT = 500000;

    private MulticastSocketHandler multicastSocketHandler = null;

    protected DvbStpReader (String address, int port) throws DvbStpException {
        try {

            multicastSocketHandler = new MulticastSocketHandler(address, port);
            multicastSocketHandler.connect();

        } catch (IOException e) {
            throw new DvbStpException("Failed to connect", e);
        }
    }

    public static DvbStpReader open (String address, int port) throws DvbStpException {
        return new DvbStpReader(address, port);
    }

    public Map<Integer, MetadataContent<Integer>> download(List<Integer> contentKeys) throws DvbStpException {
        DvbStpHeader header;
        DatagramPacket packet;
        MetadataContent<Integer> metadataContent = null;
        Map<Integer, MetadataContent<Integer>> metadataContents = new HashMap<Integer, MetadataContent<Integer>>();

        int contentKey = 0;
        int payloadLength = 0;

        long initialTime = SystemClock.elapsedRealtime(); // TODO: bail out on exceeded time limit
        long nowTime = initialTime;

        int totalContents = 0;
        int completedContents = 0;

        while (contentKeys.size() > 0 && (nowTime-initialTime < TIMEOUT)) {

            if (null == (packet = multicastSocketHandler.readPacket()))
                continue;

            nowTime = SystemClock.elapsedRealtime();

            header = DvbStpHeader.decode(packet.getData(), packet.getOffset(), packet.getLength());
            contentKey = (header.getPayloadId() << 16) | header.getSegmentId();

            if (contentKeys.contains(header.getId())) {

                payloadLength = packet.getLength() - header.getLength() - (header.getCRC() * 4);

                if (null == (metadataContent = metadataContents.get(contentKey))) {

                    metadataContent = new MetadataContent<Integer>(contentKey,
                            header.getLastSectionNumber() + 1, nowTime, header.getSegmentVersion());

                    if (MetadataContent.COMPLETED == metadataContent.addFragment(packet.getData(), packet.getOffset(),
                            header.getLength(), payloadLength, header.getSectionNumber(), header.getSegmentVersion())) {

                        completedContents++;
                    }

                    metadataContents.put(contentKey, metadataContent);
                    totalContents++;

                } else if (!metadataContent.isBufferCompleted()) {

                    if (metadataContent.getVersion() != header.getSegmentVersion()) {

                        metadataContent = new MetadataContent<Integer>(contentKey,
                                header.getLastSectionNumber() + 1, nowTime, header.getSegmentVersion());

                        metadataContents.put(contentKey, metadataContent);
                    }

                    if (MetadataContent.COMPLETED == metadataContent.addFragment(packet.getData(), packet.getOffset(),
                            header.getLength(), payloadLength, header.getSectionNumber(), header.getSegmentVersion())) {

                        completedContents++;
                    }
                }

                if ((totalContents > 0) && (totalContents == completedContents))
                    break;
            }
        }
      
        for (Map.Entry<Integer, MetadataContent<Integer>> entry : metadataContents.entrySet()) {
            MetadataContent<Integer> content = entry.getValue();
            if (!content.isBufferCompleted())
                metadataContents.remove(content.getId());
        }

        Log.d(LOG_TAG, "finished DvbStp processing.");

        return metadataContents;
    }

    public void close() {
        multicastSocketHandler.disconnect();
    }
}
