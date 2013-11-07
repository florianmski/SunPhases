package com.florianmski.SunPhases;

import android.app.Application;
import com.crashlytics.android.Crashlytics;
import com.florianmski.SunPhases.utils.Constants;
import com.florianmski.SunPhases.utils.SensorManager;
import com.florianmski.SunPhases.utils.SunPhasesPreferences;

public class SunPhasesApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();

        Constants.create(getApplicationContext());
        SensorManager.INSTANCE.create(getApplicationContext());
        SunPhasesPreferences.INSTANCE.create(getApplicationContext());
        Crashlytics.start(getApplicationContext());
    }
}
