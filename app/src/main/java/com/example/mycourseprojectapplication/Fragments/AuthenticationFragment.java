package com.example.mycourseprojectapplication.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.mycourseprojectapplication.Activities.LoginActivity;
import com.example.mycourseprojectapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;


public class AuthenticationFragment extends BottomSheetDialogFragment {

    private TextInputEditText editTextPassword,editTextEmail;
    private Button buttonVerify;
    private FirebaseAuth mAuth;
    private String currentUid = "";

    public AuthenticationFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        currentUid = mAuth.getCurrentUser().getUid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view =  inflater.inflate(R.layout.fragment_authentication, container, false);
        editTextEmail = view.findViewById(R.id.textInputAuthenticationEmail);
        editTextPassword = view.findViewById(R.id.textInputAuthenticationPass);
        buttonVerify = view.findViewById(R.id.buttonAuthenticationVerify);
        buttonVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkForEmptyFields()) {

                    AuthCredential credential = EmailAuthProvider.getCredential(editTextEmail.getText().toString(), editTextPassword.getText().toString());
                                            // Prompt the user to re-provide their sign-in credentials
                                            Objects.requireNonNull(mAuth.getCurrentUser()).reauthenticate(credential).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                                                            mAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                databaseReference.child(currentUid).removeValue();
                                                                                Toast.makeText(getContext(), "Account Deleted!", Toast.LENGTH_SHORT).show();
                                                                                Intent intent = new Intent(getContext(), LoginActivity.class);
                                                                                startActivity(intent);
                                                                                getActivity().finishAffinity();
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getContext(), "Email/Password is wrong", Toast.LENGTH_SHORT).show();
                                                    dismiss();
                                                }
                                            });

                                        }



            }
        });
       return view;
    }
    private boolean checkForEmptyFields()
    {
        if(editTextEmail.getText().toString().isEmpty())
        {
            return false;
        }
        if(editTextPassword.getText().toString().isEmpty())
        {
            return false;
        }
        return true;
    }
}