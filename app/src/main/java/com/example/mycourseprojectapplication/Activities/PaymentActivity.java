package com.example.mycourseprojectapplication.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.interfaces.HttpResponseCallback;
import com.braintreepayments.api.internal.HttpClient;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.example.mycourseprojectapplication.Fragments.BottomSheetDialogPayment;
import com.example.mycourseprojectapplication.Fragments.CodeBottomFragment;
import com.example.mycourseprojectapplication.Models.Card;
import com.example.mycourseprojectapplication.Models.Cart;
import com.example.mycourseprojectapplication.Models.OrderStatus;
import com.example.mycourseprojectapplication.Models.Orders;
import com.example.mycourseprojectapplication.Models.Payments;
import com.example.mycourseprojectapplication.Models.TimeLineModel;
import com.example.mycourseprojectapplication.Models.TimelineStatus;
import com.example.mycourseprojectapplication.Models.Tracking;
import com.example.mycourseprojectapplication.R;
import com.example.mycourseprojectapplication.Utilities.ConnectionDetector;
import com.example.mycourseprojectapplication.Utilities.MailSender;
import com.example.mycourseprojectapplication.Utilities.UserSession;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static android.view.View.TEXT_ALIGNMENT_CENTER;

public class PaymentActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private final String TAG = this.getClass().getSimpleName(); // this is used for debugging
    private FirebaseAuth mAuth; // declare our variables
    private double totalAmount = 0.0, priceDiscount = 0.0;
    private boolean isDirect;
    private UserSession userSession;
    private ArrayList<Cart> products;
    private String city,firstName,lastName,phoneNumber,address;
    private LottieAnimationView lottieAnimationView;
    private CheckBox checkBoxPayment;
    private TextView textViewDiscount;
    private TextView textViewDiscountLabel;
    private TextView textViewSaveOrderLabel;
    private double latitude,longitude;
    private BottomSheetDialogPayment bottomSheet;
    private boolean isVisible;
    private FirebaseAnalytics mFirebaseAnalytics;
    private static final int REQUEST_CODE = 1234;
    private String API_GET_TOKEN="http://10.0.2.2/braintree/main.php";
    private String API_CHECKOUT="http://10.0.2.2/braintree/checkout.php";
    private String token,amount;
    private HashMap<String,String> paramsHash;
    private int previousValue = 0;
    private  Map<String, Object> values;
    private Button addCode,buttonClearCode;
    private String result = "Default";
    private  TextView textViewTotal;
    private double delivery = 4.99;
    private String reward_id = "Default";
    private double totalAmountWithoutDiscountCode = 0.0;
    private double totalAmountWithDiscount = 0.0;
    private String paymentOption = "Credit Card";
    private int  currentQuantity = 0;
    private Map<String, Object> values1 = new HashMap();
    private CardView paymentMethod;
    private CardView methods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        new ConnectionDetector(this);  // check if user has Internet connection
        userSession = new UserSession(this); // create user session
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Toolbar toolbar = findViewById(R.id.toolbarPayment);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // setup the action toolbar with activity title and back icon functionality
        setTitle("Payment Checkout");

        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar5);
        Button buttonPay = findViewById(R.id.buttonPayCredit);// initialize views and classes
        progressBar.setVisibility(View.GONE);
        paymentMethod = findViewById(R.id.CardViewPaymentMethod);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() { // when back is clicked just finish the activity
            @Override
            public void onClick(View v) {
                if(progressBar.isShown())
                {

                    if(progressBar.isIndeterminate())
                    {
                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                }
                else
                {
                    finish();
                }

            }
        });
        textViewTotal = findViewById(R.id.textViewTotalPayment);
        TextView textViewName = findViewById(R.id.textViewAddressFirstName);
        TextView textViewAddress = findViewById(R.id.textViewAddressPayment);
        TextView textViewCity = findViewById(R.id.textViewCityPayment);
        TextView textViewPhoneNumber = findViewById(R.id.textViewPhoneNumberPayment);
        checkBoxPayment = findViewById(R.id.checkBoxPaymentMethod);
        lottieAnimationView = findViewById(R.id.imageViewPayment);
        TextView textView = findViewById(R.id.textViewPayPal);
        TextView textViewSubTotal = findViewById(R.id.textViewSubTotalPayment);
        TextView textViewDelivery = findViewById(R.id.textViewDeliveryFees);
        textViewDiscount = findViewById(R.id.textViewDiscountAmount);
        textViewDiscountLabel = findViewById(R.id.textViewDiscount);
        LottieAnimationView lottieAnimationView1 = findViewById(R.id.imageView13);
        buttonClearCode = findViewById(R.id.buttonClearCode);
        isDirect = getIntent().getBooleanExtra("isDirect",false);                         // get the values from the previous activity using intent
        totalAmount = getIntent().getDoubleExtra("total",0);
        products = (ArrayList<Cart>) getIntent().getSerializableExtra("products");
        latitude = getIntent().getDoubleExtra("latitude",0);
        longitude = getIntent().getDoubleExtra("longitude",0);
        city = getIntent().getStringExtra("city");
        firstName = getIntent().getStringExtra("fn");
        lastName = getIntent().getStringExtra("ln");
        phoneNumber = getIntent().getStringExtra("pn");

        ImageView imageView = findViewById(R.id.imageViewSelectPayment);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("cardDate",true);
                bottomSheet = new BottomSheetDialogPayment();
                bottomSheet.setArguments(bundle);
                bottomSheet.show(getSupportFragmentManager(),
                        "ModalBottomSheet");
            }
        });
        textViewName.setText(firstName + " " + lastName);
        textViewCity.setText(city);
        textViewPhoneNumber.setText(phoneNumber);
        LinearLayout linearLayout = findViewById(R.id.LinearLayoutPayment);
        LinearLayout linearLayout1 = findViewById(R.id.LinearLayoutPaymentFields);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(16,16,16,16);

        DecimalFormat decimalFormat = new DecimalFormat("0.#####");
        final int N = products.size(); // total number of textviews to add
        Log.i(TAG,N+"");
        for (int i = 0; i < N; i++) {
            // create a new textview

            final TextView rowTextView = new TextView(this);
            rowTextView.setLayoutParams(params);
            // set some properties of rowTextView or something
            if(products.get(i).getConfigurationSelected().equals("Default"))
            {
                if(products.get(i).getColorSelected().equals("Default"))
                {
                    rowTextView.setText(products.get(i).getQuantity()+" x " + products.get(i).getProduct().getProductName());
                }
                else
                {
                    rowTextView.setText(products.get(i).getQuantity()+" x " + products.get(i).getProduct().getProductName() +" " + products.get(i).getColorSelected());
                }
            }
            else {
                if(products.get(i).getConfigurationSelected().length() > 7)
                {
                    rowTextView.setText(products.get(i).getQuantity() + " x " + products.get(i).getProduct().getProductName() + " " + products.get(i).getColorSelected());
                }
                else
                {
                    rowTextView.setText(products.get(i).getQuantity() + " x " + products.get(i).getProduct().getProductName() + " " + products.get(i).getConfigurationSelected() + " " + products.get(i).getColorSelected());
                }

            }
            if(totalAmount == 0) {

                String result = decimalFormat.format(Double.valueOf(products.get(i).getTotalPrice()));
                textViewSubTotal.setText("$" + result);
            }
            else
            {
                String result1 = decimalFormat.format(Double.valueOf(totalAmount));
                textViewSubTotal.setText("$"+result1);
            }


            // add the textview to the linearlayout
            int childBefore = linearLayout.getChildCount();
            if(childBefore >= 1) {
                if(isDirect) {
                    linearLayout.removeViewAt(0);
                }
                linearLayout.addView(rowTextView,0);
            }


        }

        totalAmount = totalAmount + delivery;
        totalAmountWithoutDiscountCode = totalAmount;
        totalAmountWithDiscount = totalAmount;
        result = decimalFormat.format(Double.valueOf(totalAmount));
        textViewTotal.setText("$" + result );
        for (int i = 0; i < N; i++) {
            // create a new textview
            final TextView rowTextView = new TextView(this);
            rowTextView.setLayoutParams(params);
            // set some properties of rowTextView or something

            String result1 = decimalFormat.format(Double.valueOf(products.get(i).getTotalPrice()));
            rowTextView.setText("$"+ result1);
            rowTextView.setTextAlignment(TEXT_ALIGNMENT_CENTER);
            // add the textview to the linearlayout
            int childBefore = linearLayout1.getChildCount();;
            if(childBefore >= 1) {
                if(isDirect) {
                    linearLayout1.removeViewAt(0);
                }
                linearLayout1.addView(rowTextView,0);
            }


        }


        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(this, Locale.getDefault());

        try { // get user address
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            if(address.contains("Sib"))
            {
                address = addresses.get(0).getAddressLine(0).replace("Sib","Seeb");
            }
            else
            {
                address = addresses.get(0).getAddressLine(0);
            }
            textViewAddress.setText(address);
        } catch (IOException e) {
            e.printStackTrace();
        }




        isVisible = false;
       //new getToken().execute();
        buttonPay.setOnClickListener(new View.OnClickListener() { // when button pay is clicked
            @Override
            public void onClick(View v) {

                if(isVisible) {
                    if (checkBoxPayment.isChecked())
                    {
                        if(checkBoxPayment.getText().toString().equals("PayPal"))
                        {
                            new getToken().execute();
                            if(token != null)
                            {
                                submitPayment();
                                paymentOption = "PayPal";

                            }
                            else {
                                new getToken().execute();
                            }
                        }
                        else {
                            paymentOption = "Credit Card";
                           createOrder(firstName, lastName, phoneNumber, latitude, longitude, city, products, totalAmount); // create the order in database

                        }
                    }
                    else{
                        Toast.makeText(PaymentActivity.this, "You have to check the payment method first!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }    else {
                    Toast.makeText(PaymentActivity.this, "You have to select a payment method first!", Toast.LENGTH_SHORT).show();
                    return;
                }





            }
        });
        buttonClearCode.setVisibility(View.GONE);
        addCode = findViewById(R.id.buttonAddCode);
        textViewSaveOrderLabel = findViewById(R.id.textViewSaveOnYourOrder);
        addCode.setVisibility(View.VISIBLE);
        addCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CodeBottomFragment codeBottomFragment = new CodeBottomFragment();
                codeBottomFragment.show(getSupportFragmentManager(),
                        "ModalBottomSheet");

            }
        });

        buttonClearCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                textViewDiscount.setVisibility(View.GONE);
                textViewSaveOrderLabel.setText("Save on your order!");
                textViewDiscountLabel.setVisibility(View.GONE);
                addCode.setVisibility(View.VISIBLE);
                buttonClearCode.setVisibility(View.GONE);
                DecimalFormat decimalFormat = new DecimalFormat("0.#####");
                result = decimalFormat.format(Double.valueOf(totalAmountWithoutDiscountCode));
                totalAmount = Double.parseDouble(result);
                textViewTotal.setText("$" + result);
                reward_id = "Default";
            }
        });



    }

    public void showProgressBar() // method to show progress bar
    {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() // method to hide progress bar
    {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        if(progressBar.isShown())
        {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            return;
        }
        else
        {
            super.onBackPressed();
        }

    }

    private void createOrder(String firstName, String lastName, String phoneNumber, double latitude, double longitude, String city, @NotNull ArrayList<Cart> products, double total) // method to create new order
    {
        showProgressBar(); // show progress bar
        try {
            String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
            String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
            TimeLineModel timeLineModel1 = new TimeLineModel("Your order has been placed",currentDate, OrderStatus.ORDERED, TimelineStatus.COMPLETED,"USA, New York",1,currentTime);
            TimeLineModel timeLineModel2 = new TimeLineModel("Your product is begin prepared","Processing", OrderStatus.PREPARED,TimelineStatus.INACTIVE,"USA, New York",2,"NA");
            TimeLineModel timeLineModel3 = new TimeLineModel("Product received from the manufacturer","Processing", OrderStatus.RECEIVED_FROM_MANUFACTURE,TimelineStatus.INACTIVE,"USA, New York",3,"NA");
            TimeLineModel timeLineModel4 = new TimeLineModel("Product is prepared for shipping","Processing",OrderStatus.PREPARED_FOR_SHIPPING,TimelineStatus.INACTIVE,"USA, New York",4,"NA");
            TimeLineModel timeLineModel5 = new TimeLineModel("Product Shipped","Processing",OrderStatus.SHIPPED,TimelineStatus.INACTIVE,"USA, New York",5,"NA");
            TimeLineModel timeLineModel6 = new TimeLineModel("In Transit","Processing", OrderStatus.IN_TRANSIT,TimelineStatus.INACTIVE,"USA, New York",6,"NA");
            TimeLineModel timeLineModel7 = new TimeLineModel("Product reached our HQ","Processing", OrderStatus.REACHED_HQ,TimelineStatus.INACTIVE,"USA, New York",7,"NA");
            TimeLineModel timeLineModel8 = new TimeLineModel("Order is schedule for delivery","Processing", OrderStatus.SCHEDULED_FOR_DELIVERY,TimelineStatus.INACTIVE,"USA, New York",8,"NA");
            TimeLineModel timeLineModel9 = new TimeLineModel("Out for delivery","Processing", OrderStatus.OUT_FOR_DELIVERY,TimelineStatus.INACTIVE,"USA, New York",9,"NA");
            TimeLineModel timeLineModel10 = new TimeLineModel("Delivered","Processing", OrderStatus.DELIVERED,TimelineStatus.INACTIVE,"USA, New York",10,"NA");
            Map<String,TimeLineModel> timeLineModelMap = new HashMap<>();
            timeLineModelMap.put(OrderStatus.ORDERED.name().toLowerCase(),timeLineModel1);
            timeLineModelMap.put(OrderStatus.PREPARED.name().toLowerCase(),timeLineModel2);
            timeLineModelMap.put(OrderStatus.RECEIVED_FROM_MANUFACTURE.name().toLowerCase(),timeLineModel3);
            timeLineModelMap.put(OrderStatus.PREPARED_FOR_SHIPPING.name().toLowerCase(),timeLineModel4);
            timeLineModelMap.put(OrderStatus.SHIPPED.name().toLowerCase(),timeLineModel5);
            timeLineModelMap.put(OrderStatus.IN_TRANSIT.name().toLowerCase(),timeLineModel6);
            timeLineModelMap.put(OrderStatus.REACHED_HQ.name().toLowerCase(),timeLineModel7);
            timeLineModelMap.put(OrderStatus.SCHEDULED_FOR_DELIVERY.name().toLowerCase(),timeLineModel8);
            timeLineModelMap.put(OrderStatus.OUT_FOR_DELIVERY.name().toLowerCase(),timeLineModel9);
            timeLineModelMap.put(OrderStatus.DELIVERED.name().toLowerCase(),timeLineModel10);
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Orders");
            String order_key = myRef.push().getKey();
            String order_id = UUID.randomUUID().toString();// create order id
            DatabaseReference myRef1 = database.getReference("Tracking");
            HashMap<String, String> tracking_keys = new HashMap<>();
            for (int i = 0; i < products.size(); i++) {
                String tracking_key = UUID.randomUUID().toString(); // create tracking key
                Tracking tracking = new Tracking(latitude, longitude, 0, 0, tracking_key, products.get(i).getProduct().getProduct_id(),order_id,timeLineModelMap); // create tracking object
                myRef1.child(tracking_key).setValue(tracking); // create tracking info
                tracking_keys.put(products.get(i).getProduct().getProduct_id(), tracking_key);
                String transaction_id = UUID.randomUUID().toString().substring(0, 6);
                Bundle purchaseParams = new Bundle();
                purchaseParams.putString(FirebaseAnalytics.Param.TRANSACTION_ID, transaction_id);
                purchaseParams.putString(FirebaseAnalytics.Param.ITEM_ID, products.get(i).getProduct().getProduct_id());
                purchaseParams.putString(FirebaseAnalytics.Param.AFFILIATION, "Dope Tech");
                purchaseParams.putString(FirebaseAnalytics.Param.CURRENCY, "USD");
                purchaseParams.putDouble(FirebaseAnalytics.Param.VALUE, products.get(i).getTotalPrice());
                purchaseParams.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, products.get(i).getProduct().getCategory());
                purchaseParams.putString(FirebaseAnalytics.Param.ITEM_BRAND, products.get(i).getProduct().getProductBrand());
                purchaseParams.putString(FirebaseAnalytics.Param.ITEM_NAME, products.get(i).getProduct().getProductName());
                purchaseParams.putString(FirebaseAnalytics.Param.ITEM_VARIANT, products.get(i).getConfigurationSelected());
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.ECOMMERCE_PURCHASE, purchaseParams);

            }

            if (order_key != null) {

                String uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid(); // get user id
                long date = System.currentTimeMillis();
                Orders order = new Orders(order_id, uid, paymentOption, products, "Ordered", firstName, lastName, city, phoneNumber, System.currentTimeMillis(), total, tracking_keys); // create new order
                myRef.child(uid).child(order_key).setValue(order).addOnCompleteListener(new OnCompleteListener<Void>() { // set the value using use id and order key
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // on complete
                        if(task.isSuccessful()) {
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference("Payments");
                            String payment_key = myRef.push().getKey(); // create payment key
                            if (payment_key != null) {
                                String payment_id = UUID.randomUUID().toString(); // create payment id
                                Payments payment = new Payments(payment_id, order_id, date); // create new payment

                                myRef.child(uid).child(payment_key).setValue(payment).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if(task.isSuccessful()) {
                                            if (!isDirect) // if it is not direct
                                            {

                                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                                DatabaseReference myRef = database.getReference("Cart");
                                                myRef.child(uid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() { // remove the item from the cart in the database
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        hideProgressBar(); // hide progress bar

                                                        new UserSession(getApplicationContext()).setCartValue(0); // set cart value to zero in the session
                                                        Intent intent = new Intent(getApplicationContext(), OrderCompleteActivity.class); // go to order complete activity
                                                        startActivity(intent);
                                                        finish();

                                                    }
                                                });
                                            } else { // if direct
                                                Intent intent = new Intent(getApplicationContext(), OrderCompleteActivity.class); // go to order complete activity
                                                startActivity(intent);
                                                finish();
                                            }
                                            String date = new SimpleDateFormat("dd MMMM yyyy hh:mm").format(System.currentTimeMillis());
                                            MailSender mailSender = new MailSender(); // create a mail sender object to send a background email
                                            mailSender.sendMail(userSession.getEmail(), "Order Confirmed", "\n\nThis is an automated message to let you know that you have placed an order in our system\n\nOrder details : \nOrder id :"+order_id.substring(0,13)+
                                                    "\nCustomer name : "+firstName+" "+lastName+"\nOrder Time :"+date+"\nPayment Method : "+paymentOption+"\nDeliver to : "+address+"\n\nProducts Purchased : \n\n" + listPurchasedProducts(),PaymentActivity.this); // send email with subject and body
                                            updateUserPoints(Math.round(total));
                                            decreaseProductQuantity(products);
                                            removeDiscountCode();
                                        }
                                    }
                                });
                            }
                        }
                    }
                });
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }


    }

    private void updateUserPoints(double points)
    {

        try {

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Users");
            values = new HashMap<>();
            myRef.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {


                    previousValue = Integer.parseInt(snapshot.child("userRewardPoints").getValue().toString());

                    for (DataSnapshot user : snapshot.getChildren()) {
                        values.put(user.getKey(), user.getValue());

                    }
                    values.put("userRewardPoints", points + previousValue);
                    myRef.child(mAuth.getCurrentUser().getUid()).updateChildren(values);

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
    private void removeDiscountCode()
    {

        try {

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Rewards");
            if (reward_id.equals("Default")) {
                return;
            } else {
                myRef.child(mAuth.getCurrentUser().getUid()).child(reward_id).removeValue();
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }



    }
    private void decreaseProductQuantity(ArrayList<Cart> products)
    {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Products");
        DatabaseReference myRef1 = database.getReference("Products");
        for(Cart mProduct : products)
        {
            myRef.child(mProduct.getProduct().getProduct_id()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    currentQuantity = Integer.parseInt(snapshot.child("quantity").getValue().toString());
                    Map<String, Object> values = new HashMap();
                    for (DataSnapshot productInfo : snapshot.getChildren()) {
                        values.put(productInfo.getKey(),productInfo.getValue());
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        for (Cart mProduct : products)
        {
            values1.put("quantity",(mProduct.getProduct().getQuantity() - mProduct.getQuantity()));
            myRef1.child(mProduct.getProduct().getProduct_id()).updateChildren(values1);

            Log.d(TAG,currentQuantity+"");
        }




    }
    public void setDiscount(String discount,String reward_id_from_frag)
    {
        if(discount.isEmpty())
        {
            textViewDiscount.setVisibility(View.GONE);
            textViewSaveOrderLabel.setText("Save on your order!");
            textViewDiscountLabel.setVisibility(View.GONE);
            addCode.setVisibility(View.VISIBLE);
            buttonClearCode.setVisibility(View.GONE);

        }
        else
        {
            priceDiscount = Double.parseDouble(discount.substring(1));
            textViewDiscount.setVisibility(View.VISIBLE);
            textViewDiscountLabel.setVisibility(View.VISIBLE);
            textViewDiscount.setText("-$"+String.valueOf(priceDiscount));
            textViewSaveOrderLabel.setText("Discount code added");
            addCode.setVisibility(View.GONE);
            buttonClearCode.setVisibility(View.VISIBLE);
            DecimalFormat decimalFormat = new DecimalFormat("0.#####");
            result = decimalFormat.format(Double.valueOf(totalAmountWithDiscount-priceDiscount));
            totalAmount = Double.parseDouble(result);
            textViewTotal.setText("$" + result );
            reward_id = reward_id_from_frag;


        }
    }


    private void submitPayment() {

        DropInRequest dropInRequest = new DropInRequest().clientToken(token);
        startActivityForResult(dropInRequest.getIntent(this), REQUEST_CODE);

    }

    private void sendPayments(){
        showProgressBar();
        RequestQueue queue = Volley.newRequestQueue(PaymentActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API_CHECKOUT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.toString().contains("Successful")){

                            createOrder(firstName,lastName,phoneNumber,latitude,longitude,city,products,totalAmount);

                        }
                        else {
                            Toast.makeText(PaymentActivity.this, "Payment Failed, please retry again..", Toast.LENGTH_SHORT).show();
                            hideProgressBar();
                        }
                        Log.d("Response",response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Err",error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                if(paramsHash==null)
                    return null;
                Map<String,String> params=new HashMap<>();
                for(String key:paramsHash.keySet())
                {
                    params.put(key,paramsHash.get(key));
                }
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("Content-type","application/x-www-form-urlencoded");
                return params;
            }
        };
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(0,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(mRetryPolicy);
        queue.add(stringRequest);
    }

    private class getToken extends AsyncTask {
        ProgressDialog mDailog;

        @Override
        protected Object doInBackground(Object[] objects) {
            HttpClient client = new HttpClient();
            client.get(API_GET_TOKEN, new HttpResponseCallback() {
                @Override
                public void success(final String responseBody) {
                    mDailog.dismiss();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            token = responseBody;
                        }
                    });
                }

                @Override
                public void failure(Exception exception) {
                    mDailog.dismiss();
                    Log.d("Err",exception.toString());
                }
            });
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDailog = new ProgressDialog(PaymentActivity.this,android.R.style.Theme_Material_Dialog_Alert);
            mDailog.setCancelable(false);
            mDailog.setMessage("Loading..., Please Wait");
            mDailog.show();
        }

        @Override
        protected void onPostExecute(Object o){
            super.onPostExecute(o);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                PaymentMethodNonce nonce = result.getPaymentMethodNonce();
                String strNounce = nonce.getNonce();
                String amountInString = String.valueOf(totalAmount);
                paramsHash = new HashMap<>();
                paramsHash.put("amount", amountInString);
                paramsHash.put("payment_method_nonce", strNounce);
                sendPayments();
                paymentOption = nonce.getTypeLabel();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "User canceled", Toast.LENGTH_SHORT).show();
            } else {
                Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
                Log.d("Err", error.toString());
            }
        }
    }



    private String listPurchasedProducts() // method to get list of purchased products for email
    {
        String item = "";
        double total = 0.0;
        for(Cart product : products)
        {
            item += "Product Name : "+product.getProduct().getProductName() + " \n"+ "Product Price : $"+product.getTotalPrice() + " \nQuantity : " + product.getQuantity()+"\n\n";
            total = total +product.getTotalPrice();
        }
        double totalWithDelivery = total+delivery;
        return item +"\nTotal Amount with Delivery Fees : $"+ totalWithDelivery;
    }

    public void setLottieAnimationViewForCard(Card card)
    {
        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:
                if(card.getCardNumber().startsWith("4"))
                {
                    lottieAnimationView.setAnimation("visa_white.json");
                }
                else if(card.getCardNumber().startsWith("5"))
                {
                    lottieAnimationView.setAnimation("master_white.json");
                }
                else
                {

                }

                break;
            case Configuration.UI_MODE_NIGHT_NO:
                // process
                if(card.getCardNumber().startsWith("4"))
                {
                    lottieAnimationView.setAnimation("visa-payment.json");
                }
                else if(card.getCardNumber().startsWith("5"))
                {
                    lottieAnimationView.setAnimation("mastercard-payment.json");
                }
                else
                {

                }
                break;
        }
        isVisible = true;
        checkBoxPayment.setText("Card ends with XXXX "+card.getCardNumber().trim().substring(15,19));
        checkBoxPayment.setVisibility(View.VISIBLE);
        lottieAnimationView.setVisibility(View.VISIBLE);
        lottieAnimationView.playAnimation();


    }
    public void setLottieAnimationViewForPayPal()
    {
        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:
                lottieAnimationView.setAnimation("paypal_white.json");
                isVisible = true;
                checkBoxPayment.setText("PayPal");
                checkBoxPayment.setVisibility(View.VISIBLE);
                lottieAnimationView.setVisibility(View.VISIBLE);
                lottieAnimationView.playAnimation();
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                // process
                lottieAnimationView.setAnimation("paypal.json");
                isVisible = true;
                checkBoxPayment.setText("PayPal");
                checkBoxPayment.setVisibility(View.VISIBLE);
                lottieAnimationView.setVisibility(View.VISIBLE);
                lottieAnimationView.playAnimation();
                break;
        }



    }


}