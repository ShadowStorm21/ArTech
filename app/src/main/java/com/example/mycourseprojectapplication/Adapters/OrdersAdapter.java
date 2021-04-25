package com.example.mycourseprojectapplication.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycourseprojectapplication.Activities.OrderActivity;
import com.example.mycourseprojectapplication.Models.Cart;
import com.example.mycourseprojectapplication.Models.OrderStatus;
import com.example.mycourseprojectapplication.Models.Orders;
import com.example.mycourseprojectapplication.Models.TimelineStatus;
import com.example.mycourseprojectapplication.Models.Tracking;
import com.example.mycourseprojectapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrdersViewHolder> {
    private final ArrayList<Orders> ordersArrayList;
    private ArrayList<Tracking> trackingArrayList = new ArrayList<>();       // declare our variables
    private Context context;
    private RecyclerView mRecyclerView;
    private ArrayList<OrderStatus> stringArrayList = new ArrayList<>();
    private String status = "";
    private String s = "";
    public OrdersAdapter(ArrayList<Orders> ordersArrayList, Context context) {        // adapter constructor
        this.ordersArrayList = ordersArrayList;
        this.context = context;
    }

    public class OrdersViewHolder extends RecyclerView.ViewHolder // view holder describes item view in the recycler view
    {
        public TextView textViewDate,textViewStatus,textViewOrderNumber,textViewItemStatus,textViewItemStatus1;     // declare our variables
        public Button buttonViewOrder;
        public LinearLayout linearLayout,linearLayoutStatus;
        public OrdersViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
            textViewOrderNumber = itemView.findViewById(R.id.textViewOrderNumber);  // initialize our views
            buttonViewOrder = itemView.findViewById(R.id.buttonViewOrder);
            textViewItemStatus = itemView.findViewById(R.id.textViewItemStaus);
            linearLayout = itemView.findViewById(R.id.linearLayoutItemStatus);
            textViewItemStatus1 = itemView.findViewById(R.id.textViewStatusOrder);
            linearLayoutStatus = itemView.findViewById(R.id.linearLayoutStatus);
        }
    }

    @NonNull
    @Override
    public OrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {  // create item view for the recycler view
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_item,parent,false);
       context = view.getContext();
       return new OrdersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersViewHolder holder, int position) { // bind the data
        Orders order = ordersArrayList.get(position); // get individual order
        String simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy").format(order.getOrder_date()); // format long date to string format
        holder.textViewOrderNumber.setText(order.getOrder_id().substring(0,13));
        holder.textViewStatus.setText(order.getOrder_status());       // set order details
        holder.textViewDate.setText(simpleDateFormat);
        holder.buttonViewOrder.setOnClickListener(new View.OnClickListener() { // on view order clicked
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OrderActivity.class);
                intent.putExtra("order",order);
                context.startActivity(intent);             // go to order activity bundled with order info and location
            }
        });

        if(order.getPurchasedProducts().size() > 1)
        {

            holder.linearLayout.setVisibility(View.VISIBLE);
            holder.linearLayoutStatus.setVisibility(View.VISIBLE);
            getItemsStatus(order,holder);

        }
        else
        {
            holder.linearLayout.setVisibility(View.GONE);
            holder.linearLayoutStatus.setVisibility(View.GONE);
        }



    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    private void getItemsStatus(Orders order, OrdersViewHolder holder)
    {

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Tracking");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                trackingArrayList.clear();
                stringArrayList.clear();
                status = "";
                s = "";
                for(DataSnapshot items : snapshot.getChildren())
                {
                    if(items.child("order_id").getValue().toString().equals(order.getOrder_id()))
                    {
                        trackingArrayList.add(items.getValue(Tracking.class));
                    }
                }

                Log.i("Adapter",trackingArrayList.toString());

                for(int i = 0; i < trackingArrayList.size(); i++)
                {

                        if(trackingArrayList.get(i).getTimeLineModelMap() != null)
                        {

                            if (trackingArrayList.get(i).getTimeLineModelMap().get(OrderStatus.ORDERED.name().toLowerCase()).getOrder_status() != null && trackingArrayList.get(i).getTimeLineModelMap().get(OrderStatus.ORDERED.name().toLowerCase()).getStatus().equals(TimelineStatus.ACTIVE))
                            {
                                stringArrayList.add(trackingArrayList.get(i).getTimeLineModelMap().get(OrderStatus.ORDERED.name().toLowerCase()).getOrder_status());
                            }
                            else if (trackingArrayList.get(i).getTimeLineModelMap().get(OrderStatus.PREPARED.name().toLowerCase()).getOrder_status() != null && trackingArrayList.get(i).getTimeLineModelMap().get(OrderStatus.PREPARED.name().toLowerCase()).getStatus().equals(TimelineStatus.ACTIVE) )
                            {
                                stringArrayList.add(trackingArrayList.get(i).getTimeLineModelMap().get(OrderStatus.PREPARED.name().toLowerCase()).getOrder_status());
                            }
                            else if (trackingArrayList.get(i).getTimeLineModelMap().get(OrderStatus.RECEIVED_FROM_MANUFACTURE.name().toLowerCase()).getOrder_status() != null && trackingArrayList.get(i).getTimeLineModelMap().get(OrderStatus.RECEIVED_FROM_MANUFACTURE.name().toLowerCase()).getStatus().equals(TimelineStatus.ACTIVE))
                            {
                                stringArrayList.add(trackingArrayList.get(i).getTimeLineModelMap().get(OrderStatus.RECEIVED_FROM_MANUFACTURE.name().toLowerCase()).getOrder_status());
                            }
                            else if (trackingArrayList.get(i).getTimeLineModelMap().get(OrderStatus.PREPARED_FOR_SHIPPING.name().toLowerCase()).getOrder_status() != null && trackingArrayList.get(i).getTimeLineModelMap().get(OrderStatus.PREPARED_FOR_SHIPPING.name().toLowerCase()).getStatus().equals(TimelineStatus.ACTIVE) )
                            {
                                stringArrayList.add(trackingArrayList.get(i).getTimeLineModelMap().get(OrderStatus.PREPARED_FOR_SHIPPING.name().toLowerCase()).getOrder_status());
                            }
                            else if (trackingArrayList.get(i).getTimeLineModelMap().get(OrderStatus.SHIPPED.name().toLowerCase()).getOrder_status() != null && trackingArrayList.get(i).getTimeLineModelMap().get(OrderStatus.SHIPPED.name().toLowerCase()).getStatus().equals(TimelineStatus.ACTIVE) )
                            {
                                stringArrayList.add(trackingArrayList.get(i).getTimeLineModelMap().get(OrderStatus.SHIPPED.name().toLowerCase()).getOrder_status());
                            }
                            else if (trackingArrayList.get(i).getTimeLineModelMap().get(OrderStatus.IN_TRANSIT.name().toLowerCase()).getOrder_status() != null && trackingArrayList.get(i).getTimeLineModelMap().get(OrderStatus.IN_TRANSIT.name().toLowerCase()).getStatus().equals(TimelineStatus.ACTIVE) )
                            {
                                stringArrayList.add(trackingArrayList.get(i).getTimeLineModelMap().get(OrderStatus.IN_TRANSIT.name().toLowerCase()).getOrder_status());
                            }
                            else if (trackingArrayList.get(i).getTimeLineModelMap().get(OrderStatus.REACHED_HQ.name().toLowerCase()).getOrder_status() != null && trackingArrayList.get(i).getTimeLineModelMap().get(OrderStatus.REACHED_HQ.name().toLowerCase()).getStatus().equals(TimelineStatus.ACTIVE) )
                            {
                                stringArrayList.add(trackingArrayList.get(i).getTimeLineModelMap().get(OrderStatus.REACHED_HQ.name().toLowerCase()).getOrder_status());
                            }
                            else if (trackingArrayList.get(i).getTimeLineModelMap().get(OrderStatus.SCHEDULED_FOR_DELIVERY.name().toLowerCase()).getOrder_status() != null && trackingArrayList.get(i).getTimeLineModelMap().get(OrderStatus.SCHEDULED_FOR_DELIVERY.name().toLowerCase()).getStatus().equals(TimelineStatus.ACTIVE) )
                            {
                                stringArrayList.add(trackingArrayList.get(i).getTimeLineModelMap().get(OrderStatus.SCHEDULED_FOR_DELIVERY.name().toLowerCase()).getOrder_status());
                            }
                            else if (trackingArrayList.get(i).getTimeLineModelMap().get(OrderStatus.OUT_FOR_DELIVERY.name().toLowerCase()).getOrder_status() != null && trackingArrayList.get(i).getTimeLineModelMap().get(OrderStatus.OUT_FOR_DELIVERY.name().toLowerCase()).getStatus().equals(TimelineStatus.ACTIVE) )
                            {
                                stringArrayList.add(trackingArrayList.get(i).getTimeLineModelMap().get(OrderStatus.OUT_FOR_DELIVERY.name().toLowerCase()).getOrder_status());
                            }
                            else if (trackingArrayList.get(i).getTimeLineModelMap().get(OrderStatus.DELIVERED.name().toLowerCase()).getOrder_status() != null && trackingArrayList.get(i).getTimeLineModelMap().get(OrderStatus.DELIVERED.name().toLowerCase()).getStatus().equals(TimelineStatus.ACTIVE) )
                            {
                                stringArrayList.add(trackingArrayList.get(i).getTimeLineModelMap().get(OrderStatus.DELIVERED.name().toLowerCase()).getOrder_status());
                            }
                            else if (trackingArrayList.get(i).getTimeLineModelMap().get(OrderStatus.DELIVERED.name().toLowerCase()).getOrder_status() != null && trackingArrayList.get(i).getTimeLineModelMap().get(OrderStatus.DELIVERED.name().toLowerCase()).getStatus().equals(TimelineStatus.COMPLETED) )
                            {
                                stringArrayList.add(trackingArrayList.get(i).getTimeLineModelMap().get(OrderStatus.DELIVERED.name().toLowerCase()).getOrder_status());
                            }
                        }


                }


                for(Cart item : order.getPurchasedProducts())
                {


                    status += "#"+item.getProduct().getProductName() +"\n\n";

                }
                if(stringArrayList.size() == 0)
                {
                    s = s + "Processing "+"\n\n";
                }
                else
                {
                    for(OrderStatus orderStatus : stringArrayList)
                    {
                        s = s + orderStatus.name().toLowerCase()+"\n\n";
                        s = s.replaceAll("_"," ");
                    }
                }

                holder.textViewItemStatus.setText(status);
                holder.textViewItemStatus1.setText(s);
                Log.d("Adapter",stringArrayList.toString());









            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    @Override
    public int getItemCount() {
        return ordersArrayList.size();  // return size of array list
    }




}
