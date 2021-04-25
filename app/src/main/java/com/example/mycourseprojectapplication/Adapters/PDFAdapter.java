package com.example.mycourseprojectapplication.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mycourseprojectapplication.Models.Users;
import com.example.mycourseprojectapplication.R;

import java.util.List;

public class PDFAdapter extends ArrayAdapter {

    private List<Users> usersArrayList;
    private Context context;

    public PDFAdapter(@NonNull Context context, int resource, List<Users> usersList) {
        super(context, resource, usersList);
        this.context = context;
        this.usersArrayList = usersList;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_pdf_user_layout, null);

        Users user = usersArrayList.get(position);
        TextView textViewUserId,textViewUsername,textViewEmail,textViewRewardPoints;

        textViewUserId = view.findViewById(R.id.textViewUserId);
        textViewEmail = view.findViewById(R.id.textViewEmail);
        textViewRewardPoints = view.findViewById(R.id.textViewUserRewardPoints);
        textViewUsername = view.findViewById(R.id.textViewUsername);

        textViewUserId.setText(user.getUser_id().substring(0,13));
        textViewUsername.setText(user.getUsername());
        textViewRewardPoints.setText(String.valueOf(user.getUserRewardPoints()));
        textViewEmail.setText(user.getEmail());

        return  view;
    }

}
