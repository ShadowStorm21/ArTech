package com.example.mycourseprojectapplication.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.transition.Explode;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycourseprojectapplication.Adapters.ProductAdapter;
import com.example.mycourseprojectapplication.Interfaces.ItemClickListener;
import com.example.mycourseprojectapplication.Models.Products;
import com.example.mycourseprojectapplication.R;
import com.example.mycourseprojectapplication.Utilities.ConnectionDetector;
import com.example.mycourseprojectapplication.Utilities.UserSession;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;

import static com.example.mycourseprojectapplication.Activities.IndividualCategoryProducts.PRODUCT_TRANSITION_IMAGE;


public class SearchableActivity extends AppCompatActivity implements ItemClickListener {

    private final String TAG = this.getClass().getSimpleName(); // this is used for debugging purposes
    private ArrayList<Products> productsArrayList;       // declare our variables
    private int numOfProducts = 0;
    private ProductAdapter productAdapter;
    private UserSession userSession;
    private HashSet<String> userSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Explode explode = new Explode(); // create explode transition
        View decor = getWindow().getDecorView(); // get the window decoder
        explode.excludeTarget(decor.findViewById(R.id.action_bar_container), true); // exclude the action bar from the transition
        explode.excludeTarget(android.R.id.statusBarBackground, true); // exclude the status bar from the transition
        explode.excludeTarget(android.R.id.navigationBarBackground, true); // exclude the navigation bar from the transition
        getWindow().setEnterTransition(explode); // set the enter and exit transition to explode
        getWindow().setExitTransition(explode);
        getWindow().setSharedElementEnterTransition(TransitionInflater.from(this).inflateTransition(R.transition.changeimage)); // set the shared element transitions to custom transition -> change image
        getWindow().setSharedElementExitTransition(TransitionInflater.from(this).inflateTransition(R.transition.changeimage));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);

        new ConnectionDetector(this); // check if user has internet connection or not
        userSession = new UserSession(this);

        Toolbar toolbar = findViewById(R.id.toolbarSearch);
        setSupportActionBar(toolbar);
        setTitle("Search Results");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // setup the action toolbar with activity title and back icon functionality
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {    // when back is clicked just finish the activity
                finish();
            }
        });

        Intent intent = getIntent(); // get the intent object from the previous activity
        final String query = intent.getStringExtra("query");  // get the query
        RecyclerView recyclerView = findViewById(R.id.searchRecycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);         // setup the products recycler view
        recyclerView.setLayoutManager(layoutManager);
        productsArrayList = new ArrayList<>();
        productAdapter = new ProductAdapter(productsArrayList,this);
        recyclerView.setAdapter(productAdapter);

        if (userSession.getSearchQueries() == null) {
            userSearch = new HashSet<>();
        }
        else
        {
            userSearch = userSession.getSearchQueries();
        }
        userSearch.add(query);

        getProducts(query); // method to get all products based on query

    }

    private void getProducts(final String query) // method to get all products based on search query
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Products");


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for(DataSnapshot products : dataSnapshot.getChildren())
                {
                    if(products.child("productName").getValue().toString().toLowerCase().contains(query.toLowerCase()) || products.child("productBrand").getValue().toString().toLowerCase().contains(query.toLowerCase()) || products.child("category").getValue().toString().toLowerCase().contains(query.toLowerCase())) // if product name or brand or category equals to query
                    {
                        productsArrayList.add(products.getValue(Products.class)); // add product to array list
                        numOfProducts++; // increase the number of products
                    }
                    if(numOfProducts == 1)
                    {
                        setTitle(numOfProducts +" items found!"); // set title to numbers of product found
                    }
                }
                if(numOfProducts == 1)
                {
                    setTitle(numOfProducts +" item found!"); // set title to numbers of product found
                    productAdapter.notifyDataSetChanged(); // notify the adapter with new data
                }
                else
                {
                    setTitle(numOfProducts +" items found!"); // set title to numbers of product found
                    productAdapter.notifyDataSetChanged(); // notify the adapter with new data
                }
                Log.d(TAG, "Value is: " + productsArrayList.toString());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }


    @Override
    public void onItemClick(RecyclerView.ViewHolder myViewHolder, int pos, ImageView imageView) { // on item click

        Intent intent = new Intent(this, IndividualProductActivity.class); // create an intent to go to individual product activity
        intent.putExtra("items",productsArrayList.get(pos)); // get the current product info
        intent.putExtra(PRODUCT_TRANSITION_IMAGE, ViewCompat.getTransitionName(imageView)); // send the transition key and transition name
        Pair<View, String> pair1 = Pair.create((View) ((ProductAdapter.MyViewHolder)myViewHolder).productImage, ((ProductAdapter.MyViewHolder)myViewHolder).productImage.getTransitionName());  // create a pair of views and string to image transition, send product image and transition name
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                pair1);   // create the transition animation with the pair
        startActivity(intent,options.toBundle());  // start the activity and pass the options as a bundle
    }

    @Override
    protected void onResume() {
        super.onResume();
        new ConnectionDetector(this); // on Resume ( this method is fired before on create )  activity, check if user has connection or not
    }

    @Override
    protected void onPause() {
        super.onPause();
        userSession.setUserSearchQueries(userSearch);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}