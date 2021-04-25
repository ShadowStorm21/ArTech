package com.example.mycourseprojectapplication.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.example.mycourseprojectapplication.R;
import com.google.zxing.Result;

public class ScanQRActivity extends AppCompatActivity {
    private CodeScanner mCodeScanner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!checkPermissions())
        {
            requestPermissions();
        }
        setContentView(R.layout.activity_scan_q_r);
        Toolbar toolbar = findViewById(R.id.toolbar7);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   // setup the action toolbar with activity title and back icon functionality
        setTitle("Scan QR Code");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       if( Patterns.WEB_URL.matcher(result.toString()).matches())
                        {
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(result.toString()));
                            startActivity(i);
                        }
                       else
                       {
                           Toast.makeText(ScanQRActivity.this, "Invalid Code", Toast.LENGTH_SHORT).show();
                           mCodeScanner.startPreview();
                       }


                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
    }

    private boolean checkPermissions() // method to check the camera permission
    {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
        {
            return true;
        }
        return false;
    }

    private void requestPermissions() // method to request the camera permission
    {
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA}, 1000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) { // on permission request result
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1000) // check if request code is the same
        {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) // if permission is granted
            {

            }
            else
            {
                requestPermissions(); // otherwise request permission
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(checkPermissions())
        {
            mCodeScanner.startPreview();
        }

    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }
}