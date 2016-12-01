package com.movistar.tvservices.miviewtv.discovery.dvbipi.dvbstp;

import android.util.Log;

public class DvbStpHeader {

    private static final int HEADER_LENGTH = 12;

    private int version;
    private int reserved;
    private int encription;
    private int crc;
    private int totalSegmentSize;
    private int payloadId;
    private int segmentId;
    private int segmentVersion;
    private int sectionNumber;
    private int lastSectionNumber;
    private int compression;
    private int providerIdFlag;
    private int privateHeaderLength;
    private int length;

    public DvbStpHeader(int version, int reserved, int encription, int crc, int totalSegmentSize, int payloadId, int segmentId,
                        int segmentVersion, int sectionNumber, int lastSectionNumber, int compression, int providerIdFlag,
                        int privateHeaderLength, int length)
    {
        this.version = version;
        this.reserved = reserved;
        this.encription = encription;
        this.crc = crc;
        this.totalSegmentSize = totalSegmentSize;
        this.payloadId = payloadId;
        this.segmentId = segmentId;
        this.segmentVersion = segmentVersion;
        this.sectionNumber = sectionNumber;
        this.lastSectionNumber = lastSectionNumber;
        this.compression = compression;
        this.providerIdFlag = providerIdFlag;
        this.privateHeaderLength = privateHeaderLength;
        this.length = length;
    }

    public int getVersion () { return version; }

    public int getReserved() { return reserved; }

    public int getEncription() { return encription; }

    public int getCRC() { return crc; }

    public int getTotalSegmentSize() { return totalSegmentSize; }

    public int getPayloadId() { return payloadId; }

    public int getSegmentId() { return segmentId; }

    public int getSegmentVersion() { return segmentVersion; }

    public int getSectionNumber() { return sectionNumber; }

    public int getLastSectionNumber() { return lastSectionNumber; }

    public int getCompression() { return compression; }

    public int getProviderIdFlag() { return providerIdFlag; }

    public int getPrivateHeaderLength() { return privateHeaderLength; }

    public int getLength() { return length; }

    public static DvbStpHeader decode(byte[] buffer, int bufferOffset, int bufferLength) throws DvbStpException {
        if (bufferLength < HEADER_LENGTH) {
            throw new DvbStpException("Bad header, length too short");
        }

        int version = (buffer[bufferOffset] >> 6) & 0x03;
        int reserved = (buffer[bufferOffset] >> 3) & 0x07;
        int encription = (buffer[bufferOffset] >> 1) & 0x03;
        int crc = buffer[bufferOffset] & 0x01;

        int totalSegmentSize = ((buffer[bufferOffset + 1] << 16) & 0xff0000) |
                ((buffer[bufferOffset + 2] << 8) & 0xff00) |
                (buffer[bufferOffset + 3] & 0xff);

        int payloadId = buffer[bufferOffset + 4] & 0xff;

        int segmentId = ((buffer[bufferOffset + 5] << 8) & 0xff00) |
                (buffer[bufferOffset +  6] & 0xff);

        int segmentVersion = buffer[bufferOffset + 7] & 0xff;

        int sectionNumber = ((buffer[bufferOffset + 8] << 4) & 0xff0) |
                ((buffer[bufferOffset + 9] >> 4) & 0xf);

        int lastSectionNumber = ((buffer[bufferOffset + 9] << 8) & 0xf00) |
                (buffer[bufferOffset + 10]) & 0xff;

        int compression = (buffer[bufferOffset + 11] >> 5) & 0xff;
        int providerIdFlag = (buffer[bufferOffset + 11] >> 4) & 0x01;
        int privateHeaderLength = buffer[bufferOffset + 11] & 0x0f;

        // add private head size to header length
        int length = HEADER_LENGTH + ((privateHeaderLength + providerIdFlag) * 4);

        return new DvbStpHeader(version, reserved, encription, crc, totalSegmentSize, payloadId, segmentId,
                segmentVersion, sectionNumber, lastSectionNumber, compression, providerIdFlag,
                privateHeaderLength, length);
    }
}
