package com.florianmski.SunPhases.utils;

import com.florianmski.suncalc.SunCalc;
import com.florianmski.suncalc.models.SunPhase;
import com.florianmski.suncalc.models.SunPosition;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public enum SunPhaseManager
{
    INSTANCE(Calendar.getInstance(), SunPhasesPreferences.INSTANCE.getLatitude(), SunPhasesPreferences.INSTANCE.getLongitude());

    private Calendar date;
    private double latitude = 0;
    private double longitude = 0;

    private List<SunPhase> sunPhases;
    private final List<OnTimeChangedListener> timeListeners = new ArrayList<OnTimeChangedListener>();
    private final List<OnLocationChangedListener> locationListeners = new ArrayList<OnLocationChangedListener>();
    private final SunPhasesPreferences prefs = SunPhasesPreferences.INSTANCE;

    SunPhaseManager(Calendar date, double latitude, double longitude)
    {
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;

        generateSunPhases();
    }

    private void generateSunPhases()
    {
        sunPhases = SunCalc.getPhases(date, latitude, longitude);
    }

    public SunPhase getCurrentPhase()
    {
        return generateCurrentPhase();
    }

    public SunPosition getCurrentPosition()
    {
        return SunCalc.getSunPosition(date, latitude, longitude);
    }

    public SunPosition getSunPhasePosition(SunPhase.Name name, boolean positionAtTheEnd)
    {
        SunPhase sp = getPhase(name);
        return SunCalc.getSunPosition(positionAtTheEnd ? sp.getEndDate() : sp.getStartDate(), latitude, longitude);
    }

    public List<SunPhase> getSunPhases()
    {
        return sunPhases;
    }

    public SunPhase getPhase(SunPhase.Name name)
    {
        for(SunPhase sp : sunPhases)
        {
            if(sp.getName().equals(name))
                return sp;
        }

        throw new UnsupportedOperationException("Impossible to find " + name.toString());
    }

    public double getLatitude()
    {
        return latitude;
    }

    public double getLongitude()
    {
        return longitude;
    }

    public LatLng getLatLng()
    {
        return new LatLng(latitude, longitude);
    }

    public Calendar getDate()
    {
        return (Calendar) date.clone();
    }

    public void setCoordinates(double latitude, double longitude)
    {
        this.latitude = latitude;
        this.longitude = longitude;
        prefs.putLatitude(latitude);
        prefs.putLongitude(longitude);
        generateSunPhases();
        dispatchOnLocationChangedEvent(latitude, longitude);
    }

    public void setCoordinates(LatLng latLng)
    {
        setCoordinates(latLng.latitude, latLng.longitude);
    }

    public void setDate(Calendar c)
    {
        boolean sameDay;
        // if we are the same day, no need to recalculate the phases
        if(c.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR) && c.get(Calendar.YEAR) == date.get(Calendar.YEAR))
        {
            sameDay = true;
            this.date = c;
        }
        else
        {
            sameDay = false;
            this.date = c;
            generateSunPhases();
        }

        dispatchOnTimeChangedEvent(c, sameDay);
    }

    private SunPhase generateCurrentPhase()
    {
        for(SunPhase sp : sunPhases)
        {
            if(date.compareTo(sp.getStartDate()) >= 0 && date.before(sp.getEndDate()))
                return sp;
        }

        // if we're here it probably means we're not the same day anymore
        // for now throw an exception

        throw new UnsupportedOperationException("TO FIX");
    }

    public void addOnTimeChangedListener(OnTimeChangedListener listener)
    {
        timeListeners.add(listener);
    }

    public void removeOnTimeChangedListener(OnTimeChangedListener listener)
    {
        timeListeners.remove(listener);
    }

    private void dispatchOnTimeChangedEvent(Calendar c, boolean sameDay)
    {
        for(OnTimeChangedListener l : timeListeners)
            l.onTimeChanged(c, sameDay);
    }

    public interface OnTimeChangedListener
    {
        public void onTimeChanged(Calendar c, boolean sameDay);
    }

    public void addOnLocationChangedListener(OnLocationChangedListener listener)
    {
        locationListeners.add(listener);
    }

    public void removeOnLocationChangedListener(OnLocationChangedListener listener)
    {
        locationListeners.remove(listener);
    }

    private void dispatchOnLocationChangedEvent(double latitude, double longitude)
    {
        for(OnLocationChangedListener l : locationListeners)
            l.onLocationChanged(latitude, longitude);
    }

    public interface OnLocationChangedListener
    {
        public void onLocationChanged(double latitude, double longitude);
    }

}
