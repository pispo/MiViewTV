package com.movistar.iptv.platform.content.channels;

import java.lang.Exception;

/**
 * Exception thrown for all channels errors.
 */

public class ChannelsException extends Exception {

    private static final long serialVersionUID = 1L;

    public ChannelsException(String msg) {
        super(msg);
    }

    public ChannelsException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
