package com.example.mycourseprojectapplication.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycourseprojectapplication.Activities.RewardsActivity;
import com.example.mycourseprojectapplication.Fragments.CodeGenerateFragment;
import com.example.mycourseprojectapplication.Models.Rewards;
import com.example.mycourseprojectapplication.Models.Users;
import com.example.mycourseprojectapplication.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RewardAdapter extends RecyclerView.Adapter<RewardAdapter.RewardViewHolder> {

    private final ArrayList<Rewards> rewardsArrayList;
    private Context context;
    private FirebaseAuth mAuth;
    private Users user;
    public RewardAdapter(ArrayList<Rewards> rewardsArrayList) {
        this.rewardsArrayList = rewardsArrayList;
    }

    public class RewardViewHolder extends RecyclerView.ViewHolder
    {
        public TextView textViewCouponPrice,textViewPointsLeft,textViewTotalPoints;
        public Button redeem;
        public ConstraintLayout constraintLayout;
        public ProgressBar progressBar;
        public RewardViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewCouponPrice = itemView.findViewById(R.id.textViewCoponPrice);
            textViewPointsLeft = itemView.findViewById(R.id.textViewPointsLeft);
            redeem = itemView.findViewById(R.id.buttonRedeem);
            constraintLayout = itemView.findViewById(R.id.CoponLayout);
            progressBar = itemView.findViewById(R.id.progressBarCopon);
            textViewTotalPoints = itemView.findViewById(R.id.textViewTotalPoints);
        }
    }

    @NonNull
    @Override
    public RewardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reward_item,parent,false);
        context = view.getContext();
        mAuth = FirebaseAuth.getInstance();
        return new RewardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RewardViewHolder holder, int position) {
        Rewards reward = rewardsArrayList.get(position);
        holder.textViewCouponPrice.setText(reward.getReward_price());
        holder.constraintLayout.setBackgroundResource(reward.getReward_drawable());
        holder.textViewTotalPoints.setText(reward.getReward_points()+"");
        if(reward.getPointsLeft() <= 0)
        {
            holder.textViewPointsLeft.setText("Points reached!");
        }
        else {
            holder.textViewPointsLeft.setText(reward.getPointsLeft() + " points left");
        }
        holder.progressBar.setMax(reward.getReward_points());
        holder.progressBar.setProgress(reward.getUserPoints());
        holder.redeem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(reward.getPointsLeft() <= 0)
                {

                    new MaterialAlertDialogBuilder(context)                           // create an alert before logging out
                            .setTitle("Alert")
                            .setMessage("Are you sure you want to redeem your points?")
                            .setCancelable(true)
                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("discount",reward.getReward_price());
                                    bundle.putInt("points",reward.getReward_points());
                                    CodeGenerateFragment codeBottomFragment = new CodeGenerateFragment();
                                    codeBottomFragment.setArguments(bundle);
                                    codeBottomFragment.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialog) {
                                            getUserPoints();
                                        }
                                    });
                                    codeBottomFragment.show(((AppCompatActivity)context).getSupportFragmentManager(),
                                            "ModalBottomSheet");
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                          dialogInterface.dismiss();

                        }
                    }).show();

                }
                else
                {
                    Toast.makeText(context, "You don't have enough points yet!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return rewardsArrayList.size();
    }

    private void addRewards(int userPoints)
    {
        rewardsArrayList.clear();
        Rewards rewards = new Rewards("$5",1000,R.drawable.gradint_background, (1000 - userPoints),userPoints);
        Rewards rewards1 = new Rewards("$10",2000,R.drawable.gradint_background1, (2000 - userPoints),userPoints);
        Rewards rewards2 = new Rewards("$25",3500,R.drawable.gradint_background2, (3500 - userPoints),userPoints);
        Rewards rewards3 = new Rewards("$50",7000,R.drawable.gradint_background3, (7000 - userPoints),userPoints);
        rewardsArrayList.add(rewards);
        rewardsArrayList.add(rewards1);
        rewardsArrayList.add(rewards2);
        rewardsArrayList.add(rewards3);
        notifyDataSetChanged();
    }

    private void getUserPoints()
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users");
        myRef.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                user = snapshot.getValue(Users.class);
                addRewards(user.getUserRewardPoints());
                ((RewardsActivity) context).updateTextView(user.getUserRewardPoints());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
