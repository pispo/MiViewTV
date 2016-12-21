package com.movistar.iptv.platform.stb.pm.bootup.infocast;

import java.lang.Exception;

/**
 * Exception thrown for all infocast errors.
 */

public class InfocastException extends Exception {

    private static final long serialVersionUID = 1L;

    public InfocastException(String msg) {
        super(msg);
    }

    public InfocastException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
