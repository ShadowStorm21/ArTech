package com.example.mycourseprojectapplication.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.example.mycourseprojectapplication.Activities.PaymentActivity;
import com.example.mycourseprojectapplication.Models.Users;
import com.example.mycourseprojectapplication.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class CodeBottomFragment extends BottomSheetDialogFragment {

    private FirebaseAuth mAuth;
    private Users user;
    private String price = "";
    private TextInputLayout textInputLayout;
    private ArrayList<String> userCodes;
    private ArrayList<String> prices;
    private ArrayList<String> ids;
    public CodeBottomFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        Log.i("Code",mAuth.getCurrentUser().getUid());
        userCodes = new ArrayList<>();
        prices = new ArrayList<>();
        ids = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view = inflater.inflate(R.layout.fragment_code_bottom, container, false);
        TextInputEditText editTextCode = view.findViewById(R.id.TextEditTextCode);
        textInputLayout = view.findViewById(R.id.textInputLayoutCode);
        Button buttonUse = view.findViewById(R.id.buttonUseCode);
        getValidCodes();
        buttonUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(editTextCode.getText().toString().trim().isEmpty())
                {
                    textInputLayout.setError("Code must not be empty!");

                }
                else
                {
                    validateCodes(editTextCode.getText().toString());
                }
            }
        });

       return view;
    }

    private void getValidCodes()
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Rewards");
        myRef.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot codes : snapshot.getChildren())
                {
                    userCodes.add(codes.child("code").getValue().toString());
                    prices.add(codes.child("discount").getValue().toString());
                    ids.add(codes.child("reward_id").getValue().toString());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private boolean validateCodes(String code)
    {
        if(userCodes.size() == 0)
        {
            return false;
        }
        for(int i = 0 ; i < userCodes.size(); i++)
        {
            if(userCodes.get(i).equals(code))
            {
                ((PaymentActivity) getActivity()).setDiscount(prices.get(i),ids.get(i));
                dismiss();
                return true;
            }

        }
        textInputLayout.setError("Invalid Code!");
        return false;
    }

}