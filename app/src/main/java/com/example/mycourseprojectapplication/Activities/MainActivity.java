package com.example.mycourseprojectapplication.Activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.mycourseprojectapplication.Broadcasts.MyRestarter;
import com.example.mycourseprojectapplication.Fragments.OrdersFragment;
import com.example.mycourseprojectapplication.Models.Users;
import com.example.mycourseprojectapplication.R;
import com.example.mycourseprojectapplication.Utilities.ConnectionDetector;
import com.example.mycourseprojectapplication.Utilities.UserSession;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetSequence;

public class MainActivity extends AppCompatActivity  {

    private final String TAG = this.getClass().getSimpleName();        // this is used for debugging
    private AppBarConfiguration mAppBarConfiguration;
    private MaterialSearchView searchView;
    private TextView textCartItemCount;        // declare our variables
    private int mCartItemCount = 0;
    private FirebaseAuth mAuth;
    public TextView textViewNotificationCounter,textViewOrdersCounter;
    private int numOfNotifications = 0, numOfOrders = 0;
    private Users user;
    private ImageView profilePic;
    int checkedItem = -1;
    private String[] suggestions = {"Apple", "Samsung", "Huawei", "Google","OnePlus","LG",
            "Sony","TCL","Audio-Technica","Bose","Sennheiser","Beyerdynamic",
            "Garmin","Fitbit","Fossil","Canon", "Nikon","Pentax","Olympus","Fujifilm",
            "Smartphones","Laptops","TV's","Headsets","Smartwatches","Cameras",
            "TV","Pro","Ultra","Phone","watch","cam","Dell","MSI","Acer",
            "Asus","airpods","apple watch","airpods pro","headphone","iphone","iphone 12","iphone 11","Galaxy S20",
            "iphone XS","Galaxy S21"
    };
    private ConstraintLayout constraintLayoutMain;
    private CardView cardViewCart;
    private UserSession userSession;
    private int counter = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getIntent().hasExtra("FromNotification")) {
            // User has selected the notification, so we show the Dialog activity
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish(); // finish this activity
            return;
        }
        mAuth = FirebaseAuth.getInstance();
        new ConnectionDetector(this);  // check if user has Internet connection
        userSession = new UserSession(this);

        constraintLayoutMain = findViewById(R.id.mainContainer);

        Toolbar toolbar = findViewById(R.id.toolbar);         // setup the action toolbar
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);    // initialize the side drawer and navigation view
        View headerView =  navigationView.getHeaderView(0); // get the top header
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);


        toggle.setDrawerIndicatorEnabled(false);

        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        toggle.setHomeAsUpIndicator(R.drawable.active_marker);
        TextView nav_user = (TextView)headerView.findViewById(R.id.textViewHeaderEmail); // initialize the account textView
        profilePic = headerView.findViewById(R.id.imageViewProfilePicsMain);
        if(mAuth.getCurrentUser() != null) // check if the current user is not null ( signed in)
        {
            nav_user.setText(mAuth.getCurrentUser().getEmail()); // set the text with the user email
        }


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_orders, R.id.nav_profile,R.id.nav_notification,R.id.customer_support,R.id.help,R.id.settingsFragment,R.id.logout,R.id.scan_qr)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);               // setup the appbar and navigation controller
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);




        Menu menu = navigationView.getMenu();


        MenuItem menuItem1 = menu.findItem(R.id.logout);
        menuItem1.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                new MaterialAlertDialogBuilder(MainActivity.this)                           // create an alert before logging out
                        .setTitle("Alert")
                        .setMessage("Are you sure you want to logout from the Application?")
                        .setCancelable(true)
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                mAuth.signOut(); // sign out the user from firebase authentication
                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                new UserSession(MainActivity.this).setIsLogin(false);
                                startActivity(intent);
                                finish();
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();
                        new UserSession(MainActivity.this).setIsLogin(true);


                    }
                }).show();

                return true;
            }
        });

        MenuItem menuItem2 = menu.findItem(R.id.settingsFragment);
        menuItem2.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
                startActivity(intent);
                return true;
            }
        });

        MenuItem menuItem3 = menu.findItem(R.id.scan_qr);
        menuItem3.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(MainActivity.this,ScanQRActivity.class);
                startActivity(intent);
                return true;
            }
        });


        searchView = (MaterialSearchView) findViewById(R.id.search_view);      // initialize search view
        searchView.setVoiceSearch(true); //or false
        searchView.setSuggestions(suggestions);      // set the search suggestions
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {    // on search submitted go to search activity with search query
                Intent intent = new Intent(MainActivity.this,SearchableActivity.class);
                intent.putExtra("query",query);
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        textViewNotificationCounter = (TextView) navigationView.getMenu().findItem(R.id.nav_notification).getActionView();
        textViewOrdersCounter = (TextView) navigationView.getMenu().findItem(R.id.nav_orders).getActionView();
        getNotifications();
        getOrders();
        getUserData();
        searchView.showVoice(true);
        searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String sugg = parent.getItemAtPosition(position).toString();
                searchView.setQuery(sugg,true);
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() { ;
                constraintLayoutMain.animate().scaleX(0).scaleY(0).setDuration(200).setInterpolator(new AnticipateInterpolator());

            }

            @Override
            public void onSearchViewClosed() {
                constraintLayoutMain.animate().scaleX(1).scaleY(1).setDuration(400).setInterpolator(new OvershootInterpolator());
                constraintLayoutMain.setVisibility(View.VISIBLE);


            }
        });

        if(userSession.getNotificationKey() == null)
        {
            return;
        }
        else {
            updateNotificationKey(userSession.getNotificationKey());
        }



            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (userSession.isFirstTimeLogin()) {

                        final Toolbar tb = findViewById(R.id.toolbar);
                        MaterialTapTargetSequence tapTargetSequence = new MaterialTapTargetSequence()
                                .addPrompt(new MaterialTapTargetPrompt.Builder(MainActivity.this)
                                        .setTarget(findViewById(R.id.action_cart))
                                        .setAnimationInterpolator(new FastOutSlowInInterpolator())
                                        .setPrimaryText("This is your cart")
                                        .setPrimaryTextColour(ContextCompat.getColor(MainActivity.this, R.color.textColorproduct))
                                        .setSecondaryText("You can store multiple products here!")
                                        .setSecondaryTextColour(ContextCompat.getColor(MainActivity.this, R.color.textColorproduct))
                                        .setIcon(R.drawable.ic_outline_shopping_cart_24)
                                        .setFocalColour(ContextCompat.getColor(MainActivity.this, R.color.fullblack))
                                        .setIconDrawableColourFilter(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary))
                                        .setBackgroundColour(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary))
                                        .setAutoDismiss(false)
                                        .setAutoFinish(false)
                                        .setCaptureTouchEventOutsidePrompt(true)
                                        .create(), 1500)
                                .addPrompt(new MaterialTapTargetPrompt.Builder(MainActivity.this)
                                        .setTarget(findViewById(R.id.app_bar_search))
                                        .setAnimationInterpolator(new FastOutSlowInInterpolator())
                                        .setPrimaryText("Search")
                                        .setPrimaryTextColour(ContextCompat.getColor(MainActivity.this, R.color.textColorproduct))
                                        .setSecondaryText("You can search for multiple products here!")
                                        .setSecondaryTextColour(ContextCompat.getColor(MainActivity.this, R.color.textColorproduct))
                                        .setIcon(R.drawable.ic_search_black_24dp)
                                        .setFocalColour(ContextCompat.getColor(MainActivity.this, R.color.fullblack))
                                        .setIconDrawableColourFilter(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary))
                                        .setBackgroundColour(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary)).create(), 1500)
                                .addPrompt(new MaterialTapTargetPrompt.Builder(MainActivity.this)
                                        .setTarget(tb.getChildAt(1))
                                        .setPrimaryText("Menu")
                                        .setSecondaryText("You can view your different tabs here like your orders and profile")
                                        .setContentDescription("menu_prompt_content_description")
                                        .setFocalPadding(R.dimen.dp_40)
                                        .setAnimationInterpolator(new FastOutSlowInInterpolator())
                                        .setIcon(R.drawable.ic_baseline_menu_24)
                                        .setPrimaryTextColour(ContextCompat.getColor(MainActivity.this, R.color.textColorproduct))
                                        .setSecondaryTextColour(ContextCompat.getColor(MainActivity.this, R.color.textColorproduct))
                                        .setFocalColour(ContextCompat.getColor(MainActivity.this, R.color.fullblack))
                                        .setIconDrawableColourFilter(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary))
                                        .setBackgroundColour(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary)).create(), 1500)
                                .setSequenceCompleteListener(new MaterialTapTargetSequence.SequenceCompleteListener() {
                                    @Override
                                    public void onSequenceComplete() {

                                        Toast.makeText(MainActivity.this, "All set, You are ready to go!", Toast.LENGTH_SHORT).show();
                                        userSession.setIsFirstTimeLogin(false);
                                    }
                                });

                        tapTargetSequence.show();
                    }
                }
            },1000);




    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.app_bar_search);       // get search menu item
        searchView.setMenuItem(item); // set the search view with menu item
        final MenuItem menuItem = menu.findItem(R.id.action_cart);    // get action menu item
        View actionView =  menuItem.getActionView();  // get the action from cart menu item
        textCartItemCount = (TextView) actionView.findViewById(R.id.cart_badge);  // initialize the counter for the cart
        setupBadge();       // setup the cart badge
        ImageView imageView = actionView.findViewById(R.id.imageView); // initialize cart image
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);           // on click fire on OptionsItemSelected method

            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);  // on click fire on OptionsItemSelected method
            }
        });





        return true;
    }

    private void setupBadge() {

        if (textCartItemCount != null) {         // check if counter text not null ( initialized)
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
        mCartItemCount = new UserSession(this).getCartValue(); // on Resume get cart counter value from session
        invalidateOptionsMenu(); // refresh the menu
        new ConnectionDetector(this);  // on Resume ( this method is fired before on create )  activity, check if user has connection or not
        cancelBackgroundNotification();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);    // initialize the side drawer and navigation view
        View headerView =  navigationView.getHeaderView(0); // get the top header
        TextView nav_user = (TextView)headerView.findViewById(R.id.textViewHeaderEmail); // initialize the account textView
        profilePic = headerView.findViewById(R.id.imageViewProfilePicsMain);
        if(mAuth.getCurrentUser() != null) // check if the current user is not null ( signed in)
        {
            nav_user.setText(mAuth.getCurrentUser().getEmail()); // set the text with the user email
        }
        userSession = new UserSession(this);

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (userSession.isFirstTimeLogin()) {

                            final Toolbar tb = findViewById(R.id.toolbar);
                            MaterialTapTargetSequence tapTargetSequence = new MaterialTapTargetSequence()
                                    .addPrompt(new MaterialTapTargetPrompt.Builder(MainActivity.this)
                                            .setTarget(findViewById(R.id.action_cart))
                                            .setAnimationInterpolator(new FastOutSlowInInterpolator())
                                            .setPrimaryText("This is your cart")
                                            .setPrimaryTextColour(ContextCompat.getColor(MainActivity.this, R.color.textColorproduct))
                                            .setSecondaryText("You can store multiple products here!")
                                            .setSecondaryTextColour(ContextCompat.getColor(MainActivity.this, R.color.textColorproduct))
                                            .setIcon(R.drawable.ic_outline_shopping_cart_24)
                                            .setFocalColour(ContextCompat.getColor(MainActivity.this, R.color.fullblack))
                                            .setIconDrawableColourFilter(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary))
                                            .setBackgroundColour(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary))
                                            .setAutoDismiss(false)
                                            .setAutoFinish(false)
                                            .setCaptureTouchEventOutsidePrompt(true)
                                            .create(), 1500)
                                    .addPrompt(new MaterialTapTargetPrompt.Builder(MainActivity.this)
                                            .setTarget(findViewById(R.id.app_bar_search))
                                            .setAnimationInterpolator(new FastOutSlowInInterpolator())
                                            .setPrimaryText("Search")
                                            .setPrimaryTextColour(ContextCompat.getColor(MainActivity.this, R.color.textColorproduct))
                                            .setSecondaryText("You can search for multiple products here!")
                                            .setSecondaryTextColour(ContextCompat.getColor(MainActivity.this, R.color.textColorproduct))
                                            .setIcon(R.drawable.ic_search_black_24dp)
                                            .setFocalColour(ContextCompat.getColor(MainActivity.this, R.color.fullblack))
                                            .setIconDrawableColourFilter(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary))
                                            .setBackgroundColour(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary)).create(), 1500)
                                    .addPrompt(new MaterialTapTargetPrompt.Builder(MainActivity.this)
                                            .setTarget(tb.getChildAt(1))
                                            .setPrimaryText("Menu")
                                            .setSecondaryText("You can view your different tabs here like your orders and profile")
                                            .setContentDescription("menu_prompt_content_description")
                                            .setFocalPadding(R.dimen.dp_40)
                                            .setAnimationInterpolator(new FastOutSlowInInterpolator())
                                            .setIcon(R.drawable.ic_baseline_menu_24)
                                            .setPrimaryTextColour(ContextCompat.getColor(MainActivity.this, R.color.textColorproduct))
                                            .setSecondaryTextColour(ContextCompat.getColor(MainActivity.this, R.color.textColorproduct))
                                            .setFocalColour(ContextCompat.getColor(MainActivity.this, R.color.fullblack))
                                            .setIconDrawableColourFilter(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary))
                                            .setBackgroundColour(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary)).create(), 1500)
                                    .setSequenceCompleteListener(new MaterialTapTargetSequence.SequenceCompleteListener() {
                                        @Override
                                        public void onSequenceComplete() {

                                            Toast.makeText(MainActivity.this, "All set, You are ready to go!", Toast.LENGTH_SHORT).show();
                                            userSession.setIsFirstTimeLogin(false);
                                        }
                                    });

                            tapTargetSequence.show();
                        }
                    }
                }, 500);

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });


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
        long futureInMillis = SystemClock.elapsedRealtime()  ; // 2min        // 3600000 Hour  // 7200000 2 Hours // 1800000 30 min
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, alarmIntent);
    }

    @Override
    public boolean onSupportNavigateUp() {         // setup the navigation menu
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();


    }





    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.action_cart:   // on cart icon click go to cart activity

                Intent intent = new Intent(MainActivity.this,CartActivity.class);
                startActivity(intent);
            case R.id.app_bar_search:

        }
        return false;
    }


    @Override
    public void onBackPressed() { // on back pressed show an alert before exiting the application


            if (searchView.isSearchOpen()) {
                searchView.closeSearch();
            } else
                {
                    new MaterialAlertDialogBuilder(this)
                            .setTitle("Alert")
                            .setMessage("Are you sure you want to close the application")
                            .setCancelable(true)
                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mAuth.signOut();
                                    userSession.setIsLogin(false);
                                    finish();
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            dialogInterface.dismiss();

                        }
                    }).show();
            }


    }
    public void passDataToOrdersFragment(String data)
    {
        Bundle bundle = new Bundle();
        bundle.putString("data",data);
        OrdersFragment ordersFragment = new OrdersFragment();
        ordersFragment.setArguments(bundle);
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
        long futureInMillis = SystemClock.elapsedRealtime() + 5000 ; // 2min        // 3600000 Hour  // 7200000 2 Hours // 1800000 30 min
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    private void getNotifications()
    {
        try {

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Notifications");
            myRef.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    numOfNotifications = 0;
                    for (DataSnapshot notification : snapshot.getChildren()) {

                        numOfNotifications++;

                    }
                    if (numOfNotifications > 0) {
                        textViewNotificationCounter.setText(String.valueOf(numOfNotifications));
                    } else {
                        textViewNotificationCounter.setText("0");
                    }
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

    private void getOrders()
    {
        try {

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Orders");
            myRef.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    numOfOrders = 0;
                    for (DataSnapshot orders : snapshot.getChildren()) {

                        numOfOrders++;

                    }
                    if (numOfOrders > 0) {
                        textViewOrdersCounter.setText(String.valueOf(numOfOrders));
                    } else {
                        textViewOrdersCounter.setText("0");
                    }
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
    private void getUserData() {
        try {

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Users");
            myRef.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    user = snapshot.getValue(Users.class);
                    if (user != null) {
                        if (!user.getUserPhotoUrl().equals("Default")) {
                            Glide.with(MainActivity.this).load(user.getUserPhotoUrl()).centerCrop().diskCacheStrategy(DiskCacheStrategy.DATA).into(profilePic);
                        } else {
                            profilePic.setImageResource(R.drawable.ic_outline_account_circle_24);
                        }
                    }
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
    private void updateNotificationKey(String key)
    {
        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Users");
            myRef.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String oldKey = snapshot.child("notification_token").getValue().toString();
                    Map<String, Object> values = new HashMap();
                    for (DataSnapshot user : snapshot.getChildren()) {
                        values.put(user.getKey(),user.getValue());
                    }

                    if(oldKey.equals(key))
                    {
                        return;
                    }
                    else {
                        values.put("notification_token", key);
                        myRef.child(mAuth.getCurrentUser().getUid()).updateChildren(values).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                Log.d(TAG, "Notification Key Updated Successfully!");

                            }
                        });
                    }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    searchView.setQuery(searchWrd, false);
                }
            }

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}