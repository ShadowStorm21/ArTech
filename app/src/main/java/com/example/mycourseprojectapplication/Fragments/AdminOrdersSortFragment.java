package com.example.mycourseprojectapplication.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;

import androidx.fragment.app.Fragment;

import com.example.mycourseprojectapplication.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminOrdersSortFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminOrdersSortFragment extends BottomSheetDialogFragment {



    public final static String TAG = AdminOrdersSortFragment.class.getSimpleName();


    private String order_status_sort = "";
    private String filterType = "";



    public AdminOrdersSortFragment() {
        // Required empty public constructor
    }


    public static AdminOrdersSortFragment newInstance() {
        AdminOrdersSortFragment fragment = new AdminOrdersSortFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_orders_sort, container, false);
        RadioGroup radioGroupSortByStatus = view.findViewById(R.id.radioGroupStatus);
        RadioGroup radioGroupFilter = view.findViewById(R.id.radioGroupFilterSort);
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
                    default:
                        order_status_sort = "Default";

                }

            }
        });

        radioGroupFilter.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId)
                {
                    case R.id.radioButtonOrderAsce:
                        filterType = "Ascending";
                        break;
                    case R.id.radioButtonOrderDesc:
                        filterType = "Descending";
                        break;
                    default:
                        filterType = "Default";
                }
            }
        });


        Button button = view.findViewById(R.id.buttonApplyFilter);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendResult(order_status_sort,filterType);
                dismiss();
            }
        });

        Button buttonClear = view.findViewById(R.id.buttonClearOrdersFilter);
        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendResult("Default","Default");
                dismiss();
            }
        });

        return view;
    }

    private void sendResult(String order_status_sort,String filterType) {
        if( getTargetFragment() == null ) {
            return;
        }
        Intent intent = AdminHomeFragment.newIntent(order_status_sort,filterType);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
        dismiss();
    }
}