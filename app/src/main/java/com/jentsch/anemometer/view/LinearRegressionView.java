package com.jentsch.anemometer.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.jentsch.anemometer.LinearRegression;
import com.jentsch.anemometer.R;

import java.util.ArrayList;
import java.util.List;

public class LinearRegressionView extends View {

    List<Double> speeds;
    List<Double> frequencies;

    Resources res;

    double maxSpeed = 0;
    double maxFrequency = 0;

    private Paint dotPaint;
    private Paint linearRegressionPaint;
    private Paint textPaint;
    private float scaledSizeInPixels;

    private float radius;
    private LinearRegression lr = null;
    private double calibrationValue = 0;

    public LinearRegressionView(Context context) {
        super(context);
        init();
    }

    public LinearRegressionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LinearRegressionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        speeds = new ArrayList<>();
        frequencies = new ArrayList<>();
        dotPaint = new Paint();
        dotPaint.setStyle(Paint.Style.FILL);
        int color = ContextCompat.getColor(getContext(), R.color.colorPrimaryDark);
        dotPaint.setColor(color);
        dotPaint.setAntiAlias(true);

        linearRegressionPaint = new Paint();
        linearRegressionPaint.setStyle(Paint.Style.FILL);
        linearRegressionPaint.setStrokeWidth(10);
        color = ContextCompat.getColor(getContext(), R.color.textColor);
        linearRegressionPaint.setColor(color);
        linearRegressionPaint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        int spSize = 18;
        scaledSizeInPixels = spSize * getResources().getDisplayMetrics().scaledDensity;
        textPaint.setTextSize(scaledSizeInPixels);

        res = getResources();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        radius = Math.min(getWidth(), getHeight()) / 100;
        super.onDraw(canvas);
        if (lr != null) {
            float startY = (float) (getHeight() / maxFrequency  * lr.predict(0));
            float stopY = (float) (getHeight() / maxFrequency  * lr.predict(maxSpeed));
            canvas.drawLine(0, getHeight() - startY, getWidth(), getHeight() - stopY, linearRegressionPaint);
            calibrationValue = maxSpeed / lr.predict(maxSpeed);
            String interceptStr = res.getString(R.string.intercept, lr.intercept());
            String slopeStr = res.getString(R.string.slope, lr.slope());
            float x = scaledSizeInPixels;
            float y = scaledSizeInPixels;
            canvas.drawText(interceptStr, x, y, textPaint);
            y = scaledSizeInPixels * 3f;
            canvas.drawText(slopeStr, x, y, textPaint);
        }
        for (int i = 0; i < speeds.size(); i++) {
            double speed = speeds.get(i);
            double frequency = frequencies.get(i);
            float cx = (float)(getWidth() / maxSpeed * speed);
            float cy = (float)(getHeight() / maxFrequency * frequency);
            canvas.drawCircle(cx, getHeight() - cy, radius, dotPaint);
        }
    }

    public void addElement(double speed, double frequency) {
        if (speed > maxSpeed) maxSpeed = speed;
        if (frequency > maxFrequency) maxFrequency = frequency;
        speeds.add(speed);
        frequencies.add(frequency);
        Double[] speedArray = new Double[speeds.size()];
        speedArray = speeds.toArray(speedArray);

        Double[] frequencyArray = new Double[frequencies.size()];
        frequencyArray = frequencies.toArray(frequencyArray);

        lr = new LinearRegression(speedArray, frequencyArray);
        invalidate();
    }

    public double getCalibrationValue() {
        return calibrationValue;
    }
}
