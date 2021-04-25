package com.example.mycourseprojectapplication.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.mycourseprojectapplication.Activities.PDFActivity;
import com.example.mycourseprojectapplication.Models.Orders;
import com.example.mycourseprojectapplication.Models.Products;
import com.example.mycourseprojectapplication.Models.Users;
import com.example.mycourseprojectapplication.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;


public class ReportTypeFragment extends BottomSheetDialogFragment {

    private ArrayList<Users> usersArrayList;
    private ArrayList<Products> productsArrayList;
    private ArrayList<Orders> ordersArrayList;
    public ReportTypeFragment() {
        // Required empty public constructor
    }


    public static ReportTypeFragment newInstance() {
        ReportTypeFragment fragment = new ReportTypeFragment();
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
        View view = inflater.inflate(R.layout.fragment_report_type, container, false);
        usersArrayList = new ArrayList<>();
        productsArrayList = new ArrayList<>();
        ordersArrayList = new ArrayList<>();
        Button buttonReportUsers = view.findViewById(R.id.buttonReportUsers);
        Button buttonReportProducts = view.findViewById(R.id.buttonReportProducts);
        Button buttonReportOrders = view.findViewById(R.id.buttonReportOrders);
        Button buttonReportMostPurchaseProducts = view.findViewById(R.id.buttonReportPproducts);
        Button buttonReportFill = view.findViewById(R.id.buttonReportFulfilled);

        buttonReportUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PDFActivity.class);
                intent.putExtra("type","Users");
                startActivity(intent);
            }
        });
        buttonReportProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PDFActivity.class);
                intent.putExtra("type","Products");
                startActivity(intent);
            }
        });
        buttonReportOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PDFActivity.class);
                intent.putExtra("type","Orders");
                startActivity(intent);
            }
        });
        buttonReportMostPurchaseProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PDFActivity.class);
                intent.putExtra("type","MostPP");
                startActivity(intent);
            }
        });
        buttonReportFill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PDFActivity.class);
                intent.putExtra("type","fulfilled");
                startActivity(intent);
            }
        });
        return view;
    }


}