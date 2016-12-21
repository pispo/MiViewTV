package com.movistar.iptv.platform.stb.pm.bootup;

import java.lang.Exception;

/**
 * Exception thrown for all bootconfig parsing errors.
 */

public class BootException extends Exception {

    private static final long serialVersionUID = 1L;

    public BootException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
