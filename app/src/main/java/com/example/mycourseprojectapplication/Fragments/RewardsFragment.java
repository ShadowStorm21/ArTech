package com.example.mycourseprojectapplication.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycourseprojectapplication.Activities.RewardsActivity;
import com.example.mycourseprojectapplication.Adapters.RewardAdapter;
import com.example.mycourseprojectapplication.Models.Rewards;
import com.example.mycourseprojectapplication.Models.Users;
import com.example.mycourseprojectapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class RewardsFragment extends Fragment {
    private final String TAG = this.getClass().getSimpleName();
    private ArrayList<Rewards> rewardsArrayList;
    private FirebaseAuth mAuth;
    private Users user;
    private RewardAdapter rewardAdapter;
    public RewardsFragment() {
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
       View view = inflater.inflate(R.layout.fragment_rewards, container, false);
        RecyclerView recyclerViewRewards = view.findViewById(R.id.rewardRecycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rewardsArrayList = new ArrayList<>();
        rewardAdapter = new RewardAdapter(rewardsArrayList);
        recyclerViewRewards.setAdapter(rewardAdapter);
        recyclerViewRewards.setLayoutManager(layoutManager);
        getUserPoints();
        rewardAdapter.notifyDataSetChanged();
       return view;
    }

    private void addRewards(int userPoints)
    {

        Rewards rewards = new Rewards("$5",1000,R.drawable.gradint_background, (1000 - userPoints),userPoints);
        Rewards rewards1 = new Rewards("$10",2000,R.drawable.gradint_background1, (2000 - userPoints),userPoints);
        Rewards rewards2 = new Rewards("$25",3500,R.drawable.gradint_background2, (3500 - userPoints),userPoints);
        Rewards rewards3 = new Rewards("$50",7000,R.drawable.gradint_background3, (7000 - userPoints),userPoints);
        rewardsArrayList.add(rewards);
        rewardsArrayList.add(rewards1);
        rewardsArrayList.add(rewards2);
        rewardsArrayList.add(rewards3);
        rewardAdapter.notifyDataSetChanged();
    }

    private void getUserPoints()
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users");
        myRef.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                user = snapshot.getValue(Users.class);
                Log.i(TAG,user.toString());
                addRewards(user.getUserRewardPoints());
                ((RewardsActivity) getActivity()).updateTextView(user.getUserRewardPoints());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }




}