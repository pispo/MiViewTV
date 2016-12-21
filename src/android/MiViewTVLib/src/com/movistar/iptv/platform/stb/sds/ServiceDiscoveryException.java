package com.movistar.iptv.platform.stb.sds;

import java.lang.Exception;

/**
 * Exception thrown for all dvbstp errors.
 */

public class ServiceDiscoveryException extends Exception {

    private static final long serialVersionUID = 1L;

    public ServiceDiscoveryException(String msg) {
        super(msg);
    }

    public ServiceDiscoveryException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
