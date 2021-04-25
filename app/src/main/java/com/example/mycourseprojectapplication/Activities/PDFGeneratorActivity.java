package com.example.mycourseprojectapplication.Activities;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mycourseprojectapplication.Models.Orders;
import com.example.mycourseprojectapplication.Models.Products;
import com.example.mycourseprojectapplication.Models.Users;
import com.example.mycourseprojectapplication.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tejpratapsingh.pdfcreator.activity.PDFCreatorActivity;
import com.tejpratapsingh.pdfcreator.utils.PDFUtil;
import com.tejpratapsingh.pdfcreator.views.PDFBody;
import com.tejpratapsingh.pdfcreator.views.PDFFooterView;
import com.tejpratapsingh.pdfcreator.views.PDFHeaderView;
import com.tejpratapsingh.pdfcreator.views.PDFTableView;
import com.tejpratapsingh.pdfcreator.views.basic.PDFHorizontalView;
import com.tejpratapsingh.pdfcreator.views.basic.PDFImageView;
import com.tejpratapsingh.pdfcreator.views.basic.PDFLineSeparatorView;
import com.tejpratapsingh.pdfcreator.views.basic.PDFTextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class PDFGeneratorActivity extends PDFCreatorActivity {
    private String type = "";
    private ArrayList<Users> usersArrayList;
    private ArrayList<Products> productsArrayList;
    private ArrayList<Orders> ordersArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        usersArrayList = new ArrayList<>();
       // getAllOrders();
        //getProducts();
        type = getIntent().getStringExtra("type");

        createPDF("test2", new PDFUtil.PDFUtilListener() {
            @Override
            public void pdfGenerationSuccess(File savedPDFFile) {
                Toast.makeText(PDFGeneratorActivity.this, "PDF Created", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void pdfGenerationFailure(Exception exception) {
                Toast.makeText(PDFGeneratorActivity.this, "PDF NOT Created", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected PDFHeaderView getHeaderView(int page) {
        PDFHeaderView headerView = new PDFHeaderView(getApplicationContext());
        PDFHorizontalView horizontalView = new PDFHorizontalView(getApplicationContext());

        PDFTextView pdfTextView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.HEADER);
        SpannableString word = null;
        if(type.equals("Users"))
        {
            word = new SpannableString("Dope Tech Users Report");
        }
        else if(type.equals("Products"))
        {
            word = new SpannableString("Dope Tech Products Report");
        }
        else if(type.equals("Orders"))
        {
            word = new SpannableString("Dope Tech Orders Report");
        }
        else
        {
            word = new SpannableString("Dope Tech Most Purchase Products Report");
        }

        word.setSpan(new ForegroundColorSpan(Color.DKGRAY), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        pdfTextView.setText(word);
        pdfTextView.setLayout(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT, 1));
        pdfTextView.getView().setGravity(Gravity.CENTER_VERTICAL);
        pdfTextView.getView().setTypeface(pdfTextView.getView().getTypeface(), Typeface.BOLD);

        horizontalView.addView(pdfTextView);

        PDFImageView imageView = new PDFImageView(getApplicationContext());
        LinearLayout.LayoutParams imageLayoutParam = new LinearLayout.LayoutParams(
                60,
                60, 0);
        imageView.setImageScale(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setImageResource(R.drawable.ic_outline_shop_24);
        imageLayoutParam.setMargins(0, 0, 10, 0);
        imageView.setLayout(imageLayoutParam);

        horizontalView.addView(imageView);

        headerView.addView(horizontalView);

        PDFLineSeparatorView lineSeparatorView1 = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(Color.WHITE);
        headerView.addView(lineSeparatorView1);

        return headerView;
    }

    @Override
    protected PDFBody getBodyViews() {







        if(type.equals("Users"))
        {


               // getUsers();

        }
       /* else if(type.equals("Products"))
        {
            PDFTextView pdfTextView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
            PDFTextView pdfTextView1 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
            PDFTextView pdfTextView2 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
            PDFTextView pdfTextView3 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
            PDFTextView pdfTextView4 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
            PDFTextView pdfTextView5 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
            PDFTextView pdfTextView6 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
            pdfTextView.setText("Product_id");
            pdfTextView1.setText("Product Name");
            pdfTextView2.setText("Product Brand");
            pdfTextView3.setText("Category");
            pdfTextView4.setText("Price");
            pdfTextView5.setText("Quantity");
            tableHeader.addToRow(pdfTextView);
            tableHeader.addToRow(pdfTextView1);
            tableHeader.addToRow(pdfTextView2);
            tableHeader.addToRow(pdfTextView3);
            tableHeader.addToRow(pdfTextView4);
            tableHeader.addToRow(pdfTextView5);
        }
        else if(type.equals("Orders"))
        {
            PDFTextView pdfTextView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
            PDFTextView pdfTextView1 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
            PDFTextView pdfTextView2 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
            PDFTextView pdfTextView3 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
            PDFTextView pdfTextView4 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
            PDFTextView pdfTextView5 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
            PDFTextView pdfTextView6 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
            pdfTextView.setText("Order_id");
            pdfTextView1.setText("User_id");
            pdfTextView2.setText("Order_date");
            pdfTextView3.setText("City");
            pdfTextView4.setText("Order_Status");
            tableHeader.addToRow(pdfTextView);
            tableHeader.addToRow(pdfTextView1);
            tableHeader.addToRow(pdfTextView2);
            tableHeader.addToRow(pdfTextView3);
            tableHeader.addToRow(pdfTextView4);

        }
        else
        {
            pdfTableTitleView.setText("Most Purchase Products Table");
        }
        /*for (String s : textInTable) {
            PDFTextView pdfTextView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
            pdfTextView.setText("Header Title: " + s);
            tableHeader.addToRow(pdfTextView);
        }

        for (String s : textInTable) {
            PDFTextView pdfTextView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
            pdfTextView.setText("Row 1 : " + s);
            tableRowView1.addToRow(pdfTextView);
        }*/




        return getUsers();
    }

    @Override
    protected PDFFooterView getFooterView(int page) {
        PDFFooterView footerView = new PDFFooterView(getApplicationContext());

        PDFTextView pdfTextViewPage = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
        pdfTextViewPage.setText(String.format(Locale.getDefault(), "Page: %d", page + 1));
        pdfTextViewPage.setLayout(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 0));
        pdfTextViewPage.getView().setGravity(Gravity.CENTER_HORIZONTAL);

        footerView.addView(pdfTextViewPage);

        return footerView;
    }




    @Override
    protected void onNextClicked(File savedPDFFile) {

        return;
    }

    private PDFBody getUsers()                      // method to get all the users from the database and add them to an arraylist
    {
        PDFBody pdfBody = new PDFBody();

        PDFTableView.PDFTableRowView tableHeader = new PDFTableView.PDFTableRowView(getApplicationContext());
        PDFTextView pdfTextView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
        PDFTextView pdfTextView1 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
        PDFTextView pdfTextView2 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
        PDFTextView pdfTextView3 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
        PDFTextView pdfTextViewData = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
        PDFTextView pdfTextViewData1 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
        PDFTextView pdfTextViewData2 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
        PDFTextView pdfTextViewData3 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
        pdfTextView.setText("User_id");
        pdfTextView1.setText("Username");
        pdfTextView2.setText("Email");
        pdfTextView3.setText("User rewardPoints");
        tableHeader.addToRow(pdfTextView);
        tableHeader.addToRow(pdfTextView1);
        tableHeader.addToRow(pdfTextView2);
        tableHeader.addToRow(pdfTextView3);
        PDFTextView pdfCompanyNameView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.H3);
        pdfCompanyNameView.setText("Dope Tech");
        pdfBody.addView(pdfCompanyNameView);
        PDFLineSeparatorView lineSeparatorView1 = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(Color.WHITE);
        pdfBody.addView(lineSeparatorView1);
        PDFTextView pdfAddressView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
        pdfAddressView.setText("UTAS / Muscat / Oman");
        pdfBody.addView(pdfAddressView);

        PDFLineSeparatorView lineSeparatorView2 = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(Color.WHITE);
        lineSeparatorView2.setLayout(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                8, 0));
        pdfBody.addView(lineSeparatorView2);
        PDFLineSeparatorView lineSeparatorView3 = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(Color.WHITE);
        pdfBody.addView(lineSeparatorView3);
        PDFTextView pdfTableTitleView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
        pdfTableTitleView.setText("Users Table");
        pdfBody.addView(pdfTableTitleView);
        PDFTableView.PDFTableRowView tableRowView1 = new PDFTableView.PDFTableRowView(getApplicationContext());
        PDFTextView pdfTextViewx = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
        pdfTextViewx.setText("Row 1 : ");
        PDFTableView tableView = new PDFTableView(getApplicationContext(), tableHeader, tableRowView1);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                PDFTableView.PDFTableRowView tableRowView = new PDFTableView.PDFTableRowView(getApplicationContext());
                for(DataSnapshot users : snapshot.getChildren())
                {
                    PDFTextView pdfTextViewData = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
                    PDFTextView pdfTextViewData1 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
                    PDFTextView pdfTextViewData2 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
                    PDFTextView pdfTextViewData3 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
                    pdfTextViewData.setText(users.child("user_id").getValue().toString());
                    pdfTextViewData1.setText(users.child("username").getValue().toString());
                    pdfTextViewData2.setText(users.child("email").getValue().toString());
                    pdfTextViewData3.setText(String.valueOf(users.child("userRewardPoints").getValue().toString()));
                    Log.d("PDF",users.toString());
                    tableRowView1.addToRow(pdfTextViewData);
                }
                pdfBody.addView(tableView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return pdfBody;
    }

    private void getProducts()       // method to get recent products
    {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Products");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productsArrayList.clear(); // clear the arraylist each time a new product added
                for(DataSnapshot products : snapshot.getChildren())
                {
                    productsArrayList.add(products.getValue(Products.class));       // add product to  the arraylist
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

    }


    private void getAllOrders()
    {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Orders");
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                for(DataSnapshot orders : snapshot.getChildren())
                {
                    ordersArrayList.add(orders.getValue(Orders.class));
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                ordersArrayList.clear();
                for(DataSnapshot orders : snapshot.getChildren())
                {
                    ordersArrayList.add(orders.getValue(Orders.class));
                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}