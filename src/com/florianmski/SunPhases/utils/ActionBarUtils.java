package com.florianmski.SunPhases.utils;

import android.app.ActionBar;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Handler;
import com.florianmski.SunPhases.R;

public class ActionBarUtils
{
    private static Drawable oldBackground = null;

    // borrowed there https://github.com/astuetz/PagerSlidingTabStrip/blob/master/sample/src/com/astuetz/viewpager/extensions/sample/MainActivity.java
    public static void setColor(int newColor, final ActionBar actionBar)
    {
        final Handler handler = new Handler();
        Drawable.Callback drawableCallback = new Drawable.Callback()
        {
            @Override
            public void invalidateDrawable(Drawable who)
            {
                actionBar.setBackgroundDrawable(who);
            }

            @Override
            public void scheduleDrawable(Drawable who, Runnable what, long when)
            {
                handler.postAtTime(what, when);
            }

            @Override
            public void unscheduleDrawable(Drawable who, Runnable what)
            {
                handler.removeCallbacks(what);
            }
        };

        Drawable colorDrawable = new ColorDrawable(newColor);
        Drawable bottomDrawable = actionBar.getThemedContext().getResources().getDrawable(R.drawable.actionbar_bottom);
        LayerDrawable ld = new LayerDrawable(new Drawable[] { colorDrawable, bottomDrawable });

        if(oldBackground == null)
        {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1)
                ld.setCallback(drawableCallback);
            else
                actionBar.setBackgroundDrawable(ld);
        }
        else
        {
            TransitionDrawable td = new TransitionDrawable(new Drawable[] { oldBackground, ld });

            // workaround for broken ActionBarContainer drawable handling on
            // pre-API 17 builds
            // https://github.com/android/platform_frameworks_base/commit/a7cc06d82e45918c37429a59b14545c6a57db4e4
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1)
                td.setCallback(drawableCallback);
            else
                actionBar.setBackgroundDrawable(td);

            td.startTransition(200);
        }

        oldBackground = ld;
    }
}
