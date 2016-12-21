package com.movistar.iptv.platform.stb.pm.time;

import android.os.SystemClock;

import java.lang.NumberFormatException;
import java.lang.NullPointerException;

import com.movistar.iptv.platform.stb.pm.bootup.BootProperties;

public class UTCTimeManager {

    private static long seed = 0;
    private static long firstElapsedTime = 0;

    private static boolean loaded = false;

    public static void loadSeedTime() throws UTCTimeException {

        if (loaded == false) {

            try {
                seed = Long.parseLong(BootProperties.getPropertyValue(BootProperties.VAR_UTCTIME_ID));

            } catch (NumberFormatException e) {
                throw new UTCTimeException("UTC time seed with number format wrong");

            } catch (NullPointerException e) {
                throw new UTCTimeException("UTC time seed not available");
            }

            firstElapsedTime = SystemClock.elapsedRealtime() / 1000;

            loaded = true;
        }
    }

    public static long getCurrentTime() throws UTCTimeException {
        if (loaded)
            return seed + (SystemClock.elapsedRealtime() / 1000) - firstElapsedTime;

        throw new UTCTimeException("UTC time not available");
    }
}
