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


public class ScaleFragment extends Fragment {


    private ArrayList<Videos> videosArrayList;
    private VideoAdapter videoAdapter;
    private RecyclerView mRecyclerView;

    public ScaleFragment() {
        // Required empty public constructor
    }

    public static ScaleFragment newInstance(String param1, String param2) {
        ScaleFragment fragment = new ScaleFragment();
        Bundle args = new Bundle();
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
        View view = inflater.inflate(R.layout.fragment_scale, container, false);
        mRecyclerView = view.findViewById(R.id.scaleRecycler);
        initRecyclerView();
        return view;
    }

    private void getVideos()
    {
        videosArrayList.clear();
        Videos video = new Videos(R.raw.scalebygest, "You can scale up/down models using pinching gestures, this offers better control over the scale amount!");
        Videos video1 = new Videos(R.raw.scalebybutton, "You can scale up/down models using scale up/down buttons");
        videosArrayList.add(video);
        videosArrayList.add(video1);
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