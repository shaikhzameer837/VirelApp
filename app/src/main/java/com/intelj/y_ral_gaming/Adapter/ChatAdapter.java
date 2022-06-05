package com.intelj.y_ral_gaming.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.intelj.y_ral_gaming.R;
import com.intelj.y_ral_gaming.Utils.AppConstant;
import com.intelj.y_ral_gaming.db.AppDataBase;
import com.intelj.y_ral_gaming.db.Chat;

import java.io.File;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    private List<Chat> allChat;
    private Context mContext;
    private String myId;

    public void setAllChat(AppDataBase appDataBase) {
        this.allChat = appDataBase.chatDao().getAllChat();
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, time;
        LinearLayout out_layer, chat_layout;
        ImageView img;

        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            time = view.findViewById(R.id.time);
            out_layer = view.findViewById(R.id.out_layer);
            chat_layout = view.findViewById(R.id.chat_layout);
            img = view.findViewById(R.id.image);
        }
    }

    public ChatAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_row, parent, false);
        myId = new AppConstant(mContext).getId();
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Chat chat = allChat.get(position);
        holder.out_layer.setGravity(chat.owner.equals(myId) ? Gravity.RIGHT : Gravity.LEFT);
        holder.chat_layout.setBackgroundResource(chat.owner.equals(myId) ? R.drawable.chat_right : R.drawable.left_message);
        holder.title.setText(chat.messages);
        holder.title.setTextColor(chat.owner.equals(myId) ? Color.BLACK : Color.WHITE);
        holder.time.setTextColor(chat.owner.equals(myId) ? Color.parseColor("#666666") : Color.parseColor("#ffffff"));
        holder.time.setText(AppConstant.getTimeAgo(Long.parseLong(chat.times)));
        holder.img.setVisibility(chat.subject == 1 ? View.VISIBLE : View.GONE);
        holder.title.setVisibility(chat.subject == 0 ? View.VISIBLE : View.GONE);
        if (chat.subject == 1) {
            if(new File(chat.messages).exists()){
                Glide.with(mContext).load(chat.messages).into(holder.img);
            }else {
                byte[] decodedString = Base64.decode(chat.blurImg
                        , Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                holder.img.setImageBitmap(decodedByte);
            }
        }
    }

    @Override
    public int getItemCount() {
        return allChat.size();
    }
}