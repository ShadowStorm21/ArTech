package com.example.mycourseprojectapplication.Fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.mycourseprojectapplication.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static android.content.Context.CLIPBOARD_SERVICE;


public class CodeGenerateFragment extends BottomSheetDialogFragment {

    private DialogInterface.OnDismissListener onDismissListener;
    private String code;
    private FirebaseAuth mAuth;
    private Bundle bundle;
    private Map<String,Object> values;
    private int previousValue = 0;

    public CodeGenerateFragment() {
        // Required empty public constructor
    }

    public static CodeGenerateFragment newInstance() {
        CodeGenerateFragment fragment = new CodeGenerateFragment();
        return fragment;
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
        View view = inflater.inflate(R.layout.fragment_code_generate, container, false);
        bundle = getArguments();
        if(bundle == null)
        {
            dismiss();
        }
        code = UUID.randomUUID().toString().substring(0,8);
        TextView textViewCode = view.findViewById(R.id.textViewCodeGenerated);
        textViewCode.setText(code);
        Button buttonCopy = view.findViewById(R.id.buttonCopyToCB);
        Button buttonClose = view.findViewById(R.id.buttonCloseSheet);

        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        buttonCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager manager = (ClipboardManager) getContext().getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("text", code);
                manager.setPrimaryClip(clipData);
                Toast.makeText(getContext(), "Code Copied", Toast.LENGTH_LONG).show();
            }
        });
        addCodeToDB();
        return view;
    }

    private void addCodeToDB()
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Rewards");
        String key = myRef.push().getKey();
        Map<String,Object> values = new HashMap<>();
        values.put("code",code);
        values.put("reward_id",key);
        values.put("discount",bundle.getString("discount"));
        values.put("redeemDate",System.currentTimeMillis());
        myRef.child(mAuth.getCurrentUser().getUid()).child(key).setValue(values);
        updateUserPoints(bundle.getInt("points"));

    }
    private void updateUserPoints(int points)
    {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users");
        values = new HashMap<>();
        myRef.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(!snapshot.child("userRewardPoints").getValue().toString().equals(0)) {
                    previousValue = Integer.parseInt(snapshot.child("userRewardPoints").getValue().toString());

                    for (DataSnapshot user : snapshot.getChildren()) {
                        values.put(user.getKey(), user.getValue());

                    }
                    values.put("userRewardPoints", previousValue - points);
                }
                myRef.child(mAuth.getCurrentUser().getUid()).updateChildren(values);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialog);
        }
    }
}