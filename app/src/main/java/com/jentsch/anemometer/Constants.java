package com.jentsch.anemometer;

public class Constants {

    public static final String CALIBRATION_KEY            = "CALIBRATION_KEY";
    public static final String LOWPASS_FILTER_KEY         = "LOWPASS_FILTER_KEY";
    public static final String MIN_CALIBRATION_SPEED_KEY  = "MIN_CALIBRATION_SPEED_KEY";
    public static final String MIN_GPS_ACCURACY_KEY       = "MIN_GPS_ACCURACY_KEY";
    public static final String MIN_ROTATION_FREQUENCY_KEY = "MIN_ROTATION_FREQUENCY_KEY";

    public static final String CALIBRATION_FORMAT            = "%.03f";
    public static final String MIN_CALIBRATION_SPEED_FORMAT  = "%.02f";
    public static final String MIN_GPS_ACCURACY_FORMAT       = "%.02f";
    public static final String MIN_ROTATION_FREQUENCY_FORMAT = "%.02f";

    public static final int PERMISSION_REQUEST_CODE = 123;

    // Calibration Value from my calibration measurement
    public static float calibrationValueDefault          = 0.71f;
    public static int   lowpassFilterValueDefault        = 0;
    public static float minCalibrationSpeedValueDefault  = 2;
    public static float minGPSAccuracyValueDefault       = 10;
    public static float minRotationFrequencyValueDefault = 1;

    public static final int LOWPASS_FILTER_LOW    = 0;
    public static final int LOWPASS_FILTER_MEDIUM = 1;
    public static final int LOWPASS_FILTER_HIGH   = 2;

    public static float calibrationDifference          = 0.01f;
    public static float minCalibrationSpeedDifference  = 0.2f;
    public static float minGPSAccuracyDifference       = 1f;
    public static float minRotationFrequencyDifference = 0.2f;

    public static float minCalibrationValue          = 0.01f;
    public static float maxCalibrationValue          = 1f;
    public static float minMinCalibrationSpeedValue  = 0.2f;
    public static float maxMinCalibrationSpeedValue  = 3f;
    public static float minMinGPSAccuracyValue       = 1f;
    public static float maxMinGPSAccuracyValue       = 20f;
    public static float minMinRotationFrequencyValue = 0.2f;
    public static float maxMinRotationFrequencyValue = 4f;
}
