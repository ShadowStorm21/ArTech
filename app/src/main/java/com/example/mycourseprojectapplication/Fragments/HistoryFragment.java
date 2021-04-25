package com.example.mycourseprojectapplication.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycourseprojectapplication.Adapters.HistoryListAdapter;
import com.example.mycourseprojectapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment {

    private RecyclerView recyclerViewHistory;
    private FirebaseAuth mAuth;
    private ArrayList<String> rewardsPrice;
    private ArrayList<String> rewardCodes;
    private ArrayList<Long> redemptionDate;
    private HistoryListAdapter historyListAdapter;

    public HistoryFragment() {
        // Required empty public constructor
    }

    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        rewardsPrice = new ArrayList<>();
        rewardCodes = new ArrayList<>();
        redemptionDate = new ArrayList<>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        recyclerViewHistory = view.findViewById(R.id.historyRecycler);
        historyListAdapter = new HistoryListAdapter(rewardsPrice,rewardCodes,redemptionDate);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerViewHistory.setLayoutManager(layoutManager);
        getUserCodes();
        return view;
    }

    private void getUserCodes()
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Rewards");
        myRef.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                rewardCodes.clear();
                redemptionDate.clear();
                rewardsPrice.clear();
                for(DataSnapshot codes : snapshot.getChildren())
                {
                    rewardCodes.add(codes.child("code").getValue().toString());
                    rewardsPrice.add(codes.child("discount").getValue().toString());
                    redemptionDate.add(Long.parseLong(codes.child("redeemDate").getValue().toString()));
                }
                recyclerViewHistory.setAdapter(historyListAdapter);
                historyListAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}