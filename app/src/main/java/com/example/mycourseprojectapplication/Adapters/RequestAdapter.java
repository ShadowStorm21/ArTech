package com.example.mycourseprojectapplication.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycourseprojectapplication.Activities.AdminRequestActivity;
import com.example.mycourseprojectapplication.Models.Requests;
import com.example.mycourseprojectapplication.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {

    private final ArrayList<Requests> requestsArrayList;
    private Context context;

    public RequestAdapter(ArrayList<Requests> requestsArrayList) {
        this.requestsArrayList = requestsArrayList;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_item,parent,false);
        context = view.getContext();
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        Requests request = requestsArrayList.get(position);
        String simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy").format(request.getRequestDate()); // format long date to string format
        holder.textViewDate.setText(simpleDateFormat);
        if(request.isSolved())
        {
            holder.textViewStatus.setText("Solved!");
        }
        else
        {
            holder.textViewStatus.setText("Not Solved!");
        }
        holder.textViewID.setText(request.getRequest_id().substring(0,13));
        holder.buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AdminRequestActivity.class);
                intent.putExtra("request",request);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return requestsArrayList.size();
    }

    public class RequestViewHolder extends RecyclerView.ViewHolder
    {
        public TextView textViewDate,textViewID,textViewStatus;
        public Button buttonView;
        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewID = itemView.findViewById(R.id.textViewRequestId);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
            buttonView = itemView.findViewById(R.id.buttonViewRequest);
        }
    }
}
