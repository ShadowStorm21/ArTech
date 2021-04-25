package com.example.mycourseprojectapplication.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.mycourseprojectapplication.Adapters.NotificationAdapter;
import com.example.mycourseprojectapplication.Models.Notification;
import com.example.mycourseprojectapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;


public class NotificationFragment extends Fragment {

    private RecyclerView recyclerViewNotification;
    private ArrayList<Notification> notificationsArrayList;
    private NotificationAdapter notificationAdapter;
    private FirebaseAuth mAuth;
    private LottieAnimationView lottieAnimationView;
    private TextView textViewEmptyNotifications;
    private FloatingActionButton floatingActionButtonClearAll;
    private ProgressBar progressBar;


    public NotificationFragment() {
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
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        recyclerViewNotification = view.findViewById(R.id.notification_recycler);
        progressBar = view.findViewById(R.id.progressBarNotifications);
        lottieAnimationView = view.findViewById(R.id.lottieAnimationViewNotification);
        textViewEmptyNotifications = view.findViewById(R.id.textViewEmptyNotification);
        floatingActionButtonClearAll = view.findViewById(R.id.floatingActionButtonClearAll);
        notificationsArrayList = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(notificationsArrayList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerViewNotification.setLayoutManager(layoutManager);
        recyclerViewNotification.setAdapter(notificationAdapter);
        getNotifications();
        runLayoutAnimation(recyclerViewNotification);
        progressBar.setVisibility(View.GONE);
        floatingActionButtonClearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar.setVisibility(View.VISIBLE);
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("Notifications");
                for(int i = 0 ; i < notificationsArrayList.size(); i++)
                {

                    myRef.child(mAuth.getCurrentUser().getUid()).child(notificationsArrayList.get(i).getNotification_id()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful())
                            {

                                notificationAdapter.notifyDataSetChanged();
                                progressBar.setVisibility(View.GONE);

                            }
                        }
                    });

                }

            }
        });
        return view;
    }

    private void getNotifications()
    {
        try {
            progressBar.setVisibility(View.VISIBLE);
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Notifications");
            myRef.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    notificationsArrayList.clear();
                    for (DataSnapshot notifications : snapshot.getChildren()) {
                        notificationsArrayList.add(notifications.getValue(Notification.class));

                    }
                    if (notificationsArrayList.size() == 0) {
                        lottieAnimationView.setVisibility(View.VISIBLE);
                        textViewEmptyNotifications.setVisibility(View.VISIBLE);
                        floatingActionButtonClearAll.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                    } else if(notificationsArrayList.size() > 4)
                    {
                        floatingActionButtonClearAll.setVisibility(View.VISIBLE);
                    }
                    else {
                        floatingActionButtonClearAll.setVisibility(View.GONE);
                        lottieAnimationView.setVisibility(View.GONE);
                        textViewEmptyNotifications.setVisibility(View.GONE);
                        Collections.sort(notificationsArrayList,DATE_COMPARATOR);
                        progressBar.setVisibility(View.GONE);
                    }
                   notificationAdapter.notifyDataSetChanged();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private static final Comparator<Notification> DATE_COMPARATOR = new Comparator<Notification>() {
        @Override
        public int compare(Notification o1, Notification o2) {
            Date date1 = new Date();
            date1.setTime(o1.getNotification_timestamp());
            Date date2 = new Date();
            date2.setTime(o2.getNotification_timestamp());
            return date1.compareTo(date2);
        }
    }.reversed();

    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_slide_bottom);
        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();

    }
}