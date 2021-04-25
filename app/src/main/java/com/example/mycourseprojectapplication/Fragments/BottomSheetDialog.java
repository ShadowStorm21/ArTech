package com.example.mycourseprojectapplication.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.mycourseprojectapplication.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetDialog extends BottomSheetDialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
            ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.bottom_layout_sheet,
                container, false);

        TextView title = v.findViewById(R.id.textViewCardHelpTitle);
        TextView description = v.findViewById(R.id.textViewCardHelpDescription);
        ImageView imageView = v.findViewById(R.id.imageViewCardLayout);
        Button button = v.findViewById(R.id.Button_close_help);
        Bundle bundle = getArguments();


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                dismiss();
            }
        });

        if(bundle.getBoolean("cardNumber"))
        {
            title.setText("Card Number");
            description.setText("16 Digits Number on the front of your credit/debit card");
            imageView.setImageDrawable(getActivity().getDrawable(R.drawable.card_number));
        }
        if(bundle.getBoolean("cardHolder"))
        {
            title.setText("Card Holder Name");
            description.setText("The name of the card holder on the front of your credit/debit card, usually under the expired date");
            imageView.setImageDrawable(getActivity().getDrawable(R.drawable.card_holder));
        }
        if(bundle.getBoolean("cardDate"))
        {
            title.setText("Expired Date");
            description.setText("The expired date of the card on the front of your credit/debit card, usually under the card number");
            imageView.setImageDrawable(getActivity().getDrawable(R.drawable.card_date));
        }
        if(bundle.getBoolean("cardCCV"))
        {
            title.setText("Card CCV");
            description.setText("A three digit number on the back of your credit/debit card");
            imageView.setImageDrawable(getActivity().getDrawable(R.drawable.card_ccv));
        }




        return v;
    }
}
