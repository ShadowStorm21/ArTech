package com.example.mycourseprojectapplication.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.mycourseprojectapplication.Activities.LoginActivity;
import com.example.mycourseprojectapplication.Activities.MainActivity;
import com.example.mycourseprojectapplication.R;
import com.example.mycourseprojectapplication.Utilities.UserSession;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;


public class LogoutFragment extends Fragment {


    private FirebaseAuth mAuth; // declare our variables
    public LogoutFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance(); // initialize our authentication object
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_logout, container, false);
        new MaterialAlertDialogBuilder(getContext())                           // create an alert before logging out
                .setTitle("Alert")
                .setMessage("Are you sure you want to logout from the Application?")
                .setCancelable(true)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        mAuth.signOut(); // sign out the user from firebase authentication
                       Intent intent = new Intent(getContext(), LoginActivity.class);
                       new UserSession(getContext()).setIsLogin(false);
                       startActivity(intent);
                       getActivity().finish();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Intent intent = new Intent(getContext(), MainActivity.class);     // on cancel return the main activity
                startActivity(intent);
                new UserSession(getContext()).setIsLogin(true);
                getActivity().finish();

            }
        }).show();
        return view;
    }
}