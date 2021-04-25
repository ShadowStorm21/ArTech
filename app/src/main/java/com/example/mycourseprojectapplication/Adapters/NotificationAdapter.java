package com.example.mycourseprojectapplication.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycourseprojectapplication.Models.Notification;
import com.example.mycourseprojectapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private final ArrayList<Notification> notifications;
    private FirebaseAuth mAuth;
    private Context context;
    private RecyclerView mRecyclerView;
    public NotificationAdapter(ArrayList<Notification> notifications) {
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item,parent,false);
        mAuth = FirebaseAuth.getInstance();
        context = view.getContext();
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
            Notification notification = notifications.get(position);
            holder.textViewTitle.setText(notification.getNotification_title());
            holder.textViewBody.setText(notification.getNotification_body());
            holder.textViewState.setText("Current State: "+notification.getState());
            String simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm").format(notification.getNotification_timestamp());
            holder.textViewTimestamp.setText(simpleDateFormat);
            holder.buttonMarkAsRead.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("Notifications");
                    myRef.child(mAuth.getCurrentUser().getUid()).child(notification.getNotification_id()).removeValue();
                    notifications.remove(position);
                    notifyItemRemoved(position);
                }
            });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder
    {
        public TextView textViewTitle,textViewBody,textViewTimestamp,textViewState;
        public Button buttonMarkAsRead;
        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewNotficationTitle);
            textViewBody = itemView.findViewById(R.id.textViewNotificationBody);
            textViewTimestamp = itemView.findViewById(R.id.textViewNotificationTime);
            buttonMarkAsRead = itemView.findViewById(R.id.buttonMarkAsRead);
            textViewState = itemView.findViewById(R.id.textViewState);
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }



}
