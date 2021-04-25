package com.example.mycourseprojectapplication.Activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.mycourseprojectapplication.Broadcasts.MyRestarter;
import com.example.mycourseprojectapplication.R;
import com.example.mycourseprojectapplication.Utilities.UserSession;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN); // set activity to full screen
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {           // create a background handler to set a timer then execute the method inside


            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                startActivity(new Intent(SplashActivity.this,AuthenticationActivity.class)); // go the authentication activity
                finish();
            }
        }, 3500);

        String prf = new UserSession(SplashActivity.this).getPrefName();
        if(prf != null)
        if(prf.equals("Light"))
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        else if(prf.equals("Dark"))
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else if(prf.equals("System Default"))
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }

        changeStatusBarColor();
        cancelBackgroundNotification();
    }
    private void changeStatusBarColor() {        // this method changes the status bar color to transparent
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
    }


    private void cancelBackgroundNotification()
    {
        int alarmId = 0; /* Dynamically assign alarm ids for multiple alarms */
        Intent intent = new Intent(this, MyRestarter.class); /* your Intent localIntent = new Intent("com.test.sample");*/
        intent.putExtra("alarmId", alarmId); /* So we can catch the id on BroadcastReceiver */
        PendingIntent alarmIntent;
        alarmIntent = PendingIntent.getBroadcast(this,
                alarmId, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        long futureInMillis = SystemClock.elapsedRealtime();
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, alarmIntent);
    }



}