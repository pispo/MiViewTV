package com.movistar.iptv.platform.stb.sds.dvbstp;

import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.UnknownHostException;

import java.util.Collections;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

import com.movistar.iptv.util.net.MulticastSocketHandler;

/**
 * This class implements an DvbStp reader that read all
 */


public class DvbStpReader<T extends DvbStpContent> {

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

    private boolean connected = false;
    private MulticastSocketHandler multicastSocketHandler = null;

    protected DvbStpReader (String address, int port) throws DvbStpException {
        try {

            multicastSocketHandler = new MulticastSocketHandler(address, port);
            multicastSocketHandler.connect();
            connected = true;

        } catch (IOException e) {
            throw new DvbStpException("Failed to connect", e);
        }
    }

    public static <T extends DvbStpContent> DvbStpReader<T> open (String address, int port) throws DvbStpException {
        return new DvbStpReader<T>(address, port);
    }

    public <T extends DvbStpContent> List<T> download(List<Integer> contentIds) throws DvbStpException {
        DvbStpHeader header;
        DatagramPacket packet;
        T dvbStpContent = null;
        Map<Integer, T> dvbStpContents = new LinkedHashMap<Integer, T>();

        long initialTime = SystemClock.elapsedRealtime(); // TODO: bail out on exceeded time limit
        long nowTime = initialTime;

        int payloadLength = 0;

        int totalContents = 0;
        int completedContents = 0;
        int pendingContents = contentIds.size();

        while (pendingContents > 0 && (nowTime-initialTime < TIMEOUT)) {

            if (null == (packet = multicastSocketHandler.readPacket()))
                continue;

            nowTime = SystemClock.elapsedRealtime();

            header = DvbStpHeader.decode(packet.getData(), packet.getOffset(), packet.getLength());

            if (contentIds.contains(header.getId())) {

                payloadLength = packet.getLength() - header.getLength() - (header.getCRC() * 4);

                if (null == (dvbStpContent = dvbStpContents.<T>get(header.getId()))) {

                    dvbStpContent = DvbStpContentFactory.<T>create(header.getId(),
                            header.getLastSectionNumber() + 1,
                            nowTime, header.getSegmentVersion());

                    if (DvbStpContent.COMPLETED == dvbStpContent.addFragment(packet.getData(), packet.getOffset(),
                            header.getLength(), payloadLength, header.getSectionNumber(), header.getSegmentVersion())) {

                        completedContents++;
                        pendingContents--;
                    }

                    dvbStpContents.put(header.getId(), dvbStpContent);
                    totalContents++;

                } else if (!dvbStpContent.isBufferCompleted()) {

                    if (dvbStpContent.getVersion() != header.getSegmentVersion()) {

                        dvbStpContent = DvbStpContentFactory.<T>create(header.getId(),
                                header.getLastSectionNumber() + 1,
                                nowTime, header.getSegmentVersion());

                        dvbStpContents.put(header.getId(), dvbStpContent);
                    }

                    if (DvbStpContent.COMPLETED == dvbStpContent.addFragment(packet.getData(), packet.getOffset(),
                            header.getLength(), payloadLength, header.getSectionNumber(), header.getSegmentVersion())) {

                        completedContents++;
                        pendingContents--;
                    }
                }
            }
        }

        for (Map.Entry<Integer, T> entry : dvbStpContents.entrySet()) {
            T content = entry.getValue();

            if (!content.isBufferCompleted())
                dvbStpContents.remove(content.getId());
        }

        Log.d(LOG_TAG, "finished DvbStp processing.");

        return Collections.<T>list(Collections.<T>enumeration(dvbStpContents.values()));
    }

    public void close() {
        multicastSocketHandler.disconnect();
    }
}
