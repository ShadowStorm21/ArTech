package com.example.mycourseprojectapplication.Broadcasts;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.mycourseprojectapplication.Services.MyNotificationService;
import com.example.mycourseprojectapplication.Utilities.UserSession;

import static android.content.Context.JOB_SCHEDULER_SERVICE;

public class MyRestarter extends BroadcastReceiver {
    private static final int JOB_ID = 0;
    private JobScheduler mScheduler;
    private UserSession userSession;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Restarter","Service started");
        int alarmId = intent.getExtras().getInt("alarmId");

        if(alarmId == 0)
        {
            cancelJobs(context);
            Log.i("Restarter","Canceled");
        }
        if(alarmId == 1) {
            scheduleJob(context);
            Log.i("Restarter","Started");
        }

    }

    public void scheduleJob(Context context) {
        mScheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
        ComponentName serviceName = new ComponentName(context.getPackageName(),
                MyNotificationService.class.getName());
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, serviceName).setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        userSession = new UserSession(context);
        if(userSession.getCartValue() >= 1) {
            JobInfo myJobInfo = builder.build();
            mScheduler.schedule(myJobInfo);
        }
        else
        {

            cancelJobs(context);
        }

    }
    public void cancelJobs(Context context) {

        if (mScheduler != null) {
            mScheduler.cancelAll();
            mScheduler = null;
        }
    }
}