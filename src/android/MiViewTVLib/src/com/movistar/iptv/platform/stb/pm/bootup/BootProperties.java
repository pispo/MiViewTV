package com.movistar.iptv.platform.stb.pm.bootup;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

public class BootProperties {
    private static final String LOG_TAG = BootProperties.class.getSimpleName();

    public static final String CFG_RESOURCES_URL_ID = "cfg.resources.url";
    public static final String VAR_UTCTIME_ID = "var.utctime";

    private static Map<String, String> properties = new LinkedHashMap<String, String>();

    static {
        properties.put (CFG_RESOURCES_URL_ID, null);
        properties.put (VAR_UTCTIME_ID, null);
    }

    public static List<String> getPropertiesName () {
        return Arrays.asList(properties.keySet().toArray(new String[0]));
    }

    public static void setPropertyValue(String name, String value) {
        if (properties.containsKey(name)) {
            properties.put(name, value);
        }
    }

    public static String getPropertyValue(String name) {
        return properties.get(name);
    }
}
