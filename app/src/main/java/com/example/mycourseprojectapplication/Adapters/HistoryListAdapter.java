package com.example.mycourseprojectapplication.Adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycourseprojectapplication.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static android.content.Context.CLIPBOARD_SERVICE;

public class HistoryListAdapter extends RecyclerView.Adapter<HistoryListAdapter.HistoryViewHolder> {

    private final ArrayList<String> rewardsPrice;
    private final ArrayList<String> rewardCodes;
    private final ArrayList<Long> redemptionDate;
    private Context context;

    public HistoryListAdapter(ArrayList<String> rewardsPrice, ArrayList<String> rewardCodes, ArrayList<Long> redemptionDate) {
        this.rewardsPrice = rewardsPrice;
        this.rewardCodes = rewardCodes;
        this.redemptionDate = redemptionDate;
    }


    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_list_item,parent,false);
        context = view.getContext();
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        String price = rewardsPrice.get(position);
        String codes = rewardCodes.get(position);
        long date = redemptionDate.get(position);
        holder.textViewAmount.setText(price);
        holder.textViewCode.setText(codes);
        String simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy hh:mm").format(date);
        holder.textViewDate.setText(simpleDateFormat);

        holder.textViewCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ClipboardManager manager = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("text", codes);
                manager.setPrimaryClip(clipData);
                Toast.makeText(context, "Code Copied", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return rewardCodes.size();
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder
    {
        public TextView textViewAmount,textViewCode,textViewDate;
        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewAmount = itemView.findViewById(R.id.textViewDiscountCodeAmount);
            textViewCode = itemView.findViewById(R.id.textViewRedemptionCode);
            textViewDate = itemView.findViewById(R.id.textViewRedemptionDate);
        }
    }
}
