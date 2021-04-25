package com.example.mycourseprojectapplication.Activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.mycourseprojectapplication.Models.Users;
import com.example.mycourseprojectapplication.R;
import com.example.mycourseprojectapplication.Services.MyFirebaseMessagingService;
import com.example.mycourseprojectapplication.Utilities.ConnectionDetector;
import com.example.mycourseprojectapplication.Utilities.UserSession;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();  // this is used for debugging
    private FirebaseAuth mAuth;
    private TextInputEditText editTextEmail,editTextPassword;
    private TextInputLayout textInputLayoutEmail,textInputLayoutPassword;                    // declare our variables
    private ProgressBar progressBar;
    private ArrayList<Users> usersArrayList;
    private UserSession userSession;
    private MyFirebaseMessagingService firebaseMessagingService = new MyFirebaseMessagingService();
    private int colorPrimary = 0;
    private int colorError = 0;

    private ColorStateList csPrimary = null;
    private ColorStateList csError = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   // setup the action toolbar with activity title and back icon functionality
        setTitle("Log in");      // setup the title

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {         // when back is clicked just finish the activity
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        colorPrimary = ContextCompat.getColor(this, R.color.colorPrimary);
        colorError = ContextCompat.getColor(this, R.color.error_color);

        csPrimary = ColorStateList.valueOf(colorPrimary);
        csError = ColorStateList.valueOf(colorError);
        userSession = new UserSession(this); // create user session
        new ConnectionDetector(this);  // check if user has Internet connection
        mAuth = FirebaseAuth.getInstance();   // initialize our authentication object
        editTextEmail = findViewById(R.id.editTextEmailLogin);
        editTextPassword = findViewById(R.id.EditTextPasswordLogin);
        textInputLayoutEmail = findViewById(R.id.textInputLayoutEmailLogin);
        textInputLayoutPassword = findViewById(R.id.textInputLayoutPasswordLogin);  // initialize views and classes
        progressBar = findViewById(R.id.progressBar2);
        Button buttonLogin = findViewById(R.id.buttonSignIn);
        Button buttonSignUp = findViewById(R.id.buttonSignUpLogin);
        usersArrayList = new ArrayList<>();
        TextView forgetPasswordTextView = findViewById(R.id.textViewForgotPassword);
        textInputLayoutEmail.requestFocus();

        textInputLayoutEmail.setStartIconTintList(csPrimary);

        buttonLogin.setOnClickListener(new View.OnClickListener() {            // when login button is clicked
            @Override
            public void onClick(View v) {

                if(checkEmptyInputFields()) {  // check for empty fields before login

                    String email = editTextEmail.getText().toString();
                    String password = editTextPassword.getText().toString();
                    userSession.saveUserEmail(email);  // save the email for later usage
                    login(email, password);  //get the email and password and login the user

                }
            }
        });

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {      // when sign up button is clicked send user to the sign up activity

                Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });


        forgetPasswordTextView.setOnClickListener(new View.OnClickListener() {      // when forget password text is clicked go to forget password activity
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,ForgetPasswordActivity.class);
                startActivity(intent);
            }
        });


        inputTextFieldsChecker(); // this method check if the input for fields based on focus changes and act accordingly
        changeIconColor();
        getUsers(); // get all users from the database
    }

    private void inputTextFieldsChecker() {

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

                if(s.toString().isEmpty())
                {
                    textInputLayoutEmail.setError("Email must not be empty");  // if the field is not in focus and empty set the icon color to red and show an error message
                    textInputLayoutEmail.setStartIconTintList(csError);
                    textInputLayoutEmail.requestFocus();
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
                if(s.toString().isEmpty())
                {
                    textInputLayoutPassword.setError("Password must not be empty");  // if the field is not in focus and empty set the icon color to red and show an error message
                    textInputLayoutPassword.setStartIconTintList(csError);
                    textInputLayoutPassword.requestFocus();
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
    }


    private void login(final String email, String password) {        // this method logs the user the system

        progressBar.setVisibility(View.VISIBLE);      // show progress bar
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (checkAdminUser(email))       // check if user is admin or not and update view accordingly
                            {
                                updateUIAdmin(user);
                            }
                            else {
                               //if (user.isEmailVerified()) {

                                    updateUI(user);

                                } /*else {
                                    Toast.makeText(LoginActivity.this, "Your account is not verified, please check your email for verification and retry!", Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.INVISIBLE);
                                    return;
                                }*/




                        }
                        else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Email / Password is invalid.",
                                    Toast.LENGTH_SHORT).show();
                                                                                             // show error message if email or password is wrong and hide progress bar
                            progressBar.setVisibility(View.INVISIBLE);
                        }

                        // ...
                    }
                });

    }

    private void updateUI(FirebaseUser user) {        // method that sends normal user to main activity

        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        intent.putExtra("user",user);
        userSession.setIsLogin(true);
        startActivity(intent);
        finish();
    }
    private void updateUIAdmin(FirebaseUser user)          // method that sends admin user to admin activity
    {
        Intent intent = new Intent(LoginActivity.this,AdminActivity.class);
        intent.putExtra("user",user);
        startActivity(intent);
        finish();
    }


    private boolean checkEmptyInputFields()         // method to check if input fields are empty or not
    {

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

    private void getUsers()                      // method to get all the users from the database and add them to an arraylist
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
    private boolean checkAdminUser(String email)            // method check if the user is an admin or normal user
    {
        for(int i = 0 ; i < usersArrayList.size(); i++)
        {
            if(usersArrayList.get(i).getEmail().equals(email) && usersArrayList.get(i).isAdmin()) // check if the entered email equals the email in the arraylist and is an admin
            {
                return true;
            }
            else if(usersArrayList.get(i).getEmail().equals(email) && !usersArrayList.get(i).isAdmin())
            {
                userSession.createLoginSession(usersArrayList.get(i).getUsername(),usersArrayList.get(i).getEmail(),usersArrayList.get(i).getUser_id(),usersArrayList.get(i).getUserPhotoUrl()); // save the user data for later usage
                userSession.saveUserEmail(email); // save the email data for later usage
                return false;
            }
        }

        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        new ConnectionDetector(this);  // on Resume ( this method is fired before on create )  activity, check if user has connection or not
    }
}