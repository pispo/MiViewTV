package com.movistar.miviewtv.core;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

/**
 * Created by Sergio Moreno Mozota on 30/11/15.
 *
 * Copyright Telefónica de España SAU 2015
 */

public class MovistarFile {

    /**
     * Constants
     */
    public final static int TYPE_ASCII   = 0x00;
    public final static int TYPE_BINARY  = 0x01;
    public final static int TYPE_XML     = 0x02;
    public final static int TYPE_XMLBIN  = 0x03;
    public final static int TYPE_UNKNOWN = 0xFF;
    public static final int FILE_COMPLETE = 0;

    /**
     * Variables
     */
    public long timestamp;
    public String name;
    public int version;
    public int type;
    public int totalFragments;
    public int completedFragments = 0;
    public byte[] bytes;
    public String string;
    public int bufferLength = 0;
    private byte[][] fragmentsBuffer;
    public int CRC;

    /**
     * Constructors
     */
    public MovistarFile() {
    }
    
    public MovistarFile(String filename, int fragments, int filetype, long time) {
        this(filename, fragments, filetype, time, 0);
    }

    public MovistarFile(String filename, int fragments, int filetype, long time, int version) {
        this.timestamp = time;
        this.name = filename;
        this.totalFragments = fragments;
        this.fragmentsBuffer = new byte[fragments][];
        this.type = filetype;
        this.version = version;
    }

    /**
     * Public methods
     * */
    public int addFragment (byte[] fragmentBuffer, int fragmentOffset, int headerOffset, int fragmentLength, int fragment) {
        return addFragment(fragmentBuffer, fragmentOffset, headerOffset, fragmentLength, fragment, 0);
    }

    public int addFragment (byte[] fragmentBuffer, int fragmentOffset, int headerOffset, int fragmentLength, int fragment, int version) {
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
            return FILE_COMPLETE;
        }
        
        return completedFragments;
    }

    public String getName () {
        if (null != name) {
            return name;
        }
        else {
            return null;
        }
    }
    
    public byte[] getBytes() {
        if (null != bytes) {
            return bytes;
        }
        else {
            return null;
        }
    }

    public String getString() {
        if (null != string) {
            return string;
        }
        else {
            return null;
        }
    }

    public ByteArrayInputStream getByteArrayInputStream(){
        if (null != string) {
            return new ByteArrayInputStream(string.getBytes());
        }
        else {
            return null;
        }
    }

    public void cleanup() {
        bytes = null;
        string = null;
        fragmentsBuffer = null;
        completedFragments = 0;
    }

    public Boolean bufferComplete() {
        return (completedFragments == totalFragments);
    }
}
