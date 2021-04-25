package com.example.mycourseprojectapplication.Adapters;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycourseprojectapplication.Models.TimeLineModel;
import com.example.mycourseprojectapplication.Models.TimelineStatus;
import com.example.mycourseprojectapplication.R;
import com.example.mycourseprojectapplication.Utilities.VectorDrawableUtils;
import com.github.vipulasri.timelineview.TimelineView;

import java.util.List;

public class TimeLinesAdapter extends RecyclerView.Adapter<TimeLinesAdapter.TimelineViewHolder> {
    private List<TimeLineModel> timeLine;

    public TimeLinesAdapter(List<TimeLineModel> timeLine) {
        this.timeLine = timeLine;
    }

    @NonNull
    @Override
    public TimelineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_time_line,parent,false);
        return new TimelineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimelineViewHolder holder, int position) {

        TimeLineModel timeLineModel = timeLine.get(position);
        if (timeLineModel.status == TimelineStatus.ACTIVE)
        {
            setMarker(holder, R.drawable.active_marker, R.color.colorPrimary);
        }
        else if(timeLineModel.status == TimelineStatus.INACTIVE)
        {
            setMarker(holder, R.drawable.in_active_marker, R.color.colorPrimary);
        }
        else
        {
            setMarker(holder, R.drawable.marker, R.color.colorPrimary);
        }


        if (timeLineModel.status == TimelineStatus.ACTIVE)
        {
            if (!TextUtils.isEmpty(timeLineModel.getDate()+"")) {
                holder.date.setVisibility(View.VISIBLE);
                holder.date.setText(timeLineModel.getDate() + " / " + timeLineModel.getTime());
            } else
            {
                holder.date.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(timeLineModel.getLocation()+"")) {
                holder.location.setVisibility(View.VISIBLE);
                holder.location.setText(timeLineModel.getLocation());
            } else
            {
                holder.location.setVisibility(View.GONE);
            }
        }
        else if(timeLineModel.status == TimelineStatus.INACTIVE)
        {
            if (!TextUtils.isEmpty(timeLineModel.getDate()+"")) {
                holder.date.setVisibility(View.VISIBLE);
                holder.date.setText("Processing");
            } else
            {
                holder.date.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(timeLineModel.getLocation()+"")) {
                holder.location.setVisibility(View.VISIBLE);
                holder.location.setText("NA");
            } else
            {
                holder.location.setVisibility(View.GONE);
            }
        }
        else
        {
            if (!TextUtils.isEmpty(timeLineModel.getDate()+"")) {
                holder.date.setVisibility(View.VISIBLE);
                holder.date.setText(timeLineModel.getDate() + " / " + timeLineModel.getTime());
            } else
            {
                holder.date.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(timeLineModel.getLocation()+"")) {
                holder.location.setVisibility(View.VISIBLE);
                holder.location.setText(timeLineModel.getLocation());
            } else
            {
                holder.location.setVisibility(View.GONE);
            }
        }

        holder.timelineView.setEndLineColor(Color.parseColor("#673AB7"),getItemViewType(position));
        holder.timelineView.setStartLineColor(Color.parseColor("#673AB7"),getItemViewType(position));
        holder.message.setText(timeLineModel.getMessage());
    }

    @Override
    public int getItemCount() {
        return timeLine.size();
    }

    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position, getItemCount());
    }

    public class TimelineViewHolder extends RecyclerView.ViewHolder
    {
        private AppCompatTextView date,message,location;
        private TimelineView timelineView;
        public TimelineViewHolder(@NonNull View itemView) {
            super(itemView);
             date = itemView.findViewById(R.id.text_timeline_date);
             message = itemView.findViewById(R.id.text_timeline_title);
             timelineView = itemView.findViewById(R.id.timeline);
             location = itemView.findViewById(R.id.textViewTimeLineLocation);

        }
    }

    private void setMarker(TimelineViewHolder holder, int drawableResId, int colorFilter) {
        holder.timelineView.setMarker(VectorDrawableUtils.getDrawable(holder.itemView.getContext(), drawableResId, ContextCompat.getColor(holder.itemView.getContext(), colorFilter)));
    }

}
