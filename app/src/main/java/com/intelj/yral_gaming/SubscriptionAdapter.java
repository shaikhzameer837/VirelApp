package com.intelj.yral_gaming;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.intelj.yral_gaming.Utils.AppConstant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubscriptionAdapter extends RecyclerView.Adapter<SubscriptionAdapter.MyViewHolder> {

    private List<SubscriptionModel> moviesList;
    Context mContext;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title,  genre,price,apply;

        public MyViewHolder(View view) {
            super(view);
            title =  view.findViewById(R.id.title);
            genre =  view.findViewById(R.id.genre);
            price =  view.findViewById(R.id.price);
            apply =  view.findViewById(R.id.apply);
          }
    }


    public SubscriptionAdapter(List<SubscriptionModel> moviesList,Context mContext) {
        this.moviesList = moviesList;
        this.mContext = mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        SubscriptionModel SUbscriptionModel = moviesList.get(position);
        holder.title.setText(SUbscriptionModel.getTitle());
        holder.price.setText(SUbscriptionModel.getYear());
        holder.genre.setText(SUbscriptionModel.getGenre());
        holder.apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSubscription(position);
            }
        });
    }
    private void updateSubscription(int position) {
        ProgressDialog dialog = new ProgressDialog(mContext);
        dialog.setMessage("Registering for App, please wait.");
        dialog.show();
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String url = "http://y-ral-gaming.com/admin/api/save_subscription.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", new AppConstant(mContext).getUserId());
                params.put("package_id", position + "");
                params.put("time_of_purchase", (System.currentTimeMillis() / 1000) + "");
                params.put("time_of_expired", ""+((System.currentTimeMillis() / 1000) + Integer.parseInt(moviesList.get(position).getTenure())));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };

        queue.add(stringRequest);
    }
    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}