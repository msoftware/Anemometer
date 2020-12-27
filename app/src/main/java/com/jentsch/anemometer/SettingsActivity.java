package com.jentsch.anemometer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences prefs;

    private TextView calibrationValueTextView;
    private TextView lowpassFilterValueTextView;
    private TextView minCalibrationSpeedValueTextView;
    private TextView minGPSAccuracyValueTextView;
    private TextView minRotationFrequencyValueTextView;

    private float calibrationValue;
    private int lowpassFilterValue;
    private float minCalibrationSpeedValue;
    private float minGPSAccuracyValue;
    private float minRotationFrequencyValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setTitle(R.string.settings);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        calibrationValueTextView = findViewById(R.id.calibrationValue);
        lowpassFilterValueTextView = findViewById(R.id.lowpassFilterValue);
        minCalibrationSpeedValueTextView = findViewById(R.id.minCalibrationSpeedValue);
        minGPSAccuracyValueTextView = findViewById(R.id.minGPSAccuracyValue);
        minRotationFrequencyValueTextView = findViewById(R.id.minRotationFrequencyValue);

        calibrationValue = prefs.getFloat(Constants.CALIBRATION_KEY, Constants.calibrationValueDefault);
        lowpassFilterValue = prefs.getInt(Constants.LOWPASS_FILTER_KEY, Constants.lowpassFilterValueDefault);
        minCalibrationSpeedValue = prefs.getFloat(Constants.MIN_CALIBRATION_SPEED_KEY, Constants.minCalibrationSpeedValueDefault);
        minGPSAccuracyValue = prefs.getFloat(Constants.MIN_GPS_ACCURACY_KEY, Constants.minGPSAccuracyValueDefault);
        minRotationFrequencyValue = prefs.getFloat(Constants.MIN_ROTATION_FREQUENCY_KEY, Constants.minRotationFrequencyValueDefault);

        calibrationValueTextView.setText(String.format(Constants.CALIBRATION_FORMAT, calibrationValue));
        lowpassFilterValueTextView.setText(getLowpassFilterText());
        minCalibrationSpeedValueTextView.setText(String.format(Constants.MIN_CALIBRATION_SPEED_FORMAT, minCalibrationSpeedValue));
        minGPSAccuracyValueTextView.setText(String.format(Constants.MIN_GPS_ACCURACY_FORMAT, minGPSAccuracyValue));
        minRotationFrequencyValueTextView.setText(String.format(Constants.MIN_ROTATION_FREQUENCY_FORMAT, minRotationFrequencyValue));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                saveSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onCalibrationMinus(View view) {
        calibrationValue = calibrationValue - Constants.calibrationDifference;
        if (calibrationValue < Constants.minCalibrationValue) {
            calibrationValue = Constants.minCalibrationValue;
        }
        calibrationValueTextView.setText(String.format(Constants.CALIBRATION_FORMAT, calibrationValue));

    }

    public void onCalibrationPlus(View view) {
        calibrationValue = calibrationValue + Constants.calibrationDifference;
        if (calibrationValue > Constants.maxCalibrationValue) {
            calibrationValue = Constants.maxCalibrationValue;
        }
        calibrationValueTextView.setText(String.format(Constants.CALIBRATION_FORMAT, calibrationValue));
    }

    public void onMinCalibrationSpeedMinus(View view) {
        minCalibrationSpeedValue = minCalibrationSpeedValue - Constants.minCalibrationSpeedDifference;
        if (minCalibrationSpeedValue < Constants.minMinCalibrationSpeedValue) {
            minCalibrationSpeedValue = Constants.minMinCalibrationSpeedValue;
        }
        minCalibrationSpeedValueTextView.setText(String.format(Constants.MIN_CALIBRATION_SPEED_FORMAT, minCalibrationSpeedValue));
    }

    public void onMinCalibrationSpeedPlus(View view) {
        minCalibrationSpeedValue = minCalibrationSpeedValue + Constants.minCalibrationSpeedDifference;
        if (minCalibrationSpeedValue > Constants.maxMinCalibrationSpeedValue) {
            minCalibrationSpeedValue = Constants.maxMinCalibrationSpeedValue;
        }
        minCalibrationSpeedValueTextView.setText(String.format(Constants.MIN_CALIBRATION_SPEED_FORMAT, minCalibrationSpeedValue));
    }

    public void onMinGPSAccuracyMinus(View view) {
        minGPSAccuracyValue = minGPSAccuracyValue - Constants.minGPSAccuracyDifference;
        if (minGPSAccuracyValue < Constants.minMinGPSAccuracyValue) {
            minGPSAccuracyValue = Constants.minMinGPSAccuracyValue;
        }
        minGPSAccuracyValueTextView.setText(String.format(Constants.MIN_GPS_ACCURACY_FORMAT, minGPSAccuracyValue));
    }

    public void onMinGPSAccuracyPlus(View view) {
        minGPSAccuracyValue = minGPSAccuracyValue + Constants.minGPSAccuracyDifference;
        if (minGPSAccuracyValue > Constants.maxMinGPSAccuracyValue) {
            minGPSAccuracyValue = Constants.maxMinGPSAccuracyValue;
        }
        minGPSAccuracyValueTextView.setText(String.format(Constants.MIN_GPS_ACCURACY_FORMAT, minGPSAccuracyValue));
    }

    public void onMinRotationFrequencyMinus(View view) {
        minRotationFrequencyValue = minRotationFrequencyValue - Constants.minRotationFrequencyDifference;
        if (minRotationFrequencyValue < Constants.minMinRotationFrequencyValue) {
            minRotationFrequencyValue = Constants.minMinRotationFrequencyValue;
        }
        minRotationFrequencyValueTextView.setText(String.format(Constants.MIN_ROTATION_FREQUENCY_FORMAT, minRotationFrequencyValue));
    }

    public void onMinRotationFrequencyPlus(View view) {
        minRotationFrequencyValue = minRotationFrequencyValue + Constants.minRotationFrequencyDifference;
        if (minRotationFrequencyValue > Constants.maxMinRotationFrequencyValue) {
            minRotationFrequencyValue = Constants.maxMinRotationFrequencyValue;
        }
        minRotationFrequencyValueTextView.setText(String.format(Constants.MIN_ROTATION_FREQUENCY_FORMAT, minRotationFrequencyValue));
    }

    public void onLowpassFilterMinus(View view) {
        if (lowpassFilterValue > Constants.LOWPASS_FILTER_LOW) {
            lowpassFilterValue = lowpassFilterValue - 1;
            lowpassFilterValueTextView.setText(getLowpassFilterText());
        }
    }

    public void onLowpassFilterPlus(View view) {
        if (lowpassFilterValue < Constants.LOWPASS_FILTER_HIGH) {
            lowpassFilterValue = lowpassFilterValue + 1;
            lowpassFilterValueTextView.setText(getLowpassFilterText());
        }
    }


    private String getLowpassFilterText() {
        String ret = getResources().getString(R.string.lowpass_filter_undefined);
        switch (lowpassFilterValue) {
            case Constants.LOWPASS_FILTER_LOW:
                ret = getResources().getString(R.string.lowpass_filter_low);
                break;
            case Constants.LOWPASS_FILTER_MEDIUM:
                ret = getResources().getString(R.string.lowpass_filter_medium);
                break;
            case Constants.LOWPASS_FILTER_HIGH:
                ret = getResources().getString(R.string.lowpass_filter_hight);
                break;
        }
        return ret;
    }

    public void saveSettings() {
        prefs.edit().putFloat(Constants.CALIBRATION_KEY,            calibrationValue).apply();
        prefs.edit().putInt(  Constants.LOWPASS_FILTER_KEY,         lowpassFilterValue).apply();
        prefs.edit().putFloat(Constants.MIN_CALIBRATION_SPEED_KEY,  minCalibrationSpeedValue).apply();
        prefs.edit().putFloat(Constants.MIN_GPS_ACCURACY_KEY,       minGPSAccuracyValue).apply();
        prefs.edit().putFloat(Constants.MIN_ROTATION_FREQUENCY_KEY, minRotationFrequencyValue).apply();
        finish();
    }
}