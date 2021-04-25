package com.example.mycourseprojectapplication.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycourseprojectapplication.Adapters.RequestAdapter;
import com.example.mycourseprojectapplication.Models.Requests;
import com.example.mycourseprojectapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class AdminCustomerSupportFragment extends Fragment {

    private RecyclerView recyclerViewRequests;
    private RequestAdapter requestAdapter;
    private ArrayList<Requests> requestsArrayList;
    private TextView textViewEmptyRequests;
    public AdminCustomerSupportFragment() {
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
        View view =  inflater.inflate(R.layout.fragment_admin_customer_support, container, false);
        recyclerViewRequests = view.findViewById(R.id.customerServiceAdmin);
        textViewEmptyRequests = view.findViewById(R.id.textViewEmptyRequests);
        requestsArrayList = new ArrayList<>();
        requestAdapter = new RequestAdapter(requestsArrayList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerViewRequests.setLayoutManager(layoutManager);
        recyclerViewRequests.setAdapter(requestAdapter);
        getRequests();
        return view;
    }

    private void getRequests()
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Requests");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                requestsArrayList.clear();
                for(DataSnapshot requests : snapshot.getChildren())
                {
                    if(!Boolean.getBoolean(requests.child("solved").getValue().toString()))
                    {
                       requestsArrayList.add(requests.getValue(Requests.class));
                    }
                }
                if(requestsArrayList.size() == 0)
                {
                    textViewEmptyRequests.setVisibility(View.VISIBLE);
                    recyclerViewRequests.setVisibility(View.GONE);
                }
                else
                {
                    textViewEmptyRequests.setVisibility(View.GONE);
                    recyclerViewRequests.setVisibility(View.VISIBLE);
                    runLayoutAnimation(recyclerViewRequests);
                    requestAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_slide_bottom);
        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();

    }

    @Override
    public void onResume() {
        super.onResume();
        requestsArrayList.clear();
        getRequests();
    }
}