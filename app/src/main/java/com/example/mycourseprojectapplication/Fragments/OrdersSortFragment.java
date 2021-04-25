package com.example.mycourseprojectapplication.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.mycourseprojectapplication.R;
import com.example.mycourseprojectapplication.Utilities.UserSession;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;


public class OrdersSortFragment extends BottomSheetDialogFragment {


    public final static String TAG = OrdersSortFragment.class.getSimpleName();
    private String order_status_sort = "";
    private String filterType = "";
    private UserSession userSession;
    private boolean isCheckedSwitch = false;


    public OrdersSortFragment() {
        // Required empty public constructor
    }

    public static OrdersSortFragment newInstance() {
        OrdersSortFragment fragment = new OrdersSortFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userSession = new UserSession(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_orders_sort, container, false);
        RadioGroup radioGroupSortByStatus = view.findViewById(R.id.radioGroupStatus);

        if(userSession.getFilterType() == null)
        {
                radioGroupSortByStatus.clearCheck();
        }
        else
        {
                for(int i = 0; i < radioGroupSortByStatus.getChildCount(); i++)
                {
                    RadioButton radioButton = (RadioButton) radioGroupSortByStatus.getChildAt(i);
                    if(radioButton.getText().toString().equals(userSession.getFilterType()))
                    {
                        radioButton.setChecked(true);
                    }
                    else
                    {
                        radioButton.setChecked(false);
                    }
                }
        }
        radioGroupSortByStatus.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId)
                {
                    case R.id.radioButtonOrdered:
                        order_status_sort = "Ordered";
                    break;

                    case R.id.radioButtonIndelivery:
                        order_status_sort = "In Delivery";
                        break;

                    case R.id.radioButtonSortDelivered:
                        order_status_sort = "Delivered";
                        break;
                    case R.id.radioButtonProcessing:
                        order_status_sort = "Processing";
                        break;

                }

            }
        });


        Button button = view.findViewById(R.id.buttonApplyFilter);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(order_status_sort.equals(""))
                {
                    dismiss();
                }
                else
                {
                    sendResult(order_status_sort);
                    userSession.setFilterType(order_status_sort);
                    userSession.setFilterSwitchState(true);
                }

            }
        });

        Button buttonClear = view.findViewById(R.id.buttonClearOrdersFilter);
        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendResult("Default");
                userSession.setFilterType("Default");
                userSession.setFilterSwitchState(false);
                dismiss();
            }
        });

        return view;
    }

    private void sendResult(String order_status_sort) {
        if( getTargetFragment() == null ) {
            return;
        }
        Intent intent = OrdersFragment.newIntent(order_status_sort);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
        dismiss();
    }
}