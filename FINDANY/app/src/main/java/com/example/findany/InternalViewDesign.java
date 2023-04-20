package com.example.findany;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class InternalViewDesign extends View {
    private Paint mPaint;

    public InternalViewDesign(Context context) {
        super(context);

    }

    public InternalViewDesign(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InternalViewDesign(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint = new Paint();
        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.FILL);

        canvas.drawRect(100, 100, 300, 300, mPaint);
        canvas.drawCircle(600, 200, 100, mPaint);

    }

}


