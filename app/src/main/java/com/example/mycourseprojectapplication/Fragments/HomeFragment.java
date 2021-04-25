package com.example.mycourseprojectapplication.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mycourseprojectapplication.Activities.IndividualCategoryProducts;
import com.example.mycourseprojectapplication.Activities.IndividualProductActivity;
import com.example.mycourseprojectapplication.Adapters.CategoryAdapter;
import com.example.mycourseprojectapplication.Adapters.RecentProductAdapter;
import com.example.mycourseprojectapplication.Adapters.RecommendedProductsAdapter;
import com.example.mycourseprojectapplication.Interfaces.ItemClickListener;
import com.example.mycourseprojectapplication.Models.Cart;
import com.example.mycourseprojectapplication.Models.Category;
import com.example.mycourseprojectapplication.Models.Orders;
import com.example.mycourseprojectapplication.Models.Products;
import com.example.mycourseprojectapplication.R;
import com.example.mycourseprojectapplication.Utilities.ConnectionDetector;
import com.example.mycourseprojectapplication.Utilities.UserSession;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.mycourseprojectapplication.Activities.IndividualCategoryProducts.PRODUCT_TRANSITION_IMAGE;

public class HomeFragment extends Fragment {

    private final String TAG = this.getClass().getSimpleName();      // this is used for debugging
    private RecentProductAdapter productAdapter;
    private RecommendedProductsAdapter recommendedProductAdapter;
    private ArrayList<Products> productsArrayList;
    private ArrayList<Products> allProductsArrayList;
    private CategoryAdapter categoryAdapter;          // declare our variables
    private ArrayList<Category> categories;
    private ProgressBar progressBar;
    private RecyclerView recentProductsRecyclerView,recyclerViewRecommendedProducts;
    private FirebaseAuth mAuth;
    private ArrayList<Orders> ordersArrayList;
    private ArrayList<Products> recommendedProducts;
    private Map<String,Products> productsMap = new HashMap<>();
    private UserSession userSession;
    private HashSet<String> userSearchQueries = null;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView textViewRecommendedProducts;
    private Handler mHandler = new Handler();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        new ConnectionDetector(getContext()); // check if user has internet connection or not
        userSession = new UserSession(getContext());
        mAuth = FirebaseAuth.getInstance();
        productsArrayList = new ArrayList<>();
        allProductsArrayList = new ArrayList<>();
        recommendedProducts = new ArrayList<>();
        ordersArrayList = new ArrayList<>();
        categories = new ArrayList<>();
        productAdapter = new RecentProductAdapter(productsArrayList);                     // initialize views and classes and objects
        recommendedProductAdapter = new RecommendedProductsAdapter(recommendedProducts);
        categoryAdapter = new CategoryAdapter(categories);
        progressBar = root.findViewById(R.id.progressBar4);
        recentProductsRecyclerView = root.findViewById(R.id.recentProductRecycler);
        recyclerViewRecommendedProducts = root.findViewById(R.id.recyclerViewRecommendedProducts);
        RecyclerView categoryRecyclerView = root.findViewById(R.id.productsRecycler);
        swipeRefreshLayout = root.findViewById(R.id.swhome);
        textViewRecommendedProducts = root.findViewById(R.id.textViewRecommendedProducts);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        recentProductsRecyclerView.setHasFixedSize(true);
        recentProductsRecyclerView.setLayoutManager(layoutManager);                            // setup the recent products recycler view
        recentProductsRecyclerView.setAdapter(productAdapter);
        productAdapter.setOnItemClick(itemClickListener);



        recyclerViewRecommendedProducts.setHasFixedSize(true);
        recyclerViewRecommendedProducts.setLayoutManager(layoutManager2);                            // setup the recent products recycler view
        recyclerViewRecommendedProducts.setAdapter(recommendedProductAdapter);
        recommendedProductAdapter.setOnItemClick(itemClickListenerRecommendedProducts);


        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(getContext());
        categoryRecyclerView.setLayoutManager(layoutManager1);
        categoryRecyclerView.setAdapter(categoryAdapter);                      // setup the category recycler view
        categoryAdapter.setOnClickListener(onClickListener);


        swipeRefreshLayout.setProgressViewOffset(true,80,220);
        swipeRefreshLayout.setNestedScrollingEnabled(true);
        swipeRefreshLayout.setColorScheme(R.color.colorPrimary);
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(Color.WHITE);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {


                    if(swipeRefreshLayout.isRefreshing())
                    {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    else
                    {
                        swipeRefreshLayout.setRefreshing(true);
                    }
                getOrders();
            }
        });

        getRecentProducts();       // method to get recent products
        getProducts();
        addCategories();   // method to add all categories to the recycler view

        mHandler.postDelayed(runnable, 1000);

        return root;
    }

    private void getRecentProducts()       // method to get recent products
    {
        progressBar.setVisibility(View.VISIBLE);  // show progress bar
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Products");
        Query query = myRef.orderByChild("timeCreated").limitToLast(6);        // create a query to get recent products only and limit them to last 6
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productsArrayList.clear(); // clear the arraylist each time a new product added
                for(DataSnapshot products : snapshot.getChildren())
                {
                    productsArrayList.add(products.getValue(Products.class));       // add product to  the arraylist
                }
                //roductAdapter.notifyDataSetChanged();      // notifiy the recylcer adapter with new data
                runLayoutAnimation(recentProductsRecyclerView);
                progressBar.setVisibility(View.INVISIBLE); // hide progress bar
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }

    private void getProducts()       // method to get recent products
    {
        progressBar.setVisibility(View.VISIBLE);  // show progress bar
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Products");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allProductsArrayList.clear(); // clear the arraylist each time a new product added
                for(DataSnapshot products : snapshot.getChildren())
                {
                    allProductsArrayList.add(products.getValue(Products.class));       // add product to  the arraylist
                }
                progressBar.setVisibility(View.INVISIBLE); // hide progress bar
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }

    private void addCategories()  // method to add all categories
    {
        Category SmartPhones = new Category("Smartphones",R.drawable.ic_baseline_smartphone_24);
        Category Laptop = new Category("Laptops",R.drawable.ic_baseline_laptop_24);
        Category TV = new Category("TV's",R.drawable.ic_baseline_tv_24);
        Category headsets = new Category("Headsets",R.drawable.ic_outline_headset_24);
        Category SmartWatches = new Category("Smartwatches",R.drawable.ic_outline_watch_24);           // create categories and add them then notify the adapter
        Category Cameras = new Category("Cameras",R.drawable.ic_outline_camera_alt_24);
        categories.add(SmartPhones);
        categories.add(Laptop);
        categories.add(TV);
        categories.add(headsets);
        categories.add(SmartWatches);
        categories.add(Cameras);
        categoryAdapter.notifyDataSetChanged();
    }

    private ItemClickListener itemClickListener = new ItemClickListener() {         // on item clicked in recent product recycler
        @Override
        public void onItemClick(RecyclerView.ViewHolder myViewHolder, int pos, ImageView imageView) {

            Intent intent = new Intent(getContext(), IndividualProductActivity.class);          // go to that product page
            intent.putExtra("items",productsArrayList.get(pos));    // get the current product info
            intent.putExtra(PRODUCT_TRANSITION_IMAGE, ViewCompat.getTransitionName(imageView));  // send transition image key and name
            Pair<View, String> pair1 = Pair.create((View) ((RecentProductAdapter.MyViewHolder)myViewHolder).productImage, ((RecentProductAdapter.MyViewHolder)myViewHolder).productImage.getTransitionName()); // create a pair of views and string to image transition, send product image and transition name
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    getActivity(),          // create the transition animation with the pair
                    pair1);
            startActivity(intent, options.toBundle());      // start the activity and pass the options as a bundle
        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() { // on category item clicked
        @Override
        public void onClick(View v) {


            CategoryAdapter.ViewHolder viewHolder = (CategoryAdapter.ViewHolder) v.getTag(); // get the tag
            int position = viewHolder.getAdapterPosition();                                   // get the adapter position
            Intent intent = new Intent(getContext(), IndividualCategoryProducts.class); // go that category activity
            intent.putExtra("categroy",  categories.get(position)); // bundle category info
            startActivity(intent);



        }
    };

    private ItemClickListener itemClickListenerRecommendedProducts = new ItemClickListener() {         // on item clicked in recent product recycler
        @Override
        public void onItemClick(RecyclerView.ViewHolder myViewHolder, int pos, ImageView imageView) {

            Intent intent = new Intent(getContext(), IndividualProductActivity.class);          // go to that product page
            intent.putExtra("items",recommendedProducts.get(pos));    // get the current product info
            intent.putExtra(PRODUCT_TRANSITION_IMAGE, ViewCompat.getTransitionName(imageView));  // send transition image key and name
            Pair<View, String> pair1 = Pair.create((View) ((RecommendedProductsAdapter.RecommendedProductsViewHolder)myViewHolder).productImage, ((RecommendedProductsAdapter.RecommendedProductsViewHolder)myViewHolder).productImage.getTransitionName()); // create a pair of views and string to image transition, send product image and transition name
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    getActivity(),          // create the transition animation with the pair
                    pair1);
            startActivity(intent, options.toBundle());      // start the activity and pass the options as a bundle
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        new ConnectionDetector(getContext()); // on Resume ( this method is fired before on create )  activity, check if user has connection or not
    }

    private void getOrders()
    {
        try {

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Orders");
            myRef.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    recommendedProducts.clear();
                    productsMap.clear();
                    ordersArrayList.clear();
                    for (DataSnapshot orders : snapshot.getChildren()) {

                        ordersArrayList.add(orders.getValue(Orders.class));

                    }
                    for(Orders order : ordersArrayList)
                    {
                        for(Cart product : order.getPurchasedProducts())
                        {
                            productsMap.put(product.getProduct().getProduct_id(),product.getProduct());
                        }
                    }
                    if(userSession.getSearchQueries() != null) {
                        userSearchQueries = userSession.getSearchQueries();
                        swipeRefreshLayout.setEnabled(true);
                    }
                    else
                    {
                        swipeRefreshLayout.setEnabled(false);
                       userSearchQueries = new HashSet<>();
                    }

                    for(Products product : allProductsArrayList)
                    {
                        for(Map.Entry<String,Products> entry : productsMap.entrySet())
                        {

                            if(!entry.getKey().equals(product.getProduct_id()))
                            {
                                if(entry.getValue().getCategory().equals(product.getCategory()) || entry.getValue().getProductBrand().equals(product.getProductBrand()))
                                {
                                    recommendedProducts.add(product);
                                }
                            }


                        }

                        for(String s : userSearchQueries)
                        {
                            if(product.getProductBrand().toLowerCase().contains(s.toLowerCase()) || product.getCategory().toLowerCase().contains(s.toLowerCase()) || product.getProductName().toLowerCase().contains(s.toLowerCase()))
                            {
                                recommendedProducts.add(product);

                            }

                        }

                    }


                    for (int i = recommendedProducts.size() - 1; i >= 0; i--){

                        for(Map.Entry<String,Products> entry : productsMap.entrySet())
                        {

                            if (recommendedProducts.get(i).getProduct_id().equals(entry.getKey())){
                                recommendedProducts.remove(i);
                            }

                        }
                    }
                    List<Products> listWithoutDuplicates = recommendedProducts.stream()
                            .distinct()
                            .collect(Collectors.toList());
                    recommendedProducts.clear();
                    recommendedProducts.addAll(listWithoutDuplicates);
                    if(recommendedProducts.size() == 0)
                    {
                        swipeRefreshLayout.setEnabled(false);
                        recyclerViewRecommendedProducts.setVisibility(View.GONE);
                        textViewRecommendedProducts.setVisibility(View.GONE);
                    }
                    else if(recommendedProducts.size() < 3)
                    {
                        swipeRefreshLayout.setEnabled(false);
                        recyclerViewRecommendedProducts.setVisibility(View.GONE);
                        textViewRecommendedProducts.setVisibility(View.GONE);
                    }
                    else
                    {
                        swipeRefreshLayout.setEnabled(true);
                        recyclerViewRecommendedProducts.setVisibility(View.VISIBLE);
                        textViewRecommendedProducts.setVisibility(View.VISIBLE);
                    }
                    Collections.shuffle(recommendedProducts);
                    recyclerViewRecommendedProducts.setAdapter(recommendedProductAdapter);
                    progressBar.setVisibility(View.GONE);
                    Log.d(TAG,productsMap.toString());
                    //recommendedProductAdapter.notifyDataSetChanged();
                    runLayoutAnimation(recyclerViewRecommendedProducts);
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

    private Runnable runnable = new Runnable() {    // create a runnable object to  retrieve user's info in background

        public void run() {
            progressBar.setVisibility(View.VISIBLE);
                getOrders();


        }
    };



    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down);
        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();

    }
}


