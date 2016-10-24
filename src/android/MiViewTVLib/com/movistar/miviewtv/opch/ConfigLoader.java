package com.movistar.miviewtv.opch;

import com.movistar.miviewtv.core.MovistarFile;

import android.util.Log;

import java.io.IOException;
import java.util.HashMap;

public class ConfigLoader {

/**
 * Constants:
 */
    public static final String LOG_TAG = Opch.class.getSimpleName();

    private static final String defaultAddress = "239.0.2.10";
    private static final int defaultPort = 22222;
    
    public static final int OK                   =  0;
    public static final int ARGUMENTS_ERROR      = -1;
    public static final int DATA_ERROR           = -2;
    public static final int DATA_NOT_FOUND_ERROR = -3;
    public static final int CONNECTION_ERROR     = -4;
    
/**
 * Variables
 */
    String opchAddress;
    int opchPort;
    HashMap<String, MovistarFile> infocastFiles = null;


/**
 * Constructors
 */
    public ConfigLoader() {
        this(defaultOpchAddress, defaultOpchPort);
    }

    public ConfigLoader(String address, int port) {
        this.opchAddress = address;
        this.opchPort = port;
        this.infocastFiles = new HashMap<>();
    }

/**
 * Methods
 */
//    @Nullable
    public int updateOpchData() {
        Infocast infocastInfo = null;
        try {
            infocastInfo = new Infocast(this.opchAddress, this.opchPort);
        } catch (IOException e) {
            e.printStackTrace();
            return CONNECTION_ERROR;
        }

        HashMap<String, MovistarFile> opchFiles = new HashMap<String, MovistarFile>(){
            {
                put("0001_4000_cfg.resources.url", null);
                put("0007_0001_var.utctime", null);
            }
        };

        if (infocastInfo.update(opchFiles) <= 0) {
            Log.d(TAG, "updateOpchData: Error processing Infocast stream. No usable data available.");
            return DATA_ERROR;
        }
        infocastInfo.getFiles(opchFiles);

        infocastFiles.putAll(opchFiles);

        return opchFiles.size() == 2 ? OK : DATA_NOT_FOUND_ERROR;
    }

    public MovistarFile getFile(String fileName) {
        return infocastFiles.get(fileName);
    }
}
