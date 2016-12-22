package com.movistar.iptv.platform.stb.pm.bootup.infocast;

/**
 * Created by Sergio Moreno Mozota on 30/11/15.
 *
 * Copyright Telef�nica de Espa�a SAU 2015
 */

import android.os.SystemClock;
import android.util.Log;

import com.movistar.iptv.util.net.MulticastSocketHandler;

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
        int pendingContents = contentIds.size();

        if (!connected)
            throw new InfocastException("InfocastReader is closed");

        while (pendingContents > 0 && (nowTime-initialTime < TIMEOUT)) {

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

                if (null == (infocastContent = infocastContents.get(header.getId()))) {

                    infocastContent = new InfocastContent(header.getId(), header.getTotalPackets(), nowTime);

                    if (InfocastContent.COMPLETED == infocastContent.addFragment(packet.getData(), packet.getOffset(),
                            header.getLength(), payloadLength, header.getPacketNumber() - 1)) {
                        completedContents++;
                        pendingContents--;
                    }

                    infocastContents.put(header.getId(), infocastContent);
                    totalContents++;

                } else if (!infocastContent.isBufferCompleted()) {
                    if (InfocastContent.COMPLETED == infocastContent.addFragment(packet.getData(), packet.getOffset(),
                            header.getLength(), payloadLength, header.getPacketNumber() - 1)) {
                        completedContents++;
                        pendingContents--;
                    }
                }
            }
        }

        for (Map.Entry<String, InfocastContent> entry : infocastContents.entrySet()) {
            InfocastContent content = entry.getValue();

            if (!content.isBufferCompleted())
                infocastContents.remove(content.getId());
        }

        Log.d(LOG_TAG, "Finished Infocast processing [" + infocastContents.size() + "]");

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
