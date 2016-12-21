package com.movistar.iptv.platform.stb.pm.time;

import java.lang.Exception;

/**
 * Exception thrown for UTC time handling.
 */

public class UTCTimeException extends Exception {

    private static final long serialVersionUID = 1L;

    public UTCTimeException(String msg) {
        super(msg);
    }

    public UTCTimeException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
