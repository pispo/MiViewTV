package com.movistar.iptv.util.metadata;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

/**
 * Created by Sergio Moreno Mozota on 30/11/15.
 *
 * Copyright Telefónica de España SAU 2015
 */

public abstract class MetadataContent<K> {

    /**
     * Constants
     */
    public static final int TYPE_ASCII   = 0x00;
    public static final int TYPE_BINARY  = 0x01;
    public static final int TYPE_XML     = 0x02;
    public static final int TYPE_XMLBIN  = 0x03;
    public static final int TYPE_UNKNOWN = 0xFF;

    public static final int COMPLETED = 0;

    private K id;
    private int type;
    private int version;
    private long timestamp;
    private int totalFragments;
    private int completedFragments;
    private String string;
    private int bufferLength;
    public byte[] bytes;
    private byte[][] fragmentsBuffer;
    private int CRC;

    public MetadataContent(K id, int fragments, int type, long time) {
        this(id, fragments, type, time, 0);
    }

    public MetadataContent(K id, int fragments, int type, long time, int version) {
        this.id = id;
        this.totalFragments = fragments;
        this.type = type;
        this.fragmentsBuffer = new byte[fragments][];
        this.timestamp = time;
        this.version = version;
    }

    public K getId () {
        return id;
    }

    public int getVersion() {
        return version;
    }

    public int getTotalFragments() {
        return totalFragments;
    }

    public int getCompletedFragments() {
        return completedFragments;
    }

    public int getBufferLength() {
        return bufferLength;
    }

    public int getCRC() {
        return CRC;
    }

    public int getType() { return type; }

    public long getTimestamp() {
        return timestamp;
    }

    public String getString() {
        return string;
    }

    public byte[] getBytes() {
        return bytes;
    }

    /**
     * Public methods
     * */
    public int addFragment(byte[] fragmentBuffer, int fragmentOffset, int headerOffset, int fragmentLength, int fragment) {
        return addFragment(fragmentBuffer, fragmentOffset, headerOffset, fragmentLength, fragment, 0);
    }

    public int addFragment(byte[] fragmentBuffer, int fragmentOffset, int headerOffset, int fragmentLength, int fragment, int version) {
        if (version != this.version) {
            // Ha cambiado la version empezamos de nuevo
            this.cleanup();
        }
        
        if (null == fragmentsBuffer[fragment]) {
            fragmentsBuffer[fragment] = new byte[fragmentLength];
            System.arraycopy(fragmentBuffer, fragmentOffset + headerOffset, fragmentsBuffer[fragment], 0, fragmentLength);
            completedFragments++;
            bufferLength += fragmentLength;
        }
        
        if ((completedFragments == totalFragments) && (null != this.fragmentsBuffer)) {
            // tenemos el fichero completo
            int offset = 0;
            bytes = new byte[bufferLength];

            for (byte[] currentBuffer : fragmentsBuffer) {
                System.arraycopy(currentBuffer, 0, bytes, offset, currentBuffer.length);
                offset += currentBuffer.length;
            }
            
            fragmentsBuffer = null;
            
            if (type == TYPE_ASCII || type == TYPE_XML) {
                string = new String(bytes, StandardCharsets.UTF_8);
                bytes = null;
            }

            // TODO: verify CRC
            return COMPLETED;
        }
        
        return completedFragments;
    }
    
    public ByteArrayInputStream getByteArrayInputStream() {
        if (null != string)
            return new ByteArrayInputStream(string.getBytes());
        else
            return null;
    }

    public void cleanup() {
        string = null;
        fragmentsBuffer = null;
        completedFragments = 0;
    }

    public boolean isBufferCompleted() {
        return (completedFragments == totalFragments);
    }
}
