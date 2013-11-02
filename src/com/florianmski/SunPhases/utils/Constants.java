package com.florianmski.SunPhases.utils;

import android.content.Context;

public class Constants
{
    private static String packageName;

    public static final String PREF_LATITUDE = get("Latitude");
    public static final String PREF_LONGITUDE = get("Longitude");
    public static final String PREF_LOCATION_ENABLED = get("LocationEnabled");
    public static final String PREF_MAP_TYPE = get("MapType");

    public static void create(Context context)
    {
        packageName = context.getPackageName();
    }

    private static String get(String id)
    {
        return String.format(packageName + ".%s", id);
    }
}
