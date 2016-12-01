package com.movistar.tvservices.miviewtv.discovery.dvbipi.dvbstp;

import java.lang.Exception;

/**
 * Exception thrown for all dvbstp errors.
 */

public class DvbStpTimeoutException extends Exception {

    private static final long serialVersionUID = 1L;

    public DvbStpTimeoutException() {
        super();
    }

    public DvbStpTimeoutException(String msg) {
        super(msg);
    }
}
