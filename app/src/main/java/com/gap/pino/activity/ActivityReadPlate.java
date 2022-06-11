package com.gap.pino.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.gap.pino.R;
import com.shahaabco.satpa.SatpaLicense;

import java.util.ArrayList;
//import org.opencv.android.OpenCVLoader;

public class ActivityReadPlate extends AppCompatActivity {

    private static final String  TAG                 = "ANPR_Demo";
    static
    {
        //ANPRLIB Step 2
        //System.loadLibrary("satpa");

        /*if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
        }*/
    }

    Button btnActivate, btnPhotoDemo, btnCameraDemo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_plate);


        btnActivate = (Button) findViewById(R.id.btn_activate);
        btnActivate.setOnClickListener(onActivateClicked);

        btnPhotoDemo = (Button) findViewById(R.id.btn_photo_demo);
        btnPhotoDemo.setOnClickListener(onPhotoDemoClicked);

        btnCameraDemo = (Button) findViewById(R.id.btn_camera_demo);
        btnCameraDemo.setOnClickListener(onCameraDemoClicked);

        // Asking for permissions
        String[] accessPermissions = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE
        };

        ArrayList<String> permissionNeededList = new ArrayList<String>();
        for (String access : accessPermissions) {
            int curPermission = ActivityCompat.checkSelfPermission(this, access);
            if (curPermission != PackageManager.PERMISSION_GRANTED) {
                permissionNeededList.add(access);
            }
        }

        if (permissionNeededList.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionNeededList.toArray(new String[permissionNeededList.size()]),
                    1);
        }
    }


    private View.OnClickListener onActivateClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(ActivityReadPlate.this, SatpaLicense.class);
            startActivityForResult(intent, 1);        }
    };

    private View.OnClickListener onPhotoDemoClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(ActivityReadPlate.this, PhotoActivity.class);
            startActivityForResult(intent, 2);
        }
    };

    private View.OnClickListener onCameraDemoClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(ActivityReadPlate.this, VideoActivity.class);
            startActivityForResult(intent, 2);
        }
    };

}