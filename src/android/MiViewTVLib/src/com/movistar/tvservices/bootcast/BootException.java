package com.movistar.tvservices.bootcast;

import java.io.IOException;

/**
 * Exception thrown for all bootconfig parsing errors.
 */

public class BootException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public BootException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
