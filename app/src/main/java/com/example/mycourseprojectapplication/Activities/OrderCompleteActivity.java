package com.example.mycourseprojectapplication.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.mycourseprojectapplication.R;

public class OrderCompleteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_complete);

        Toolbar toolbar = findViewById(R.id.toolbarOrderComplete);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // setup the action toolbar with activity title and exit icon functionality
        setTitle("Order Status");
        toolbar.setNavigationIcon(R.drawable.ic_action_navigation_close);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(OrderCompleteActivity.this,MainActivity.class);          // when exit button is clicked go to main activity and finish the activity
                startActivity(intent);
                finishAffinity();
            }
        });

    }

    @Override
    public void onBackPressed() { // on back pressed
        super.onBackPressed();
        Intent intent = new Intent(OrderCompleteActivity.this,MainActivity.class);  // on back pressed go to main activity and finish the activity
        startActivity(intent);
        finishAffinity();
    }
}