package com.movistar.tvservices.bootcast.infocast;

/**
 * Created by Sergio Moreno Mozota on 30/11/15.
 *
 * Copyright Telef�nica de Espa�a SAU 2015
 */

import android.os.SystemClock;
import android.util.Log;

import com.movistar.tvservices.utils.net.MulticastSocketHandler;

import java.io.IOException;
import java.net.DatagramPacket;

import java.util.Collections;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

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

    public List<InfocastContent> download(List<String> contentIds) throws InfocastException {
        InfocastHeader header;
        DatagramPacket packet = null;
        InfocastContent infocastContent = null;
        Map<String, InfocastContent> infocastContents = new LinkedHashMap<String, InfocastContent>();

        long initialTime = SystemClock.elapsedRealtime(); // TODO: bail out on exceeded time limit
        long nowTime = initialTime;

        int contentType;
        int payloadLength = 0;
        
        int totalContents = 0;
        int completedContents = 0;

        if (!connected)
            throw new InfocastException("InfocastReader is closed");

        while (contentIds.size() > 0 && (nowTime-initialTime < TIMEOUT)) {

            if (null == (packet = multicastSocketHandler.readPacket()))
                continue;

            nowTime = SystemClock.elapsedRealtime();

            header = InfocastHeader.decode(packet.getData(), packet.getOffset(), packet.getLength());

            if (contentIds.contains(header.getId())) {

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
                        contentType = infocastContent.TYPE_ASCII;
                        break;
                    case TYPE_FILE:
                    case TYPE_SW:
                        contentType = infocastContent.TYPE_BINARY;
                        break;
                    default:
                        contentType = infocastContent.TYPE_UNKNOWN;
                        break;
                }

                if (null == (infocastContent = infocastContents.get(header.getId()))) {

                    infocastContent = new InfocastContent(header.getId(), header.getTotalPackets(),
                            contentType, nowTime);

                    if (InfocastContent.COMPLETED == infocastContent.addFragment(packet.getData(), packet.getOffset(),
                            header.getLength(), payloadLength, header.getPacketNumber() - 1)) {
                        completedContents++;
                    }

                    infocastContents.put(header.getId(), infocastContent);
                    totalContents++;

                } else if (!infocastContent.isBufferCompleted()) {
                    if (InfocastContent.COMPLETED == infocastContent.addFragment(packet.getData(), packet.getOffset(),
                            header.getLength(), payloadLength, header.getPacketNumber() - 1)) {
                        completedContents++;
                    }
                }
            }

            if ((totalContents > 0) && (totalContents == completedContents))
                break;
        }

        for (Map.Entry<String, InfocastContent> entry : infocastContents.entrySet()) {
            InfocastContent content = entry.getValue();

            if (!content.isBufferCompleted())
                infocastContents.remove(content.getId());
        }

        Log.d(LOG_TAG, "finished Infocast processing.");

        return Collections.list(Collections.enumeration(infocastContents.values()));
    }

    public void close() {
        if (connected) {
            multicastSocketHandler.disconnect();
            connected = false;
        }
    }

    public boolean isConnected() { return connected; }
}
