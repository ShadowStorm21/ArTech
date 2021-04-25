package com.example.mycourseprojectapplication.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.example.mycourseprojectapplication.Activities.HelpActivity;
import com.example.mycourseprojectapplication.R;


public class HelpFragment extends Fragment {



    public HelpFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_help, container, false);
        LinearLayout linearLayoutAboutUs = view.findViewById(R.id.linearLayoutAboutUs);
        LinearLayout linearLayoutFAQ = view.findViewById(R.id.LinearLayoutFAQ);
        LinearLayout linearLayoutTOS = view.findViewById(R.id.LinearLayoutTOS);
        LinearLayout linearLayoutPP = view.findViewById(R.id.linearLayoutPP);
        LinearLayout linearLayoutLisc = view.findViewById(R.id.LinearLayoutLice);
        LinearLayout linearLayoutAuthor = view.findViewById(R.id.LinearLayoutAuthor);


        linearLayoutAboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), HelpActivity.class);
                intent.putExtra("flag","aboutus");
                startActivity(intent);
            }
        });

        linearLayoutFAQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), HelpActivity.class);
                intent.putExtra("flag","faq");
                startActivity(intent);
            }
        });

        linearLayoutTOS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), HelpActivity.class);
                intent.putExtra("flag","tos");
                startActivity(intent);
            }
        });

        linearLayoutPP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), HelpActivity.class);
                intent.putExtra("flag","pp");
                startActivity(intent);
            }
        });

        linearLayoutLisc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), HelpActivity.class);
                intent.putExtra("flag","lisc");
                startActivity(intent);
            }
        });

        linearLayoutAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), HelpActivity.class);
                intent.putExtra("flag","author");
                startActivity(intent);
            }
        });
        return view;
    }
}