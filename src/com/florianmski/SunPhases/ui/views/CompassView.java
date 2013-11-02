package com.florianmski.SunPhases.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

// Test view, not used in production
public class CompassView extends View
{
    private float north = 0;
    private float position = 0;
    private float sun = 0;
    private float diff = 0;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private boolean firstDraw;

    public CompassView(Context context)
    {
        super(context);
        init();
    }

    public CompassView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public CompassView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    private void init()
    {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setColor(Color.BLACK);
        paint.setTextSize(30);

        firstDraw = true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        int cxCompass = getMeasuredWidth()/2;
        int cyCompass = getMeasuredHeight()/2;
        float radiusCompass;

        if(cxCompass > cyCompass)
            radiusCompass = (float) (cyCompass * 0.9);
        else
            radiusCompass = (float) (cxCompass * 0.9);

        canvas.drawCircle(cxCompass, cyCompass, radiusCompass, paint);
        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), paint);

        if(!firstDraw)
        {
            paint.setColor(Color.RED);

            canvas.drawLine(cxCompass, cyCompass,
                    (float)(cxCompass + radiusCompass * Math.sin(north)),
                    (float)(cyCompass - radiusCompass * Math.cos(north)),
                    paint);

            paint.setColor(Color.GREEN);

            canvas.drawLine(cxCompass, cyCompass,
                    (float)(cxCompass + radiusCompass * Math.sin(position)),
                    (float)(cyCompass - radiusCompass * Math.cos(position)),
                    paint);

            paint.setColor(Color.YELLOW);

            canvas.drawLine(cxCompass, cyCompass,
                    (float)(cxCompass + radiusCompass * Math.sin(sun)),
                    (float)(cyCompass - radiusCompass * Math.cos(sun)),
                    paint);

            paint.setColor(Color.BLUE);

            canvas.drawLine(cxCompass, cyCompass,
                    (float)(cxCompass + radiusCompass * Math.sin(diff)),
                    (float)(cyCompass - radiusCompass * Math.cos(diff)),
                    paint);

//            canvas.drawText(String.valueOf(direction), cxCompass, cyCompass, paint);
        }

    }

    public void updateDirection(float north, float position, float sun, float diff)
    {
        firstDraw = false;
        this.north = north;
        this.position = position;
        this.diff = diff;
        this.sun = sun;
        invalidate();
    }

}