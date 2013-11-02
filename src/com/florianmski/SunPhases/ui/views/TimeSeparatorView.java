package com.florianmski.SunPhases.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import com.florianmski.SunPhases.R;

import java.util.Calendar;

public class TimeSeparatorView extends View
{
    private Paint mTextPaint;
    private Paint mLinePaint;
    private String mText;
    private int mAscent;
    private boolean top;
    private final static int PADDING = 20;

    public TimeSeparatorView(Context context)
    {
        super(context);
        init();
    }

    public TimeSeparatorView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    // draws one half of the text displaying time
    // this is a little trick in order to have the text between two list rows
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        float y;

        if(top)
            y = -mAscent;
        else
            y = -mAscent + getHeight();

        float textSize = mTextPaint.measureText(mText);
        float bottomHeight = getHeight()-1;
        float topHeight = 0;

        canvas.drawText(mText, getWidth() - 3*PADDING - textSize, y, mTextPaint);
        canvas.drawLine(PADDING, top ? topHeight : bottomHeight, getWidth() - 4*PADDING - textSize, top ? topHeight : bottomHeight, mLinePaint);
        canvas.drawLine(getWidth() - 2*PADDING, top ? topHeight : bottomHeight, getWidth() - PADDING, top ? topHeight : bottomHeight, mLinePaint);
    }

    private void init()
    {
        int colorBlack = getResources().getColor(R.color.black_transparent);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(16);
        mTextPaint.setColor(colorBlack);
        mTextPaint.setStyle(Paint.Style.FILL);

        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setColor(colorBlack);
        mLinePaint.setStyle(Paint.Style.FILL);

        setTextSize(50);

        // TODO an attr will be more clean
        top = getId() == R.id.timeSeparatorViewTop;
    }

    public void setTime(Calendar d)
    {
        if(d.getTimeInMillis() > 0)
            setText(String.format("%02d:%02d", d.get(Calendar.HOUR_OF_DAY), d.get(Calendar.MINUTE)));
        else
            setText("n/a");
    }

    public void setText(String text)
    {
        mText = text;
        requestLayout();
        invalidate();
    }

    public void setTextSize(int size)
    {
        mTextPaint.setTextSize(size);
        requestLayout();
        invalidate();
    }

    public void setTextColor(int color)
    {
        mTextPaint.setColor(color);
        requestLayout();
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureWidth(int measureSpec)
    {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY)
        {
            // We were told how big to be
            result = specSize;
        }
        else
        {
            // Measure the tv
            result = (int) mTextPaint.measureText(mText) + getPaddingLeft() + getPaddingRight();
            if (specMode == MeasureSpec.AT_MOST)
            {
                // Respect AT_MOST value if that was what is called for by measureSpec
                result = Math.min(result, specSize);
            }
        }

        return result;
    }

    private int measureHeight(int measureSpec)
    {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        mAscent = (int) (mTextPaint.ascent()/3f);
        if (specMode == MeasureSpec.EXACTLY)
        {
            // We were told how big to be
            result = specSize;
        }
        else
        {
            // Measure the tv (beware: ascent is a negative number)
            result = (int) (-mAscent + mTextPaint.descent()) + getPaddingTop() + getPaddingBottom();
            if (specMode == MeasureSpec.AT_MOST)
            {
                // Respect AT_MOST value if that was what is called for by measureSpec
                result = Math.min(result, specSize);
            }
        }
        return result;
    }
}