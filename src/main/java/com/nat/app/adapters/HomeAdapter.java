package com.nat.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.nat.app.R;
import com.nat.app.activities.NewFeedDetailScreen;
import com.nat.app.models.BangTinItem;
import com.nat.app.models.NewsFeedItem;
import com.nat.app.utils.Constants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {
    private List<BangTinItem> mData;
    Context context;

    public HomeAdapter(Context context, List<BangTinItem> data) {
        mData = data;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bangtinitem, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        BangTinItem item = mData.get(position);
        holder.textView.setText(item.getTitle());
        holder.createdAt.setText(item.getCreatedAt());
        Picasso.with(context).load(item.getImg()).into(holder.view);
        holder.manager.setOnClickListener(v -> {
            Intent intent = new Intent(context, NewFeedDetailScreen.class);
            NewsFeedItem newsFeedItem = new NewsFeedItem();
            newsFeedItem.setAttachment(new ArrayList<>());
            newsFeedItem.setHtml(item.getContent());
            newsFeedItem.setTitle("");
            intent.putExtra("header", item.getTitle());
            intent.putExtra(Constants.NEW_FEED_DETAIL, new Gson().toJson(newsFeedItem));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView, createdAt;
        ImageView view;
        LinearLayout manager;

        public MyViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.title);
            createdAt = itemView.findViewById(R.id.createdAt);
            view = itemView.findViewById(R.id.img);
            manager = itemView.findViewById(R.id.manager);
        }
    }
}