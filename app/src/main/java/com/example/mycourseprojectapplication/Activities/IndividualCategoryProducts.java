package com.example.mycourseprojectapplication.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.transition.Explode;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycourseprojectapplication.Adapters.ProductAdapter;
import com.example.mycourseprojectapplication.Fragments.ProductsFilterFragment;
import com.example.mycourseprojectapplication.Interfaces.ItemClickListener;
import com.example.mycourseprojectapplication.Models.Category;
import com.example.mycourseprojectapplication.Models.Products;
import com.example.mycourseprojectapplication.Models.Rating;
import com.example.mycourseprojectapplication.R;
import com.example.mycourseprojectapplication.Utilities.ConnectionDetector;
import com.example.mycourseprojectapplication.Utilities.UserSession;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static android.graphics.Typeface.BOLD;

public class IndividualCategoryProducts extends AppCompatActivity implements ItemClickListener {

    private final String TAG = this.getClass().getSimpleName(); // this is used for debugging
    private ProductAdapter productAdapter;
    private MaterialSearchView searchView;
    private ArrayList<Products> productsArrayList;
    public static final String PRODUCT_TRANSITION_IMAGE = "product_image";             // declare our variables
    private Category category;
    private TextView textCartItemCount,textViewEmptyProducts;
    private int mCartItemCount = 0;
    private ArrayList<Rating>ratingArrayList;
    private String[] mobileBrands = {"Apple", "Samsung", "Huawei", "Google","OnePlus","LG","Sony"};
    private String[] tvBrands = {"Samsung","Apple","LG","Sony","TCL"};
    private String[] laptopBrands = {"Apple", "Samsung","Dell","Acer","MSI","LG"};
    private String[] headsetBrands = {"Apple", "Audio-Technica","Bose","Sony","Sennheiser","Beyerdynamic"};
    private String[] smartWatcheBrands = {"Apple", "Samsung","Garmin","Fitbit","Fossil"};
    private String[] cameraBrands = {"Canon", "Nikon","Pentax","Sony","Olympus","Fujifilm"};
    private String[] categories = {"Smartphones","Laptops","TV's","Headsets","Smartwatches","Cameras"};
    private  RecyclerView recyclerView;
    private UserSession userSession;
    private FloatingActionButton floatingActionButton;
    private ProgressBar progressBar;
    private CardView cardView;
    private CoordinatorLayout coordinatorLayout;
    private TextView counter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        Explode fade = new Explode(); // create explode transition
        View decor = getWindow().getDecorView(); // get the window decoder
        fade.excludeTarget(decor.findViewById(R.id.action_bar_container), true); // exclude the action bar from the transition
        fade.excludeTarget(android.R.id.statusBarBackground, true); // exclude the status bar from the transition
        fade.excludeTarget(android.R.id.navigationBarBackground, true); // exclude the navigation bar from the transition
        getWindow().setEnterTransition(fade); // set the enter and exit transition to explode
        getWindow().setExitTransition(fade);
        getWindow().setSharedElementEnterTransition(TransitionInflater.from(this).inflateTransition(R.transition.changeimage)); // set the shared element transitions to custom transition -> change image
        getWindow().setSharedElementExitTransition(TransitionInflater.from(this).inflateTransition(R.transition.changeimage));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_category_products);
        new ConnectionDetector(this);          // check if user has Internet connection
        userSession = new UserSession(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // setup the action toolbar with activity title and back icon functionality
        category = (Category) getIntent().getSerializableExtra("categroy"); // get the category from the previous activity
        toolbar.setTitle(category.getTitle());
        counter = findViewById(R.id.textViewProductsCounter);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {  // when back button is clicked finish the activity
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolBarLayout.setTitle(category.getTitle()); // set the title to the category title

        toolBarLayout.setExpandedTitleGravity(Gravity.CENTER);
        toolBarLayout.setExpandedTitleTextAppearance(BOLD);
        toolBarLayout.setExpandedTitleColor(Color.BLACK);
        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:
                toolBarLayout.setExpandedTitleColor(Color.WHITE);
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                // process
                toolBarLayout.setExpandedTitleColor(Color.BLACK);
                break;
        }

        AppBarLayout mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    isShow = true;
                    counter.animate().scaleY(0).scaleX(0).setInterpolator(new LinearInterpolator());
                    counter.setVisibility(View.GONE);
                } else if (isShow) {
                    isShow = false;
                    counter.animate().scaleY(1).scaleX(1).setInterpolator(new LinearInterpolator());
                    counter.setVisibility(View.VISIBLE);
                }
            }
        });
        productsArrayList = new ArrayList<>();
        /*ImageView imageView = findViewById(R.id.imageView6);
        //imageView.setImageResource(category.getRes_img());
        switch (category.getTitle())
        {
            case "Smartphones":
                imageView.setImageResource(R.drawable.smartphone);
                break;
            case "Laptops":
                imageView.setImageResource(R.drawable.laptop1);
                break;
            case "TV's":
                imageView.setImageResource(R.drawable.tv);
                break;
            case "Headsets":
                imageView.setImageResource(R.drawable.hs);
                break;
            case "Smartwatches":
                imageView.setImageResource(R.drawable.sw);
                break;
            case "Cameras":
                imageView.setImageResource(R.drawable.cam);
                break;

        }*/
        progressBar = findViewById(R.id.progressBarCategoryProducts);
        recyclerView = findViewById(R.id.categroyProductRecycler);
        textViewEmptyProducts = findViewById(R.id.textViewEmptyProducts);
        coordinatorLayout = findViewById(R.id.coordinatorProducts);
        productAdapter = new ProductAdapter(productsArrayList,IndividualCategoryProducts.this::onItemClick);
        recyclerView.setAdapter(productAdapter);
        getProducts();
        userSession.setFilterSwitchStateProducts(false);
        userSession.setFilterTypeProducts("Default");
        userSession.setProductBrandFilter("Default");

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        ratingArrayList = new ArrayList<>();
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        NestedScrollView nestedScrollView = findViewById(R.id.nestedScrollView);
        floatingActionButton = findViewById(R.id.floatingActionButtonSortProducts);
        floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_round_sort_24));
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("category", category);

                switch (category.getTitle()) {
                    case "Smartphones":
                        bundle.putStringArray("brands", mobileBrands);
                        break;
                    case "Laptops":
                        bundle.putStringArray("brands", laptopBrands);
                        break;
                    case "TV's":
                        bundle.putStringArray("brands", tvBrands);
                        break;
                    case "Headsets":
                        bundle.putStringArray("brands", headsetBrands);
                        break;
                    case "Smartwatches":
                        bundle.putStringArray("brands", smartWatcheBrands);
                        break;
                    case "Cameras":
                        bundle.putStringArray("brands", cameraBrands);
                        break;
                }
                ProductsFilterFragment productsFilterFragment = ProductsFilterFragment.newInstance();
                productsFilterFragment.setArguments(bundle);
                productsFilterFragment.show(getSupportFragmentManager(), productsFilterFragment.getTag());
                /*
                Intent intent = new Intent(IndividualCategoryProducts.this,ProductFilterActivity.class);
                intent.putExtra("brands",bundle);
                startActivityForResult(intent,1);*/

                /*
                com.google.android.material.transition.platform.MaterialArcMotion materialArcMotion = new MaterialArcMotion();
                MaterialContainerTransform materialContainerTransform = new MaterialContainerTransform();
                materialContainerTransform.setStartView(floatingActionButton);
                materialContainerTransform.setEndView(cardView);
                materialContainerTransform.addTarget(cardView);
                // materialContainerTransform.setStartShapeAppearanceModel(ShapeAppearanceModel.builder().build().withCornerSize(10f));
                materialContainerTransform.setDuration(600L);
                materialContainerTransform.setScrimColor(Color.TRANSPARENT);
                materialContainerTransform.setContainerColor(Color.TRANSPARENT);
                materialContainerTransform.setPathMotion(materialArcMotion);
                materialContainerTransform.setFadeMode(MaterialContainerTransform.FADE_MODE_THROUGH);
                beginDelayedTransition(coordinatorLayout,materialContainerTransform);
                floatingActionButton.setVisibility(View.GONE);
                cardView.setVisibility(View.VISIBLE);
                nestedScrollView.setNestedScrollingEnabled(false);
                nestedScrollView.setSmoothScrollingEnabled(false);*/


            }
        });

      /*  Button buttonApply = findViewById(R.id.buttonSortProductApply);
        buttonApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                com.google.android.material.transition.platform.MaterialArcMotion materialArcMotion = new MaterialArcMotion();
                MaterialContainerTransform materialContainerTransform = new MaterialContainerTransform();
                materialContainerTransform.setStartView(cardView);
                materialContainerTransform.setEndView(floatingActionButton);
                materialContainerTransform.addTarget(floatingActionButton);
                materialContainerTransform.setStartShapeAppearanceModel(ShapeAppearanceModel.builder().build().withCornerSize(10f));
                materialContainerTransform.setDuration(600L);
                materialContainerTransform.setScrimColor(Color.TRANSPARENT);
                materialContainerTransform.setContainerColor(Color.TRANSPARENT);
                materialContainerTransform.setPathMotion(materialArcMotion);
                materialContainerTransform.setFadeMode(MaterialContainerTransform.FADE_MODE_THROUGH);
                beginDelayedTransition(coordinatorLayout,materialContainerTransform);
                floatingActionButton.setVisibility(View.VISIBLE);
                cardView.setVisibility(View.GONE);

            }
        });

        Button buttonClear = findViewById(R.id.buttonClearFilterProducts);
        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.google.android.material.transition.platform.MaterialArcMotion materialArcMotion = new MaterialArcMotion();
                MaterialContainerTransform materialContainerTransform = new MaterialContainerTransform();
                materialContainerTransform.setStartView(cardView);
                materialContainerTransform.setEndView(floatingActionButton);
                materialContainerTransform.addTarget(floatingActionButton);
                materialContainerTransform.setStartShapeAppearanceModel(ShapeAppearanceModel.builder().build().withCornerSize(10f));
                materialContainerTransform.setDuration(600L);
                materialContainerTransform.setScrimColor(Color.TRANSPARENT);
                materialContainerTransform.setContainerColor(Color.TRANSPARENT);
                materialContainerTransform.setPathMotion(materialArcMotion);
                materialContainerTransform.setFadeMode(MaterialContainerTransform.FADE_MODE_THROUGH);
                beginDelayedTransition(coordinatorLayout,materialContainerTransform);
                floatingActionButton.setVisibility(View.VISIBLE);
                cardView.setVisibility(View.GONE);

            }
        });*/


        if(userSession.getFilterSwitchStateProducts())
        {
            floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_outline_filter_alt_24));
            if(userSession.getBrandProductFilter() == null || userSession.getFilterTypeProducts() == null)
            {
                floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_round_sort_24));
                getProducts();
            }
            else
            {
                if (userSession.getFilterTypeProducts().equals("Default") || userSession.getBrandProductFilter().equals("Default")) {
                    getProducts();

                }
                getProductsFiltered(userSession.getBrandProductFilter(),userSession.getFilterTypeProducts());
            }
        }
        else
        {
            getProducts();
            floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_round_sort_24));
        }





    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1)
        {
            /*if(resultCode == Activity.RESULT_OK)
            {
                if(data.getStringExtra("brand").isEmpty())
                {
                    return;
                }
                if(data.getStringExtra("filter").isEmpty())
                {
                    return;
                }
                if(data.getSerializableExtra("categroy") == null)
                {
                    return;
                }

                String brand = data.getStringExtra("brand");
                String filter = data.getStringExtra("filter");
                if (userSession.getFilterTypeProducts().equals("Default") || userSession.getBrandProductFilter().equals("Default")) {
                    getProducts();
                    productAdapter.notifyDataSetChanged();
                }
                if (!brand.isEmpty()) {

                    getProductsFiltered(brand,filter);
                }
                else {

                    getProducts();
                    if (filter.equals("Price: Low to High")) {
                        Collections.sort(productsArrayList, PRICE_COMPARATOR);

                    } else if (filter.equals("Price: High to Low")) {
                        Collections.sort(productsArrayList, PRICE_COMPARATOR_DSCE);

                    } else if (filter.equals("Descending")) {
                        Collections.sort(productsArrayList, ALPHABETICAL_COMPARATOR_DSCE);


                    } else {
                        Collections.sort(productsArrayList, ALPHABETICAL_COMPARATOR);

                    }
                }
                productAdapter = new ProductAdapter(productsArrayList,this::onItemClick);
                floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_outline_filter_alt_24));
                recyclerView.setAdapter(productAdapter);
                productAdapter.notifyDataSetChanged();


            }*/
        }
    }

    public void getProducts() // method to get all products related to each category
    {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Products");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                productsArrayList.clear(); // clear the array each time a new product is added to not get redundant products
                ratingArrayList.clear();
                for(DataSnapshot products : dataSnapshot.getChildren())
                {
                    if(products.child("category").getValue().toString().equals(category.getTitle())) { // get the products related to the current category tilte
                        productsArrayList.add(products.getValue(Products.class)); // add the product to the array list

                    }

                }
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("Reviews");

                for(int i = 0 ; i < productsArrayList.size(); i++) { // get the reviews for each product in the arraylist
                    myRef.child(productsArrayList.get(i).getProduct_id()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {


                            for (DataSnapshot reviews : snapshot.getChildren()) {
                                ratingArrayList.add(reviews.getValue(Rating.class)); // add the rating to the arraylist

                            }


                            Log.i(TAG,ratingArrayList.toString());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                if(productsArrayList.size() == 0)
                {
                    textViewEmptyProducts.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    counter.setText("0 items found");
                }
                else
                {
                    textViewEmptyProducts.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);        //show recycler view and  hide lottie animation and corresponding text and change background color to grayish
                    recyclerView.setAdapter(productAdapter);
                    progressBar.setVisibility(View.GONE);
                    floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_round_sort_24));
                    runLayoutAnimation(recyclerView);
                    productAdapter.notifyDataSetChanged();
                    counter.setText(productsArrayList.size()+" items found");
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

    public void getProductsFiltered(String brand, String filter) // method to get all products related to each category
    {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Products");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                productsArrayList.clear(); // clear the array each time a new product is added to not get redundant products
                ratingArrayList.clear();
                for(DataSnapshot products : dataSnapshot.getChildren())
                {
                    if(products.child("category").getValue().toString().equals(category.getTitle()) && products.child("productBrand").getValue().toString().toLowerCase().equals(brand.toLowerCase())) { // get the products related to the current category tilte
                        productsArrayList.add(products.getValue(Products.class)); // add the product to the array list

                    }

                }
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("Reviews");

                for(int i = 0 ; i < productsArrayList.size(); i++) { // get the reviews for each product in the arraylist
                    myRef.child(productsArrayList.get(i).getProduct_id()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {


                            for (DataSnapshot reviews : snapshot.getChildren()) {
                                ratingArrayList.add(reviews.getValue(Rating.class)); // add the rating to the arraylist

                            }


                            Log.i(TAG,ratingArrayList.toString());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                if(productsArrayList.size() == 0)
                {

                    textViewEmptyProducts.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    counter.setText("0 items found");
                }
                else
                {

                    textViewEmptyProducts.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);        //show recycler view and  hide lottie animation and corresponding text and change background color to grayish
                    if (filter.equals("Price: Low to High")) {
                        Collections.sort(productsArrayList, PRICE_COMPARATOR);

                    } else if (filter.equals("Price: High to Low")) {
                        Collections.sort(productsArrayList, PRICE_COMPARATOR_DSCE);

                    } else if (filter.equals("Descending")) {
                        Collections.sort(productsArrayList, ALPHABETICAL_COMPARATOR_DSCE);


                    } else {
                        Collections.sort(productsArrayList, ALPHABETICAL_COMPARATOR);

                    }
                floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_outline_filter_alt_24));
                    progressBar.setVisibility(View.GONE);
                recyclerView.setAdapter(productAdapter);
                    runLayoutAnimation(recyclerView);
                productAdapter.notifyDataSetChanged();
                    counter.setText(productsArrayList.size()+" items found");


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

    public void getProductsSorted(String filter) // method to get all products related to each category
    {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Products");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                productsArrayList.clear(); // clear the array each time a new product is added to not get redundant products
                ratingArrayList.clear();
                for(DataSnapshot products : dataSnapshot.getChildren())
                {
                    if(products.child("category").getValue().toString().equals(category.getTitle())) { // get the products related to the current category tilte
                        productsArrayList.add(products.getValue(Products.class)); // add the product to the array list

                    }

                }
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("Reviews");

                for(int i = 0 ; i < productsArrayList.size(); i++) { // get the reviews for each product in the arraylist
                    myRef.child(productsArrayList.get(i).getProduct_id()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {


                            for (DataSnapshot reviews : snapshot.getChildren()) {
                                ratingArrayList.add(reviews.getValue(Rating.class)); // add the rating to the arraylist

                            }


                            Log.i(TAG,ratingArrayList.toString());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                if(productsArrayList.size() == 0)
                {

                    textViewEmptyProducts.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    counter.setText("0 items found");
                }
                else
                {

                    textViewEmptyProducts.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);        //show recycler view and  hide lottie animation and corresponding text and change background color to grayish
                    if (filter.equals("Price: Low to High")) {
                        Collections.sort(productsArrayList, PRICE_COMPARATOR);

                    } else if (filter.equals("Price: High to Low")) {
                        Collections.sort(productsArrayList, PRICE_COMPARATOR_DSCE);

                    } else if (filter.equals("Descending")) {
                        Collections.sort(productsArrayList, ALPHABETICAL_COMPARATOR_DSCE);


                    } else {
                        Collections.sort(productsArrayList, ALPHABETICAL_COMPARATOR);

                    }
                    floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_outline_filter_alt_24));
                    recyclerView.setAdapter(productAdapter);
                    progressBar.setVisibility(View.GONE);
                    runLayoutAnimation(recyclerView);
                    productAdapter.notifyDataSetChanged();
                    counter.setText(productsArrayList.size()+" items found");

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
        Pair<View, String> pair1 = Pair.create((View) ((ProductAdapter.MyViewHolder)myViewHolder).productImage, ((ProductAdapter.MyViewHolder)myViewHolder).productImage.getTransitionName()); // create a pair of views and string to image transition, send product image and transition name
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                pair1);   // create the transition animation with the pair

        startActivity(intent, options.toBundle());  // start the activity and pass the options as a bundle
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_product, menu); // inflate the menu
        final MenuItem menuItem = menu.findItem(R.id.action_cart); // get the cart item
        View actionView =  menuItem.getActionView(); // get the action view for the cart
        textCartItemCount = (TextView) actionView.findViewById(R.id.cart_badge); // initialize the counter for the cart
        setupBadge();  // setup the cart badge
        ImageView imageView = actionView.findViewById(R.id.imageView); // initialize cart image
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem); // on click fire on OptionsItemSelected method
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem); // on click fire on OptionsItemSelected method
            }
        });
        return true;
    }

    private void setupBadge() {

        if (textCartItemCount != null) {  // check if counter text not null ( initialized)
            if (mCartItemCount == 0) { // check if its visibility not gone set it to gone
                if (textCartItemCount.getVisibility() != View.GONE) {
                    textCartItemCount.setVisibility(View.GONE);
                }
            } else { // else set the value with mCartItemCount

                textCartItemCount.setText(String.valueOf(Math.min(mCartItemCount, 99)));
                if (textCartItemCount.getVisibility() != View.VISIBLE) { // if the view is not viable set it to visible
                    textCartItemCount.setVisibility(View.VISIBLE);
                }
            }

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        new UserSession(this).setCartValue(mCartItemCount); // save cart value in user session on activity pause (stop)
    }

    @Override
    protected void onResume() {
        super.onResume();
        new ConnectionDetector(this); // on Resume ( this method is fired before on create )  activity, check if user has connection or not
        mCartItemCount = new UserSession(this).getCartValue(); // on Resume get cart counter value from session
        invalidateOptionsMenu(); // refresh the menu

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.action_cart: // on cart icon click go to cart activity
                Intent intent = new Intent(IndividualCategoryProducts.this,CartActivity.class);
                startActivity(intent);
        }
        return false;
    }

    private static final Comparator<Products> ALPHABETICAL_COMPARATOR = new Comparator<Products>() {
        @Override
        public int compare(Products o1, Products o2) {
            return o1.getProductName().compareToIgnoreCase(o2.getProductName());
        }
    };

    private static final Comparator<Products> ALPHABETICAL_COMPARATOR_DSCE = new Comparator<Products>() {
        @Override
        public int compare(Products o1, Products o2) {
            return o1.getProductName().compareToIgnoreCase(o2.getProductName());
        }
    }.reversed();
    private static final Comparator<Products> PRICE_COMPARATOR_DSCE = new Comparator<Products>() {
        @Override
        public int compare(Products o1, Products o2) {
            if (o1.getProductPrice()> o2.getProductPrice()) {
                return 1;
            }
            else if (o1.getProductPrice() < o2.getProductPrice()) {
                return -1;
            }
            else {
                return 0;
            }
        }
    }.reversed();
    private static final Comparator<Products> PRICE_COMPARATOR = new Comparator<Products>() {
        @Override
        public int compare(Products o1, Products o2) {
            if (o1.getProductPrice()> o2.getProductPrice()) {
                return 1;
            }
            else if (o1.getProductPrice() < o2.getProductPrice()) {
                return -1;
            }
            else {
                return 0;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_slide_right);
        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();

    }


}