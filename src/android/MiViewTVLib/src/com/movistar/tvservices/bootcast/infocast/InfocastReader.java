package com.movistar.tvservices.bootcast.infocast;

/**
 * Created by Sergio Moreno Mozota on 30/11/15.
 *
 * Copyright Telef�nica de Espa�a SAU 2015
 */

import android.os.SystemClock;
import android.util.Log;

import com.movistar.tvservices.utils.metadata.MetadataContent;
import com.movistar.tvservices.utils.net.MulticastSocketHandler;

import java.io.IOException;
import java.net.DatagramPacket;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * This class implements an Infocast Reader that download all the info related to the booting
 * and configuration of the Infocast service
 */
public class InfocastReader {

    private static final String LOG_TAG = InfocastReader.class.getSimpleName();

    private static final int TYPE_INFO           =   0x00;
    private static final int TYPE_GENERIC        =   0x01;
    private static final int TYPE_FILE_INFO      =   0x02;
    private static final int TYPE_FILE           =   0x03;
    private static final int TYPE_SW_INFO        =   0x04;
    private static final int TYPE_SW             =   0x05;
    private static final int TYPE_TIME           =   0x06;
    private static final int TYPE_UTCTIME        =   0x07;

    private static final int PRECODING_NONE_OIS  =   0x00;
    private static final int PRECODING_NONE      =   0x01;
    private static final int PRECODING_CRC16     =   0x02;
    private static final int PRECODING_SHA1      =   0x03;
    private static final int PRECODING_CRC32     =   0x04;
    private static final int PRECODING_CRC32_OIS = 0x4000;

    private static final int TIMEOUT = 500000;

    private boolean connected = false;
    private MulticastSocketHandler multicastSocketHandler = null;

    protected InfocastReader(String address, int port) throws InfocastException {
        try {

            multicastSocketHandler = new MulticastSocketHandler(address, port);
            multicastSocketHandler.connect();
            connected = true;

        } catch (IOException e) {
            throw new InfocastException("Failed to connect", e);
        }
    }

    public static InfocastReader open(String address, int port) throws InfocastException {
        return new InfocastReader(address, port);
    }

    public Map<String, MetadataContent> download(List<String> contentKeys) throws InfocastException {
        InfocastHeader header;
        DatagramPacket packet = null;
        MetadataContent content = null;
        Map<String, MetadataContent> metadataContents = new HashMap<String, MetadataContent>();

        long initialTime = SystemClock.elapsedRealtime(); // TODO: bail out on exceeded time limit
        long nowTime = initialTime;

        int contentType;
        int totalContents = 0;
        int completedContents = 0;

        if (!connected)
            throw new InfocastException("InfocastReader is closed", e);

        while (contentKeys.size() > 0 && (nowTime-initialTime < TIMEOUT)) {

            if (null == (packet = multicastSocketHandler.readPacket()))
                continue;

            nowTime = SystemClock.elapsedRealtime();

            header = InfocastHeader.decode(packet.getData(), packet.getOffset(), packet.getLength());

            if (null != contentKeys.contains(header.contentKey())) {

                payloadLength = packet.getLength() - header.getLength();

                switch (header.getPrecoding()) {
                    case PRECODING_CRC16:
                        payloadLength -= 2;
                        break;
                    case PRECODING_CRC32:
                    case PRECODING_CRC32_OIS:
                        payloadLength -= 4;
                        break;
                    default:
                        break;
                }

                switch (header.getType()) {
                    case TYPE_INFO:
                    case TYPE_GENERIC:
                    case TYPE_FILE_INFO:
                    case TYPE_SW_INFO:
                    case TYPE_TIME:
                    case TYPE_UTCTIME:
                        contentType = MetadataContent.TYPE_ASCII;
                        break;
                    case TYPE_FILE:
                    case TYPE_SW:
                        contentType = MetadataContent.TYPE_BINARY;
                        break;
                    default:
                        contentType = MetadataContent.TYPE_UNKNOWN;
                        break;
                }

                if (null == (metadataContent = metadataContents.get(header.getId()))) {

                    metadataContent = new MetadataContent(header.getId(), header.getTotalPackets(),
                            contentType, nowTime);

                    if (MetadataContent.COMPLETED == metadataContent.addFragment(packet.getData(), packet.getOffset(),
                            header.getLength(), payloadLength, header.getPacketNumber() - 1)) {
                        completedContents++;
                    }

                    metadataContents.put(header.getId(), metadataContent);
                    totalContents++;

                } else if (!metadataContent.isBufferCompleted()) {

                    if (metadataContent.getVersion() != header.getSegmentVersion()) {
                        metadataContent = new MetadataContent(header.getId(), header.getTotalPackets(), contentType, nowTime);
                        metadataContents.put(contentKey, metadataContent);
                    }

                    if (MetadataContent.COMPLETED == metadataContent.addFragment(packet.getData(), packet.getOffset(),
                            header.getLength(), payloadLength, header.getPacketNumber() - 1)) {
                        completedContents++;
                    }
                }
            }

            if ((totalContents > 0) && (totalContents == completedContents))
                break;
        }

        for (MetadataContent content : metadataContents) {
            if (!content.isBufferCompleted())
                metadataContents.remove(content.getId());
        }

        Log.d(LOG_TAG, "finished Infocast processing.");

        return metadataContents;
    }

    public void close() {
        if (connected) {
            multicastSocketHandler.disconnect();
            connected = false;
        }
    }

    public boolean isConnected() { return connected; }
}
