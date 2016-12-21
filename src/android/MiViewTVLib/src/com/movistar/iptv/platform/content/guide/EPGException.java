package com.movistar.iptv.platform.content.guide;

import java.lang.Exception;

/**
 * Exception thrown for all epg errors.
 */

public class EPGException extends Exception {

    private static final long serialVersionUID = 1L;

    public EPGException(String msg) {
        super(msg);
    }

    public EPGException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
