package com.movistar.iptv.util.metadata;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

/**
 * Created by Sergio Moreno Mozota on 30/11/15.
 *
 * Copyright Telefónica de España SAU 2015
 */

public abstract class MetadataContent<K> {

    public static final int COMPLETED = 0;

    private K id;
    private int type;
    private int version;
    private long timestamp;
    private int totalFragments;
    private int completedFragments;
    private int length;
    public byte[] bytes;
    private byte[][] fragmentsBuffer;
    private int CRC;

    public MetadataContent(K id, int fragments, long time) {
        this(id, fragments, time, 0);
    }

    public MetadataContent(K id, int fragments, long time, int version) {
        this.id = id;
        this.totalFragments = fragments;
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

    public int getLength() {
        return length;
    }

    public int getCRC() {
        return CRC;
    }

    public long getTimestamp() {
        return timestamp;
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
            length += fragmentLength;
        }
        
        if ((completedFragments == totalFragments) && (null != this.fragmentsBuffer)) {
            // tenemos el fichero completo
            int offset = 0;
            bytes = new byte[length];

            for (byte[] currentBuffer : fragmentsBuffer) {
                System.arraycopy(currentBuffer, 0, bytes, offset, currentBuffer.length);
                offset += currentBuffer.length;
            }
            
            fragmentsBuffer = null;
            
            // TODO: verify CRC
            return COMPLETED;
        }
        
        return completedFragments;
    }
    
    public ByteArrayInputStream getByteArrayInputStream() {
        return new ByteArrayInputStream(getBytes());
    }

    public void cleanup() {
        fragmentsBuffer = null;
        completedFragments = 0;
    }

    public boolean isBufferCompleted() {
        return (completedFragments == totalFragments);
    }
}
