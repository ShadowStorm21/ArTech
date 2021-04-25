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
 * Use the {@link RotateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RotateFragment extends Fragment {


    private ArrayList<Videos> videosArrayList;
    private VideoAdapter videoAdapter;
    private RecyclerView mRecyclerView;


    public RotateFragment() {
        // Required empty public constructor
    }

    public static RotateFragment newInstance(String param1, String param2) {
        RotateFragment fragment = new RotateFragment();
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
        View view = inflater.inflate(R.layout.fragment_rotate, container, false);
        mRecyclerView = view.findViewById(R.id.rotateRecycler);
        initRecyclerView();
        return view;
    }

    private void getVideos()
    {
        videosArrayList.clear();
        Videos video = new Videos(R.raw.rotatebygest, "You can rotate models using twist gestures, this offers better control over the rotating degree");
        Videos video1 = new Videos(R.raw.rotatebybutton, "You can rotate models using rotate button");
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