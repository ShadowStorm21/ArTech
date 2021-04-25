package com.example.mycourseprojectapplication.Fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.mycourseprojectapplication.Activities.CallDetailsActivity;
import com.example.mycourseprojectapplication.Activities.EmailInquiresActivity;
import com.example.mycourseprojectapplication.Broadcasts.MyRestarter;
import com.example.mycourseprojectapplication.BuildConfig;
import com.example.mycourseprojectapplication.R;


public class CustomerSupportFragment extends Fragment {

    private CardView cardViewEmail,cardViewCall,cardViewFeedback;

    public CustomerSupportFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view =  inflater.inflate(R.layout.fragment_customer_support, container, false);
       cardViewEmail = view.findViewById(R.id.cardViewRewards);
       cardViewCall = view.findViewById(R.id.cardViewProfileCards);
       cardViewFeedback = view.findViewById(R.id.cardView2);
        String versionName = BuildConfig.VERSION_NAME;
       cardViewEmail.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(getContext(), EmailInquiresActivity.class);
               startActivity(intent);
           }
       });
       cardViewCall.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(getContext(), CallDetailsActivity.class);
               startActivity(intent);
           }
       });
       cardViewFeedback.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent email = new Intent(Intent.ACTION_SEND);
               email.putExtra(Intent.EXTRA_EMAIL, new String[]{ "ddopetech@gmail.com"});
               email.putExtra(Intent.EXTRA_SUBJECT, versionName);
//need this to prompts email client only
               email.setType("message/rfc822");
               startActivity(Intent.createChooser(email, "Choose an Email client :"));
           }
       });
       return view;
    }


    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onResume() {
        super.onResume();
        cancelBackgroundNotification();
    }

    private void cancelBackgroundNotification()
    {
        int alarmId = 0; /* Dynamically assign alarm ids for multiple alarms */
        Intent intent = new Intent(getContext(), MyRestarter.class); /* your Intent localIntent = new Intent("com.test.sample");*/
        intent.putExtra("alarmId", alarmId); /* So we can catch the id on BroadcastReceiver */
        PendingIntent alarmIntent;
        alarmIntent = PendingIntent.getBroadcast(getContext(),
                alarmId, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        long futureInMillis = SystemClock.elapsedRealtime()  ; // 2min        // 3600000 Hour  // 7200000 2 Hours // 1800000 30 min
        AlarmManager alarmManager = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, alarmIntent);
    }


}