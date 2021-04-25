package com.example.mycourseprojectapplication.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.mycourseprojectapplication.Activities.LoginActivity;
import com.example.mycourseprojectapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


public class EditProfileFragment extends BottomSheetDialogFragment {


    private TextInputEditText editTextEmail,editTextUsername;
    private TextInputLayout textInputLayoutEmail,textInputLayoutUsername;
    private FirebaseAuth mAuth;
    private Button buttonUpdate;
    public EditProfileFragment() {
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
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        editTextUsername = view.findViewById(R.id.textInputProfileUs);
        editTextEmail = view.findViewById(R.id.textInputProfileEm);
        textInputLayoutUsername = view.findViewById(R.id.textInputLayoutProfileUsername);
        textInputLayoutUsername = view.findViewById(R.id.textInputLayoutProfileEmail);
        Bundle bundle = getArguments();
        editTextUsername.setText(bundle.getString("username"));
        editTextEmail.setText(bundle.getString("email"));
        buttonUpdate = view.findViewById(R.id.buttonProfileUpdate);
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkForEmptyFields())
                {
                    if(checkInputFields())
                    {
                        updateProfile();
                    }


                }
            }
        });
        return view;
    }
    private boolean checkInputFields()
    {
        if(!Patterns.EMAIL_ADDRESS.matcher(editTextEmail.getText().toString().trim()).matches()) //check the entered email is valid or not
        {
            Toast.makeText(getContext(), "Email format is Invalid!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(editTextUsername.getText().toString().trim().length() > 15) // if the field length is more than 15 characters set the icon color to red and show an error message
        {
            Toast.makeText(getContext(), "Username should not exceed 15 characters", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private boolean checkForEmptyFields()
    {
        if(editTextUsername.getText().toString().trim().isEmpty())
        {
            Toast.makeText(getContext(), "username must not be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(editTextEmail.getText().toString().trim().isEmpty())
        {
            Toast.makeText(getContext(), "Email must not be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void updateProfile()
    {

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Users");
        myRef.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Map<String, Object> values = new HashMap();
                for (DataSnapshot user : snapshot.getChildren()) {
                    values.put(user.getKey(),user.getValue());
                }

                values.put("username",editTextUsername.getText().toString());
                values.put("email",editTextEmail.getText().toString());
                myRef.child(mAuth.getCurrentUser().getUid()).updateChildren(values).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (!editTextEmail.getText().toString().equals(mAuth.getCurrentUser().getEmail())) {
                            mAuth.getCurrentUser().updateEmail(editTextEmail.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Toast.makeText(getContext(), "Profile Details Updated Successfully", Toast.LENGTH_SHORT).show();
                                    mAuth.getCurrentUser().sendEmailVerification();
                                    dismiss();
                                    mAuth.signOut();
                                    Intent intent = new Intent(getContext(), LoginActivity.class);
                                    startActivity(intent);
                                    getActivity().finishAffinity();
                                }
                            });

                        }
                        else
                        {
                            Toast.makeText(getContext(), "Profile Details Updated Successfully", Toast.LENGTH_SHORT).show();
                            dismiss();
                        }
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}