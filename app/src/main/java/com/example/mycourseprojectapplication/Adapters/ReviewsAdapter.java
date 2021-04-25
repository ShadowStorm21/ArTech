package com.example.mycourseprojectapplication.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.mycourseprojectapplication.Models.Rating;
import com.example.mycourseprojectapplication.Models.Users;
import com.example.mycourseprojectapplication.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewHolder> {

    private final ArrayList<Rating> ratingArrayList;         // declare our variables
    private final Users user;
    private Context context;

    public ReviewsAdapter(ArrayList<Rating> ratingArrayList, Users user) {
        this.ratingArrayList = ratingArrayList;
        this.user = user;
    }

    @NonNull
    @Override
    public ReviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item,parent,false);    // create item view for the recycler view
        context = view.getContext();
        return new ReviewHolder(view);  // return view for each item
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewHolder holder, int position) {       // bind the data
        Rating review = ratingArrayList.get(position);         // get individual review
        holder.textViewAuthor.setText(review.getUsername());
        String simpleDateFormat = new SimpleDateFormat("dd MM yyyy").format(review.getDate());  // format the date from long to string format
        holder.textViewDate.setText(simpleDateFormat);                   // set the values of review
        holder.textViewComment.setText(review.getComment());
        holder.ratingBar.setRating(review.getRatting());
        if(user != null) {
            if (user.getUserPhotoUrl().equals("Default")) {
                holder.circleImageView.setImageResource(R.drawable.ic_outline_account_circle_24);
            } else {
                Glide.with(context).load(user.getUserPhotoUrl()).diskCacheStrategy(DiskCacheStrategy.DATA).centerCrop().into(holder.circleImageView);
            }
        }
        holder.circleImageView.setImageResource(R.drawable.ic_outline_account_circle_24);
    }

    @Override
    public int getItemCount() {
        return ratingArrayList.size();            // return the size of array list
    }

    public class ReviewHolder extends RecyclerView.ViewHolder     // view holder describes item view in the recycler view
    {
        public TextView textViewAuthor,textViewComment,textViewDate;        // declare our variables
        public RatingBar ratingBar;
        public CircleImageView circleImageView;
        public ReviewHolder(@NonNull View itemView) {
            super(itemView);
            textViewAuthor = itemView.findViewById(R.id.textViewCommentAuthor);
            textViewComment = itemView.findViewById(R.id.textViewComment);
            textViewDate = itemView.findViewById(R.id.textViewCommentDate);           // initialize our views
            ratingBar = itemView.findViewById(R.id.ratingBarReview);
            circleImageView = itemView.findViewById(R.id.imageViewReviewImage);
        }
    }

}
