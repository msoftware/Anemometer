package com.jentsch.anemometer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.jentsch.anemometer.SensorDataList;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class GraphView extends View {

    private Paint paint, tanPaint, textPaint;
    private SensorDataList sensorDataList;
    private NumberFormat formatter;

    public GraphView(Context context) {
        super(context);
        init();
    }

    public GraphView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GraphView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
        formatter = new DecimalFormat("#0.00");
    }

    public void setSensorDataList (SensorDataList sensorDataList) {
        this.sensorDataList = sensorDataList;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float width = getWidth();
        float height = getHeight();
        if (sensorDataList != null) {
            int sensorDataSize = sensorDataList.getSize();

            int lastX = 0;
            float lastY = Float.NEGATIVE_INFINITY;
            for (float i = 0; i < sensorDataSize; i++) {
                float widthPercent = i / sensorDataSize;
                int x = (int) (width * widthPercent);
                float y = (float)sensorDataList.getSensorValue((int) i, height);
                // canvas.drawLine(x, height, x,  y, paint);
                float ly = lastY;
                if (ly == Float.NEGATIVE_INFINITY) {
                    ly = y;
                }
                canvas.drawLine(lastX, ly, x,  y, paint);
                lastX = x;
                lastY = y;
            }

/*
            for (float i = 0; i < sensorDataSize; i++) {
                float widthPercent = i / sensorDataSize;
                int x = (int) (width * widthPercent);
                double y = sensorDataList.getTanHValue((int) i, 100) * height / 2 + height / 2;
                canvas.drawLine(x, height, x, (float) y, tanPaint);
            }
*/
            String val = formatter.format(sensorDataList.getFrequency()) + " Hz";
            canvas.drawText(val, 100, 100, textPaint);
        }
    }
}
