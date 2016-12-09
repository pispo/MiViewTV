package com.movistar.tvservices.miviewtv.discovery.dvbipi.dvbstp;

import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.UnknownHostException;

import java.util.Collections;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

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

    public List<DvbStpContent> download(List<Integer> contentIds) throws DvbStpException {
        DvbStpHeader header;
        DatagramPacket packet;
        DvbStpContent dvbStpContent = null;
        Map<Integer, DvbStpContent> dvbStpContents = new LinkedHashMap<Integer, DvbStpContent>();

        long initialTime = SystemClock.elapsedRealtime(); // TODO: bail out on exceeded time limit
        long nowTime = initialTime;

        int payloadLength = 0;
        int totalContents = 0;
        int completedContents = 0;

        while (contentIds.size() > 0 && (nowTime-initialTime < TIMEOUT)) {

            if (null == (packet = multicastSocketHandler.readPacket()))
                continue;

            nowTime = SystemClock.elapsedRealtime();

            header = DvbStpHeader.decode(packet.getData(), packet.getOffset(), packet.getLength());

            if (contentIds.contains(header.getId())) {

                payloadLength = packet.getLength() - header.getLength() - (header.getCRC() * 4);

                if (null == (dvbStpContent = dvbStpContents.get(header.getId()))) {

                    dvbStpContent = new DvbStpContent(header.getId(),
                            header.getLastSectionNumber() + 1, DvbStpContent.TYPE_UNKNOWN, nowTime,
                            header.getSegmentVersion());

                    if (DvbStpContent.COMPLETED == dvbStpContent.addFragment(packet.getData(), packet.getOffset(),
                            header.getLength(), payloadLength, header.getSectionNumber(), header.getSegmentVersion())) {

                        completedContents++;
                    }

                    dvbStpContents.put(header.getId(), dvbStpContent);
                    totalContents++;

                } else if (!dvbStpContent.isBufferCompleted()) {

                    if (dvbStpContent.getVersion() != header.getSegmentVersion()) {

                        dvbStpContent = new DvbStpContent(header.getId(),
                                header.getLastSectionNumber() + 1, DvbStpContent.TYPE_UNKNOWN, nowTime,
                                header.getSegmentVersion());

                        dvbStpContents.put(header.getId(), dvbStpContent);
                    }

                    if (DvbStpContent.COMPLETED == dvbStpContent.addFragment(packet.getData(), packet.getOffset(),
                            header.getLength(), payloadLength, header.getSectionNumber(), header.getSegmentVersion())) {

                        completedContents++;
                    }
                }

                if ((totalContents > 0) && (totalContents == completedContents))
                    break;
            }
        }

        for (Map.Entry<Integer, DvbStpContent> entry : dvbStpContents.entrySet()) {
            DvbStpContent content = entry.getValue();

            if (!content.isBufferCompleted())
                dvbStpContents.remove(content.getId());
        }

        Log.d(LOG_TAG, "finished DvbStp processing.");

        return Collections.list(Collections.enumeration(dvbStpContents.values()));
    }

    public void close() {
        multicastSocketHandler.disconnect();
    }
}
