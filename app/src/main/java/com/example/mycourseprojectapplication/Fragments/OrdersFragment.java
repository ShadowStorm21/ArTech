package com.example.mycourseprojectapplication.Fragments;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.interpolator.view.animation.FastOutLinearInInterpolator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import com.airbnb.lottie.LottieAnimationView;
import com.example.mycourseprojectapplication.Adapters.OrdersAdapter;
import com.example.mycourseprojectapplication.Broadcasts.MyRestarter;
import com.example.mycourseprojectapplication.Models.Orders;
import com.example.mycourseprojectapplication.R;
import com.example.mycourseprojectapplication.Utilities.ConnectionDetector;
import com.example.mycourseprojectapplication.Utilities.UserSession;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.transition.MaterialFade;
import com.google.android.material.transition.MaterialFadeThrough;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class OrdersFragment extends Fragment {

    private final String TAG = OrdersFragment.class.getSimpleName();
    private FirebaseAuth mAuth;
    private ArrayList<Orders> ordersArrayList;
    private OrdersAdapter ordersAdapter;
    private TextView emptyTextView;                            // declare our variables
    private LottieAnimationView lottieAnimationView;
    private RecyclerView recyclerView;
    private ConstraintLayout constraintLayout;
    private UserSession userSession;
    private FloatingActionButton floatingActionButtonSort;
    private ProgressBar progressBar;
    private Handler mHandler = new Handler();
    private LinearLayout linearLayoutItemStatus;
    public static OrdersFragment newInstance() {
        OrdersFragment fragment = new OrdersFragment();
        return fragment;
    }




    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_orders, container, false);
        new ConnectionDetector(getContext()); // check if user has internet connection or not
        userSession = new UserSession(getContext());
        progressBar = root.findViewById(R.id.progressBarOrders);
        progressBar.setVisibility(View.VISIBLE);
        floatingActionButtonSort = root.findViewById(R.id.floatingActionButtonSort);
        mAuth = FirebaseAuth.getInstance();
        ordersArrayList = new ArrayList<>();
        ordersAdapter = new OrdersAdapter(ordersArrayList, getContext());
        recyclerView = root.findViewById(R.id.ordersRecyclerView);                // initialize views and adapters and objects
        emptyTextView = root.findViewById(R.id.textViewEmptyOrders);
        lottieAnimationView = root.findViewById(R.id.list_empty);
        constraintLayout = root.findViewById(R.id.orders_layout);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());        // setup the orders recycler view
        recyclerView.setAdapter(ordersAdapter);
        recyclerView.setLayoutManager(layoutManager);
        // call method to get users orders

        try {

            floatingActionButtonSort.setImageResource(R.drawable.ic_outline_filter_list_24);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        floatingActionButtonSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                OrdersSortFragment sortFragment = OrdersSortFragment.newInstance();
                sortFragment.setTargetFragment(OrdersFragment.this, 1);
                sortFragment.show(getFragmentManager(), OrdersSortFragment.TAG);

            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_DRAGGING) {

                    MaterialFadeThrough materialFadeThrough = new MaterialFadeThrough();
                    materialFadeThrough.setDuration(84L);
                    materialFadeThrough.addTarget(floatingActionButtonSort);
                    materialFadeThrough.setInterpolator(new LinearInterpolator());
                    TransitionManager.beginDelayedTransition(constraintLayout, materialFadeThrough);
                    floatingActionButtonSort.setVisibility(View.VISIBLE);
                }
                else if(!recyclerView.canScrollVertically(-1) && newState==RecyclerView.SCROLL_STATE_DRAGGING)
                {
                    MaterialFadeThrough materialFadeThrough = new MaterialFadeThrough();
                    materialFadeThrough.setDuration(84L);
                    materialFadeThrough.addTarget(floatingActionButtonSort);
                    materialFadeThrough.setInterpolator(new LinearInterpolator());
                    TransitionManager.beginDelayedTransition(constraintLayout, materialFadeThrough);
                    floatingActionButtonSort.setVisibility(View.VISIBLE);
                }
                else if(newState == RecyclerView.SCROLL_STATE_DRAGGING)
                {
                    MaterialFade materialFadeThrough1 = new MaterialFade();
                    materialFadeThrough1.setDuration(150L);
                    materialFadeThrough1.setInterpolator(new FastOutLinearInInterpolator());
                    materialFadeThrough1.addTarget(floatingActionButtonSort);
                    TransitionManager.beginDelayedTransition(constraintLayout, materialFadeThrough1);
                    floatingActionButtonSort.setVisibility(View.GONE);
                }
                else if(newState == RecyclerView.SCROLL_STATE_IDLE)
                {
                    MaterialFadeThrough materialFadeThrough = new MaterialFadeThrough();
                    materialFadeThrough.setDuration(84L);
                    materialFadeThrough.addTarget(floatingActionButtonSort);
                    materialFadeThrough.setInterpolator(new LinearInterpolator());
                    TransitionManager.beginDelayedTransition(constraintLayout, materialFadeThrough);
                    floatingActionButtonSort.setVisibility(View.VISIBLE);
                }
                else if(newState == RecyclerView.SCROLL_STATE_SETTLING)
                {
                    MaterialFade materialFadeThrough1 = new MaterialFade();
                    materialFadeThrough1.setDuration(150L);
                    materialFadeThrough1.setInterpolator(new FastOutLinearInInterpolator());
                    materialFadeThrough1.addTarget(floatingActionButtonSort);
                    TransitionManager.beginDelayedTransition(constraintLayout, materialFadeThrough1);
                    floatingActionButtonSort.setVisibility(View.GONE);
                }
            }
        });

        try {

            mHandler.postDelayed(runnable, 1000);
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        return root;
    }
    private Runnable runnable = new Runnable() {    // create a runnable object to  retrieve user's info in background

        public void run() {

            progressBar.setVisibility(View.VISIBLE);
            if(userSession.getFilterSwitchState())
            {
                floatingActionButtonSort.setImageResource(R.drawable.ic_outline_filter_alt_24);
            }
            else
            {
                floatingActionButtonSort.setImageResource(R.drawable.ic_outline_filter_list_24);
            }
            if (userSession.getFilterType() == null) {
                getUserOrders();
            } else {

                if (userSession.getFilterType().equals("Default")) {
                    getUserOrders();

                } else {

                    switch (userSession.getFilterType()) {
                        case "Ordered":
                            getFilteredOrders("Ordered");
                            break;
                        case "In Delivery":
                            getFilteredOrders("In Delivery");
                            break;
                        case "Delivered":
                            getFilteredOrders("Delivered");
                            break;
                        case "Processing":
                            getFilteredOrders("Processing");
                            break;
                    }

                }

            }

        }
    };


    public void getUserOrders() // method to the current user orders
    {
        try {
            progressBar.setVisibility(View.VISIBLE);
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Orders");
            myRef.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() { // orders associated with user only

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ordersArrayList.clear(); // for each new order clear the list first each time so that you don't get duplicate orders
                    for (DataSnapshot orders : snapshot.getChildren()) {
                        ordersArrayList.add(orders.getValue(Orders.class));     // add order to arraylist
                    }
                    Collections.sort(ordersArrayList, DATE_COMPARATOR);
                    floatingActionButtonSort.setImageResource(R.drawable.ic_outline_filter_list_24);
                    setViews();
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

    private void setViews() {
        if (ordersArrayList.size() == 0)    // if arraylist size is zero after adding
        {

            emptyTextView.setText("You don't have any orders yet!");
            lottieAnimationView.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);              // hide recycler view and show lottie animation and corresponding text and change background color to white
            progressBar.setVisibility(View.INVISIBLE);
            return;
        } else // if not empty
        {

            lottieAnimationView.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);        //show recycler view and  hide lottie animation and corresponding text and change background color to grayish
            progressBar.setVisibility(View.INVISIBLE);
            runLayoutAnimation(recyclerView);
            ordersAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == 1) {
            String order_status = data.getStringExtra("order_status");

            if (order_status.equals("Default")) {

                getUserOrders();
            } else {
                switch (order_status) {
                    case "Ordered":
                        getFilteredOrders("Ordered");
                        break;
                    case "In Delivery":
                        getFilteredOrders("In Delivery");
                        break;
                    case "Delivered":
                        getFilteredOrders("Delivered");
                        break;
                    case "Processing":
                        getFilteredOrders("Processing");
                        break;

                }

            }

        }
    }

    public static Intent newIntent(String order_status) {
        Intent intent = new Intent();
        intent.putExtra("order_status", order_status);
        return intent;
    }

    @Override
    public void onResume() {
        super.onResume();
        new ConnectionDetector(getContext()); // on Resume ( this method is fired before on create )  activity, check if user has connection or not
        cancelBackgroundNotification();
    }


    public void getFilteredOrders(String filter) {
        try {

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Orders");
            myRef.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() { // orders associated with user only

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ordersArrayList.clear(); // for each new order clear the list first each time so that you don't get duplicate orders
                    for (DataSnapshot orders : snapshot.getChildren()) {
                        if (orders.child("order_status").getValue().toString().equals(filter)) {
                            ordersArrayList.add(orders.getValue(Orders.class));     // add order to arraylist
                        }
                    }
                    Collections.sort(ordersArrayList, DATE_COMPARATOR);
                    floatingActionButtonSort.setImageResource(R.drawable.ic_outline_filter_alt_24);
                    setViews();


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
    private void cancelBackgroundNotification()
    {
        int alarmId = 0; /* Dynamically assign alarm ids for multiple alarms */
        Intent intent = new Intent(getContext(), MyRestarter.class); /* your Intent localIntent = new Intent("com.test.sample");*/
        intent.putExtra("alarmId", alarmId); /* So we can catch the id on BroadcastReceiver */
        PendingIntent alarmIntent;
        alarmIntent = PendingIntent.getBroadcast(getContext(),
                alarmId, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        long futureInMillis = SystemClock.elapsedRealtime()  ; // 2min        // 3600000 Hour  // 7200000 2 Hours // 1800000 30 min
        AlarmManager alarmManager = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, alarmIntent);
    }


    private static final Comparator<Orders> DATE_COMPARATOR = new Comparator<Orders>() {
        @Override
        public int compare(Orders o1, Orders o2) {
            Date date1 = new Date();
            date1.setTime(o1.getOrder_date());
            Date date2 = new Date();
            date2.setTime(o2.getOrder_date());
           return date1.compareTo(date2);
        }
    }.reversed();

    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_slide_bottom);
        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();

    }


}