package com.jentsch.anemometer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.jentsch.anemometer.SpeedList;


public class SpeedView extends View {

    private Paint paint, tanPaint, textPaint;
    private SpeedList speedList;

    public SpeedView(Context context) {
        super(context);
        init();
    }

    public SpeedView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SpeedView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(4);
        tanPaint = new Paint();
        tanPaint.setColor(Color.BLUE);
        tanPaint.setStrokeWidth(4);
        tanPaint.setAlpha(127);
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(80);
    }

    public void setSpeedList (SpeedList speedList) {
        this.speedList = speedList;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float width = getWidth();
        float height = getHeight();
        if (speedList != null) {
            int speedDataSize = speedList.getSize();
            for (float i = 0; i < speedDataSize; i++) {
                float widthPercent = i / speedDataSize;
                int x = (int) (width * widthPercent);
                double y = speedList.getSpeedValue((int) i, height);
                canvas.drawLine(x, height, x, (float) (height - y), paint);
            }
        }
    }
}
