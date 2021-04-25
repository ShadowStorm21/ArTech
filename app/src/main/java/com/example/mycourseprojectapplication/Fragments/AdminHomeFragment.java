package com.example.mycourseprojectapplication.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.interpolator.view.animation.FastOutLinearInInterpolator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import com.example.mycourseprojectapplication.Adapters.AdminOrdersAdapter;
import com.example.mycourseprojectapplication.Models.Orders;
import com.example.mycourseprojectapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.transition.MaterialFade;
import com.google.android.material.transition.MaterialFadeThrough;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;


public class AdminHomeFragment extends Fragment {

    private ArrayList<Orders> ordersArrayList;
    private AdminOrdersAdapter adminOrdersAdapter;
    private TextView textViewEmpty;
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private ConstraintLayout constraintLayoutOrders;
    public AdminHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_admin_home, container, false);
        ordersArrayList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.orders_recycler);
        textViewEmpty = view.findViewById(R.id.textViewAdminEmptyOrders);
        floatingActionButton = view.findViewById(R.id.floatingActionButtonAdminSortOrders);
        constraintLayoutOrders = view.findViewById(R.id.container_admin_orders);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adminOrdersAdapter = new AdminOrdersAdapter(ordersArrayList);
        recyclerView.setAdapter(adminOrdersAdapter);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdminOrdersSortFragment adminOrdersSortFragment = AdminOrdersSortFragment.newInstance();
                adminOrdersSortFragment.setTargetFragment(AdminHomeFragment.this, 1);
                adminOrdersSortFragment.show(getFragmentManager(),AdminOrdersSortFragment.TAG);
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_DRAGGING) {

                    MaterialFadeThrough materialFadeThrough = new MaterialFadeThrough();
                    materialFadeThrough.setDuration(84L);
                    materialFadeThrough.addTarget(floatingActionButton);
                    materialFadeThrough.setInterpolator(new LinearInterpolator());
                    TransitionManager.beginDelayedTransition(constraintLayoutOrders, materialFadeThrough);
                    floatingActionButton.setVisibility(View.VISIBLE);
                }
                else if(!recyclerView.canScrollVertically(-1) && newState==RecyclerView.SCROLL_STATE_DRAGGING)
                {
                    MaterialFadeThrough materialFadeThrough = new MaterialFadeThrough();
                    materialFadeThrough.setDuration(84L);
                    materialFadeThrough.addTarget(floatingActionButton);
                    materialFadeThrough.setInterpolator(new LinearInterpolator());
                    TransitionManager.beginDelayedTransition(constraintLayoutOrders, materialFadeThrough);
                    floatingActionButton.setVisibility(View.VISIBLE);
                }
                else if(newState == RecyclerView.SCROLL_STATE_DRAGGING)
                {
                    MaterialFade materialFadeThrough1 = new MaterialFade();
                    materialFadeThrough1.setDuration(150L);
                    materialFadeThrough1.setInterpolator(new FastOutLinearInInterpolator());
                    materialFadeThrough1.addTarget(floatingActionButton);
                    TransitionManager.beginDelayedTransition(constraintLayoutOrders, materialFadeThrough1);
                    floatingActionButton.setVisibility(View.GONE);
                }
                else if(newState == RecyclerView.SCROLL_STATE_IDLE)
                {
                    MaterialFadeThrough materialFadeThrough = new MaterialFadeThrough();
                    materialFadeThrough.setDuration(84L);
                    materialFadeThrough.addTarget(floatingActionButton);
                    materialFadeThrough.setInterpolator(new LinearInterpolator());
                    TransitionManager.beginDelayedTransition(constraintLayoutOrders, materialFadeThrough);
                    floatingActionButton.setVisibility(View.VISIBLE);
                }
                else if(newState == RecyclerView.SCROLL_STATE_SETTLING)
                {
                    MaterialFade materialFadeThrough1 = new MaterialFade();
                    materialFadeThrough1.setDuration(150L);
                    materialFadeThrough1.setInterpolator(new FastOutLinearInInterpolator());
                    materialFadeThrough1.addTarget(floatingActionButton);
                    TransitionManager.beginDelayedTransition(constraintLayoutOrders, materialFadeThrough1);
                    floatingActionButton.setVisibility(View.GONE);
                }
            }
        });

        floatingActionButton.setImageResource(R.drawable.ic_outline_filter_list_24);
        getAllOrders("Default");

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == 1) {
            String order_status = data.getStringExtra("order_status");
            String filter_type = data.getStringExtra("order_filter");

            if(filter_type.equals("Default"))
            {
                getAllOrders("Default");
                floatingActionButton.setImageResource(R.drawable.ic_outline_filter_list_24);
            }
            else
            {
                switch (filter_type)
                {
                    case "Ascending":
                        getAllOrders("Ascending");
                        floatingActionButton.setImageResource(R.drawable.ic_outline_filter_alt_24);
                        break;
                    case "Descending":
                        getAllOrders("Descending");
                        floatingActionButton.setImageResource(R.drawable.ic_outline_filter_alt_24);
                        break;

                }
            }
            if (order_status.equals("Default")) {
                getAllOrders("Default");
                floatingActionButton.setImageResource(R.drawable.ic_outline_filter_list_24);
            } else {
                switch (order_status) {
                    case "Ordered":
                        getFilteredOrders("Ordered",filter_type);
                        break;
                    case "In Delivery":
                        getFilteredOrders("In Delivery",filter_type);
                        break;
                    case "Delivered":
                        getFilteredOrders("Delivered",filter_type);
                        break;
                    case "Processing":
                        getFilteredOrders("Processing",filter_type);
                        break;

                }

            }

        }
    }

    public static Intent newIntent(String order_status,String filter) {
        Intent intent = new Intent();
        intent.putExtra("order_status", order_status);
        intent.putExtra("order_filter", filter);
        return intent;
    }


   public void getFilteredOrders(String filter,String sortType) {
       try
       {
           FirebaseDatabase database = FirebaseDatabase.getInstance();
           DatabaseReference myRef = database.getReference("Orders");
           myRef.addValueEventListener(new ValueEventListener() { // orders associated with user only

               @Override
               public void onDataChange(@NonNull DataSnapshot snapshot) {
                   ordersArrayList.clear(); // for each new order clear the list first each time so that you don't get duplicate orders
                   for (DataSnapshot orderKeys : snapshot.getChildren())
                   {
                       for (DataSnapshot orders : orderKeys.getChildren())
                       {
                           if(orders.child("order_status").getValue().toString().equals(filter))
                           {
                               ordersArrayList.add(orders.getValue(Orders.class));
                           }
                       }
                   }
                   if (ordersArrayList.size() == 0) {
                       textViewEmpty.setVisibility(View.VISIBLE);
                       recyclerView.setVisibility(View.GONE);
                       floatingActionButton.setImageResource(R.drawable.ic_outline_filter_alt_24);
                   } else {
                       if(sortType.equals("Ascending"))
                       {
                           Collections.sort(ordersArrayList,DATE_COMPARATOR_ASC);
                       }
                       else
                       {
                           Collections.sort(ordersArrayList,DATE_COMPARATOR_DESC);
                       }
                       textViewEmpty.setVisibility(View.GONE);
                       recyclerView.setVisibility(View.VISIBLE);
                       runLayoutAnimation(recyclerView);
                       floatingActionButton.setImageResource(R.drawable.ic_outline_filter_alt_24);
                       adminOrdersAdapter.notifyDataSetChanged();
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
    private void getAllOrders(String sortType)
    {
        try
        {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Orders");
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ordersArrayList.clear();
                    for (DataSnapshot orderKeys : snapshot.getChildren()) {
                        for (DataSnapshot orders : orderKeys.getChildren()) {
                            ordersArrayList.add(orders.getValue(Orders.class));
                        }
                    }
                    if (ordersArrayList.size() == 0) {
                        textViewEmpty.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        if(sortType.equals("Ascending"))
                        {
                            Collections.sort(ordersArrayList,DATE_COMPARATOR_ASC);
                        }
                        else
                        {
                            Collections.sort(ordersArrayList,DATE_COMPARATOR_DESC);
                        }
                        textViewEmpty.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        runLayoutAnimation(recyclerView);
                        adminOrdersAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
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


    private static final Comparator<Orders> DATE_COMPARATOR_DESC = new Comparator<Orders>() {
        @Override
        public int compare(Orders o1, Orders o2) {
            Date date1 = new Date();
            date1.setTime(o1.getOrder_date());
            Date date2 = new Date();
            date2.setTime(o2.getOrder_date());
            return date1.compareTo(date2);
        }
    }.reversed();

    private static final Comparator<Orders> DATE_COMPARATOR_ASC = new Comparator<Orders>() {
        @Override
        public int compare(Orders o1, Orders o2) {
            Date date1 = new Date();
            date1.setTime(o1.getOrder_date());
            Date date2 = new Date();
            date2.setTime(o2.getOrder_date());
            return date1.compareTo(date2);
        }
    };

    public class CustomScrollListener extends RecyclerView.OnScrollListener {
        public CustomScrollListener() {
        }

        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            switch (newState) {
                case RecyclerView.SCROLL_STATE_IDLE:
                    if (!recyclerView.canScrollVertically(1)) {
                        MaterialFadeThrough materialFadeThrough = new MaterialFadeThrough();
                        materialFadeThrough.setDuration(84L);
                        materialFadeThrough.addTarget(floatingActionButton);
                        materialFadeThrough.setInterpolator(new LinearInterpolator());
                        TransitionManager.beginDelayedTransition(constraintLayoutOrders, materialFadeThrough);
                        floatingActionButton.setVisibility(View.VISIBLE);
                    }
                    break;
                case RecyclerView.SCROLL_STATE_DRAGGING:
                    if (!recyclerView.canScrollVertically(1)) {
                        MaterialFade materialFadeThrough1 = new MaterialFade();
                        materialFadeThrough1.setDuration(150L);
                        materialFadeThrough1.setInterpolator(new FastOutLinearInInterpolator());
                        materialFadeThrough1.addTarget(floatingActionButton);
                        TransitionManager.beginDelayedTransition(constraintLayoutOrders, materialFadeThrough1);
                        floatingActionButton.setVisibility(View.GONE);
                    }
                    break;

            }


        }
    }

}