package com.florianmski.SunPhases.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.florianmski.SunPhases.utils.Constants;
import com.google.android.gms.maps.GoogleMap;

public enum SunPhasesPreferences
{
    INSTANCE;

    private SharedPreferences prefs;

    public void create(Context context)
    {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public SharedPreferences getPrefs()
    {
        return prefs;
    }

    public double getLatitude()
    {
        return getDouble(Constants.PREF_LATITUDE, 0);
    }

    public double getLongitude()
    {
        return getDouble(Constants.PREF_LONGITUDE, 0);
    }

    // Jesse style!
    public boolean doWeHaveALocationBitch()
    {
        return !(getLatitude() == 0 && getLongitude() == 0);
    }

    public int getMapType()
    {
        return getInt(Constants.PREF_MAP_TYPE, GoogleMap.MAP_TYPE_NORMAL);
    }

    public boolean isLocationEnabled()
    {
        return getBoolean(Constants.PREF_LOCATION_ENABLED, true);
    }

    public void putLatitude(double latitude)
    {
        putDouble(Constants.PREF_LATITUDE, latitude);
    }

    public void putLongitude(double longitude)
    {
        putDouble(Constants.PREF_LONGITUDE, longitude);
    }

    public void putMapType(int mapType)
    {
        putInt(Constants.PREF_MAP_TYPE, mapType);
    }

    public void putLocationEnabled(boolean locationEnabled)
    {
        putBoolean(Constants.PREF_LOCATION_ENABLED, locationEnabled);
    }

    private boolean getBoolean(String key, boolean defValue)
    {
        return prefs.getBoolean(key, defValue);
    }

    private int getInt(String key, int defValue)
    {
        return prefs.getInt(key, defValue);
    }

    private long getLong(String key, long defValue)
    {
        return prefs.getLong(key, defValue);
    }

    private double getDouble(String key, double defValue)
    {
        return Double.longBitsToDouble(getLong(key, Double.doubleToLongBits(defValue)));
    }

    private double getDouble(String key, long defValue)
    {
        return Double.longBitsToDouble(getLong(key, defValue));
    }

    private void putBoolean(String key, boolean value)
    {
        prefs.edit().putBoolean(key, value).commit();
    }

    private void putInt(String key, int value)
    {
        prefs.edit().putInt(key, value).commit();
    }

    private void putLong(String key, long value)
    {
        prefs.edit().putLong(key, value).commit();
    }

    private void putDouble(String key, double value)
    {
        putLong(key, Double.doubleToLongBits(value));
    }
}

