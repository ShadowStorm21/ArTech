package com.example.mycourseprojectapplication.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycourseprojectapplication.Activities.ViewOrdersActivity;
import com.example.mycourseprojectapplication.Fragments.ModifyOrderStatusFragment;
import com.example.mycourseprojectapplication.Models.Orders;
import com.example.mycourseprojectapplication.Models.Tracking;
import com.example.mycourseprojectapplication.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class AdminOrdersAdapter extends RecyclerView.Adapter<AdminOrdersAdapter.AdminOrdersViewHolder> {

    private final ArrayList<Orders> ordersArrayList;
    private ArrayList<Tracking> trackingArrayList = new ArrayList<>();   // declare our variables
    private Context context;
    private AppCompatActivity activity;


    public AdminOrdersAdapter(ArrayList<Orders> ordersArrayList) {        // adapter constructor
        this.ordersArrayList = ordersArrayList;
    }

    @NonNull
    @Override
    public AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { // create item view for the recycler view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_order_item,parent,false);
        context = view.getContext();
        activity = (AppCompatActivity) view.getContext();
        return new AdminOrdersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminOrdersViewHolder holder, int position) { // bind the data

        Orders order = ordersArrayList.get(position);
        String simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy").format(order.getOrder_date());
        holder.textViewOrderNumber.setText(order.getOrder_id().substring(0,13));
        holder.textViewStatus.setText(order.getOrder_status());
        DecimalFormat decimalFormat = new DecimalFormat("0.#####");
        String price = decimalFormat.format(order.getTotal());
        holder.textViewTotal.setText("$"+price);
        holder.textViewDate.setText(simpleDateFormat);
        holder.buttonViewOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewOrdersActivity.class);
                intent.putExtra("order",order);
                context.startActivity(intent);
            }
        });

        holder.buttonModifyStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ModifyOrderStatusFragment modifyOrderStatusFragment = ModifyOrderStatusFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putSerializable("order",order);
                modifyOrderStatusFragment.setArguments(bundle);
                modifyOrderStatusFragment.show(activity.getSupportFragmentManager(),"BottomDialog");

            }
        });

    }

    @Override
    public int getItemCount() {
        return ordersArrayList.size();
    }

    public class AdminOrdersViewHolder extends RecyclerView.ViewHolder
    {
        public TextView textViewDate,textViewTotal,textViewStatus,textViewOrderNumber;
        public Button buttonViewOrder,buttonModifyStatus;
        public AdminOrdersViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewTotal = itemView.findViewById(R.id.textViewOrderTotal);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
            textViewOrderNumber = itemView.findViewById(R.id.textViewOrderNumber);
            buttonViewOrder = itemView.findViewById(R.id.buttonViewOrder);
            buttonModifyStatus = itemView.findViewById(R.id.buttonModifyStatus);
        }
    }


}
