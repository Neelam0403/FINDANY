package com.example.findany;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class InternalViewDesign extends View {
    private Paint paint;

    public InternalViewDesign(Context context) {
        super(context);

    }

    public InternalViewDesign(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InternalViewDesign(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    private void initPaints() {
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBuildings1(canvas);
        drawBuildings2(canvas);
        


    }
    private void drawBuildings1(Canvas canvas) {
        initPaints();
        // Example buildings, these are
        canvas.drawRect(50, 200, 150, 400, paint);

    }
    private void drawBuildings2(Canvas canvas) {
        initPaints();
        // Example buildings, these are
        canvas.drawRect(200, 100, 300, 400, paint);
    }
    

}


