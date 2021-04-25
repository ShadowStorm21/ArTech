package com.example.mycourseprojectapplication.Activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.mycourseprojectapplication.Models.Requests;
import com.example.mycourseprojectapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class EmailInquiresActivity extends AppCompatActivity {
    private AutoCompleteTextView autoCompleteTextViewHelp1;
    private AutoCompleteTextView autoCompleteTextViewHelp2;
    private String[] help1Questions = {"I have a question about my account","I have an issue buying products", "I have an issue with my order", "I have a question about payment system", "I have a technical issue","Other"};
    private String[] help2Questions1 = {"I want to edit my profile","I want to delete my account", "other"};
    private String[] help2Questions2 = {"I can't find product x", "other"};
    private String[] help2Questions3 = {"I can't find my order","my order is not delivered","I want to cancel my order" ,"other"};
    private String[] help2Questions4 = {"I can't find my preferred payment method","I'm not able to make the payment","I want to get my money back" ,"other"};
    private String[] help2Questions5 = {"I'm using the android app","other"};
    private TextInputLayout textInputLayoutHelp2,textInputLayoutName,textInputLayoutEmail,textInputLayoutPN,textInputLayoutDescription,textInputLayoutTellUsMore;
    private EditText editTextDesc;
    private TextInputEditText email,name,phoneNumber;
    private String s1 = "How can we help you?",s2 = "Tell us more";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_inquires);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // setup the action toolbar with activity title and back icon functionality
        setTitle("Submit a request");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        autoCompleteTextViewHelp1 = findViewById(R.id.autoCompleteTextViewHelp1);
        autoCompleteTextViewHelp2 = findViewById(R.id.autoCompleteTextViewHelp2);
        textInputLayoutHelp2 = findViewById(R.id.textInputLayout3);
        editTextDesc = findViewById(R.id.editTextHelpWhatHappended);
        email = findViewById(R.id.editTextHelpEmail);
        name = findViewById(R.id.editTextHelpName);
        phoneNumber = findViewById(R.id.editTextHelpMobileNumber);
        ArrayAdapter<String> question1Adapter = new ArrayAdapter(this, R.layout.dropdown_list_item, help1Questions);
        autoCompleteTextViewHelp1.setAdapter(question1Adapter);
        autoCompleteTextViewHelp1.setText("How can we help you?",false);
        autoCompleteTextViewHelp2.setText("Tell us more",false);
        autoCompleteTextViewHelp1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(!s.toString().isEmpty())
                {
                    textInputLayoutHelp2.setVisibility(View.VISIBLE);
                }
                if(s.toString().equals("I have a question about my account"))
                {
                    autoCompleteTextViewHelp2.setText("Tell us more",false);
                    ArrayAdapter<String> question2Adapter1 = new ArrayAdapter(EmailInquiresActivity.this, R.layout.dropdown_list_item, help2Questions1);
                    autoCompleteTextViewHelp2.setAdapter(question2Adapter1);
                }
                if(s.toString().equals("I have an issue buying products"))
                {
                    autoCompleteTextViewHelp2.setText("Tell us more",false);
                    ArrayAdapter<String> question2Adapter1 = new ArrayAdapter(EmailInquiresActivity.this, R.layout.dropdown_list_item, help2Questions2);
                    autoCompleteTextViewHelp2.setAdapter(question2Adapter1);
                }
                if(s.toString().equals("I have an issue with my order"))
                {
                    autoCompleteTextViewHelp2.setText("Tell us more",false);
                    ArrayAdapter<String> question2Adapter1 = new ArrayAdapter(EmailInquiresActivity.this, R.layout.dropdown_list_item, help2Questions3);
                    autoCompleteTextViewHelp2.setAdapter(question2Adapter1);
                }
                if(s.toString().equals("I have a question about payment system"))
                {
                    autoCompleteTextViewHelp2.setText("Tell us more",false);
                    ArrayAdapter<String> question2Adapter1 = new ArrayAdapter(EmailInquiresActivity.this, R.layout.dropdown_list_item, help2Questions4);
                    autoCompleteTextViewHelp2.setAdapter(question2Adapter1);
                }
                if(s.toString().equals("I have a technical issue"))
                {
                    autoCompleteTextViewHelp2.setText("Tell us more",false);
                    ArrayAdapter<String> question2Adapter1 = new ArrayAdapter(EmailInquiresActivity.this, R.layout.dropdown_list_item, help2Questions5);
                    autoCompleteTextViewHelp2.setAdapter(question2Adapter1);
                }
                if(s.toString().equals("Other"))
                {
                   textInputLayoutHelp2.setVisibility(View.GONE);
                }
                s1 = s.toString();


            }
        });

        autoCompleteTextViewHelp2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                s2 = s.toString();
            }
        });

        Button buttonSubmit = findViewById(R.id.buttonHelpSubmit);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkEmptyFields())
                {
                    submitRequest();
                }
                else
                {
                    return;
                }
            }
        });

    }

    private boolean checkEmptyFields()
    {
        if(editTextDesc.getText().toString().isEmpty())
        {
            Toast.makeText(EmailInquiresActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(name.getText().toString().isEmpty())
        {
            Toast.makeText(EmailInquiresActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(email.getText().toString().isEmpty())
        {
            Toast.makeText(EmailInquiresActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(phoneNumber.getText().toString().isEmpty())
        {
            Toast.makeText(EmailInquiresActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(s1.equals("How can we help you?"))
        {
            Toast.makeText(EmailInquiresActivity.this, "You need to specify your problem!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(s2.equals("Tell us more"))
        {
            Toast.makeText(EmailInquiresActivity.this, "You need to give more details about your problem!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
    private void submitRequest()
    {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Requests");
        String request_id = UUID.randomUUID().toString();
        Requests request = new Requests(request_id,editTextDesc.getText().toString(),s1,s2,name.getText().toString(),email.getText().toString(),phoneNumber.getText().toString(),false,System.currentTimeMillis());
        myRef.child(request_id).setValue(request).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(EmailInquiresActivity.this, "Your Request have been submitted, our team will contact you shortly!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EmailInquiresActivity.this, "Error occurred while submitting your request, please trying again later!", Toast.LENGTH_SHORT).show();
            }
        });
    }



















}