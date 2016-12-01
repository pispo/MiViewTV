package com.movistar.tvservices.bootcast.infocast;

/**
 * Created by Sergio Moreno Mozota on 30/11/15.
 *
 * Copyright Telef�nica de Espa�a SAU 2015
 */
import android.util.Log;

public class InfocastHeader {

    private static final int HEADER_LENGTH = 44;

    private int type;
    private int precoding;
    private int packetNumber;
    private int totalPackets;
    private String id;
    private int dynamicHeadInfo;
    private int length;

    public InfocastHeader(String id, int type, int precoding, int packetNumber, int totalPackets, int dynamicHeadInfo, int length)
    {
        this.id = id;
        this.type = type;
        this.length = length;
        this.precoding = precoding;
        this.packetNumber = packetNumber;
        this.totalPackets = totalPackets;
        this.dynamicHeadInfo = dynamicHeadInfo;
    }

    public int getType() {
        return type;
    }

    public int getPrecoding() {
        return precoding;
    }

    public int getPacketNumber() {
        return packetNumber;
    }

    public int getTotalPackets() {
        return totalPackets;
    }

    public String getId() {
        return id;
    }

    public int getDynamicHeadInfo() {
        return dynamicHeadInfo;
    }

    public int getLength() {
        return length;
    }

    public static InfocastHeader decode(byte[] buffer, int bufferOffset, int bufferLength) throws InfocastException {
        if (bufferLength < HEADER_LENGTH) {
            throw new InfocastException("Bad header, length too short");
        }

        int type = ((buffer[bufferOffset] << 8) & 0xff00) | (buffer[bufferOffset + 1] & 0xff);
        int precoding = ((buffer[bufferOffset + 2] << 8) & 0xff00) | (buffer[bufferOffset + 3] & 0xff);

        int packetNumber = ((buffer[bufferOffset + 4] << 24) & 0xff000000)
                | ((buffer[bufferOffset + 5] << 16) & 0xff0000)
                | ((buffer[bufferOffset + 6] << 8) & 0xff00)
                | (buffer[bufferOffset + 7] & 0xff);

        int totalPackets = ((buffer[bufferOffset + 8] << 24) & 0xff000000)
                | ((buffer[bufferOffset + 9] << 16) & 0xff0000)
                | ((buffer[bufferOffset + 10] << 8) & 0xff00)
                | (buffer[bufferOffset + 11] & 0xff);

        int idLen = 0;
        while (idLen < 30 && buffer[bufferOffset + 12 + idLen] != 0x00)
            idLen++;

        String id = new String(buffer, 12, idLen);

        int dynamicHeadInfo =  ((buffer[bufferOffset + 42] << 8) & 0xff00)
                | (buffer[bufferOffset + 43] & 0xff);

        // add dynamic head size to header length
        int length = HEADER_LENGTH + ((dynamicHeadInfo >> (13 - 4)) & 0x0030);

        return new InfocastHeader(id, type, precoding, packetNumber, totalPackets, dynamicHeadInfo, length);
    }
}
