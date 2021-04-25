package com.example.mycourseprojectapplication.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mycourseprojectapplication.Models.Orders;
import com.example.mycourseprojectapplication.R;

import java.text.SimpleDateFormat;
import java.util.List;

public class PDFOrders extends ArrayAdapter {

    private List<Orders> orders;
    private Context context;

    public PDFOrders(@NonNull Context context, int resource, List<Orders> ordersList) {
        super(context, resource, ordersList);
        this.context = context;
        this.orders = ordersList;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_pdf_order, null);

        Orders order = orders.get(position);
        TextView textViewOrderId,textViewCID,textViewFN,textViewLN,textViewOD,textViewOS,textViewPM,textViewTotal;

        textViewOrderId = view.findViewById(R.id.textViewEmail);
        textViewCID = view.findViewById(R.id.textViewOrderI);
        textViewFN = view.findViewById(R.id.textViewUserRewardPoints);
        textViewLN = view.findViewById(R.id.textViewLN);
        textViewOD = view.findViewById(R.id.textViewOD);
        textViewOS = view.findViewById(R.id.textViewOS);
        textViewPM = view.findViewById(R.id.textViewPM);
        textViewTotal = view.findViewById(R.id.textViewPDFtotal);

        textViewOrderId.setText(order.getOrder_id().substring(0,13));
        textViewCID.setText(order.getCustomer_id().substring(0,13));
        textViewFN.setText(order.getFirstName());
        textViewLN.setText(order.getLastName());
        String dateFormat = new SimpleDateFormat("dd MMMM yyyy").format(order.getOrder_date());
        textViewOD.setText(dateFormat);
        textViewOS.setText(order.getOrder_status());
        textViewPM.setText(order.getPayment_option());
        textViewTotal.setText("$"+order.getTotal());


        return  view;
    }
}
