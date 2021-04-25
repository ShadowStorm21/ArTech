package com.example.mycourseprojectapplication.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mycourseprojectapplication.Adapters.PDFAdapter;
import com.example.mycourseprojectapplication.Adapters.PDFMostPurchased;
import com.example.mycourseprojectapplication.Adapters.PDFOrders;
import com.example.mycourseprojectapplication.Adapters.PDFOrdersFulFilled;
import com.example.mycourseprojectapplication.Adapters.PDFProducts;
import com.example.mycourseprojectapplication.Models.Cart;
import com.example.mycourseprojectapplication.Models.Orders;
import com.example.mycourseprojectapplication.Models.Products;
import com.example.mycourseprojectapplication.Models.Users;
import com.example.mycourseprojectapplication.R;
import com.gkemon.XMLtoPDF.PdfGenerator;
import com.gkemon.XMLtoPDF.PdfGeneratorListener;
import com.gkemon.XMLtoPDF.model.FailureResponse;
import com.gkemon.XMLtoPDF.model.SuccessResponse;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PDFActivity extends AppCompatActivity {

    private ArrayList<Users> usersArrayList;
    private ArrayList<Products> productsArrayList;
    private ArrayList<Orders> ordersArrayList;
    private ArrayList<Orders> fulfilledOrdersArrayList;
    private PDFAdapter pdfAdapter;
    private PDFOrders pdfOrders;
    private PDFOrdersFulFilled pdfOrdersFulFilled;
    private PDFProducts pdfProductsAdapter;
    private PDFMostPurchased pdfMostPurchased;
    private ListView recyclerView;
    private ScrollView scrollView;
    private ArrayList<Products> mostProductsArrayList;
    private Map<String,Integer> counter = new HashMap<>();
    private String type = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p_d_f);
        recyclerView = findViewById(R.id.recyclerViewContent);
        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        if(intent.getStringExtra("type").equals("Users"))
        {

            usersArrayList = new ArrayList<>();
            pdfAdapter = new PDFAdapter(this,R.layout.item_pdf_user_layout,usersArrayList);
            recyclerView.setAdapter(pdfAdapter);
            getUsers();
        }
        if(intent.getStringExtra("type").equals("Products"))
        {
            recyclerView = findViewById(R.id.recyclerViewContent);
            productsArrayList = new ArrayList<>();
            pdfProductsAdapter = new PDFProducts(this,R.layout.item_pdf_products,productsArrayList);
            recyclerView.setAdapter(pdfProductsAdapter);
            getProducts();
        }
        if(intent.getStringExtra("type").equals("Orders"))
        {
            ordersArrayList = new ArrayList<>();
            pdfOrders = new PDFOrders(this,R.layout.item_pdf_order,ordersArrayList);
            recyclerView.setAdapter(pdfOrders);
            getOrders();
        }
        if(intent.getStringExtra("type").equals("MostPP"))
        {
            mostProductsArrayList = new ArrayList<>();
            productsArrayList = new ArrayList<>();
            ordersArrayList = new ArrayList<>();
            getMostPurchasedProducts();
        }
        if(intent.getStringExtra("type").equals("fulfilled"))
        {
           fulfilledOrdersArrayList = new ArrayList<>();
            pdfOrdersFulFilled = new PDFOrdersFulFilled(this,R.layout.item_pdf_order_fulfilled,fulfilledOrdersArrayList);
            recyclerView.setAdapter(pdfOrdersFulFilled);
            getFulfilledOrders();
        }

        scrollView = findViewById(R.id.SC);




        FloatingActionButton floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                PdfGenerator.getBuilder()
                        .setContext(PDFActivity.this)
                        .fromViewSource()
                        .fromView(scrollView)
                        /* "fromViewID()" takes array of view ids and the host layout xml where the view ids are belonging.
                         * You can also invoke "fromViewIDList()" method here which takes list of view ids instead of array.*/
                        .setPageSize(PdfGenerator.PageSize.A4)
                        .setFileName("Dope_Tech_Summary_Report")
                        .setFolderName("folder")
                        .openPDFafterGeneration(true)
                        .build(new PdfGeneratorListener() {
                            @Override
                            public void onFailure(FailureResponse failureResponse) {
                                super.onFailure(failureResponse);
                            }

                            @Override
                            public void onStartPDFGeneration() {
                                /*When PDF generation begins to start*/
                            }

                            @Override
                            public void onFinishPDFGeneration() {
                                /*When PDF generation is finished*/
                            }

                            @Override
                            public void showLog(String log) {
                                super.showLog(log);
                            }

                            @Override
                            public void onSuccess(SuccessResponse response) {
                                super.onSuccess(response);
                            }
                        });

            }
        });



    }

    private void getUsers()                      // method to get all the users from the database and add them to an arraylist
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    usersArrayList.clear();
                for(DataSnapshot users : snapshot.getChildren())
                {
                    usersArrayList.add(users.getValue(Users.class));
                }
                pdfAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getOrders() // method to the current user orders
    {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Orders");

            myRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    for (DataSnapshot orders : snapshot.getChildren()) {

                            ordersArrayList.add(orders.getValue(Orders.class));     // add order to arraylist
                    }
                    if( ordersArrayList.size() == 0)
                    {
                        Toast.makeText(PDFActivity.this, "There are no orders yet!", Toast.LENGTH_LONG).show();
                    }
                    pdfOrders.notifyDataSetChanged();
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    public void getFulfilledOrders() // method to the current user orders
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Orders");

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                for (DataSnapshot orders : snapshot.getChildren()) {

                    if(orders.child("order_status").getValue().toString().equals("Delivered")) {
                        fulfilledOrdersArrayList.add(orders.getValue(Orders.class));     // add order to arraylist
                    }

                }
                if(fulfilledOrdersArrayList.size() == 0)
                {
                    Toast.makeText(PDFActivity.this, "There are no fulfilled orders yet!", Toast.LENGTH_LONG).show();
                }

                pdfOrdersFulFilled.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getProducts()
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Products");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot products : snapshot.getChildren())
                {
                    productsArrayList.add(products.getValue(Products.class));
                }
                if(productsArrayList.size() == 0)
                {
                    Toast.makeText(PDFActivity.this, "There are no products available yet!", Toast.LENGTH_LONG).show();
                }
                pdfProductsAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getMostPurchasedProducts()
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Orders");

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                for (DataSnapshot orders : snapshot.getChildren()) {

                    ordersArrayList.add(orders.getValue(Orders.class));     // add order to arraylist
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase database1 = FirebaseDatabase.getInstance();
        DatabaseReference myRef1 = database1.getReference("Products");
        myRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot products : snapshot.getChildren())
                {
                    productsArrayList.add(products.getValue(Products.class));
                }

                for(Products products : productsArrayList)
                {
                    for (Orders order : ordersArrayList)
                    {
                        for (Cart cart : order.getPurchasedProducts())
                        {
                            if (cart.getProduct().getProduct_id().equals(products.getProduct_id()))
                            {
                                mostProductsArrayList.add(products);
                            }
                        }
                    }
                }
                int occurrences = 0;
                for (Products product : mostProductsArrayList) {

                    occurrences = Collections.frequency(mostProductsArrayList,product);
                    counter.put(product.getProduct_id(),occurrences);
                }
                Map<String,Products> productMap = new HashMap<String,Products>();
                Map<String,Integer> counterProductMap = new HashMap<String,Integer>();
                for (Products product : mostProductsArrayList)
                {
                    productMap.put(product.getProduct_id(),product);
                }

                List<Products> result = productMap.values().stream().distinct().collect(Collectors.toList());
                pdfMostPurchased = new PDFMostPurchased(PDFActivity.this,R.layout.item_pdf_most_purchased_products,result,counter);
                recyclerView.setAdapter(pdfMostPurchased);

                pdfMostPurchased.notifyDataSetChanged();
                Log.d("PDF",mostProductsArrayList.toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

}