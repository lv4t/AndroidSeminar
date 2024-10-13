package com.nat.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.nat.app.R;
import com.nat.app.activities.NewFeedDetailScreen;
import com.nat.app.interfaces.IFeedActions;
import com.nat.app.models.NewsFeedItem;
import com.nat.app.presenters.NewFeedPresenter;
import com.nat.app.utils.Constants;
import com.nat.app.utils.TimeUtils;

import java.util.ArrayList;

public class NewsFeedAdapter extends
        RecyclerView.Adapter<NewsFeedAdapter.ViewHolder> implements IFeedActions.ViewHolder {
    private final Context mContext;
    private final ArrayList<NewsFeedItem> newsFeedItems;

    private final NewFeedPresenter newFeedPresenter = new NewFeedPresenter(this);

    public NewsFeedAdapter(Context mContext, ArrayList<NewsFeedItem> newsFeedItems) {
        this.mContext = mContext;
        this.newsFeedItems = newsFeedItems;
    }

    @androidx.annotation.NonNull
    @Override
    public ViewHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View newFeed = inflater.inflate(R.layout.newsfeed_item_layout, viewGroup, false);
        return new ViewHolder(newFeed);
    }

    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull ViewHolder viewHolder, int i) {
        NewsFeedItem newFeedItem = newsFeedItems.get(i);
        viewHolder.mTitle.setText(newFeedItem.getTitle());
        viewHolder.mTime.setText(TimeUtils.millisecondsToDateTime(newFeedItem.getCreatedAt()));
        viewHolder.mContent.setText(newFeedItem.getContent());
        if (newFeedItem.isSeen()) {
            viewHolder.cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.white));
        }
        viewHolder.cardView.setOnClickListener(v -> {
            newFeedPresenter.onClickNewFeedItem(v.getContext(), newFeedItem, i);
            newFeedPresenter.onMarkReadNewFeed(v.getContext(), newFeedItem);
        });
    }

    @Override
    public int getItemCount() {
        return newsFeedItems.size();
    }

    @Override
    public void onClickedNewFeedItem(Context context, NewsFeedItem newsFeedItem, int position) {
        Intent intent = new Intent(context, NewFeedDetailScreen.class);
        intent.putExtra(Constants.NEW_FEED_DETAIL, new Gson().toJson(newsFeedItem));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView mTitle;
        private final TextView mTime;
        private final TextView mContent;
        private final CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.title);
            mTime = itemView.findViewById(R.id.time);
            mContent = itemView.findViewById(R.id.content);
            cardView = itemView.findViewById(R.id.newFeedCardView);
        }
    }
}
