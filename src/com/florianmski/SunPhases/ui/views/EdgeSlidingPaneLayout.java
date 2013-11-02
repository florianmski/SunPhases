package com.florianmski.SunPhases.ui.views;

import android.content.Context;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

// This class allow a fragment to have a "touch content" (in this case case, a map view) without disturbing the
// SlidingPaneLayout mechanism (user can slide the panel by the edge)
public class EdgeSlidingPaneLayout extends SlidingPaneLayout
{
    public EdgeSlidingPaneLayout(Context context)
    {
        super(context);
    }

    public EdgeSlidingPaneLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public EdgeSlidingPaneLayout(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event)
    {
        if(!isOpen() && event.getX() > (getWidth() / 5))
            return false;

        // seems like doing a pinch to zoom on the map throw an exception here
        // not much time to investigate so use this little hack for now
        try
        {
            return super.onInterceptTouchEvent(event);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        // seems like doing a pinch to zoom on the map throw an exception here
        // not much time to investigate so use this little hack for now
        try
        {
            return super.onTouchEvent(event);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            return false;
        }
    }
}
