package com.example.mycourseprojectapplication.Activities;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycourseprojectapplication.Adapters.AdminOrderedProductAdapter;
import com.example.mycourseprojectapplication.BuildConfig;
import com.example.mycourseprojectapplication.Models.Cart;
import com.example.mycourseprojectapplication.Models.Orders;
import com.example.mycourseprojectapplication.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import ru.alexbykov.nopermission.PermissionHelper;

public class ViewOrdersActivity extends AppCompatActivity {
    private RecyclerView recyclerViewProducts;
    private AdminOrderedProductAdapter adminOrderedProductAdapter;
    private PermissionHelper permissionHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_orders);

        permissionHelper = new PermissionHelper(this); //don't use getActivity in fragment!
        permissionHelper.check(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .withDialogBeforeRun(R.string.dialog_before_run_title, R.string.dialog_before_run_message, R.string.dialog_positive_button)
                .setDialogPositiveButtonColor(android.R.color.holo_orange_dark)
                .onSuccess(this::onSuccess)
                .onDenied(this::onDenied)
                .onNeverAskAgain(this::onNeverAskAgain)
                .run();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);     // setup the action toolbar with activity title and back icon functionality
        setTitle("Modify Order");                                  // setup the title
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {               // when back is clicked just finish the activity
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView date = findViewById(R.id.textViewOrderDate);
        TextView paymentMode = findViewById(R.id.textViewPaymentType);
        TextView textViewName = findViewById(R.id.textViewName);
        TextView textViewPhone = findViewById(R.id.textViewPhone);   // initialize views and classes
        TextView textViewTotal = findViewById(R.id.textViewTotal);
        TextView textViewTitle = findViewById(R.id.textViewTitleAdminOrder);
        recyclerViewProducts = findViewById(R.id.product_order_recycler_admin);
        Orders order = (Orders) getIntent().getSerializableExtra("order");
        textViewTitle.setText(order.getFirstName() +" Order's");
        ArrayList<Cart> products = order.getPurchasedProducts();
        adminOrderedProductAdapter = new AdminOrderedProductAdapter(products,this,order);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewProducts.setLayoutManager(layoutManager);
        recyclerViewProducts.setAdapter(adminOrderedProductAdapter);
        adminOrderedProductAdapter.notifyDataSetChanged();
        String simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy HH:ss").format(order.getOrder_date());
        date.setText(simpleDateFormat);
        paymentMode.setText(order.getPayment_option());                                       // set the order data
        textViewName.setText(order.getFirstName() + " " + order.getLastName());
        textViewPhone.setText(order.getPhoneNumber());
        textViewTotal.setText("$"+order.getTotal());


    }
    private void onNeverAskAgain() {
        startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID))); // go to application settings
        Toast.makeText(this, "Enable location!", Toast.LENGTH_LONG).show();
    }

    private void onDenied() {
        Toast.makeText(this, "Tracking Functionality may not work", Toast.LENGTH_SHORT).show();
    }

    private void onSuccess() {
        Toast.makeText(this, "Granted!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        permissionHelper = new PermissionHelper(this); //don't use getActivity in fragment!
        permissionHelper.check(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .withDialogBeforeRun(R.string.dialog_before_run_title, R.string.dialog_before_run_message, R.string.dialog_positive_button)
                .setDialogPositiveButtonColor(android.R.color.holo_orange_dark)
                .onSuccess(this::onSuccess)
                .onDenied(this::onDenied)
                .onNeverAskAgain(this::onNeverAskAgain)
                .run();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionHelper.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }
}