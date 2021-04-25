package com.example.mycourseprojectapplication.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycourseprojectapplication.Activities.AddCardActivity;
import com.example.mycourseprojectapplication.Adapters.CardAdapter;
import com.example.mycourseprojectapplication.Interfaces.OnCardSelected;
import com.example.mycourseprojectapplication.Models.Card;
import com.example.mycourseprojectapplication.R;
import com.example.mycourseprojectapplication.Utilities.CardLab;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewCardsBottomFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewCardsBottomFragment extends BottomSheetDialogFragment implements OnCardSelected {

    public final String TAG = ViewCardsBottomFragment.this.getClass().getSimpleName();
    private ArrayList<Card> cards;
    private CardAdapter cardAdapter;
    private RecyclerView cardsRecyclerView;
    private Card card;


    public ViewCardsBottomFragment() {
        // Required empty public constructor
    }


    public static ViewCardsBottomFragment newInstance() {
        ViewCardsBottomFragment fragment = new ViewCardsBottomFragment();
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
        View view = inflater.inflate(R.layout.fragment_view_cards_bottom, container, false);
        cardsRecyclerView = view.findViewById(R.id.RecyclerViewMyCards);
        ImageView imageView = view.findViewById(R.id.imageView12);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddCardActivity.class);
                getActivity().startActivity(intent);
            }
        });

        CardLab cardLab = new CardLab(getContext());
        cards = cardLab.getmCards();

        if(cards != null) {
            cardAdapter = new CardAdapter(cards,this, R.layout.card_layout_item_profile);
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

        dismiss();
    }

    @Override
    public void onResume() {
        super.onResume();
        CardLab cardLab = new CardLab(getContext());
        cards = cardLab.getmCards();

        if(cards != null) {
            CardAdapter cardAdapter = new CardAdapter(cards,this, R.layout.card_layout_item_profile);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            cardsRecyclerView.setAdapter(cardAdapter);
            cardsRecyclerView.setLayoutManager(layoutManager);
            cardAdapter.notifyDataSetChanged();
        }
    }
}