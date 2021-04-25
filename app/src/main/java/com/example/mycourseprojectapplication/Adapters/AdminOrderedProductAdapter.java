package com.example.mycourseprojectapplication.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mycourseprojectapplication.Activities.AdminItemStatusActivity;
import com.example.mycourseprojectapplication.Activities.AdminMapsActivity;
import com.example.mycourseprojectapplication.Models.Cart;
import com.example.mycourseprojectapplication.Models.Orders;
import com.example.mycourseprojectapplication.Models.Products;
import com.example.mycourseprojectapplication.Models.Tracking;
import com.example.mycourseprojectapplication.Models.Users;
import com.example.mycourseprojectapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdminOrderedProductAdapter extends RecyclerView.Adapter<AdminOrderedProductAdapter.AdminOrderedProductViewHolder> {
    private ArrayList<Cart> productsArrayList;        // declare our variables
    private Context context;
    private ArrayList<Tracking> trackingArrayList = new ArrayList<>();
    private ArrayList<Tracking> currentProductTracking = new ArrayList<>();
    private HashMap<String,String> tracking_keys = new HashMap<>();
    private Orders order;
    private AppCompatActivity activity;
    private String status;
    private Users user;

    public AdminOrderedProductAdapter(ArrayList<Cart> productsArrayList, Context context, Orders order) {
        this.productsArrayList = productsArrayList;
        this.context = context;
        this.order = order;
    }

    public AdminOrderedProductAdapter() {
    }

    @NonNull
    @Override
    public AdminOrderedProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { // create item view for the recycler view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_product_item,parent,false);
        context = view.getContext();
        tracking_keys = order.getTracking_key();
        activity = (AppCompatActivity) view.getContext();
        return new AdminOrderedProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminOrderedProductViewHolder holder, int position) {
        Products product = productsArrayList.get(position).getProduct(); // get the products of the order
        Cart cart = productsArrayList.get(position);  // get the color selected and config from cart object
        holder.textViewProductName.setText(product.getProductName());
        holder.textViewProductBrand.setText(product.getProductBrand());        // set the order details and product details
        DecimalFormat decimalFormat = new DecimalFormat("0.#####");
        String price = decimalFormat.format(cart.getTotalPrice());
        holder.textViewProductPrice.setText("$"+price);
        holder.textViewProductConfig.setText(cart.getConfigurationSelected());
        holder.textViewQuantity.setText(String.valueOf(cart.getQuantity()));
        holder.textViewColor.setText(cart.getColorSelected());
        Glide.with(context).load(product.getProductImages().get("ImgLink0")).fitCenter().placeholder(R.drawable.ic_baseline_cached_24).into(holder.productImage);  // load the image from the url into product image
        getUserTrackingInfo();
        getUserData();
        holder.buttonReview.setOnClickListener(new View.OnClickListener() { // on review button is clicked
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, AdminItemStatusActivity.class);
                intent.putExtra("product_id",product.getProduct_id());               // go to order status activity
                intent.putExtra("tracking",trackingArrayList.get(position));
                Bundle bundle = new Bundle();
                bundle.putString("tracking_key",trackingArrayList.get(position).getTracking_key());
                bundle.putString("token",user.getNotification_token());
                bundle.putString("user_id",user.getUser_id());
                bundle.putString("user_email",user.getEmail());
                bundle.putSerializable("product",product);
                bundle.putSerializable("order",order);
                intent.putExtra("bundle",bundle);
                context.startActivity(intent);

            }
        });


        holder.buttonTrack.setOnClickListener(new View.OnClickListener() { // when track button is clicked
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, AdminMapsActivity.class);
                intent.putExtra("product_id",product.getProduct_id());               // go to order status activity
                intent.putExtra("tracking",trackingArrayList.get(position));
                Bundle bundle = new Bundle();
                bundle.putString("tracking_key",trackingArrayList.get(position).getTracking_key());
                bundle.putString("token",user.getNotification_token());
                bundle.putString("user_id",user.getUser_id());
                bundle.putString("user_email",user.getEmail());
                bundle.putSerializable("product",product);
                bundle.putSerializable("order",order);
                intent.putExtra("bundle",bundle);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return productsArrayList.size();
    }

    public class AdminOrderedProductViewHolder extends RecyclerView.ViewHolder
    {
        public TextView textViewProductName,textViewProductBrand,textViewProductPrice,textViewProductConfig,textViewColor,textViewQuantity;
        public Button buttonReview,buttonTrack;               // declare our variables for views
        public ImageView productImage;

        public AdminOrderedProductViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewProductName = itemView.findViewById(R.id.textViewProductName);
            textViewProductBrand = itemView.findViewById(R.id.textViewProductBrand);
            textViewProductPrice = itemView.findViewById(R.id.textViewProductPrice);          // initialize our views
            textViewProductConfig = itemView.findViewById(R.id.textViewProductConfig);
            textViewColor = itemView.findViewById(R.id.textViewColor);
            textViewQuantity = itemView.findViewById(R.id.textViewQuantity);
            buttonReview = itemView.findViewById(R.id.buttonReview);
            buttonTrack = itemView.findViewById(R.id.buttonTrack);
            productImage = itemView.findViewById(R.id.imageView9);
        }

    }
     private void getUserTrackingInfo()
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Tracking");

            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    trackingArrayList.clear();
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        for (Map.Entry<String,String> entry : tracking_keys.entrySet()) {

                                if(dataSnapshot.child("tracking_key").getValue().toString().equals(entry.getValue()) && entry.getKey().equals(dataSnapshot.child("product_id").getValue().toString()))
                                {
                                    trackingArrayList.add(dataSnapshot.getValue(Tracking.class));

                                }
                        }

                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

    }


    private void getUserData()
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users");
        Log.d("Adapter",order.toString());
       myRef.child(order.getCustomer_id()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                user = snapshot.getValue(Users.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

}
