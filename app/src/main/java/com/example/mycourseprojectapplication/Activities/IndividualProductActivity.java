package com.example.mycourseprojectapplication.Activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.transition.Explode;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.mycourseprojectapplication.Adapters.RelatedProductAdapter;
import com.example.mycourseprojectapplication.Adapters.ReviewsAdapter;
import com.example.mycourseprojectapplication.Interfaces.ItemClickListener;
import com.example.mycourseprojectapplication.Models.Cart;
import com.example.mycourseprojectapplication.Models.Products;
import com.example.mycourseprojectapplication.Models.Rating;
import com.example.mycourseprojectapplication.Models.Users;
import com.example.mycourseprojectapplication.R;
import com.example.mycourseprojectapplication.Utilities.ConnectionDetector;
import com.example.mycourseprojectapplication.Utilities.UserSession;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

import static com.example.mycourseprojectapplication.Activities.IndividualCategoryProducts.PRODUCT_TRANSITION_IMAGE;

public class IndividualProductActivity extends AppCompatActivity implements ItemClickListener {

    private final String TAG = this.getClass().getSimpleName(); // this is used for debugging
    private Products product;
    private FirebaseAuth mAuth;
    private int mCartItemCount = 0;
    private ChipGroup colorGroup,storageGroup;
    private TextView productPrice;
    private String colorSelected = "", configurationSelected = "";
    private double price = 0;
    private Chip chipColor2,chipColor3,chipColor4,chipColor5,chipConfig2,chipConfig3,chipConfig4;            // declare our variables
    private TextView textViewQuantity;
    private int quantity = 1;
    private ArrayList<Cart> carts;
    private ArrayList<Rating> ratingArrayList;
    private ReviewsAdapter reviewsAdapter;
    private RatingBar ratingBar;
    private double total;
    private double avg;
    private double rate;
    private TextView textViewReviewsCount;
    private RelatedProductAdapter productAdapter;
    private ArrayList<Products>productsArrayList;
    private TextView textViewProductStock;
    private Users user;
    private static final String DIALOG_IMAGE = "image";
    private List<String> imagesUrls;
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
        setContentView(R.layout.activity_individual_product);
        supportPostponeEnterTransition();

        new ConnectionDetector(this); // check if user has internet connection or not

        final Toolbar toolbar = findViewById(R.id.toolbarIndivdualProduct);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // setup the action toolbar with activity title and back icon functionality
        setTitle("Product Details");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        DotsIndicator dotsIndicator = (DotsIndicator) findViewById(R.id.dotsIndicator);
        ratingBar = findViewById(R.id.ratingBar);
        textViewReviewsCount = findViewById(R.id.textViewReviewsCount);
        chipColor2 = findViewById(R.id.chipColorBlack);
        chipColor3 = findViewById(R.id.chipColorWhite);
        chipColor4 = findViewById(R.id.chipColorGold);
        chipColor5 = findViewById(R.id.chipColorBlue);
        chipConfig2 = findViewById(R.id.chipConfigration2);
        chipConfig3 = findViewById(R.id.chipConfigration3);
        chipConfig4 = findViewById(R.id.chipConfigration4);
        textViewProductStock = findViewById(R.id.textViewProductStock);
        Button buttonDecrease = findViewById(R.id.buttonDecreaseQuantity);
        Button buttonIncrease = findViewById(R.id.buttonIncreaseQuantity);        // initialize views and classes
        textViewQuantity = findViewById(R.id.textViewQuantityProduct);
        RecyclerView reviewRecyclerView = findViewById(R.id.reviewsRecycler);
        ViewPager viewPager = findViewById(R.id.viewPagerImage);
        TextView productName = findViewById(R.id.textViewProductNameRecent);
        TextView productBrand = findViewById(R.id.textViewProductBrandRecent);
        productPrice = findViewById(R.id.textViewProductPriceRecent);
        TextView productDescription = findViewById(R.id.textViewProductDescription);
        Button buttonCart = findViewById(R.id.buttonAddToCart);
        colorGroup = findViewById(R.id.colorChipGroup);
        storageGroup = findViewById(R.id.storageChipGroup);
        LinearLayout colorViewGroup = findViewById(R.id.LinearLayoutColor);
        LinearLayout configViewGroup = findViewById(R.id.LinearLayoutConfig);
        View divider = findViewById(R.id.divider4);
        View divider1 = findViewById(R.id.divider5);
        Button buttonBuy = findViewById(R.id.buttonBuy);
        RecyclerView recyclerView = findViewById(R.id.relatedProductRecyclerView);

        carts = new ArrayList<>();
        ratingArrayList = new ArrayList<>();
        reviewsAdapter = new ReviewsAdapter(ratingArrayList,user);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        reviewRecyclerView.setAdapter(reviewsAdapter);
        reviewRecyclerView.setLayoutManager(layoutManager); // setup reviews recycler view
        DividerItemDecoration itemDecorator = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        reviewRecyclerView.addItemDecoration(itemDecorator);

        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager1);          // setup related product recycler view
        productsArrayList = new ArrayList<>();
        productAdapter = new RelatedProductAdapter(productsArrayList,this);
        recyclerView.setAdapter(productAdapter);

        Bundle extras = getIntent().getExtras();
        product = (Products) extras.get("items");
        Cart cartItem = (Cart) extras.get("cart");
        String imageTransitionName = extras.getString(PRODUCT_TRANSITION_IMAGE);
        viewPager.setTransitionName(imageTransitionName);
        productName.setText(product.getProductName());                         // set the values from the previous activity
        productBrand.setText(product.getProductBrand());
        productPrice.setText("$"+product.getProductPrice());
        productDescription.setText(product.getProductDescription());
        HashMap<String, String> hashMap = product.getProductImages();
        imagesUrls = new ArrayList<>(hashMap.values());
        myViewPagerAdapter myViewPagerAdapter = new myViewPagerAdapter((ArrayList<String>) imagesUrls);
        viewPager.setAdapter(myViewPagerAdapter);
        dotsIndicator.setViewPager(viewPager);





        buttonCart.setOnClickListener(new View.OnClickListener() { // on button add to cart click
            @Override
            public void onClick(View v) {


                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("Cart");
                if (Build.VERSION.SDK_INT >= 26) { // check if android version is 8 and above
                    ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE)); // get vibrate service and vibrate phone with effect
                } else {
                    ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(300);  // get vibrate service and vibrate phone
                }
                myRef.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() { // add items to the database
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(snapshot.child(product.getProduct_id()).exists()) // check if item already exists
                        {
                            Toast.makeText(IndividualProductActivity.this, "Product Already in Cart", Toast.LENGTH_SHORT).show(); // show error message
                        }
                        else
                        {
                            if(product.getQuantity() == 0)
                            {
                                Toast.makeText(IndividualProductActivity.this, "This Product is out of stock!", Toast.LENGTH_SHORT).show(); // show error message
                                return;
                            }
                            if(quantity > product.getQuantity())
                            {
                                Toast.makeText(IndividualProductActivity.this, "Your reached the maximum quantity for this item!", Toast.LENGTH_SHORT).show();
                            }
                            if(colorSelected.isEmpty() || configurationSelected.isEmpty()) // check if config and color is selected
                            {
                                Toast.makeText(IndividualProductActivity.this, "Please Choose the Color and Configuration", Toast.LENGTH_SHORT).show(); // show error message
                                return;
                            }
                            FirebaseDatabase.getInstance().getReference("Cart").child(mAuth.getCurrentUser().getUid()).child(product.getProduct_id()).setValue(getProductObject()); // else add product to user cart
                            mCartItemCount++; // increase session cart value
                            invalidateOptionsMenu(); // refresh menu
                            Toast.makeText(IndividualProductActivity.this, "Product Added to Cart", Toast.LENGTH_SHORT).show(); // show success message
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        if(product.getQuantity() == 0)
        {
            textViewProductStock.setText("Out of Stock!");
        }
        else if(product.getQuantity() >= 10)
        {
            textViewProductStock.setText("In Stock!");
        }
        else
        {
            textViewProductStock.setText(product.getQuantity() + " left, Order now!");
        }

        TextView textView = findViewById(R.id.textViewConfigartion);
        switch (product.getCategory()) { // change the config based on category

            case "Smartphones":
                colorViewGroup.setVisibility(View.VISIBLE);
                divider.setVisibility(View.VISIBLE);
                chipColor5.setCheckable(true);
                chipColor4.setCheckable(true);
                configViewGroup.setVisibility(View.VISIBLE);
                divider1.setVisibility(View.VISIBLE);

                switch (product.getProductBrand())
                {
                    case "Apple":
                        switch (product.getProductName())
                        {
                            case "iPhone 11 Pro":
                                chipColor2.setVisibility(View.VISIBLE);
                                chipColor2.setText("White");
                                chipColor3.setText("Gold");
                                chipColor5.setText("Green");
                                chipColor5.setVisibility(View.VISIBLE);
                                chipColor4.setVisibility(View.GONE);
                                break;
                            case "iPhone 12 Pro":
                                chipColor2.setVisibility(View.VISIBLE);
                                break;

                        }

                        chipConfig2.setText("128GB");
                        chipConfig3.setText("256GB");
                        chipConfig4.setText("512GB");
                        break;
                    case "Samsung":
                        switch (product.getProductName())
                        {
                            case "Galaxy S21":
                                chipColor4.setText("Purple");
                                chipColor4.setVisibility(View.VISIBLE);
                                break;
                            case "Galaxy Note20 Ultra":
                                chipColor2.setText("Black");
                                chipColor3.setText("White");
                                chipColor4.setText("Bronze");
                                chipColor4.setVisibility(View.VISIBLE);
                                break;
                            case "Galaxy S21 Ultra":
                                chipColor2.setText("Black");
                                chipColor3.setText("White");
                                chipColor4.setVisibility(View.GONE);

                        }
                        chipColor5.setVisibility(View.GONE);
                        chipConfig2.setText("128GB");
                        chipConfig3.setText("256GB");
                        chipConfig4.setText("512GB");
                        break;
                    case "Huawei" :
                        chipColor2.setText("Black");
                        chipColor4.setText("Sliver");
                        chipColor3.setVisibility(View.GONE);
                        chipColor5.setVisibility(View.GONE);
                        chipColor4.setVisibility(View.VISIBLE);
                        chipConfig2.setText("128GB");
                        chipConfig3.setText("256GB");
                        chipConfig4.setText("512GB");
                        break;
                    case "OnePlus":
                        chipColor2.setText("Black");
                        chipColor3.setText("Green");
                        chipColor5.setVisibility(View.GONE);
                        chipColor4.setVisibility(View.GONE);
                        chipConfig2.setText("128GB");
                        chipConfig3.setText("256GB");
                        chipConfig4.setText("512GB");
                    default:
                        chipColor2.setText("Black");
                        chipColor4.setVisibility(View.GONE);
                        chipColor5.setVisibility(View.GONE);
                        chipConfig2.setText("128GB");
                        chipConfig3.setText("256GB");
                        chipConfig4.setText("512GB");
                        break;

                }
                break;
            case "Laptops":

                switch (product.getProductBrand())
                {
                    case "Apple":
                        chipColor2.setText("Sliver");
                        chipColor3.setText("Gold");
                        chipColor3.setVisibility(View.VISIBLE);
                        chipColor5.setVisibility(View.GONE);
                        chipColor4.setVisibility(View.GONE);
                        chipConfig2.setText("Intel Core i5 + 8GB Ram + 256GB SSD");
                        chipConfig3.setText("Intel Core i7 + 16GB Ram + 1TB SSD");
                        chipConfig4.setText("Apple M1 + 16GB Ram + 1TB SDD");
                        break;
                    case "Samsung":
                        chipColor2.setText("Black");
                        chipColor3.setText("Purple");
                        chipColor3.setVisibility(View.VISIBLE);
                        chipColor5.setVisibility(View.GONE);
                        chipColor4.setVisibility(View.GONE);
                        chipConfig2.setText("Intel Core i3 + 8GB Ram + 256GB SSD");
                        chipConfig3.setText("Intel Core i5 + 8GB Ram + 512GB SSD");
                        chipConfig4.setVisibility(View.GONE);
                        break;
                    case "MSI" :
                        chipColor2.setText("Black");
                        chipColor3.setVisibility(View.GONE);
                        chipColor5.setVisibility(View.GONE);
                        chipColor4.setVisibility(View.GONE);
                        chipConfig2.setText("Intel Core i7 + 16GB Ram + 1TB HDD + 512GB SSD + RTX 2070");
                        chipConfig4.setText("Intel Core i9 + 32GB Ram + 1TB HDD + 1TB SSD + RTX 3070");
                        chipConfig4.setVisibility(View.VISIBLE);
                        chipConfig3.setVisibility(View.GONE);
                        break;
                    case "Acer":
                        chipColor2.setText("Black");
                        chipColor3.setText("White");
                        chipColor3.setVisibility(View.VISIBLE);
                        chipColor5.setVisibility(View.GONE);
                        chipColor4.setVisibility(View.GONE);
                        chipConfig2.setText("Intel Core i7 + 16GB Ram + 1TB SSD");
                        chipConfig3.setVisibility(View.GONE);
                        chipConfig4.setVisibility(View.GONE);
                        break;
                    case "Dell":
                        chipColor2.setText("Black");
                        chipColor3.setVisibility(View.GONE);
                        chipColor5.setVisibility(View.GONE);
                        chipColor4.setVisibility(View.GONE);
                        chipConfig2.setText("Intel Core i7 + 16GB Ram + 512GB SSD");
                        chipConfig3.setText("Intel Core i7 + 16GB Ram + 1TB SSD");
                        chipConfig4.setText("Intel Core i7 + 32GB Ram + 1TB SDD");
                        break;
                    case "LG" :
                        chipColor2.setText("Black");
                        chipColor4.setText("Sliver");
                        chipColor5.setVisibility(View.VISIBLE);
                        chipColor4.setVisibility(View.VISIBLE);
                        break;
                    default:
                        chipColor2.setText("Black");
                        chipColor4.setVisibility(View.GONE);
                        chipColor5.setVisibility(View.GONE);
                        chipConfig2.setText("Intel Core i5 + 8GB Ram + 1TB HDD + 256GB SSD");
                        chipConfig3.setText("Intel Core i7 + 16GB Ram + 1TB HDD + 500GB SSD");
                        chipConfig4.setText("Intel Core i9 + 32GB Ram + 1TB SDD");
                        break;

                }


                break;
            case "TV's":
                price = product.getProductPrice();
                colorViewGroup.setVisibility(View.GONE);
                divider.setVisibility(View.GONE);
                configViewGroup.setVisibility(View.GONE);
                colorViewGroup.setVisibility(View.GONE);
                textView.setVisibility(View.GONE);
                divider1.setVisibility(View.GONE);
                colorSelected = "Default";
                configurationSelected = "Default";
                break;
            case "Headsets":
                price = product.getProductPrice();
                switch (product.getProductBrand())
                {
                    case "Apple":
                        switch (product.getProductName())
                        {
                            case "AirPods Pro":
                            chipColor2.setText("White");
                            chipColor3.setVisibility(View.GONE);
                            chipColor4.setVisibility(View.GONE);
                            chipColor5.setVisibility(View.GONE);
                            break;
                            case "AirPods Max":
                                chipColor2.setText("Gray");
                                chipColor5.setVisibility(View.VISIBLE);
                                chipColor4.setVisibility(View.VISIBLE);
                                chipColor4.setText("Pink");
                                chipColor5.setText("Green");
                                chipColor5.setCheckable(true);
                                chipColor4.setCheckable(true);
                                break;

                        }
                        configViewGroup.setVisibility(View.GONE);
                        configurationSelected = "Default";
                        textView.setVisibility(View.GONE);
                        divider1.setVisibility(View.GONE);
                        break;
                    case "Audio-Technica":
                        chipColor2.setText("Black");
                        chipColor4.setText("Purple");
                        chipColor5.setText("Red");
                        chipColor5.setVisibility(View.VISIBLE);
                        chipColor4.setVisibility(View.VISIBLE);
                        configViewGroup.setVisibility(View.GONE);
                        configurationSelected = "Default";
                        textView.setVisibility(View.GONE);
                        divider1.setVisibility(View.GONE);
                        chipColor5.setCheckable(true);
                        chipColor4.setCheckable(true);
                        break;
                    case "Bose" :
                        chipColor2.setText("Black");
                        chipColor3.setText("White");
                        chipColor4.setText("Gold");
                        chipColor5.setText("Blue");
                        chipColor5.setVisibility(View.VISIBLE);
                        chipColor4.setVisibility(View.VISIBLE);
                        configViewGroup.setVisibility(View.GONE);
                        configurationSelected = "Default";
                        textView.setVisibility(View.GONE);
                        divider1.setVisibility(View.GONE);
                        chipColor5.setCheckable(true);
                        chipColor4.setCheckable(true);
                        break;
                    case "Sony":
                        chipColor2.setText("Black");
                        chipColor3.setText("White");
                        chipColor5.setVisibility(View.GONE);
                        chipColor4.setVisibility(View.GONE);
                        configViewGroup.setVisibility(View.GONE);
                        configurationSelected = "Default";
                        textView.setVisibility(View.GONE);
                        divider1.setVisibility(View.GONE);

                        break;
                    case "Sennheiser":
                        break;
                    case "Beyerdynamic":
                        break;

                }
                break;
            case "Smartwatches":
                price = product.getProductPrice();
                switch (product.getProductBrand())
                {
                    case "Apple":

                        switch (product.getProductName())
                        {
                            case "Apple Watch Series 6":
                                chipColor2.setText("Black");
                                chipColor3.setText("White");
                                chipColor4.setText("Red");
                                chipColor5.setText("Blue");
                                chipColor5.setCheckable(true);
                                chipColor4.setCheckable(true);
                                break;
                        }

                        configViewGroup.setVisibility(View.GONE);
                        configurationSelected = "Default";
                        textView.setVisibility(View.GONE);
                        divider1.setVisibility(View.GONE);
                        break;
                    case "Samsung":
                        chipColor2.setText("Black");
                        chipColor3.setText("Blue");
                        chipColor4.setText("Bronze");
                        chipColor5.setVisibility(View.GONE);
                        chipColor4.setVisibility(View.VISIBLE);
                        configViewGroup.setVisibility(View.GONE);
                        configurationSelected = "Default";
                        textView.setVisibility(View.GONE);
                        divider1.setVisibility(View.GONE);
                        chipColor4.setCheckable(true);
                        break;
                    case "Garmin" :
                        chipColor2.setText("Black");
                        chipColor3.setText("White");
                        chipColor4.setText("Gold");
                        chipColor5.setText("Blue");
                        chipColor5.setVisibility(View.VISIBLE);
                        chipColor4.setVisibility(View.VISIBLE);
                        configViewGroup.setVisibility(View.GONE);
                        configurationSelected = "Default";
                        textView.setVisibility(View.GONE);
                        divider1.setVisibility(View.GONE);
                        chipColor5.setCheckable(true);
                        chipColor4.setCheckable(true);
                        break;
                    case "Fitbit":
                        chipColor2.setText("Black");
                        chipColor3.setText("White");
                        chipColor5.setVisibility(View.GONE);
                        chipColor4.setVisibility(View.GONE);
                        configViewGroup.setVisibility(View.GONE);
                        configurationSelected = "Default";
                        textView.setVisibility(View.GONE);
                        divider1.setVisibility(View.GONE);
                        chipColor5.setCheckable(true);
                        chipColor4.setCheckable(true);
                        break;
                    case "Fossil":
                        chipColor2.setText("Gray");
                        chipColor5.setVisibility(View.VISIBLE);
                        chipColor4.setVisibility(View.VISIBLE);
                        configViewGroup.setVisibility(View.GONE);
                        configurationSelected = "Default";
                        textView.setVisibility(View.GONE);
                        divider1.setVisibility(View.GONE);
                        break;

                }
                break;
            case "Cameras":
                price = product.getProductPrice();
                colorViewGroup.setVisibility(View.GONE);
                divider.setVisibility(View.GONE);
                colorSelected = "Default";
                configurationSelected = "Default";
                chipConfig3.setVisibility(View.GONE);
                chipConfig4.setVisibility(View.GONE);
                chipConfig2.setVisibility(View.GONE);
                configViewGroup.setVisibility(View.INVISIBLE);
                textView.setVisibility(View.GONE);
                divider1.setVisibility(View.GONE);
                break;

        }




        colorGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() { // get the selected color
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                switch (checkedId)
                {

                    case R.id.chipColorBlack:
                        viewPager.setCurrentItem(0);
                        colorSelected = chipColor2.getText().toString();
                        break;
                    case R.id.chipColorWhite:
                        viewPager.setCurrentItem(1);
                        colorSelected = chipColor3.getText().toString();
                        break;
                    case R.id.chipColorGold:
                        viewPager.setCurrentItem(2);
                        colorSelected = chipColor4.getText().toString();
                        break;
                    case R.id.chipColorBlue:
                        viewPager.setCurrentItem(3);
                        colorSelected = chipColor5.getText().toString();
                        break;
                    default:
                        viewPager.setCurrentItem(0);
                }
            }
        });

        storageGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() { // based on storage change the product price and get the selected config
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                switch (checkedId)
                {

                    case R.id.chipConfigration2:
                        configurationSelected = chipConfig2.getText().toString();
                        price = product.getProductPrice();
                        productPrice.setText("$"+product.getProductPrice());
                        break;
                    case R.id.chipConfigration3:
                        configurationSelected = chipConfig3.getText().toString();
                        price = product.getProductPrice() + 240;
                        productPrice.setText("$"+price);
                        break;
                    case R.id.chipConfigration4:
                        configurationSelected = chipConfig4.getText().toString();
                        price = product.getProductPrice() + 360;
                        productPrice.setText("$"+price);
                        break;
                    default:
                        price = product.getProductPrice();

                }
            }
        });

        buttonIncrease.setOnClickListener(new View.OnClickListener() { // when button increase is clicked
            @Override
            public void onClick(View v) {
                // set the text with value
                if(quantity < product.getQuantity())
                {
                    quantity++; // increase value
                }
                else
                {
                    Toast.makeText(IndividualProductActivity.this, "Your reached the maximum quantity for this item!", Toast.LENGTH_SHORT).show();
                }
                textViewQuantity.setText(String.valueOf(quantity)); // set the text with value

            }
        });

        buttonDecrease.setOnClickListener(new View.OnClickListener() { // when button decrease is clicked
            @Override
            public void onClick(View v) {
                if(quantity > 1) // check if quantity more than 1
                {
                    quantity--; // decrease value
                }
                textViewQuantity.setText(String.valueOf(quantity)); // set the text with value
            }
        });



        buttonBuy.setOnClickListener(new View.OnClickListener() { // when buy button is clicked
            @Override
            public void onClick(View v) {

                if(colorSelected.isEmpty() || configurationSelected.isEmpty()) // check for empty fields
                {
                    Toast.makeText(IndividualProductActivity.this, "Please Choose the Color and Configuration", Toast.LENGTH_SHORT).show(); // show error message
                    return;
                }
                if(product.getQuantity() == 0)
                {
                    Toast.makeText(IndividualProductActivity.this, "This Product is out of stock!", Toast.LENGTH_SHORT).show(); // show error message
                    return;
                }
                // else
                Intent intent = new Intent(IndividualProductActivity.this,AddressActivity.class); // create a intent object to send the user to the address activity
                carts.add(getProductObject()); // add cart object to array list
                intent.putExtra("products",carts); // send array it with intent
                intent.putExtra("total",price);
                intent.putExtra("isDirect",true); // send value it with intent
                startActivity(intent);


            }
        });

        AsyncTask.execute(new Runnable() { // get the reviews in the background
            @Override
            public void run() {
                getReviews();
            }
        });



        getRelatedProducts();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(getResources().getIdentifier(product.getProductName().replaceAll(" ","").toLowerCase(), "raw", getPackageName()) == 0) // if product model is not avaliable, hide the AR button
                {


                }
                else { // else show the button
                    if(findViewById(R.id.ar_view_item_menu) != null) {
                        new MaterialTapTargetPrompt.Builder(IndividualProductActivity.this)
                                .setPrimaryText("View Product In AR")
                                .setSecondaryText("You can view this product in AR")
                                .setFocalPadding(R.dimen.dp_40)
                                .setAnimationInterpolator(new FastOutSlowInInterpolator())
                                .setTarget(findViewById(R.id.ar_view_item_menu))
                                .setPrimaryTextColour(ContextCompat.getColor(IndividualProductActivity.this, R.color.textColorproduct))
                                .setSecondaryTextColour(ContextCompat.getColor(IndividualProductActivity.this, R.color.textColorproduct))
                                .setFocalColour(ContextCompat.getColor(IndividualProductActivity.this, R.color.fullblack))
                                .setIconDrawableColourFilter(ContextCompat.getColor(IndividualProductActivity.this, R.color.colorPrimary))
                                .setBackgroundColour(ContextCompat.getColor(IndividualProductActivity.this, R.color.colorPrimary))
                                .setIcon(R.drawable.ic_outline_view_in_ar_24).showFor(2500);
                    }
                }
            }
        },1000);



    }

    private Cart getProductObject() { // return cart object

        return new Cart(product,colorSelected,configurationSelected,price,quantity);

    }

    @Override
    public void onItemClick(RecyclerView.ViewHolder myViewHolder, int pos, ImageView imageView) { // on click listener for relate products

        Intent intent = new Intent(this, IndividualProductActivity.class); // create an intent object to the individual product
        intent.putExtra("items",productsArrayList.get(pos)); // get the product info
        startActivity(intent);
    }


    public class myViewPagerAdapter extends PagerAdapter { // create a viewpager  for the images

        private ArrayList<String> uriArrayList; // declare our variables

        public myViewPagerAdapter(ArrayList<String> uriArrayList) { // set our variables
            this.uriArrayList = uriArrayList;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            View view = (View)object; // destroy the view when it is not shown
            container.removeView(view);
            view = null;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            ImageView imageView = new ImageView(getApplicationContext()); // create an image view
            imageView.findViewById(R.id.productImageRecent); // initialize our image view inside the viewpager
            container.addView(imageView); // add the image view to the viewpager
            Glide.with(IndividualProductActivity.this).load(uriArrayList.get(position)).fitCenter().dontAnimate().diskCacheStrategy(DiskCacheStrategy.DATA).placeholder(R.drawable.ic_baseline_cached_24).listener(new RequestListener<Drawable>() { // use glide to load the uri lins
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    supportStartPostponedEnterTransition();
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                   supportStartPostponedEnterTransition();

                    return false;
                }
            }).into(imageView); // use glide to load the uri links into the image view
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(IndividualProductActivity.this, FullScreenProductActivity.class);
                    i.putExtra("position", position);
                    i.putExtra("images", uriArrayList);
                    startActivity(i);
                }
            });
            return imageView; // return the imageview
        }

        @Override
        public int getCount() {
            return uriArrayList.size(); // return the size of uri
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {

            return view==((ImageView)object); // view is an imageview object

        }
    }

    class OnImageClickListener implements View.OnClickListener {

        int mPosition;

        // constructor
        public OnImageClickListener(int position) {
            this.mPosition = position;
        }

        @Override
        public void onClick(View v) {
            // on selecting grid view image
            // launch full screen activity
            Intent i = new Intent(IndividualProductActivity.this, FullScreenProductActivity.class);
            i.putExtra("position", mPosition);
            i.putExtra("images", (Serializable) imagesUrls);
            startActivity(i);
        }

    }

    private void getReviews() // get product reviews
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Reviews");

        myRef.child(product.getProduct_id()).addValueEventListener(new ValueEventListener() { // get reviews associated with current product only
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                for(DataSnapshot reviews : snapshot.getChildren()) {
                    ratingArrayList.add(reviews.getValue(Rating.class));

                    if (reviews.child("ratting") != null) {

                        try {
                            rate = (double) reviews.child("ratting").getValue();         // get rating value in double -> add  it to total -> get avg - > set the values
                            total = total + rate;
                            i++;
                            avg = total / i;
                            textViewReviewsCount.setText("( "+i+" )");
                            ratingBar.setRating((float) avg);
                        } catch (Exception e) {
                            rate = (long) reviews.child("ratting").getValue();  // get rating value in double -> add  it to total -> get avg - > set the values
                            total = total + rate;
                            i++;
                            textViewReviewsCount.setText("( "+i+" )");
                            avg = total / i;
                            ratingBar.setRating((float) avg);
                        }
                    }
                }

                reviewsAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void invalidateOptionsMenu() {
        super.invalidateOptionsMenu();

    }

    private void getRelatedProducts() // method to get related products based on category
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Products");


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                productsArrayList.clear(); // clear the list so that you don't get duplicate products
                for(DataSnapshot products : dataSnapshot.getChildren())
                {
                    if(products.child("category").getValue().toString().toLowerCase().equals(product.getCategory().toLowerCase()) && !products.child("productName").getValue().toString().toLowerCase().equals(product.getProductName().toLowerCase())) { // check if product has same category as the product category and does not equal to the same product
                        productsArrayList.add(products.getValue(Products.class));

                    }

                }
                productAdapter.notifyDataSetChanged(); // notify the adapter with new data
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
    public void supportInvalidateOptionsMenu() {
        super.supportInvalidateOptionsMenu();
    }

    @Override
    protected void onPause() {
        super.onPause();
        new UserSession(this).setCartValue(mCartItemCount);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new ConnectionDetector(this); // on Resume ( this method is fired before on create )  activity, check if user has connection or not
        mCartItemCount = new UserSession(this).getCartValue();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_indivdual_product,menu);
        final MenuItem menuItem = menu.findItem(R.id.ar_view_item_menu);    // get action menu item

        if(getResources().getIdentifier(product.getProductName().replaceAll(" ","").toLowerCase(), "raw", getPackageName()) == 0) // if product model is not avaliable, hide the AR button
        {
            menu.findItem(R.id.ar_view_item_menu).setVisible(false);
            Log.i(TAG,"Not found");
        }
        else { // else show the button
            menu.findItem(R.id.ar_view_item_menu).setVisible(true);

        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.ar_view_item_menu)
        {
            Intent intent = new Intent(IndividualProductActivity.this, ARActivity.class);  // create an intent to go to the AR activity
            intent.putExtra("product_name", product.getProductName()); // send the product name with intent
            startActivity(intent);
            return true;
        }
        return false;
    }
}