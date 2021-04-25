package com.example.mycourseprojectapplication.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.mycourseprojectapplication.Activities.AddCardActivity;
import com.example.mycourseprojectapplication.Interfaces.OnCardSelected;
import com.example.mycourseprojectapplication.Models.Card;
import com.example.mycourseprojectapplication.R;

import java.util.ArrayList;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.MyViewHolder> {

    private  ArrayList<Card> cards;
    private int lastCheckedPosition = -1;
    private Context context;
    private ArrayList<Card> checkedCards = new ArrayList<>();
    private OnCardSelected onCardSelected;
    private final int layout_res;

    public CardAdapter(int layout_res) {
        this.layout_res = layout_res;
    }

    public CardAdapter(ArrayList<Card> cards, OnCardSelected onCardSelected, int layout_res) {
        this.cards = cards;
        this.onCardSelected = onCardSelected;
        this.layout_res = layout_res;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        public TextView cardNum;
        public ImageView viewCard;
        public LottieAnimationView cardType;
        public RadioButton radioButton;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cardNum = itemView.findViewById(R.id.textViewCardNum);
            cardType = itemView.findViewById(R.id.imageViewCardType);
            radioButton = itemView.findViewById(R.id.radioButtonChooseCard);
            viewCard = itemView.findViewById(R.id.imageViewSelectCard);

        }
    }
    public ArrayList<Card> getCheckedCards() {
        return checkedCards;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout_res,parent,false);
        context = view.getContext();
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Card card = cards.get(position);
        holder.cardNum.setText(String.valueOf(card.getCardNumber()));

        switch (context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:
                if(card.getCardNumber().startsWith("4"))
                {
                    holder.cardType.setAnimation("visa_white.json");
                }
                else if(card.getCardNumber().startsWith("5"))
                {

                    holder.cardType.setAnimation("master_white.json");
                }
                else
                {
                    holder.cardType.setImageDrawable(context.getDrawable(R.drawable.ic_outline_credit_card_24));
                    holder.cardType.setImageTintList(ColorStateList.valueOf(Color.BLACK));

                }

                break;
            case Configuration.UI_MODE_NIGHT_NO:
                // process
                if(card.getCardNumber().startsWith("4"))
                {
                    holder.cardType.setAnimation("visa-payment.json");
                }
                else if(card.getCardNumber().startsWith("5"))
                {

                    holder.cardType.setAnimation("mastercard-payment.json");
                }
                else
                {
                    holder.cardType.setImageDrawable(context.getDrawable(R.drawable.ic_outline_credit_card_24));
                    holder.cardType.setImageTintList(ColorStateList.valueOf(Color.BLACK));

                }
                break;
        }

        if(layout_res == R.layout.card_item) {
            holder.radioButton.setChecked(position == lastCheckedPosition);
            holder.radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkedCards.clear();
                    if (position == lastCheckedPosition) {
                        holder.radioButton.setChecked(false);
                        lastCheckedPosition = -1;
                    } else {
                        lastCheckedPosition = position;
                        notifyDataSetChanged();
                    }
                    checkedCards.add(card);
                    Log.i("Adapter", checkedCards.toString());
                }
            });

            holder.radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    onCardSelected.onItemClick(holder,holder.getAdapterPosition());
                }
            });

        }



        holder.viewCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AddCardActivity.class);
                intent.putExtra("card", card);
                context.startActivity(intent);
            }
        });


    }

    public void setOnCardSelected(OnCardSelected onCardSelected) {
        this.onCardSelected = onCardSelected;
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }
}
