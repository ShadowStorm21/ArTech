package com.example.mycourseprojectapplication.Fragments;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.mycourseprojectapplication.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductImageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductImageFragment extends DialogFragment {

    public static final String IMAGE_PATH = "PATH";
    private ImageView imageView;



    public ProductImageFragment() {
        // Required empty public constructor
    }


    public static ProductImageFragment newInstance(String param1) {
        ProductImageFragment fragment = new ProductImageFragment();
        Bundle args = new Bundle();
        args.putSerializable(IMAGE_PATH,param1);
        fragment.setArguments(args);
        fragment.setStyle(DialogFragment.STYLE_NO_TITLE,0);
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
        imageView = new ImageView(getActivity());
        String path = (String) getArguments().getSerializable(IMAGE_PATH);
        Glide.with(getActivity()).load(path).into(imageView);
        return imageView;
    }

    @Override
    public int getTheme() {
        return R.style.fullscreen;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cleanImageView(imageView);
    }

    public static void cleanImageView(ImageView imageView)
    {
        if(!(imageView.getDrawable() instanceof BitmapDrawable))
        {
            return;
        }
        BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
        bitmapDrawable.getBitmap().recycle();
        imageView.setImageDrawable(null);
    }
}