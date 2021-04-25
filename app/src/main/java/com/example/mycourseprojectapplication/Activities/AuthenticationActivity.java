package com.example.mycourseprojectapplication.Activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.mycourseprojectapplication.Broadcasts.MyRestarter;
import com.example.mycourseprojectapplication.R;
import com.example.mycourseprojectapplication.Utilities.ConnectionDetector;
import com.example.mycourseprojectapplication.Utilities.UserSession;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.google.android.material.transition.platform.MaterialArcMotion;
import com.google.android.material.transition.platform.MaterialContainerTransform;

import static android.transition.TransitionManager.beginDelayedTransition;

public class AuthenticationActivity extends AppCompatActivity {
    private MaterialAlertDialogBuilder materialAlertDialogBuilder;
    private View view;
    private AlertDialog isShowing;
    private CardView cardViewTerms;
    private UserSession userSession;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);


        new ConnectionDetector(this); // check if user has internet connection or not
        Button signUp = findViewById(R.id.button3);           // initialize both buttons
        Button signIn = findViewById(R.id.button4);

        cardViewTerms = findViewById(R.id.cardViewTerms);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(AuthenticationActivity.this,LoginActivity.class));           // login button will send you to login activity
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(AuthenticationActivity.this,SignUpActivity.class));      // sign up button will send you to sign up activity
            }
        });
        TextView textViewTermsAndCondition = findViewById(R.id.textViewTermsandCondition);
        textViewTermsAndCondition.setVisibility(View.VISIBLE);
        textViewTermsAndCondition.setPaintFlags(textViewTermsAndCondition.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        ConstraintLayout viewGroup = findViewById(R.id.AuthViewGroup);
        ScrollView scrollView = findViewById(R.id.scrollviewterms);
        textViewTermsAndCondition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                com.google.android.material.transition.platform.MaterialArcMotion materialArcMotion = new MaterialArcMotion();
                MaterialContainerTransform materialContainerTransform = new MaterialContainerTransform();
                materialContainerTransform.setStartView(textViewTermsAndCondition);
                materialContainerTransform.setEndView(cardViewTerms);
                materialContainerTransform.addTarget(cardViewTerms);
                materialContainerTransform.setDuration(600L);
                materialContainerTransform.setScrimColor(Color.TRANSPARENT);
                materialContainerTransform.setContainerColor(Color.TRANSPARENT);
                materialContainerTransform.setPathMotion(materialArcMotion);
                materialContainerTransform.setFadeMode(MaterialContainerTransform.FADE_MODE_THROUGH);
                beginDelayedTransition(viewGroup,materialContainerTransform);
                textViewTermsAndCondition.setVisibility(View.GONE);
                cardViewTerms.setVisibility(View.VISIBLE);
                scrollView.smoothScrollTo(0,0);


            }
        });


        cardViewTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.google.android.material.transition.platform.MaterialArcMotion materialArcMotion = new MaterialArcMotion();
                MaterialContainerTransform materialContainerTransform = new MaterialContainerTransform();
                materialContainerTransform.setStartView(cardViewTerms);
                materialContainerTransform.setEndView(textViewTermsAndCondition);
                materialContainerTransform.addTarget(textViewTermsAndCondition);
                materialContainerTransform.setStartShapeAppearanceModel(ShapeAppearanceModel.builder().build().withCornerSize(10f));
                materialContainerTransform.setDuration(600L);
                materialContainerTransform.setScrimColor(Color.TRANSPARENT);
                materialContainerTransform.setContainerColor(Color.TRANSPARENT);
                materialContainerTransform.setPathMotion(materialArcMotion);
                materialContainerTransform.setFadeMode(MaterialContainerTransform.FADE_MODE_THROUGH);
                beginDelayedTransition(viewGroup,materialContainerTransform);
                textViewTermsAndCondition.setVisibility(View.VISIBLE);
                cardViewTerms.setVisibility(View.GONE);

            }
        });

        Button buttonClose = findViewById(R.id.button2);
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.google.android.material.transition.platform.MaterialArcMotion materialArcMotion = new MaterialArcMotion();
                MaterialContainerTransform materialContainerTransform = new MaterialContainerTransform();
                materialContainerTransform.setStartView(cardViewTerms);
                materialContainerTransform.setEndView(textViewTermsAndCondition);
                materialContainerTransform.addTarget(textViewTermsAndCondition);
                materialContainerTransform.setStartShapeAppearanceModel(ShapeAppearanceModel.builder().build().withCornerSize(10f));
                materialContainerTransform.setDuration(600L);
                materialContainerTransform.setScrimColor(Color.TRANSPARENT);
                materialContainerTransform.setContainerColor(Color.TRANSPARENT);
                materialContainerTransform.setPathMotion(materialArcMotion);
                materialContainerTransform.setFadeMode(MaterialContainerTransform.FADE_MODE_THROUGH);
                beginDelayedTransition(viewGroup,materialContainerTransform);
                textViewTermsAndCondition.setVisibility(View.VISIBLE);
                cardViewTerms.setVisibility(View.GONE);
            }
        });






    }

    @Override
    protected void onResume() {
        super.onResume();
        new ConnectionDetector(this);       // on Resume ( this method is fired before on create )  activity, check if user has connection or not
        cancelBackgroundNotification();
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
        long futureInMillis = SystemClock.elapsedRealtime()   ; // 2min        // 3600000 Hour  // 7200000 2 Hours // 1800000 30 min
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, alarmIntent);
    }


}