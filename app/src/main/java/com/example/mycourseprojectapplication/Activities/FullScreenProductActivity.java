package com.example.mycourseprojectapplication.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.mycourseprojectapplication.R;
import com.example.mycourseprojectapplication.Utilities.ConnectionDetector;
import com.example.mycourseprojectapplication.Utilities.PinchZoomImageView;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.ArrayList;

public class FullScreenProductActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_product);

        new ConnectionDetector(this); // check if user has internet connection or not
        DotsIndicator dotsIndicator = (DotsIndicator) findViewById(R.id.dotsIndicator);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // setup the action toolbar with activity title and back icon functionality
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        setTitle("Images");
        ArrayList<String> imagesUrls = (ArrayList<String>) getIntent().getStringArrayListExtra("images");
        FullScreenImagesAdapter myViewPagerAdapter = new FullScreenImagesAdapter(this, (ArrayList<String>) imagesUrls);
        ViewPager viewPager = findViewById(R.id.pager);
        viewPager.setAdapter(myViewPagerAdapter);
        dotsIndicator.setViewPager(viewPager);
    }


    public class FullScreenImagesAdapter extends PagerAdapter {

        private Activity activity;
        private ArrayList<String> imageUrl;
        private LayoutInflater inflater;


        // constructor
        public FullScreenImagesAdapter(Activity activity, ArrayList<String> imageUrl) {
            this.activity = activity;
            this.imageUrl = imageUrl;
        }

        @Override
        public int getCount() {
            return imageUrl.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==((ImageView)object); // view is an imageview object
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            PinchZoomImageView imageView = new PinchZoomImageView(activity); // create an image view
            imageView.findViewById(R.id.imageView6); // initialize our image view inside the viewpager
            container.addView(imageView); // add the image view to the viewpager
            Glide.with(activity).load(imageUrl.get(position)).fitCenter().diskCacheStrategy(DiskCacheStrategy.DATA).placeholder(R.drawable.ic_baseline_cached_24).into(imageView); // use glide to load the uri links into the image view
            return imageView; // return the imageview
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View)object; // destroy the view when it is not shown
            container.removeView(view);
            view = null;

        }
    }
}