package com.example.mycourseprojectapplication.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycourseprojectapplication.Activities.AddCardActivity;
import com.example.mycourseprojectapplication.Activities.PaymentActivity;
import com.example.mycourseprojectapplication.Adapters.CardAdapter;
import com.example.mycourseprojectapplication.Interfaces.OnCardSelected;
import com.example.mycourseprojectapplication.Models.Card;
import com.example.mycourseprojectapplication.R;
import com.example.mycourseprojectapplication.Utilities.CardLab;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;


public class BottomSheetDialogPayment extends BottomSheetDialogFragment implements OnCardSelected {

    private ArrayList<Card> cardArrayList;
    private ArrayList<Card> cards;
    private CardAdapter cardAdapter;
    private RecyclerView cardsRecyclerView;
    private Card card;
    private ConstraintLayout payPalLayout;
    public BottomSheetDialogPayment() {
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
        View view =  inflater.inflate(R.layout.bottom_layout_payment_method, container, false);
        ImageView imageView = view.findViewById(R.id.imageView12);
        cardsRecyclerView = view.findViewById(R.id.RecyclerViewMyCards);
        payPalLayout = view.findViewById(R.id.paypalConstraint);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddCardActivity.class);
                getActivity().startActivity(intent);
            }
        });

        payPalLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((PaymentActivity) getActivity()).setLottieAnimationViewForPayPal();
                dismiss();
            }
        });


        CardLab cardLab = new CardLab(getContext());
        cards = cardLab.getmCards();

        if(cards != null) {
            cardAdapter = new CardAdapter(cards,this, R.layout.card_item);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            cardsRecyclerView.setAdapter(cardAdapter);
            cardsRecyclerView.setLayoutManager(layoutManager);
            cardAdapter.notifyDataSetChanged();
        }
        cardAdapter.notifyDataSetChanged();
        return view;
    }

    @Override
    public void onItemClick(CardAdapter.MyViewHolder myViewHolder, int pos) {


            card = cards.get(pos);
            ((PaymentActivity) getActivity()).setLottieAnimationViewForCard(card);
            dismiss();



    }

    @Override
    public void onResume() {
        super.onResume();
        CardLab cardLab = new CardLab(getContext());
        cards = cardLab.getmCards();

        if(cards != null) {
            CardAdapter cardAdapter = new CardAdapter(cards,this, R.layout.card_item);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            cardsRecyclerView.setAdapter(cardAdapter);
            cardsRecyclerView.setLayoutManager(layoutManager);
            cardAdapter.notifyDataSetChanged();
        }
    }
}