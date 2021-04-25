package com.example.mycourseprojectapplication.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycourseprojectapplication.Adapters.AdminAllProductAdapter;
import com.example.mycourseprojectapplication.Models.Category;
import com.example.mycourseprojectapplication.Models.Products;
import com.example.mycourseprojectapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminCategoriesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<Products> productsArrayList;
    private AdminAllProductAdapter productAdapter;
    private Category category;
    private TextView textViewEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_categories);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        category = (Category) getIntent().getSerializableExtra("categroy");
        setTitle(category.getTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // setup the action toolbar with activity title and back icon functionality
        // Intent intent = getIntent();
        //boolean intentValidator = intent.getBooleanExtra("started_from_notification",false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   // when back button is clicked finish the activity
                finish();
            }
        });
        recyclerView = findViewById(R.id.product_admin_all_products);
        textViewEmpty = findViewById(R.id.textViewEmptyAdminProducts);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        productsArrayList = new ArrayList<>();
        productAdapter = new AdminAllProductAdapter(productsArrayList);
        recyclerView.setAdapter(productAdapter);
        getAllProducts();
    }

    private void getAllProducts()
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Products");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productsArrayList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    if(dataSnapshot.child("category").getValue().toString().equals(category.getTitle())) {
                        productsArrayList.add(dataSnapshot.getValue(Products.class));
                    }
                }
                if(productsArrayList.size() == 0)
                {
                    textViewEmpty.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.INVISIBLE);
                }
                else
                {
                    textViewEmpty.setVisibility(View.INVISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
                productAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}