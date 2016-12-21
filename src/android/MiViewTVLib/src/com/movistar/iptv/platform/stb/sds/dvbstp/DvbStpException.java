package com.movistar.iptv.platform.stb.sds.dvbstp;

import java.lang.Exception;

/**
 * Exception thrown for all dvbstp errors.
 */

public class DvbStpException extends Exception {

    private static final long serialVersionUID = 1L;

    public DvbStpException(String msg) {
        super(msg);
    }

    public DvbStpException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
