package com.example.mycourseprojectapplication.Activities;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.mycourseprojectapplication.Models.Users;
import com.example.mycourseprojectapplication.R;
import com.example.mycourseprojectapplication.Utilities.ConnectionDetector;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ForgetPasswordActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName(); // used for debugging
    private FirebaseAuth mAuth;
    private TextInputEditText editTextEmail;
    private TextInputLayout textInputLayoutEmail;   // declare our variables
    private ProgressBar progressBar;
    private ArrayList<Users> usersArrayList;
    private int colorPrimary = 0;
    private int colorError = 0;

    private ColorStateList csPrimary = null;
    private ColorStateList csError = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        new ConnectionDetector(this);  // check if user has Internet connection

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   // setup the action toolbar with activity title and back icon functionality
        setTitle("Forget Password");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        colorPrimary = ContextCompat.getColor(this, R.color.colorPrimary);
        colorError = ContextCompat.getColor(this, R.color.error_color);

        csPrimary = ColorStateList.valueOf(colorPrimary);
        csError = ColorStateList.valueOf(colorError);
        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.editTextEmailForgetPassword);
        textInputLayoutEmail = findViewById(R.id.textInputLayoutForgetPassword);  // setup the action toolbar with activity title and back icon functionality
        progressBar = findViewById(R.id.progressBarForgetPassword);
        Button buttonReset = findViewById(R.id.buttonReset);
        usersArrayList = new ArrayList<>();

        getUsers(); // get all users from the database

        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE); // show the progress bar
                if (checkEmailField()) // check for empty field
                {


                    if(checkRegisteredEmail(editTextEmail.getText().toString())) // check if the registered email equals the entered email
                    {
                        FirebaseAuth.getInstance().sendPasswordResetEmail(editTextEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() { // send email to user email
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) { // on complete and task is successful
                                    Toast.makeText(ForgetPasswordActivity.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show(); // show success message
                                } else {
                                    Toast.makeText(ForgetPasswordActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show(); // else show error message
                                }

                                progressBar.setVisibility(View.INVISIBLE); // hide progress bar
                            }
                        }).addOnCanceledListener(new OnCanceledListener() {
                            @Override
                            public void onCanceled() {

                            }
                        });
                    }
                    else
                    {
                        Toast.makeText(ForgetPasswordActivity.this, "The email you have entered does not match our records!", Toast.LENGTH_SHORT).show(); // if entered email does not equal the email in the database, show an error
                        progressBar.setVisibility(View.INVISIBLE); // hide progress bar
                    }
                }
                else
                {
                    Toast.makeText(ForgetPasswordActivity.this, "Please Enter the email!", Toast.LENGTH_SHORT).show(); // if field is empty show error message
                    progressBar.setVisibility(View.INVISIBLE); // hide progress bar
                }

            }
        });
        changeIconColor();
        inputTextFieldsChecker();
    }

    private boolean checkEmailField() // method to check if field is empty or not
    {
        if(editTextEmail.getText().toString().isEmpty())
        {
            return false;
        }
        return true;
    }



    private void inputTextFieldsChecker()
    {
        editTextEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textInputLayoutEmail.setError(null);
                textInputLayoutEmail.setStartIconTintList(csPrimary);
            }

            @Override
            public void afterTextChanged(Editable s) {

                if(s.toString().isEmpty()) // if the field is not in focus and empty set the icon color to red and show an error message
                {
                    textInputLayoutEmail.setError("Email must not be empty");
                    textInputLayoutEmail.setStartIconTintList(csError);
                }
            }
        });

    }

    private void changeIconColor()
    {
        editTextEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(!hasFocus)
                {
                    validateEmail(((EditText) v).getText());
                }
                else
                {
                    textInputLayoutEmail.setError(null);
                    textInputLayoutEmail.setStartIconTintList(csPrimary);
                }
            }
        });
    }

    private void validateEmail(Editable editable)
    {
        if (!TextUtils.isEmpty(editable)) {
            textInputLayoutEmail.setError(null);
            textInputLayoutEmail.setStartIconTintList(ColorStateList.valueOf(Color.GRAY));
        }
        else{
            textInputLayoutEmail.setError("Email must not be empty");
            textInputLayoutEmail.setStartIconTintList(csError);

        }
    }
    private void getUsers() // get all users from the database
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot users : snapshot.getChildren())
                {
                    usersArrayList.add(users.getValue(Users.class));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private boolean checkRegisteredEmail(String email) // method check if entered email equals to the registered email in the database
    {
        for(int i = 0 ; i < usersArrayList.size(); i++)
        {
            if(usersArrayList.get(i).getEmail().equals(email) && !usersArrayList.get(i).isAdmin()) // if email equals entered email and not admin
            {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        new ConnectionDetector(this); // on Resume ( this method is fired before on create )  activity, check if user has connection or not
    }
}