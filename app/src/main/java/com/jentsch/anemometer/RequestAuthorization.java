package com.jentsch.anemometer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class RequestAuthorization extends AppCompatActivity {

    private final String[] PERMISSION = { Manifest.permission.ACCESS_FINE_LOCATION };
    private final int PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_autorization);
        if(this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            permissionDone();
        }
    }

    public void requestPermission(View v){
        EasyPermissions.requestPermissions(this, getString(R.string.permission_alert_text),PERMISSION_CODE, PERMISSION);
    }

    @AfterPermissionGranted(PERMISSION_CODE)
    public void permissionDone(){
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}
