package com.example.mycourseprojectapplication.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.airbnb.lottie.LottieAnimationView;
import com.example.mycourseprojectapplication.R;

import host.stjin.expandablecardview.ExpandableCardView;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        final Toolbar toolbar = findViewById(R.id.toolbar6);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // setup the action toolbar with activity title and back icon functionality

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        ExpandableCardView q1 = findViewById(R.id.expandableQ1);
        ExpandableCardView q2 = findViewById(R.id.expandableQ2);
        ExpandableCardView q3 = findViewById(R.id.expandableQ3);
        ExpandableCardView q4 = findViewById(R.id.expandableQ4);
        ExpandableCardView q5 = findViewById(R.id.expandableQ5);
        ExpandableCardView q6 = findViewById(R.id.expandableQ6);
        ExpandableCardView q7 = findViewById(R.id.expandableQ7);
        TextView textViewAppTitle = findViewById(R.id.textViewTitleApp);
        TextView textViewCP = findViewById(R.id.textViewCP);
        TextView textViewTM = findViewById(R.id.textViewTMTitle);
        TextView textViewVersion = findViewById(R.id.textViewVersion);
        TextView textViewTOS = findViewById(R.id.textViewTOShelp);
        TextView textViewAuthor = findViewById(R.id.textViewAuthorDev);
        TextView textViewUniv = findViewById(R.id.textViewUniv);
        TextView textView2021 = findViewById(R.id.textView2021);
        TextView textViewEmail = findViewById(R.id.textViewEmailHelp);
        ImageView imageViewIcon = findViewById(R.id.imageViewIcon);
        LottieAnimationView lottieAnimationView = findViewById(R.id.splashView);


        Intent intent = getIntent();

        String flag = intent.getStringExtra("flag");

        if(flag.equals("aboutus"))
        {
            setTitle("About us");
            textViewAppTitle.setVisibility(View.VISIBLE);
            textViewCP.setVisibility(View.VISIBLE);
            textViewTM.setVisibility(View.VISIBLE);
            textViewVersion.setVisibility(View.VISIBLE);
            imageViewIcon.setVisibility(View.VISIBLE);
        }

        if(flag.equals("faq"))
        {
            setTitle("FAQ");
            q1.setVisibility(View.VISIBLE);
            q2.setVisibility(View.VISIBLE);
            q3.setVisibility(View.VISIBLE);
            q4.setVisibility(View.VISIBLE);
            q5.setVisibility(View.VISIBLE);
            q6.setVisibility(View.VISIBLE);
            q7.setVisibility(View.VISIBLE);
        }

        if(flag.equals("tos"))
        {
            setTitle("Terms of Service");
            textViewTOS.setVisibility(View.VISIBLE);
        }

        if(flag.equals("pp"))
        {
            setTitle("Help");
        }

        if(flag.equals("lisc"))
        {
            setTitle("Help");
        }

        if(flag.equals("author"))
        {
            setTitle("Author");
            textViewAuthor.setVisibility(View.VISIBLE);
            lottieAnimationView.setVisibility(View.VISIBLE);
            lottieAnimationView.playAnimation();
            textView2021.setVisibility(View.VISIBLE);
            textViewUniv.setVisibility(View.VISIBLE);
            textViewEmail.setVisibility(View.VISIBLE);
        }



    }
}