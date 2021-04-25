package com.example.mycourseprojectapplication.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycourseprojectapplication.Adapters.CardAdapter;
import com.example.mycourseprojectapplication.Interfaces.OnCardSelected;
import com.example.mycourseprojectapplication.Models.Card;
import com.example.mycourseprojectapplication.Models.Cart;
import com.example.mycourseprojectapplication.R;
import com.example.mycourseprojectapplication.Utilities.CardLab;
import com.example.mycourseprojectapplication.Utilities.ConnectionDetector;

import java.util.ArrayList;

public class PaymentMethodActivity extends AppCompatActivity implements OnCardSelected {
    private final String TAG = this.getClass().getSimpleName(); // used for debugging
    private LinearLayout linearLayoutAddCard;
    private RecyclerView cardsRecyclerView;
    private boolean isDateValid = false;
    private double totalAmount = 0.0;
    private boolean isDirect;
    private int quantity = 0;
    private final int REQUEST_CODE_SCAN_CARD = 1;
    private ArrayList<Cart> products;
    private String city,firstName,lastName,phoneNumber,address;
    private CardAdapter cardAdapter;
    private ConstraintLayout payPalLayout,googlePayLayout;
    private double latitude,longitude;
    private ArrayList<Card> cardArrayList;
    private ArrayList<Card> cards;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);

        new ConnectionDetector(this);  // check if user has Internet connection

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   // setup the action toolbar with activity title and back icon functionality
        setTitle("Pay with");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        linearLayoutAddCard = findViewById(R.id.linearLayoutAddCard);
        cardsRecyclerView = findViewById(R.id.RecyclerViewMyCards);
        payPalLayout = findViewById(R.id.paypalConstraint);
        googlePayLayout = findViewById(R.id.googleConstraint);

        linearLayoutAddCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentMethodActivity.this,AddCardActivity.class);
                startActivity(intent);
            }
        });
        CardLab cardLab = new CardLab(this);
        cards = cardLab.getmCards();

        if(cards != null) {
            cardAdapter = new CardAdapter(cards,this, R.layout.card_item);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            cardsRecyclerView.setAdapter(cardAdapter);
            cardsRecyclerView.setLayoutManager(layoutManager);
            cardAdapter.notifyDataSetChanged();
            Toast.makeText(this, cards.toString() + "", Toast.LENGTH_SHORT).show();
        }

        latitude = getIntent().getDoubleExtra("latitude",0);
        longitude = getIntent().getDoubleExtra("longitude",0);
        firstName = getIntent().getStringExtra("fn");
        lastName = getIntent().getStringExtra("ln");
        phoneNumber = getIntent().getStringExtra("pn");
        city = getIntent().getStringExtra("city");
        payPalLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(PaymentMethodActivity.this, PaymentActivity.class);
                intent.putExtra("total", getIntent().getDoubleExtra("total", 0));
                intent.putExtra("products", getIntent().getSerializableExtra("products"));                   // go to payment activity and bundle required info
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                intent.putExtra("city", city);
                intent.putExtra("country", getIntent().getStringExtra("country"));
                intent.putExtra("fn",firstName);
                intent.putExtra("ln",lastName);
                intent.putExtra("pn",phoneNumber);
                intent.putExtra("isDirect",getIntent().getBooleanExtra("isDirect",false));
                intent.putExtra("cardType","PayPal");
                startActivity(intent);
            }
        });

        googlePayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(PaymentMethodActivity.this, PaymentActivity.class);
                intent.putExtra("total", getIntent().getDoubleExtra("total", 0));
                intent.putExtra("products", getIntent().getSerializableExtra("products"));                   // go to payment activity and bundle required info
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                intent.putExtra("city", city);
                intent.putExtra("country", getIntent().getStringExtra("country"));
                intent.putExtra("fn",firstName);
                intent.putExtra("ln",lastName);
                intent.putExtra("pn",phoneNumber);
                intent.putExtra("isDirect",getIntent().getBooleanExtra("isDirect",false));
                intent.putExtra("cardType","GooglePay");
                startActivity(intent);
            }
        });
        cardArrayList = cardAdapter.getCheckedCards();
        Button buttonContinue = findViewById(R.id.buttonContinue);
        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getPaymentMethod();
            }
        });



    }

    private void getPaymentMethod()
    {


        if(cardArrayList.size() != 0)
        {
            Intent intent = new Intent(PaymentMethodActivity.this, PaymentActivity.class);
            intent.putExtra("total", getIntent().getDoubleExtra("total", 0));
            intent.putExtra("products", getIntent().getSerializableExtra("products"));                   // go to payment activity and bundle required info
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);
            intent.putExtra("city", city);
            intent.putExtra("country", getIntent().getStringExtra("country"));
            intent.putExtra("fn",firstName);
            intent.putExtra("ln",lastName);
            intent.putExtra("pn",phoneNumber);
            intent.putExtra("isDirect",getIntent().getBooleanExtra("isDirect",false));
            intent.putExtra("cardType","PayPal");
            startActivity(intent);
        }
        else
        {
            Toast.makeText(this, "You must choose a payment method first!", Toast.LENGTH_SHORT).show();
            return;
        }


    }



    @Override
    protected void onResume() {
        super.onResume();
        CardLab cardLab = new CardLab(this);
        ArrayList<Card> cards = cardLab.getmCards();

        if(cards != null) {
            CardAdapter cardAdapter = new CardAdapter(cards,this, R.layout.card_item);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            cardsRecyclerView.setAdapter(cardAdapter);
            cardsRecyclerView.setLayoutManager(layoutManager);
            cardAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(CardAdapter.MyViewHolder myViewHolder, int pos) {

        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra("total", getIntent().getDoubleExtra("total", 0));
        intent.putExtra("products", getIntent().getSerializableExtra("products"));                   // go to payment activity and bundle required info
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        intent.putExtra("city", city);
        intent.putExtra("country", getIntent().getStringExtra("country"));
        intent.putExtra("fn",firstName);
        intent.putExtra("ln",lastName);
        intent.putExtra("pn",phoneNumber);
        intent.putExtra("isDirect",getIntent().getBooleanExtra("isDirect",false));
        intent.putExtra("cardType","CreditCard");
        intent.putExtra("card",cards.get(pos));
        startActivity(intent);

    }
}