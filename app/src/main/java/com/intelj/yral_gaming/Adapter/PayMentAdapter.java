package com.intelj.yral_gaming.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.intelj.yral_gaming.R;
import com.intelj.yral_gaming.Utils.AppConstant;
import com.intelj.yral_gaming.model.PaymentHistoryModel;

import org.json.JSONArray;

import java.util.ArrayList;

public class PayMentAdapter extends RecyclerView.Adapter<PayMentAdapter.MyViewHolder> {
    Context mContext;
    AppConstant appConstant;
    ArrayList<PaymentHistoryModel> paymentHistoryModel;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView pinfo, transaction, amount;
        ImageView reg;

        public MyViewHolder(View view) {
            super(view);
            pinfo = view.findViewById(R.id.pinfo);
            transaction = view.findViewById(R.id.transaction);
            amount = view.findViewById(R.id.amount);
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
            holder.pinfo.append(new AppConstant(mContext).getDateFromMilli(Long.parseLong(paymentHistoryModel.get(position).getDate()),"dd/MM/yyyy hh:mm:ss.SSS"));
            holder.transaction.append(paymentHistoryModel.get(position).getTransaction());
            holder.amount.append(paymentHistoryModel.get(position).getAmount());
    }

    @Override
    public int getItemCount() {
        return paymentHistoryModel.size();
    }
}
