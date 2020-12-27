package com.jentsch.anemometer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jentsch.anemometer.view.GraphView;
import com.jentsch.anemometer.view.SpeedView;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private TextView calibrationInfo;
    private TextView windSpeedValue1;
    private TextView windSpeedValue2;
    private TextView windSpeedValue3;
    private TextView rotationSpeedValue;
    private SharedPreferences prefs;
    private float calibrationValue;
    private SensorManager sensorManager;
    private ConstraintLayout anemometerLayout;
    private SensorDataList sensorDataList;

    private double speed1 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        windSpeedValue1 = findViewById(R.id.wind_speed_value1);
        windSpeedValue2 = findViewById(R.id.wind_speed_value2);
        windSpeedValue3 = findViewById(R.id.wind_speed_value3);
        rotationSpeedValue = findViewById(R.id.rotation_speed_value);
        calibrationInfo = findViewById(R.id.calibration_info);
        anemometerLayout = findViewById(R.id.anemometer_layout);
        sensorDataList = new SensorDataList(128);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        calibrationValue = prefs.getFloat(Constants.CALIBRATION_KEY, Constants.calibrationValueDefault);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        showCalibrationInfo(false);
    }


    private void showCalibrationInfo(boolean view) {
        if (view) {
            calibrationInfo.setVisibility(View.VISIBLE);
            anemometerLayout.setVisibility(View.GONE);
        } else {
            calibrationInfo.setVisibility(View.GONE);
            anemometerLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.calibration:
                showCalibration();
                return true;
            case R.id.settings:
                showSettings();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    private void showSettings() {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }

    private void showCalibration() {
        Intent i = new Intent(this, RunCalibrationActivity.class);
        startActivityForResult(i, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    private double getLowpassFilterFactor() {
        double ret = 0.2;
        int lowpassFilterValue = prefs.getInt(Constants.LOWPASS_FILTER_KEY, 0);
        switch (lowpassFilterValue) {
            case Constants.LOWPASS_FILTER_LOW:
                ret = 0.1;
                break;
            case Constants.LOWPASS_FILTER_MEDIUM:
                ret = 0.05;
                break;
            case Constants.LOWPASS_FILTER_HIGH:
                ret = 0.01;
                break;
        }
        return ret;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            double lowpassFilterFactor = getLowpassFilterFactor();
            float magX = event.values[0];
            sensorDataList.addValue(magX);

            double rotationSpeed = sensorDataList.getFrequency();
            double currentSpeed = calibrationValue * rotationSpeed;

            // use lowpass filter
            speed1 = (speed1 * (1 - lowpassFilterFactor))
                    + (currentSpeed * lowpassFilterFactor);

            windSpeedValue1.setText(String.format("%.1f", speed1));
            double speed2 = speed1 * 3600 / 1000;
            windSpeedValue2.setText(String.format("%.1f", speed2));
            int speed3 = getBft(speed1);
            windSpeedValue3.setText(new Integer(speed3).toString());
            rotationSpeedValue.setText(String.format("%.1f", rotationSpeed));
        }
    }

    private int getBft(double speedms) {
        // https://www.easyunitconverter.com/wind-speed-calculator
        int bft;
        if (speedms > 32.6) {
            bft = 12;
        } else if (speedms > 28.4) {
            bft = 11;
        } else if (speedms > 24.4) {
            bft = 10;
        } else if (speedms > 20.7) {
            bft = 9;
        } else if (speedms > 17.1) {
            bft = 8;
        } else if (speedms > 13.8) {
            bft = 7;
        } else if (speedms > 10.7) {
            bft = 6;
        } else if (speedms > 7.9) {
            bft = 5;
        } else if (speedms > 5.4) {
            bft = 4;
        } else if (speedms > 3.3) {
            bft = 3;
        } else if (speedms > 1.5) {
            bft = 2;
        } else if (speedms > 0.2) {
            bft = 1;
        } else {
            bft = 0;
        }
        return bft;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}