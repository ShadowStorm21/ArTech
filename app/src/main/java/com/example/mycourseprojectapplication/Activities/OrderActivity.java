package com.example.mycourseprojectapplication.Activities;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycourseprojectapplication.Adapters.OrderProductAdapter;
import com.example.mycourseprojectapplication.Models.Cart;
import com.example.mycourseprojectapplication.Models.Orders;
import com.example.mycourseprojectapplication.Models.Tracking;
import com.example.mycourseprojectapplication.R;
import com.example.mycourseprojectapplication.Utilities.ConnectionDetector;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrderActivity extends AppCompatActivity {

    private ArrayList<Cart> cartArrayList;
    private OrderProductAdapter orderProductAdapter;       // declare our variables
    private FirebaseAuth mAuth;
    private ArrayList<Tracking> trackingArrayList;
    private HashMap<String,String> tracking_keys;
    private TextView textViewAddress;
    private List<Address> addresses = null;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ScrollView scrollView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        new ConnectionDetector(this);          // check if user has Internet connection
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarOrder);
        setSupportActionBar(toolbar);
        setTitle("My Order");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // setup the action toolbar with activity title and back icon functionality
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        Orders order = (Orders) intent.getSerializableExtra("order"); // get order object from previous activity
        trackingArrayList = new ArrayList<>();
        tracking_keys = order.getTracking_key();
        TextView date = findViewById(R.id.textViewOrderDate);
        TextView paymentMode = findViewById(R.id.textViewPaymentType);
        TextView textViewName = findViewById(R.id.textViewName);
        TextView textViewPhone = findViewById(R.id.textViewPhone);   // initialize views and classes
        textViewAddress = findViewById(R.id.textViewAddress);
        TextView textViewTotal = findViewById(R.id.textViewTotal);
        recyclerView = findViewById(R.id.product_order_recycler);
        progressBar = findViewById(R.id.progressBarLoadOrder);
        scrollView = findViewById(R.id.scrollViewOrder);
        cartArrayList = new ArrayList<>();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this); // setup the orders recycler view
        recyclerView.setLayoutManager(layoutManager);

        if(order != null && trackingArrayList != null) // if the order is not null
        {
            String simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy HH:ss").format(order.getOrder_date());
            date.setText(simpleDateFormat);
            paymentMode.setText(order.getPayment_option());                                       // set the order data
            textViewName.setText(order.getFirstName() + " " + order.getLastName());
            textViewPhone.setText(order.getPhoneNumber());
            textViewTotal.setText("$"+order.getTotal());
            getUserTrackingInfo(order);
        }

    }
    private void getUserTrackingInfo(Orders order)
    {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Tracking");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                trackingArrayList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    for (Map.Entry<String,String> entry : tracking_keys.entrySet()) {

                        if(dataSnapshot.child("tracking_key").getValue().toString().equals(entry.getValue()))
                        {
                            trackingArrayList.add(dataSnapshot.getValue(Tracking.class));

                        }
                    }
                }
                cartArrayList = order.getPurchasedProducts();
                orderProductAdapter = new OrderProductAdapter(cartArrayList,order,trackingArrayList);
                recyclerView.setAdapter(orderProductAdapter);
                orderProductAdapter.notifyDataSetChanged();
                try {
                    // use user latitude and longitude to determine the address
                    Geocoder geocoder;
                    geocoder = new Geocoder(OrderActivity.this, Locale.getDefault());
                    addresses = geocoder.getFromLocation(trackingArrayList.get(0).getUserLatitude(), trackingArrayList.get(0).getUserLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String address = null;
                String city = addresses.get(0).getLocality();                    // get the address, city and state
                String state = addresses.get(0).getAdminArea();
                if(addresses.get(0).getAddressLine(0).contains("Sib"))
                {
                    address = addresses.get(0).getAddressLine(0).replace("Sib","Seeb");
                }
                else
                {
                    address = addresses.get(0).getAddressLine(0);
                }
                if(address != null)
                {
                    textViewAddress.setText(address + " " + city + " " + state); // set the address in the textView
                }
                Log.i("Order Adapter", trackingArrayList.toString());
                progressBar.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }


    @Override
    protected void onResume() {
        super.onResume();
        new ConnectionDetector(this); // on Resume ( this method is fired before on create )  activity, check if user has connection or not
    }
}