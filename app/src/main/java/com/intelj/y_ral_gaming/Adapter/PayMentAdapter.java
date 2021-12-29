package com.intelj.y_ral_gaming.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.intelj.y_ral_gaming.R;
import com.intelj.y_ral_gaming.Utils.AppConstant;
import com.intelj.y_ral_gaming.model.PaymentHistoryModel;

import org.json.JSONArray;

import java.util.ArrayList;

public class PayMentAdapter extends RecyclerView.Adapter<PayMentAdapter.MyViewHolder> {
    Context mContext;
    AppConstant appConstant;
    ArrayList<PaymentHistoryModel> paymentHistoryModel;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView pinfo, transaction, amount, ticket_id;
        ImageView imgs;

        public MyViewHolder(View view) {
            super(view);
            pinfo = view.findViewById(R.id.pinfo);
            transaction = view.findViewById(R.id.transaction);
            amount = view.findViewById(R.id.amount);
            imgs = view.findViewById(R.id.imgs);
            ticket_id = view.findViewById(R.id.ticket_id);
        }
    }

    public PayMentAdapter(Context mContext, ArrayList<PaymentHistoryModel> paymentHistoryModel) {
        this.mContext = mContext;
        this.paymentHistoryModel = paymentHistoryModel;
        appConstant = new AppConstant(mContext);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.payment_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.pinfo.setText(paymentHistoryModel.get(position).getDate());
            holder.transaction.append(paymentHistoryModel.get(position).getTransaction());
            holder.amount.setText(paymentHistoryModel.get(position).getAmount());
            holder.ticket_id.append(paymentHistoryModel.get(position).getTicket_id());
            Glide.with(mContext)
                .load(paymentHistoryModel.get(position).getImg_url())
                .placeholder(R.drawable.game_avatar)
                    .circleCrop()
                .into(holder.imgs);
    }

    @Override
    public int getItemCount() {
        return paymentHistoryModel.size();
    }
}
