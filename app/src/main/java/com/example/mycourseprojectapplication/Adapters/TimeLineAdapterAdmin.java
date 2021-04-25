package com.example.mycourseprojectapplication.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycourseprojectapplication.Activities.AdminAddItemStatusActivity;
import com.example.mycourseprojectapplication.Models.Orders;
import com.example.mycourseprojectapplication.Models.Products;
import com.example.mycourseprojectapplication.Models.TimeLineModel;
import com.example.mycourseprojectapplication.Models.TimelineStatus;
import com.example.mycourseprojectapplication.R;
import com.example.mycourseprojectapplication.Utilities.VectorDrawableUtils;
import com.github.vipulasri.timelineview.TimelineView;

import java.util.List;

public class TimeLineAdapterAdmin extends RecyclerView.Adapter<TimeLineAdapterAdmin.TimelineViewHolder> {

    private List<TimeLineModel> timeLine;
    private Context context;
    private String trackingId;
    private Orders order;
    private Products product;
    private String user_notification_key;
    private String user_id;
    private String user_email;
    private AppCompatActivity appCompatActivity;

    public TimeLineAdapterAdmin(List<TimeLineModel> timeLine, Context context, String trackingId, Orders order, Products product, String user_notification_key, String user_id, String user_email) {
        this.timeLine = timeLine;
        this.context = context;
        this.trackingId = trackingId;
        this.order = order;
        this.product = product;
        this.user_notification_key = user_notification_key;
        this.user_id = user_id;
        this.user_email = user_email;
    }

    @Override
    public int getItemCount() {
        return timeLine.size();
    }

    @NonNull
    @Override
    public TimelineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timeline_admin,parent,false);
        context = view.getContext();
        appCompatActivity = (AppCompatActivity) view.getContext();
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
        if (!TextUtils.isEmpty(timeLineModel.getDate()+"")) {
            holder.date.setVisibility(View.VISIBLE);
            holder.date.setText(timeLineModel.getDate() + " / "+timeLineModel.getTime());
        } else
        {
            holder.date.setVisibility(View.GONE);
        }
        if (timeLineModel.status == TimelineStatus.ACTIVE)
        {
            if (!TextUtils.isEmpty(timeLineModel.getDate()+"")) {
                holder.date.setVisibility(View.VISIBLE);
                holder.date.setText(timeLineModel.getDate() + " / "+timeLineModel.getTime());
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
                holder.date.setText(timeLineModel.getDate() + " / "+timeLineModel.getTime());
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

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AdminAddItemStatusActivity.class);
                intent.putExtra("model",timeLineModel);
                intent.putExtra("trackingid",trackingId);
                intent.putExtra("user_id",user_id);
                intent.putExtra("user_email",user_email);
                intent.putExtra("token",user_notification_key);
                intent.putExtra("product",product);
                intent.putExtra("order",order);
                context.startActivity(intent);
              //  appCompatActivity.finish();


            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position, getItemCount());
    }

    public class TimelineViewHolder extends RecyclerView.ViewHolder
    {
        private AppCompatTextView date,message,location;
        private TimelineView timelineView;
        private Button button;
        public TimelineViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.text_timeline_date);
            message = itemView.findViewById(R.id.text_timeline_title);
            timelineView = itemView.findViewById(R.id.timeline);
            location = itemView.findViewById(R.id.textViewTimeLineLocation);
            button = itemView.findViewById(R.id.buttonUpdateStatus);
        }
    }

    private void setMarker(TimeLineAdapterAdmin.TimelineViewHolder holder, int drawableResId, int colorFilter) {
        holder.timelineView.setMarker(VectorDrawableUtils.getDrawable(holder.itemView.getContext(), drawableResId, ContextCompat.getColor(holder.itemView.getContext(), colorFilter)));
    }
}
