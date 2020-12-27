package com.jentsch.anemometer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jentsch.anemometer.view.GraphView;
import com.jentsch.anemometer.view.LinearRegressionView;

import java.text.DecimalFormat;

public class RunCalibrationActivity extends AppCompatActivity implements LocationListener, SensorEventListener {

    private static final String TAG = "RunCalibrationActivity";

    private SharedPreferences prefs;
    private LocationManager locationManager;
    private TextView speedLabel, accuracyLabel, factorLabel;
    private GraphView graphView;
    private LinearRegressionView linearRegressionView;
    private Resources resources;
    private SensorManager sensorManager;
    private SensorDataList sensorDataList;
    private DecimalFormat formatter = new DecimalFormat("#.##");

    private float minCalibrationSpeedValue; // Min speed in m/s
    private float minGPSAccuracyValue; // Max. 10 m GPS accuracy
    private float minRotationFrequencyValue; // Min. rotation 1 Hz
    private float calibrationValue; // Calibration value determined by calibration

    private int green;
    private int red;
    private boolean isStarted = false;
    private Thread fakeDataThread;
    private View calibrationIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_calibration);
        getSupportActionBar().setTitle(R.string.calibration);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        minCalibrationSpeedValue = prefs.getFloat(Constants.MIN_CALIBRATION_SPEED_KEY, Constants.minCalibrationSpeedValueDefault);
        minGPSAccuracyValue = prefs.getFloat(Constants.MIN_GPS_ACCURACY_KEY, Constants.minGPSAccuracyValueDefault);
        minRotationFrequencyValue = prefs.getFloat(Constants.MIN_ROTATION_FREQUENCY_KEY, Constants.minRotationFrequencyValueDefault);
        calibrationValue = prefs.getFloat(Constants.CALIBRATION_KEY, Constants.calibrationValueDefault);

        green = ContextCompat.getColor(this, R.color.green);
        red = ContextCompat.getColor(this, R.color.red);

        calibrationIndicator = findViewById(R.id.calibration_indicator);
        speedLabel = findViewById(R.id.speed_label);
        accuracyLabel = findViewById(R.id.accuracy_label);
        factorLabel = findViewById(R.id.factor_label);
        graphView = findViewById(R.id.graph);
        sensorDataList = new SensorDataList(128);
        graphView.setSensorDataList(sensorDataList);
        linearRegressionView = findViewById(R.id.regression);
        resources = getResources();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        invalidateOptionsMenu();
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
        fakeDataThread.interrupt();
        fakeDataThread = null;
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.PERMISSION_REQUEST_CODE) {
            if (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, this);
                // runDemoDataGenerator();
            } else {
                Toast.makeText(this, R.string.no_permission, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (BuildConfig.DEBUG) {
            inflater.inflate(R.menu.menu_save_debug, menu);
        } else {
            inflater.inflate(R.menu.menu_save, menu);
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        if (isStarted) {
            menu.findItem(R.id.save).setEnabled(true);
            if (BuildConfig.DEBUG) {
                menu.findItem(R.id.fake).setEnabled(true);
            }
        } else {
            menu.findItem(R.id.save).setEnabled(false);
            if (BuildConfig.DEBUG) {
                menu.findItem(R.id.fake).setEnabled(false);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                saveCalibration();
            case R.id.fake:
                runFakeDataGen();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void runFakeDataGen() {
        fakeDataThread = new Thread( ) {
            private boolean running;

            @Override
            public void run() {
                while (running) {
                    try {
                        sleep(500);
                        double speed = Math.random() * 100;
                        double frequency = speed + (Math.random() - 0.5) * 60;
                        linearRegressionView.addElement (speed, frequency);
                        calibrationValue = (float)linearRegressionView.getCalibrationValue();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                calibrationIndicator.setBackgroundColor(green);
                            }
                        });
                        Log.d(TAG, "runFakeDataGen " + speed + " " + frequency);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void interrupt() {
                super.interrupt();
                running = false;
            }

            @Override
            public synchronized void start() {
                running = true;
                super.start();
            }
        };
        fakeDataThread.start();
    }

    @Override
    public void onLocationChanged(Location location) {
        float speed = location.getSpeed();
        float accuracy = location.getAccuracy();
        float frequency = (float) sensorDataList.getFrequency();
        if (speed     > minCalibrationSpeedValue &&
            accuracy  < minGPSAccuracyValue &&
            frequency > minRotationFrequencyValue)
        {
            linearRegressionView.addElement (speed, frequency);
            calibrationValue = (float)linearRegressionView.getCalibrationValue();
            calibrationIndicator.setBackgroundColor(green);
        } else {
            if (fakeDataThread == null) {
                calibrationIndicator.setBackgroundColor(red);
            }
        }

        speedLabel.setText(resources.getString(R.string.speed, formatter.format(speed)));
        accuracyLabel.setText(resources.getString(R.string.accuracy, formatter.format(accuracy)));
        factorLabel.setText(resources.getString(R.string.factor, formatter.format(calibrationValue)));
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            float magX = event.values[0];
            sensorDataList.addValue(magX);
            graphView.invalidate();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        switch (accuracy) {
            case SensorManager.SENSOR_STATUS_UNRELIABLE:
                break;
            case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
                break;
            case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
                break;
            case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
                break;
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) { }

    @Override
    public void onProviderEnabled(String s) { }

    @Override
    public void onProviderDisabled(String s) { }

    public void onStartCalibration(View view) {
        findViewById(R.id.calibration_help_layout).setVisibility(View.GONE);
        isStarted = true;
        invalidateOptionsMenu();
        startCalibration();
    }

    private void startCalibration() {
        if(this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500,0,this);
        } else {
            Intent it = new Intent(this, RequestAuthorization.class);
            startActivityForResult(it, Constants.PERMISSION_REQUEST_CODE);
        }
    }

    public void saveCalibration() {
        prefs.edit().putFloat(Constants.CALIBRATION_KEY, calibrationValue).apply();
        Toast.makeText(this, getResources().getText(R.string.save_ok), Toast.LENGTH_LONG).show();
        finish();
    }
}
