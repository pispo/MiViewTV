package com.movistar.iptv.platform.stb.sds.parser;

import java.lang.Exception;

/**
 * Exception thrown for all dvbstp errors.
 */

public class DiscoveryParserException extends Exception {

    private static final long serialVersionUID = 1L;

    public DiscoveryParserException(String msg) {
        super(msg);
    }

    public DiscoveryParserException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
