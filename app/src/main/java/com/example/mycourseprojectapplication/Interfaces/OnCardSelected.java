package com.example.mycourseprojectapplication.Interfaces;

import com.example.mycourseprojectapplication.Adapters.CardAdapter;

public interface OnCardSelected {

    void onItemClick(CardAdapter.MyViewHolder myViewHolder, int pos);
}
