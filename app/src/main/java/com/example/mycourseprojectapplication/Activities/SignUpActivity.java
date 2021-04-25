package com.example.mycourseprojectapplication.Activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private final String TAG = SignUpActivity.this.getClass().getSimpleName(); // this is used for debugging
    private FirebaseAuth mAuth;
    private TextInputEditText editTextName,editTextEmail,editTextPassword;
    private EditText editTextConfirmPassword;                                                                                       // declare our variables
    private TextInputLayout textInputLayoutName,textInputLayoutEmail,textInputLayoutPassword,textInputLayoutConfirmPassword;
    private ProgressBar progressBar;
    private static final String PATTERN_SPECIAL_SYM = "[!@#$%&*()_+=|<>?{}/~-]";
    private Pattern patternSym = Pattern.compile(PATTERN_SPECIAL_SYM, Pattern.CASE_INSENSITIVE);
    private int colorPrimary = 0;
    private int colorError = 0;

    private ColorStateList csPrimary = null;
    private ColorStateList csError = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        new ConnectionDetector(this);          // check if user has Internet connection

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);     // setup the action toolbar with activity title and back icon functionality
        setTitle("Create Account");                                  // setup the title
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {               // when back is clicked just finish the activity
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        colorPrimary = ContextCompat.getColor(this, R.color.colorPrimary);
        colorError = ContextCompat.getColor(this, R.color.error_color);

        csPrimary = ColorStateList.valueOf(colorPrimary);
        csError = ColorStateList.valueOf(colorError);
        mAuth = FirebaseAuth.getInstance();        // initialize our authentication object
        editTextName = findViewById(R.id.EditTextName);
        editTextEmail = findViewById(R.id.EditTextEmailSignUp);
        editTextPassword = findViewById(R.id.EditTextPasswordSignUp);
        editTextConfirmPassword = findViewById(R.id.EditTextCpassword);
        textInputLayoutName = findViewById(R.id.TextInputLayoutName);
        textInputLayoutEmail = findViewById(R.id.TextInputLayoutEmailSignUp);             // initialize views and classes
        textInputLayoutPassword = findViewById(R.id.TextInputLayoutPasswordSignUp);
        textInputLayoutConfirmPassword = findViewById(R.id.TextInputLayoutCpassword);
        Button buttonSignUp = findViewById(R.id.buttonSignUp);
        Button buttonLogin = findViewById(R.id.buttonLoginSignUp);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        textInputLayoutName.requestFocus();
        textInputLayoutName.setStartIconTintList(csPrimary);


        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(checkEmptyInputFields()) {        // when sign up button is clicked check for empty input fields first

                    if(checkFieldConditions()) {    // check if fields met the conditions

                        String email = editTextEmail.getText().toString();
                        String password = editTextPassword.getText().toString();        // get the email and password and sign up the user
                        signUp(email, password);
                    }


                }
                else
                {
                    Toast.makeText(SignUpActivity.this, "Please fill the empty fields!", Toast.LENGTH_SHORT).show();         // if empty show a toast message with an error message
                }



    }
});

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUI();         // when button login is clicked send the user to the login page
            }
        });
        inputTextFieldsChecker();     // this method check if the input fields based focus changes and act accordingly
        changeIconColor();
    }



    private void inputTextFieldsChecker()
    {
        editTextName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textInputLayoutName.setError(null);
                textInputLayoutName.setStartIconTintList(csPrimary);
            }

            @Override
            public void afterTextChanged(Editable s) {

                if(s.toString().isEmpty())    // if the field is not in focus and empty set the icon color to red and show an error message
                {
                    textInputLayoutName.setStartIconTintList(csError);
                    textInputLayoutName.setError("Username must not be empty!");
                }

            }
        });

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

        editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textInputLayoutPassword.setError(null);
                textInputLayoutPassword.setStartIconTintList(csPrimary);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().isEmpty())  // if the field is not in focus and empty set the icon color to red and show an error message
                {
                    textInputLayoutPassword.setError("Password must not be empty");
                    textInputLayoutPassword.setStartIconTintList(csError);
                }

            }
        });

        editTextConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textInputLayoutConfirmPassword.setError(null);
                textInputLayoutConfirmPassword.setStartIconTintList(csPrimary);
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.toString().isEmpty()) {   // if the field is not in focus and empty set the icon color to red and show an error message
                    textInputLayoutConfirmPassword.setError("Confirm Password must not be empty");
                    textInputLayoutConfirmPassword.setStartIconTintList(csError);
                }

            }
        });

    }

    private void changeIconColor()
    {
        editTextName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                {
                    validateUsername(((EditText) v).getText());
                }
                else
                {
                    textInputLayoutName.setError(null);
                    textInputLayoutName.setStartIconTintList(csPrimary);
                }
            }
        });

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

        editTextPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                {
                    validatePassword(((EditText) v).getText());
                }
                else
                {
                    textInputLayoutPassword.setError(null);
                    textInputLayoutPassword.setStartIconTintList(csPrimary);
                }
            }
        });

        editTextConfirmPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(!hasFocus)
                {
                    validateConfirmPassword(((EditText) v).getText());
                }
                else
                {
                    textInputLayoutConfirmPassword.setError(null);
                    textInputLayoutConfirmPassword.setStartIconTintList(csPrimary);
                }
            }
        });
    }

    private boolean checkFieldConditions()  // this method checks if the fields conditions are valid or not
    {
        if(editTextName.getText().toString().trim().length() > 15) // if the field length is more than 15 characters set the icon color to red and show an error message
        {
            textInputLayoutName.setStartIconTintList(csError);
            textInputLayoutName.setError("Username should not exceed 15 characters");
            return false;
        }
        if(patternSym.matcher(editTextName.getText().toString().trim()).find())
        {
            textInputLayoutName.setStartIconTintList(csError);
            textInputLayoutName.setError("Username should not contain special characters!");
            return false;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(editTextEmail.getText().toString().trim()).matches()) //check the entered email is valid or not
        {
            textInputLayoutEmail.setStartIconTintList(csError);
            textInputLayoutEmail.setError("Email format is Invalid!");
            return false;
        }
        if(editTextPassword.getText().toString().trim().length() < 8)   // check if password length is more than 8 characters and and less than 32
        {
            textInputLayoutPassword.setError("Password should be more than 8 characters");
            textInputLayoutPassword.setStartIconTintList(csError);
            return false;
        }
        if(editTextPassword.getText().toString().trim().length() > 32)   // check if password length is more than 8 characters and and less than 32
        {
            textInputLayoutPassword.setError("Password should be less than 32 characters");
            textInputLayoutPassword.setStartIconTintList(csError);
            return false;
        }
        String password = editTextPassword.getText().toString().trim();
        if(!editTextConfirmPassword.getText().toString().trim().matches(password))        // check if both passwords match
        {
            textInputLayoutConfirmPassword.setError("Passwords are not the same");
            textInputLayoutConfirmPassword.setStartIconTintList(csError);
            return false;
        }
        return true;
    }



    private boolean checkEmptyInputFields() // this method checks for empty fields
    {

        if(editTextName.getText().toString().trim().isEmpty())
        {
            textInputLayoutName.setError("Username must not be empty");
            textInputLayoutName.setStartIconTintList(csError);
            return false;
        }

        if(editTextEmail.getText().toString().trim().isEmpty())
        {
            textInputLayoutEmail.setError("Email must not be empty");
            textInputLayoutEmail.setStartIconTintList(csError);
            return false;
        }
        if(editTextPassword.getText().toString().trim().isEmpty())
        {
            textInputLayoutPassword.setError("Password must not be empty");
            textInputLayoutPassword.setStartIconTintList(csError);
            return false;
        }
        if(editTextConfirmPassword.getText().toString().trim().isEmpty())
        {
            textInputLayoutConfirmPassword.setError("Confirm Password must not be empty");
            textInputLayoutConfirmPassword.setStartIconTintList(csError);
            return false;
        }

        return true;


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
    private void validatePassword(Editable editable)
    {
        if (!TextUtils.isEmpty(editable)) {
            textInputLayoutPassword.setError(null);
            textInputLayoutPassword.setStartIconTintList(ColorStateList.valueOf(Color.GRAY));
        }
        else{
            textInputLayoutPassword.setError("Password must not be empty");
            textInputLayoutPassword.setStartIconTintList(csError);

        }
    }
    private void validateUsername(Editable editable)
    {
        if (!TextUtils.isEmpty(editable)) {
            textInputLayoutName.setError(null);
            textInputLayoutName.setStartIconTintList(ColorStateList.valueOf(Color.GRAY));
        }
        else if(patternSym.matcher(editTextName.getText().toString()).find())
        {
            textInputLayoutName.setStartIconTintList(csError);
            textInputLayoutName.setError("Username should not contain special characters!");

        }
        else{
            textInputLayoutName.setError("Username must not be empty");
            textInputLayoutName.setStartIconTintList(csError);

        }
    }
    private void validateConfirmPassword(Editable editable)
    {
        if (!TextUtils.isEmpty(editable)) {
            textInputLayoutConfirmPassword.setError(null);
            textInputLayoutConfirmPassword.setStartIconTintList(ColorStateList.valueOf(Color.GRAY));
        }
        else{
            textInputLayoutConfirmPassword.setError("Confirm Password must not be empty");
            textInputLayoutConfirmPassword.setStartIconTintList(csError);

        }
    }


    private void signUp(String email,String password)  // method to sign up the user
    {
        progressBar.setVisibility(View.VISIBLE); // show progress bar
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign up success, update UI
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            user.sendEmailVerification();
                            FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
                                @Override
                                public void onSuccess(String s) {

                                    String msg = "token: "+s;
                                    Log.d(TAG, msg);
                                    String username = editTextName.getText().toString();
                                    addUserData(user,username,s); // add user info to the database
                                }
                            });
                        }  else {
                            // If sign up fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());

                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(SignUpActivity.this, Objects.requireNonNull(task.getException()).getMessage()+"",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });


    }


    private void updateUI() {       // method to send the user to the login page

        Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void addUserData(FirebaseUser user, String username,String token)  // method to add user data to the database
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users");
        Users newUser = new Users(user.getUid(),username,user.getEmail(),false,0,"default",token);   // create user object for each new user

            myRef.child(user.getUid()).setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    progressBar.setVisibility(View.INVISIBLE);
                    updateUI();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    progressBar.setVisibility(View.INVISIBLE);
                }
            });
        

    }

    @Override
    protected void onResume() {
        super.onResume();
        new ConnectionDetector(this); // on Resume ( this method is fired before on create )  activity, check if user has connection or not
    }
}