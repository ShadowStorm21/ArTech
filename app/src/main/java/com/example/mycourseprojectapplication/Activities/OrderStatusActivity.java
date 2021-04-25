package com.example.mycourseprojectapplication.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.mycourseprojectapplication.Adapters.TimeLinesAdapter;
import com.example.mycourseprojectapplication.Models.OrderStatus;
import com.example.mycourseprojectapplication.Models.Orders;
import com.example.mycourseprojectapplication.Models.TimeLineModel;
import com.example.mycourseprojectapplication.Models.TimelineStatus;
import com.example.mycourseprojectapplication.Models.Tracking;
import com.example.mycourseprojectapplication.R;
import com.example.mycourseprojectapplication.Utilities.ConnectionDetector;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

public class OrderStatusActivity extends AppCompatActivity {
    private final String TAG = OrderStatusActivity.this.getClass().getSimpleName();
    private String[] order_status = {"Ordered","In Delivery", "Delivered"}; // declare our variables
    private Button buttonTrack;
    private TextView textViewTitle,textViewDesc;
    private TimeLinesAdapter mAdapter;
    private List<TimeLineModel> mDataList = new ArrayList<TimeLineModel>();
    private LinearLayoutManager mLayoutManager;
    private RecyclerView recyclerView;
    private ArrayList<OrderStatus> stringArrayList = new ArrayList<>();
    private Set<OrderStatus> strings = new HashSet<>(stringArrayList);
    private String status = "";
    private String tracking_id = "";
    private ArrayList<Tracking> trackingArrayList;
    private TextView textViewStatus;
    private LottieAnimationView lottieAnimationView;
    private TimelineStatus timelineStatus = TimelineStatus.INACTIVE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a_r_steps);

        new ConnectionDetector(this); // check if user has internet connection or not
        recyclerView = findViewById(R.id.recyclerViewTimeLine);
        Toolbar toolbar = findViewById(R.id.toolbarARSteps);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // setup the action toolbar with activity title and back icon functionality
        setTitle("Tracking History");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() { // when back is clicked just finish the activity
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        buttonTrack = findViewById(R.id.buttonOrderStatusTrack);
        trackingArrayList = new ArrayList<>();
        //buttonTrack.setVisibility(View.GONE);

        Tracking tracking = (Tracking) getIntent().getSerializableExtra("tracking");
        mDataList.clear();
        if(tracking != null)
        {
            tracking_id = tracking.getTracking_key();
            Map<String,TimeLineModel> timeLineModelMap = tracking.getTimeLineModelMap();
            for(Map.Entry<String,TimeLineModel> entry : timeLineModelMap.entrySet())
            {
                mDataList.add(entry.getValue());
            }
            Collections.sort(mDataList,PRIORITY_COMPARATOR);
            initRecyclerView();
            runLayoutAnimation(recyclerView);
        }
        Intent intent = getIntent();
        Orders order = (Orders) intent.getSerializableExtra("order");

        textViewStatus = findViewById(R.id.textViewTrackingState);
        TextView textViewId = findViewById(R.id.textViewTrackingOrderID);
        TextView textViewDate = findViewById(R.id.textViewTrackingOrderDate);
        lottieAnimationView = findViewById(R.id.OrderAnimationView);
        if(order != null && tracking != null)
        {

            textViewId.setText("ORDER#"+order.getOrder_id().substring(0,13));
            String date = new SimpleDateFormat("dd-MM-yyy hh:mm aa").format(order.getOrder_date());
            textViewDate.setText("Purchase Date:"+date);
                if(tracking.getTimeLineModelMap() != null)
                {
                    for(Map.Entry<String,TimeLineModel> entry : tracking.getTimeLineModelMap().entrySet())
                    {
                        if (entry.getKey().equals(OrderStatus.ORDERED.name().toLowerCase()))
                        {
                            timelineStatus = entry.getValue().getStatus();
                            if(entry.getValue().getStatus().equals(TimelineStatus.ACTIVE))
                            {
                                stringArrayList.add(entry.getValue().getOrder_status());
                            }


                        }
                        if (entry.getKey().equals(OrderStatus.PREPARED.name().toLowerCase()))
                        {
                            timelineStatus = entry.getValue().getStatus();
                            if(entry.getValue().getStatus().equals(TimelineStatus.ACTIVE))
                            {
                                stringArrayList.add(entry.getValue().getOrder_status());
                            }

                        }

                        if (entry.getKey().equals(OrderStatus.RECEIVED_FROM_MANUFACTURE.name().toLowerCase()))
                        {
                            timelineStatus = entry.getValue().getStatus();
                            if(entry.getValue().getStatus().equals(TimelineStatus.ACTIVE))
                            {
                                stringArrayList.add(entry.getValue().getOrder_status());
                            }

                        }
                       if (entry.getKey().equals(OrderStatus.PREPARED_FOR_SHIPPING.name().toLowerCase()))
                        {
                            timelineStatus = entry.getValue().getStatus();
                            if(entry.getValue().getStatus().equals(TimelineStatus.ACTIVE))
                            {
                                stringArrayList.add(entry.getValue().getOrder_status());
                            }

                        }
                        if (entry.getKey().equals(OrderStatus.SHIPPED.name().toLowerCase()))
                        {
                            timelineStatus = entry.getValue().getStatus();
                            if(entry.getValue().getStatus().equals(TimelineStatus.ACTIVE))
                            {
                                stringArrayList.add(entry.getValue().getOrder_status());
                            }

                        }
                        if (entry.getKey().equals(OrderStatus.IN_TRANSIT.name().toLowerCase()))
                        {
                            timelineStatus = entry.getValue().getStatus();
                            if(entry.getValue().getStatus().equals(TimelineStatus.ACTIVE))
                            {
                                stringArrayList.add(entry.getValue().getOrder_status());
                            }

                        }
                        if (entry.getKey().equals(OrderStatus.REACHED_HQ.name().toLowerCase()))
                        {
                            timelineStatus = entry.getValue().getStatus();
                            if(entry.getValue().getStatus().equals(TimelineStatus.ACTIVE))
                            {
                                stringArrayList.add(entry.getValue().getOrder_status());
                            }

                        }
                        if (entry.getKey().equals(OrderStatus.SCHEDULED_FOR_DELIVERY.name().toLowerCase()))
                        {
                            timelineStatus = entry.getValue().getStatus();
                            if(entry.getValue().getStatus().equals(TimelineStatus.ACTIVE))
                            {
                                stringArrayList.add(entry.getValue().getOrder_status());
                            }

                        }
                        if (entry.getKey().equals(OrderStatus.OUT_FOR_DELIVERY.name().toLowerCase()))
                        {
                            timelineStatus = entry.getValue().getStatus();
                            if(entry.getValue().getStatus().equals(TimelineStatus.ACTIVE))
                            {
                                stringArrayList.add(entry.getValue().getOrder_status());
                            }

                        }
                        if (entry.getKey().equals(OrderStatus.DELIVERED.name().toLowerCase()))
                        {
                            timelineStatus = entry.getValue().getStatus();
                            if(entry.getValue().getStatus().equals(TimelineStatus.ACTIVE))
                            {
                                stringArrayList.add(entry.getValue().getOrder_status());
                            }

                        }
                        if (entry.getKey().equals(OrderStatus.DELIVERED.name().toLowerCase()))
                        {
                            timelineStatus = entry.getValue().getStatus();
                            if(entry.getValue().getStatus().equals(TimelineStatus.COMPLETED))
                            {
                                stringArrayList.add(entry.getValue().getOrder_status());
                            }

                        }
                    }
                    strings.addAll(stringArrayList);
                    for(OrderStatus orderStatus : strings) {

                        status = orderStatus.name();
                    }

                }

            if(status != null) {
                if (status.equals(OrderStatus.ORDERED.name()))
                {
                    textViewStatus.setText("Your order has been placed successfully.");
                    lottieAnimationView.setAnimation("order-placed.json");
                    lottieAnimationView.loop(false);
                }
                if (status.equals(OrderStatus.PREPARED.name()))
                {
                    textViewStatus.setText("Your item is been prepared by the manufacturer.");
                    lottieAnimationView.setAnimation("prepared.json");
                    lottieAnimationView.loop(true);
                }
                if (status.equals(OrderStatus.RECEIVED_FROM_MANUFACTURE.name()))
                {
                    textViewStatus.setText("Item received from the manufacturer.");
                   lottieAnimationView.setAnimation("receive-order.json");
                    lottieAnimationView.loop(true);
                }
                if (status.equals(OrderStatus.PREPARED_FOR_SHIPPING.name()))
                {
                    textViewStatus.setText("Your item is been prepared for shipping.");
                    lottieAnimationView.setAnimation("packed-shipping.json");
                    lottieAnimationView.loop(false);
                }
                if (status.equals(OrderStatus.SHIPPED.name()))
                {
                    textViewStatus.setText("Your item has been shipped.");
                    lottieAnimationView.setAnimation("shipped.json");
                    lottieAnimationView.loop(true);
                }
                if (status.equals(OrderStatus.IN_TRANSIT.name()))
                {
                    textViewStatus.setText("Your item is currently in transit.");
                    lottieAnimationView.setAnimation("plane_admin.json");
                    lottieAnimationView.loop(true);
                }
                if (status.equals(OrderStatus.REACHED_HQ.name()))
                {
                    textViewStatus.setText("Your item has been received successfully in our HQ.");
                    lottieAnimationView.setAnimation("received.json");
                    lottieAnimationView.loop(true);
                }
                if (status.equals(OrderStatus.SCHEDULED_FOR_DELIVERY.name()))
                {
                    textViewStatus.setText("Your item is scheduled for delivery.");
                    lottieAnimationView.setAnimation("schedule_delivery.json");
                    lottieAnimationView.loop(true);
                }
                if (status.equals(OrderStatus.OUT_FOR_DELIVERY.name()))
                {
                    textViewStatus.setText("Your item is is out for delivery, Click on Track button for Real-Time tracking.");
                    lottieAnimationView.setAnimation("out-delivery.json");
                    lottieAnimationView.loop(true);
                    buttonTrack.setVisibility(View.VISIBLE);

                }
                if (status.equals(OrderStatus.DELIVERED.name()))
                {
                    textViewStatus.setText("Your item has been delivered to you successfully.");
                    lottieAnimationView.setAnimation("delivered.json");
                    lottieAnimationView.loop(true);

                }

            }

        }

        buttonTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderStatusActivity.this,MapActivity.class);
                intent.putExtra("tracking_key",getIntent().getSerializableExtra("tracking_keys"));
                intent.putExtra("tracking",getIntent().getSerializableExtra("tracking"));
                intent.putExtra("product_id",getIntent().getStringExtra("product_id"));
                Log.i("Adapter",getIntent().getStringExtra("product_id"));
                startActivity(intent);
            }
        });



    }

    private static final Comparator<TimeLineModel> PRIORITY_COMPARATOR = new Comparator<TimeLineModel>() {
        @Override
        public int compare(TimeLineModel o1, TimeLineModel o2) {
            if(o1.getPriority() > o2.getPriority())
            {
                return 1;
            }
            else if(o1.getPriority() < o2.getPriority())
            {
                return -1;
            }
            else
            {
                return 0;
            }
        }
    };


    private void initRecyclerView() {
        mLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
                recyclerView.setLayoutManager(mLayoutManager);
                mAdapter = new TimeLinesAdapter(mDataList);
                recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new ConnectionDetector(this); // on Resume ( this method is fired before on create )  activity, check if user has connection or not
        lottieAnimationView = findViewById(R.id.OrderAnimationView);
        getUserTrackingInfo();
    }

    private void getUserTrackingInfo()
    {

        MaterialTapTargetPrompt tapTargetPrompt =  new MaterialTapTargetPrompt.Builder(OrderStatusActivity.this)
                .setPrimaryText("Track your item in Real-Time")
                .setSecondaryText("You can track your item in Real-Time using Google Maps.")
                .setFocalPadding(R.dimen.dp_40)
                .setAnimationInterpolator(new FastOutSlowInInterpolator())
                .setTarget(buttonTrack)
                .setPrimaryTextColour(ContextCompat.getColor(OrderStatusActivity.this, R.color.textColorproduct))
                .setSecondaryTextColour(ContextCompat.getColor(OrderStatusActivity.this, R.color.textColorproduct))
                .setFocalColour(ContextCompat.getColor(OrderStatusActivity.this, R.color.fullblack))
                .setIconDrawableColourFilter(ContextCompat.getColor(OrderStatusActivity.this, R.color.colorPrimary))
                .setBackgroundColour(ContextCompat.getColor(OrderStatusActivity.this, R.color.colorPrimary)).create();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Tracking");
        if(tracking_id != null) {
            myRef.child(tracking_id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    trackingArrayList.clear();
                    mDataList.clear();
                    stringArrayList.clear();
                    strings.clear();
                    trackingArrayList.add(snapshot.getValue(Tracking.class));

                    if (trackingArrayList.size() != 0) {
                        Map<String, TimeLineModel> timeLineModelMap = trackingArrayList.get(0).getTimeLineModelMap();
                        for (Map.Entry<String, TimeLineModel> entry : timeLineModelMap.entrySet()) {
                            mDataList.add(entry.getValue());
                        }
                        Collections.sort(mDataList, PRIORITY_COMPARATOR);
                        initRecyclerView();
                        runLayoutAnimation(recyclerView);
                    }


                    if(trackingArrayList.get(0).getTimeLineModelMap() != null)
                    {
                        for(Map.Entry<String,TimeLineModel> entry : trackingArrayList.get(0).getTimeLineModelMap().entrySet())
                        {
                            if (entry.getKey().equals(OrderStatus.ORDERED.name().toLowerCase()))
                            {
                                timelineStatus = entry.getValue().getStatus();
                                if(entry.getValue().getStatus().equals(TimelineStatus.ACTIVE))
                                {
                                    stringArrayList.add(entry.getValue().getOrder_status());
                                }


                            }
                            else if (entry.getKey().equals(OrderStatus.PREPARED.name().toLowerCase()))
                            {
                                timelineStatus = entry.getValue().getStatus();
                                if(entry.getValue().getStatus().equals(TimelineStatus.ACTIVE))
                                {
                                    stringArrayList.add(entry.getValue().getOrder_status());
                                }

                            }

                            else if(entry.getKey().equals(OrderStatus.RECEIVED_FROM_MANUFACTURE.name().toLowerCase()))
                            {
                                timelineStatus = entry.getValue().getStatus();
                                if(entry.getValue().getStatus().equals(TimelineStatus.ACTIVE))
                                {
                                    stringArrayList.add(entry.getValue().getOrder_status());
                                }

                            }
                            else if(entry.getKey().equals(OrderStatus.PREPARED_FOR_SHIPPING.name().toLowerCase()))
                            {
                                timelineStatus = entry.getValue().getStatus();
                                if(entry.getValue().getStatus().equals(TimelineStatus.ACTIVE))
                                {
                                    stringArrayList.add(entry.getValue().getOrder_status());
                                }

                            }
                            else if (entry.getKey().equals(OrderStatus.SHIPPED.name().toLowerCase()))
                            {
                                timelineStatus = entry.getValue().getStatus();
                                if(entry.getValue().getStatus().equals(TimelineStatus.ACTIVE))
                                {
                                    stringArrayList.add(entry.getValue().getOrder_status());
                                }

                            }
                            else if (entry.getKey().equals(OrderStatus.IN_TRANSIT.name().toLowerCase()))
                            {
                                timelineStatus = entry.getValue().getStatus();
                                if(entry.getValue().getStatus().equals(TimelineStatus.ACTIVE))
                                {
                                    stringArrayList.add(entry.getValue().getOrder_status());
                                }

                            }
                            else if (entry.getKey().equals(OrderStatus.REACHED_HQ.name().toLowerCase()))
                            {
                                timelineStatus = entry.getValue().getStatus();
                                if(entry.getValue().getStatus().equals(TimelineStatus.ACTIVE))
                                {
                                    stringArrayList.add(entry.getValue().getOrder_status());
                                }

                            }
                            else if (entry.getKey().equals(OrderStatus.SCHEDULED_FOR_DELIVERY.name().toLowerCase()))
                            {
                                timelineStatus = entry.getValue().getStatus();
                                if(entry.getValue().getStatus().equals(TimelineStatus.ACTIVE))
                                {
                                    stringArrayList.add(entry.getValue().getOrder_status());
                                }

                            }
                            else if (entry.getKey().equals(OrderStatus.OUT_FOR_DELIVERY.name().toLowerCase()))
                            {
                                timelineStatus = entry.getValue().getStatus();
                                if(entry.getValue().getStatus().equals(TimelineStatus.ACTIVE))
                                {
                                    stringArrayList.add(entry.getValue().getOrder_status());
                                }

                            }
                            else if (entry.getKey().equals(OrderStatus.DELIVERED.name().toLowerCase()))
                            {
                                timelineStatus = entry.getValue().getStatus();
                                if(entry.getValue().getStatus().equals(TimelineStatus.ACTIVE))
                                {
                                    stringArrayList.add(entry.getValue().getOrder_status());
                                }

                            }
                            else if (entry.getKey().equals(OrderStatus.DELIVERED.name().toLowerCase()))
                            {
                                timelineStatus = entry.getValue().getStatus();
                                if(entry.getValue().getStatus().equals(TimelineStatus.COMPLETED))
                                {
                                    stringArrayList.add(entry.getValue().getOrder_status());
                                }

                            }
                        }
                        strings.addAll(stringArrayList);
                        for(OrderStatus orderStatus : strings) {

                            status = orderStatus.name();
                        }

                    }

                    if(status != null) {
                        buttonTrack.setVisibility(View.GONE);

                        if (status.equals(OrderStatus.ORDERED.name()))
                        {
                            textViewStatus.setText("Your order has been placed successfully.");
                            lottieAnimationView.setAnimation("order-placed.json");
                            lottieAnimationView.loop(false);
                            lottieAnimationView.playAnimation();
                            assert tapTargetPrompt != null;
                            tapTargetPrompt.dismiss();
                        }
                        else if (status.equals(OrderStatus.PREPARED.name()))
                        {
                            textViewStatus.setText("Your item is been prepared by the manufacturer.");
                            lottieAnimationView.setAnimation("prepared.json");
                            lottieAnimationView.loop(true);
                            lottieAnimationView.playAnimation();
                            assert tapTargetPrompt != null;
                            tapTargetPrompt.dismiss();
                        }
                        else if (status.equals(OrderStatus.RECEIVED_FROM_MANUFACTURE.name()))
                        {
                            textViewStatus.setText("Item received from the manufacturer.");
                            lottieAnimationView.setAnimation("receive-order.json");
                            lottieAnimationView.loop(true);
                            lottieAnimationView.playAnimation();
                            assert tapTargetPrompt != null;
                            tapTargetPrompt.dismiss();
                        }
                        else if(status.equals(OrderStatus.PREPARED_FOR_SHIPPING.name()))
                        {
                            textViewStatus.setText("Your item is been prepared for shipping.");
                            lottieAnimationView.setAnimation("packed-shipping.json");
                            lottieAnimationView.loop(false);
                            lottieAnimationView.playAnimation();
                            assert tapTargetPrompt != null;
                            tapTargetPrompt.dismiss();
                        }
                        else if(status.equals(OrderStatus.SHIPPED.name()))
                        {
                            textViewStatus.setText("Your item has been shipped.");
                            lottieAnimationView.setAnimation("shipped.json");
                            lottieAnimationView.loop(true);
                            lottieAnimationView.playAnimation();
                            assert tapTargetPrompt != null;
                            tapTargetPrompt.dismiss();
                        }
                        else if (status.equals(OrderStatus.IN_TRANSIT.name()))
                        {
                            textViewStatus.setText("Your item is currently in transit.");
                            lottieAnimationView.setAnimation("plane_admin.json");
                            lottieAnimationView.loop(true);
                            lottieAnimationView.playAnimation();
                            assert tapTargetPrompt != null;
                            tapTargetPrompt.dismiss();
                        }
                        else if (status.equals(OrderStatus.REACHED_HQ.name()))
                        {
                            textViewStatus.setText("Your item has been received successfully in our HQ.");
                            lottieAnimationView.setAnimation("received.json");
                            lottieAnimationView.loop(true);
                            lottieAnimationView.playAnimation();
                            assert tapTargetPrompt != null;
                            tapTargetPrompt.dismiss();
                        }
                        else if(status.equals(OrderStatus.SCHEDULED_FOR_DELIVERY.name()))
                        {
                            textViewStatus.setText("Your item is scheduled for delivery.");
                            lottieAnimationView.setAnimation("schedule_delivery.json");
                            lottieAnimationView.loop(true);
                            lottieAnimationView.playAnimation();
                            assert tapTargetPrompt != null;
                            tapTargetPrompt.dismiss();
                        }
                        else if (status.equals(OrderStatus.OUT_FOR_DELIVERY.name()))
                        {
                            textViewStatus.setText("Your item is is out for delivery, Click on Track button for Real-Time tracking.");
                            lottieAnimationView.setAnimation("out-delivery.json");
                            lottieAnimationView.loop(true);
                            buttonTrack.setVisibility(View.VISIBLE);
                            lottieAnimationView.playAnimation();
                            assert tapTargetPrompt != null;
                            tapTargetPrompt.show();

                        }
                        else if(status.equals(OrderStatus.DELIVERED.name()))
                        {
                            textViewStatus.setText("Your item has been delivered to you successfully.");
                            lottieAnimationView.setAnimation("delivered.json");
                            lottieAnimationView.loop(true);
                            lottieAnimationView.playAnimation();
                            assert tapTargetPrompt != null;
                            tapTargetPrompt.dismiss();
                        }
                        else
                        {
                            if(timelineStatus.equals(TimelineStatus.INACTIVE) || timelineStatus.equals(TimelineStatus.COMPLETED))
                            textViewStatus.setText("Processing Order...");
                            lottieAnimationView.setAnimation("prepared.json");
                            lottieAnimationView.loop(true);
                            lottieAnimationView.playAnimation();
                            assert tapTargetPrompt != null;
                            tapTargetPrompt.dismiss();
                        }

                        Log.d(TAG,"String array list : "+stringArrayList.toString());
                        Log.d(TAG,"String set : "+strings.toString());



                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

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