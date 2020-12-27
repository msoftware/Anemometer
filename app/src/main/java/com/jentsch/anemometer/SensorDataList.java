package com.jentsch.anemometer;

public class SensorDataList {

    int pos = 0;
    int size = 0;
    double minValue = -20; /* Use to avoid minimum changes to be identified as frequency */
    double maxValue = 20;  /* Use to avoid minimum changes to be identified as frequency */

    SensorDataElement[] sensorData;

    public SensorDataList(int size)
    {
        this.size = size;
        sensorData = new SensorDataElement[size];
        for (int i = 0; i < size; i++) {
            sensorData[i] = new SensorDataElement(0,0);
        }
    }

    public synchronized void addValue (double value) {
        long now = System.currentTimeMillis();
        if (value < minValue) minValue = value;
        if (value > maxValue) maxValue = value;
        sensorData[pos%size].time = now;
        sensorData[pos%size].value = value;
        pos++;
    }

    public double getSensorValue(int i, float height) {
        double ret = sensorData[getArrayPos(i)].value;
        ret = ret - minValue;
        ret = ret / (maxValue - minValue);
        ret = ret * height;
        return ret;
    }

    public long getSensorTime(int i) {
        return sensorData[getArrayPos(i)].time;
    }

    private int getArrayPos(int i) {
        // size, pos, i
        int arrayPos = (size + pos + i) % size;
        return arrayPos;
    }


    public double getTanHValue(int i, int factor) {
        double value = getSensorValue(i, 2 * factor) - factor; // Get Values -50 > 50
        double tanh = Math.tanh(value);
        return tanh;
    }

    /*
       Find zero crossings
     */
    public double getFrequency() {
        double ret = 0;
        boolean current = false;
        boolean last = false;
        long zeroCrossings = 0;
        long lastZeroCrossings = 0;
        for (int i = 0; i < size; i++) {
            double tanh = getTanHValue(i, 50);
            if (tanh > 0.9f) {
                current = true;
            }
            if (tanh < -0.9f) {
                current = false;
            }
            if (current != last && current == true) {
                zeroCrossings = getSensorTime(i);
                if (lastZeroCrossings > 0) {
                    ret = zeroCrossings - lastZeroCrossings; // -milliseconds
                    ret = ret / 1000; // seconds
                    ret = 1 / ret; // to frequecy
                }
            }
            lastZeroCrossings = zeroCrossings;
            last = current;
        }
        return ret;
    }

    public int getPos() {
        return pos;
    }

    public int getSize() {
        return size;
    }

    public class SensorDataElement {
        public SensorDataElement(long time, double value){
            this.time = time;
            this.value = value;
        }

        public long time;
        public double value;
    }
}
