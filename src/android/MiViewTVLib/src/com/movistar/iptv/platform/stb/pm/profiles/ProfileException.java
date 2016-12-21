package com.movistar.iptv.platform.stb.pm.profiles;

import java.lang.Exception;

/**
 * Exception thrown for all bootconfig parsing errors.
 */

public class ProfileException extends Exception {

    private static final long serialVersionUID = 1L;

    public ProfileException(String msg) {
        super(msg);
    }

    public ProfileException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
