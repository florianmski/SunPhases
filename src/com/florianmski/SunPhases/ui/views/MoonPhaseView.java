package com.florianmski.SunPhases.ui.views;


import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import com.florianmski.SunPhases.R;

// Test class
public class MoonPhaseView extends View
{
    private double fraction = -1;
    private double realFraction = 0;

    private final Paint moonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint clearPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public MoonPhaseView(Context context)
    {
        super(context);
        init();
    }

    public MoonPhaseView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public MoonPhaseView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    private void init()
    {
        int black = getResources().getColor(R.color.black_transparent);
        int white = getResources().getColor(R.color.white_transparent);

        moonPaint.setStyle(Paint.Style.STROKE);
        moonPaint.setStrokeWidth(3);
        moonPaint.setColor(black);

        shadowPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        shadowPaint.setColor(black);
        shadowPaint.setTextSize(50);

        clearPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        clearPaint.setColor(Color.WHITE);

        setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
//                realFraction = (realFraction + 0.05)%2;
//                fraction = realFraction > 1 ? 2 - realFraction : realFraction;
                fraction += 0.1;
                if(fraction > 1)
                    fraction = -1;
                invalidate();
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        if(fraction >= -1.0 && fraction <= 1.0)
        {
            float left = 0;
            float top = 0;
            float right = getMeasuredWidth() > getMeasuredHeight() ? getMeasuredHeight() : getMeasuredWidth();
            float bottom = right;

//            float moonRatio = (float) (Math.abs(fraction - 1.0));

            float ovalRight = (float) (fraction*right);
            float ovalLeft = right - ovalRight;

            RectF r;
            if(fraction < 0)
            {
                ovalRight = (float) ((1+fraction)*right);
                ovalLeft = right - ovalRight;

                if(fraction > -0.5)
                {
                    r = new RectF(left, top, right, bottom);
                    canvas.drawArc(r, 270, 180, true, shadowPaint);
                    r = new RectF(ovalLeft, top, ovalRight, bottom);
                    canvas.drawArc(r, 90, 180, true, shadowPaint);
                }
                else
                {
                    canvas.drawCircle((right + left) / 2, (top + bottom) / 2, (right + left) / 2, shadowPaint);
                    r = new RectF(left, top, right, bottom);
                    canvas.drawArc(r, 90, 180, true, clearPaint);
                    r = new RectF(ovalRight, top, ovalLeft, bottom);
                    canvas.drawArc(r, 270, 180, true, clearPaint);
                }
            }
            else
            {
                if(fraction > 0.5)
                {
                    r = new RectF(left, top, right, bottom);
                    canvas.drawArc(r, 90, 180, true, shadowPaint);
                    r = new RectF(ovalLeft, top, ovalRight, bottom);
                    canvas.drawArc(r, 270, 180, true, shadowPaint);
                }
                else
                {
                    canvas.drawCircle((right + left) / 2, (top + bottom) / 2, (right + left) / 2, shadowPaint);
                    r = new RectF(left, top, right, bottom);
                    canvas.drawArc(r, 270, 180, true, clearPaint);
                    r = new RectF(ovalRight, top, ovalLeft, bottom);
                    canvas.drawArc(r, 90, 180, true, clearPaint);
                }
            }

            //draw outline
            canvas.drawCircle((right + left) / 2, (top + bottom) / 2, (right + left) / 2, moonPaint);

            canvas.drawText(String.format("%.1f", fraction), right/2, getMeasuredHeight(), shadowPaint);
        }
    }

    public void updateFraction(double fraction)
    {
        this.fraction = fraction;
        invalidate();
    }

}