package com.movistar.tvservices.opch.infocast;

import java.io.IOException;

/**
 * Exception thrown for all infocast errors.
 */

public class InfocastException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InfocastException(String msg) {
        super(msg);
    }

    public InfocastException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
