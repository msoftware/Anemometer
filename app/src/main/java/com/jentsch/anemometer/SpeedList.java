package com.jentsch.anemometer;

public class SpeedList {

    int pos = 0;
    int size;

    private double[] speedData;
    private double minValue;
    private double maxValue;

    public SpeedList(int size)
    {
        this.size = size;
        speedData = new double[size];
    }

    public synchronized void addValue (double value) {
        if (value < minValue) minValue = value;
        if (value > maxValue) maxValue = value;
        speedData[pos%size] = value;
        pos++;
    }

    public double getSpeedValue(int i, float height) {
        double ret = speedData[getArrayPos(i)];
        ret = ret - minValue;
        ret = ret / (maxValue - minValue);
        ret = ret * height;
        return ret;
    }

    private int getArrayPos(int i) {
        return (size + pos + i) % size;
    }

    public int getSize() {
        return size;
    }
}
