package com.movistar.tvservices.miviewtv.discovery.dvbipi.dvbstp;

import java.io.IOException;

/**
 * Exception thrown for all dvbstp errors.
 */

public class DvbStpException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DvbStpException(String msg) {
        super(msg);
    }

    public DvbStpException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
