package com.example.mycourseprojectapplication.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.mycourseprojectapplication.Activities.LoginActivity;
import com.example.mycourseprojectapplication.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;


public class PasswordEditFragment extends BottomSheetDialogFragment {

    private TextInputEditText editTextPassword,editTextConfirmPassword,editTextCurrentPassword;
    private Button buttonUpdatePassword;
    private FirebaseAuth mAuth;
    private boolean isCorrect = false;



    public PasswordEditFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view = inflater.inflate(R.layout.fragment_password_edit, container, false);
        editTextPassword = view.findViewById(R.id.textInputProfilePass);
        editTextConfirmPassword = view.findViewById(R.id.textInputProfileConfrimPass);
        editTextCurrentPassword = view.findViewById(R.id.textInputProfileCurrentPassword);
        buttonUpdatePassword = view.findViewById(R.id.buttonProfileUpdatePassword);

        buttonUpdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkForEmptyFields())
                {
                    if(checkCurrentPassword())
                    {
                        if(checkForPasswords())
                        {
                            mAuth.getCurrentUser().updatePassword(editTextPassword.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getContext(), "Password Update Successfully!", Toast.LENGTH_SHORT).show();
                                    mAuth.signOut();
                                    Intent intent = new Intent(getContext(), LoginActivity.class);
                                    startActivity(intent);
                                    getActivity().finishAffinity();
                                    dismiss();
                                }
                            });

                        }
                    }
                    else {
                        Toast.makeText(getContext(), "Current password doesn't match with our records!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });
       return view;
    }

    private boolean checkCurrentPassword()
    {

        mAuth.signInWithEmailAndPassword(Objects.requireNonNull(mAuth.getCurrentUser().getEmail()), editTextCurrentPassword.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                isCorrect = true;
            }
        });

        return isCorrect;
    }
    private boolean checkForEmptyFields()
    {
        if(editTextCurrentPassword.getText().toString().trim().isEmpty())
        {
            Toast.makeText(getContext(), "Current password must not be empty!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(editTextPassword.getText().toString().trim().isEmpty())
        {
            Toast.makeText(getContext(), "New password must not be empty!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(editTextConfirmPassword.getText().toString().trim().isEmpty())
        {
            Toast.makeText(getContext(), "Confirm new password must not be empty!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private boolean checkForPasswords()
    {
        if(editTextPassword.getText().toString().trim().length() < 8 || editTextPassword.getText().toString().trim().length() > 32)   // check if password length is more than 8 characters and and less than 32
        {
            Toast.makeText(getContext(), "Password should be more than 8 characters and less than 32 characters!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(editTextPassword.getText().toString().trim().equals(editTextCurrentPassword.getText().toString().trim()))
        {
            Toast.makeText(getContext(), "new password should be different from the old password!", Toast.LENGTH_SHORT).show();
            return false;
        }
        String password = editTextPassword.getText().toString().trim();
        if(!editTextConfirmPassword.getText().toString().trim().matches(password))        // check if both passwords match
        {
            Toast.makeText(getActivity(), "Passwords are not the same!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}