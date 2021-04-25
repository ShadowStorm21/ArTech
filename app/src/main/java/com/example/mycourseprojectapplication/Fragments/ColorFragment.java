package com.example.mycourseprojectapplication.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycourseprojectapplication.Adapters.VideoAdapter;
import com.example.mycourseprojectapplication.Models.Videos;
import com.example.mycourseprojectapplication.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ColorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ColorFragment extends Fragment {


    private ArrayList<Videos> videosArrayList;
    private VideoAdapter videoAdapter;
    private RecyclerView mRecyclerView;

    public ColorFragment() {
        // Required empty public constructor
    }


    public static ColorFragment newInstance(String param1, String param2) {
        ColorFragment fragment = new ColorFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        videosArrayList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_color, container, false);
        mRecyclerView = view.findViewById(R.id.colorRecycler);
        initRecyclerView();
        return view;
    }

    private void getVideos() {
        videosArrayList.clear();
        Videos video = new Videos(R.raw.colors, "You can change the color of the model based on the available colors, note that not all models have the ability to change colors!");
        videosArrayList.add(video);
        videoAdapter.notifyDataSetChanged();
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        videoAdapter = new VideoAdapter(videosArrayList);
        mRecyclerView.setAdapter(videoAdapter);
        getVideos();
    }




}