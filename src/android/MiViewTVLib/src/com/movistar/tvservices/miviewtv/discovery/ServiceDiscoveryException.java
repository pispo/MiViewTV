package com.movistar.tvservices.miviewtv.discovery;

import java.io.IOException;

/**
 * Exception thrown for all dvbstp errors.
 */

public class ServiceDiscoveryException extends IOException {

    private static final long serialVersionUID = 1L;

    public ServiceDiscoveryException(String msg) {
        super(msg);
    }

    public ServiceDiscoveryException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
