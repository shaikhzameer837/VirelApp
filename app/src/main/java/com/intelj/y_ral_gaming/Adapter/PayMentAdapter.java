package com.intelj.y_ral_gaming.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
        holder.amount.setText("\u20B9 " + paymentHistoryModel.get(position).getAmount());
        if(paymentHistoryModel.get(position).getType() ==3){
            holder.amount.
                    setTextColor(Color.BLACK);
            Drawable img = mContext.getResources().getDrawable(R.drawable.hold);
            holder.amount.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
            holder.ticket_id.append(paymentHistoryModel.get(position).getTicket_id());
        }else {
            holder.amount.
                    setTextColor(paymentHistoryModel.get(position).getType() == 1 ?
                            Color.parseColor("#ff0000") : Color.parseColor("#097969"));
            Drawable img = paymentHistoryModel.get(position).getType() == 1 ? mContext.getResources().getDrawable(R.drawable.debit) :
                    mContext.getResources().getDrawable(R.drawable.credit);
            holder.amount.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
            holder.ticket_id.append(paymentHistoryModel.get(position).getTicket_id());
        }
        Glide.with(mContext)
                .load(paymentHistoryModel.get(position).getImg_url())
                .placeholder(R.mipmap.ic_launcher)
                .into(holder.imgs);
        holder.imgs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView image = new ImageView(mContext);
                // image.setImageResource(R.drawable.YOUR_IMAGE_ID);
                Glide.with(mContext)
                        .load(paymentHistoryModel.get(position).getImg_url())
                        .placeholder(R.mipmap.ic_launcher)
                        .into(image);
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(mContext).
                                setMessage("").
                                setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).
                                setView(image);
                builder.create().show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return paymentHistoryModel.size();
    }
}
