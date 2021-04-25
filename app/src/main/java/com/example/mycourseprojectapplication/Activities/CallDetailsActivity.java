package com.example.mycourseprojectapplication.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.mycourseprojectapplication.R;

public class CallDetailsActivity extends AppCompatActivity {
    private CardView cardViewEmail,cardViewCall,cardViewWhatsapp,cardViewExpanded;
    private ConstraintLayout constraintLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_details);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // setup the action toolbar with activity title and back icon functionality
        setTitle("Contact us");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });

        cardViewEmail = findViewById(R.id.cardView3);
        cardViewCall = findViewById(R.id.cardView4);
        cardViewWhatsapp = findViewById(R.id.cardView5);
        constraintLayout = findViewById(R.id.constraintEmailContainer);
        cardViewEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




               Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{ "ddopetech@gmail.com"});
                //need this to prompts email client only
                email.setType("message/rfc822");
                startActivity(Intent.createChooser(email, "Choose an Email client :"));
            }
        });

        cardViewCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:+96899999999"));
                startActivity(intent);
            }
        });

        cardViewWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = "";
                Uri uri = Uri.parse(String.format("https://api.whatsapp.com/send?phone=%s&text=%s", "+96899999999", message));
                Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
            }
        });
    }
}