package com.florianmski.SunPhases.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.widget.Button;

// Not used currently, maybe later?
public class ArrowView extends Button
{
    private final static int PADDING = 20;
    private final static float TIGHTNESS = 1/3f;

    private float degrees = 0;
    private Paint paint;
    private Path path;

    public ArrowView(Context context)
    {
        super(context);

        init();
    }

    public ArrowView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        init();
    }

    public ArrowView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        init();
    }

    private void init()
    {
        setPadding(PADDING, PADDING, PADDING, PADDING);

        paint = new Paint();
        paint.setColor(0x88000000);

        path = new Path();
    }

    @Override
    public void draw(Canvas canvas)
    {
        super.draw(canvas);

        int height = getHeight();
        int width = getWidth();

        float top = getPaddingTop();
        float bottom = height - getPaddingBottom();
        float left = getPaddingLeft();
        float right = width - getPaddingRight();

        path.reset();
        path.moveTo((right + left)/2.f, top);
        path.lineTo(left + TIGHTNESS*left, bottom);
        path.lineTo((top + bottom)/2.f,(right + left)/1.5f);
        path.lineTo(right - TIGHTNESS*left, bottom);
        path.close();

        canvas.rotate(-degrees, (top + bottom)/2.f, (right + left)/2.f);
        canvas.drawPath(path, paint);
    }

    public void rotate(float rad)
    {
        this.degrees = (float) (rad * (180.f/ Math.PI));
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        setMeasuredDimension(heightMeasureSpec, heightMeasureSpec);
    }
}
