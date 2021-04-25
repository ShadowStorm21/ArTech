package com.example.mycourseprojectapplication.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.mycourseprojectapplication.Models.Requests;
import com.example.mycourseprojectapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminRequestActivity extends AppCompatActivity {
    private Requests request;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_request);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {    // when back is clicked just finish the activity
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        request = (Requests) intent.getSerializableExtra("request");
        setTitle("Request#"+request.getRequest_id().substring(0,5));   // setup the action toolbar with activity title and back icon functionality
        TextView textViewRequestId = findViewById(R.id.textViewRequstID);
        TextView textViewRequestType = findViewById(R.id.textViewRequstCategory);
        TextView textViewRequestDetail = findViewById(R.id.textViewRequestDetail);
        TextView textViewRequestFullDetails = findViewById(R.id.textViewRequestFullDetails);
        Button buttonCallCustomer = findViewById(R.id.buttonCallCustomer);
        Button buttonEmailCustomer = findViewById(R.id.buttonEmailCustomer);
        Button buttonResolve = findViewById(R.id.buttonSetResloved);

        textViewRequestId.setText(request.getRequest_id());
        textViewRequestType.setText(request.getHelpCategory());
        textViewRequestDetail.setText(request.getDetailedHelp());
        textViewRequestFullDetails.setText(request.getDescription());

        buttonCallCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:+968"+request.getCustomerPhoneNumber()));
                startActivity(intent);
            }
        });


        buttonEmailCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{ request.getCustomerEmail()});
                email.putExtra(Intent.EXTRA_SUBJECT, request.getDetailedHelp());
                email.setType("message/rfc822");
                startActivity(Intent.createChooser(email, "Choose an Email client :"));
            }
        });

        buttonResolve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("Requests");
                myRef.child(request.getRequest_id()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful())
                        {
                            Toast.makeText(AdminRequestActivity.this, "Request is resolved!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
            }
        });

    }
}