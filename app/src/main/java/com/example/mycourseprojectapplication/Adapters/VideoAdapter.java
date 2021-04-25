package com.example.mycourseprojectapplication.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycourseprojectapplication.Models.Videos;
import com.example.mycourseprojectapplication.R;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.RawResourceDataSource;

import java.util.ArrayList;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private ArrayList<Videos> videosArrayList;
    private Context context;

    public VideoAdapter(ArrayList<Videos> videosArrayList) {
        this.videosArrayList = videosArrayList;
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder
    {
        public TextView details,tipCount;
        public PlayerView playerView;


        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            details = itemView.findViewById(R.id.textViewArHelpDescription);
            playerView = itemView.findViewById(R.id.video_view);
            tipCount = itemView.findViewById(R.id.textViewtechTip);
        }


    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item,parent,false);
        context = view.getContext();
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {

        SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(context),
                new DefaultTrackSelector(), new DefaultLoadControl());

        String videoPath = RawResourceDataSource.buildRawResourceUri(videosArrayList.get(position).getUri()).toString();

        Uri uri = RawResourceDataSource.buildRawResourceUri(videosArrayList.get(position).getUri());

        ExtractorMediaSource audioSource = new ExtractorMediaSource(
                uri,
                new DefaultDataSourceFactory(context, "MyExoplayer"),
                new DefaultExtractorsFactory(),
                null,
                null
        );

        player.prepare(audioSource);
        holder.playerView.setPlayer(player);
        holder.playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.setPlayWhenReady(true);
            }
        });

        holder.details.setText(videosArrayList.get(position).getDetails());
        holder.tipCount.setText("Tech Tip #"+(position+1));


    }

    @Override
    public int getItemCount() {
        return videosArrayList.size();
    }
}
