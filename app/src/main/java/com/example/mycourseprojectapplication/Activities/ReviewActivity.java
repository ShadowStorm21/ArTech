package com.example.mycourseprojectapplication.Activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.mycourseprojectapplication.Models.Rating;
import com.example.mycourseprojectapplication.R;
import com.example.mycourseprojectapplication.Utilities.ConnectionDetector;
import com.example.mycourseprojectapplication.Utilities.UserSession;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.mycourseprojectapplication.Utilities.UserSession.KEY_USERNAME;
import static com.example.mycourseprojectapplication.Utilities.UserSession.KEY_USER_ID;

public class ReviewActivity extends AppCompatActivity {

    private EditText editTextComment;
    private FirebaseAuth mAuth;
    private float userRating;
    private ProgressBar progressBar;
    private ArrayList<Rating> ratingArrayList;                 // declare our variables
    private UserSession userSession;
    private  String product_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        new ConnectionDetector(this);          // check if user has Internet connection

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Write A Review");   // setup the action toolbar with activity title and back icon functionality
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {    // when back is clicked just finish the activity
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        ratingArrayList = new ArrayList<>();
        userSession = new UserSession(this);
        progressBar = findViewById(R.id.progressBarReview);
        RatingBar ratingBar = findViewById(R.id.ratingBarReview);  // initialize views and classes
        editTextComment = findViewById(R.id.editTextReview);
        Button buttonReview = findViewById(R.id.buttonPostReview);
        product_id = getIntent().getStringExtra("product_id"); // get the product id from the previous activity

        buttonReview.setScaleX(0);  // set the scale x and y for the button to 0
        buttonReview.setScaleY(0);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {  // when rating is changed
                if(fromUser)
                {
                    userRating = rating; // get rating
                    buttonReview.animate().scaleX(1).scaleY(1).setStartDelay(100); // show button with animation
                }
            }
        });

        buttonReview.setOnClickListener(new View.OnClickListener() {  // on click button post review
            @Override
            public void onClick(View v) {

                if(checkCommentField()) // check if comment field is empty
                {

                    if(checkForRedundantReviews()) // check if user already post a review on this product
                    {
                        insertNewReview(product_id,editTextComment.getText().toString(),userRating); // insert review to the database
                    }
                    else
                    {
                        Toast.makeText(ReviewActivity.this, "You already rated this product!", Toast.LENGTH_SHORT).show(); // show error message if redundant review
                        return;
                    }
                }
                else
                {
                    Toast.makeText(ReviewActivity.this, "Review should have description!", Toast.LENGTH_SHORT).show(); // show error message if description is empty
                    return;
                }
            }
        });

        AsyncTask.execute(new Runnable() { // get all reviews for this product in background using Async task
            @Override
            public void run() {
                getAllReviews(product_id);
            }
        });



    }

    private boolean checkCommentField() // method to check if comment field is empty or not
    {
        if(editTextComment.getText().toString().trim().isEmpty())
        {
            return false;
        }
      return true;
    }

    private void getAllReviews(String product_id) // method to get all reviews associated with product id
    {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Reviews");
        myRef.child(product_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot reviews : snapshot.getChildren()) // iterate over the children in the database
                {
                    ratingArrayList.add(reviews.getValue(Rating.class)); // add reviews to array list
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private boolean checkForRedundantReviews() // method to check for redundant reviews
    {
        for(int i = 0; i < ratingArrayList.size(); i++) // iterate over the array list
        {
            if(ratingArrayList.get(i).getUser_id().equals(mAuth.getCurrentUser().getUid())) // check if the rating list has the same id as the current user
            {
                return false;
            }

        }
        return true;
    }

    private void insertNewReview(String product_id,String comment,float userRatting) // method to insert new review to the database
    {
        progressBar.setVisibility(View.VISIBLE); // show progress bar
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Reviews");
        Rating rating = new Rating(userSession.getUserDetails().get(KEY_USER_ID),System.currentTimeMillis(),comment,userRatting,userSession.getUserDetails().get(KEY_USERNAME),product_id); // create a rating object with the required info
        String key = myRef.push().getKey();

        myRef.child(product_id).child(key).setValue(rating).addOnCompleteListener(new OnCompleteListener<Void>() {      // add the review to the database with the product id as the key
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(ReviewActivity.this, "Product rated successfully!", Toast.LENGTH_SHORT).show(); // show a success message on complete
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        new ConnectionDetector(this); // on Resume ( this method is fired before on create )  activity, check if user has connection or not
    }
}