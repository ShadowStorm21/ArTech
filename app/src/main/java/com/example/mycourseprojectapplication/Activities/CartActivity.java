package com.example.mycourseprojectapplication.Activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.mycourseprojectapplication.Adapters.CartAdapter;
import com.example.mycourseprojectapplication.Broadcasts.MyRestarter;
import com.example.mycourseprojectapplication.Models.Cart;
import com.example.mycourseprojectapplication.R;
import com.example.mycourseprojectapplication.Utilities.ConnectionDetector;
import com.example.mycourseprojectapplication.Utilities.UserSession;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {
    private CartAdapter cartAdapter;
    private ArrayList<Cart> productsArrayList;
    private FirebaseAuth mAuth;
    public static int mCartItemCount = 0;
    private TextView textViewSubTotal,textViewItemCount,textViewEmptyCart,textViewSubTitle;       // declare our variables
    private Button buttonCheckout;
    private LottieAnimationView lottieAnimationView;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        new ConnectionDetector(this);          // check if user has Internet connection


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarCart);
        setSupportActionBar(toolbar);
        setTitle("My Cart");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // setup the action toolbar with activity title and back icon functionality
        Intent intent = getIntent();
        boolean intentValidator = intent.getBooleanExtra("started_from_notification",false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   // when back button is clicked finish the activity
               if(intentValidator)
                {
                    Intent intent1 = new Intent(CartActivity.this,MainActivity.class);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent1);
                }
                finish();
            }
        });





        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBarCart);
        textViewItemCount = findViewById(R.id.textViewCartItemNumber);
        textViewSubTotal = findViewById(R.id.textViewSubTotal);
        buttonCheckout = findViewById(R.id.buttonCheckout);
        lottieAnimationView = findViewById(R.id.list_empty);
        textViewEmptyCart = findViewById(R.id.textViewEmptyCart);
        textViewSubTitle = findViewById(R.id.textViewEmptyCartSubTitle);
        recyclerView = findViewById(R.id.cartRecycler);
        productsArrayList = new ArrayList<>();
        cartAdapter = new CartAdapter(productsArrayList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);      // initialize view and setup the recycler view
        recyclerView.setAdapter(cartAdapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.scheduleLayoutAnimation();


        getCartItems(); // get cart items

        buttonCheckout.setOnClickListener(new View.OnClickListener() { // when checkout button is clicked
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(CartActivity.this,AddressActivity.class); // create an intent to go to the address activity
                intent.putExtra("products",productsArrayList); // send the product array list
                intent.putExtra("total",getTotal()); // send total
                startActivity(intent);
            }
        });


    }



    private void getCartItems() // method to get cart item related to the current user
    {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Cart");
        myRef.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() { // only items associated with current user
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productsArrayList.clear(); // clear arraylist each time new cart item is added to not get duplicate items
                for(DataSnapshot cartItems : snapshot.getChildren())
                {
                    productsArrayList.add(cartItems.getValue(Cart.class)); // add items to the array list
                }
                if(productsArrayList.size() == 0) // if array list is empty
                {
                    textViewSubTotal.setText("$0.0");
                    textViewItemCount.setText("0 Item");
                    textViewItemCount.setVisibility(View.GONE);
                    textViewSubTotal.setVisibility(View.GONE);             // hide some views and show lottie animation
                    lottieAnimationView.setVisibility(View.VISIBLE);
                    textViewEmptyCart.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    textViewSubTitle.setVisibility(View.VISIBLE);
                    buttonCheckout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    return;
                } // if not
                if(productsArrayList.size() == 1)
                {
                    textViewItemCount.setVisibility(View.VISIBLE);
                    textViewSubTotal.setVisibility(View.VISIBLE);
                    lottieAnimationView.setVisibility(View.GONE);
                    textViewEmptyCart.setVisibility(View.GONE);          // show views and hide lottie animation
                    textViewSubTitle.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    buttonCheckout.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                    cartAdapter.notifyDataSetChanged(); // notify adapter
                    textViewItemCount.setText(mCartItemCount+" Item");  // set values
                    DecimalFormat decimalFormat = new DecimalFormat("0.#####");
                    String price = decimalFormat.format(getTotal());
                    textViewSubTotal.setText("$"+price);
                }
                else {
                    textViewItemCount.setVisibility(View.VISIBLE);
                    textViewSubTotal.setVisibility(View.VISIBLE);
                    lottieAnimationView.setVisibility(View.GONE);
                    textViewEmptyCart.setVisibility(View.GONE);          // show views and hide lottie animation
                    textViewSubTitle.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    buttonCheckout.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                    cartAdapter.notifyDataSetChanged(); // notify adapter
                    textViewItemCount.setText(mCartItemCount + " Items");  // set values
                    DecimalFormat decimalFormat = new DecimalFormat("0.#####");
                    String price = decimalFormat.format(getTotal());
                    textViewSubTotal.setText("$" + price);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }



    private double getTotal() // method to get the total amount of products
    {
        double total = 0.0;
       for(int i = 0 ; i < productsArrayList.size(); i++)
       {
           total += productsArrayList.get(i).getTotalPrice() * productsArrayList.get(i).getQuantity(); // P * Q
       }
       return total;
    }


    @Override
    protected void onResume() {
        super.onResume();
        new ConnectionDetector(this); // on Resume ( this method is fired before on create )  activity, check if user has connection or not
        mCartItemCount = new UserSession(this).getCartValue(); // on resume get cart value from user session
        textViewItemCount.setText(mCartItemCount+" Items"); // set cart value
        cancelBackgroundNotification();
        recyclerView.scheduleLayoutAnimation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        new UserSession(this).setCartValue(mCartItemCount); // on pause save cart value in user session

    }


    @Override
    protected void onDestroy() {
        generateBackgroundNotification();
        super.onDestroy();
    }
    private void generateBackgroundNotification()
    {
        Intent notificationIntent = new Intent(this, MyRestarter.class);
        int alarmId = 1; /* Dynamically assign alarm ids for multiple alarms */
        notificationIntent.putExtra("alarmId", alarmId); /* So we can catch the id on BroadcastReceiver */
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        long futureInMillis = SystemClock.elapsedRealtime() + 180000 ;        // 3600000 Hour  // 7200000 2 Hours // 1800000 30 min
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }
    private void cancelBackgroundNotification()
    {
        int alarmId = 0; /* Dynamically assign alarm ids for multiple alarms */
        Intent intent = new Intent(this, MyRestarter.class); /* your Intent localIntent = new Intent("com.test.sample");*/
        intent.putExtra("alarmId", alarmId); /* So we can catch the id on BroadcastReceiver */
        PendingIntent alarmIntent;
        alarmIntent = PendingIntent.getBroadcast(this,
                alarmId, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        long futureInMillis = SystemClock.elapsedRealtime()   ; // 2min        // 3600000 Hour  // 7200000 2 Hours // 1800000 30 min
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, alarmIntent);
    }



    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_slide_bottom);
        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();

    }

}