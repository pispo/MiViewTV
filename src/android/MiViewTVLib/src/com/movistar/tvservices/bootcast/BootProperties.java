package com.movistar.tvservices.bootcast;

import java.util.HashMap;

public class BootProperties {
    private static final String LOG_TAG = BootProperties.class.getSimpleName();

    private static final String CFG_RESOURCES_URL_ID = "cfg.resources.url";
    private static final String VAR_UTCTIME_ID = "var.utctime";

    private static Map<String, Object> properties = new HashMap<String, Object>();

    static {
        properties.add (CFG_RESOURCES_URL_ID, null);
        properties.add (VAR_UTCTIME_ID, null);
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
        return properties.get(name)
    }
}
