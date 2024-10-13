package com.nat.app.interfaces;

import android.content.Context;
import android.content.Intent;

import com.nat.app.models.NewsFeedItem;

import java.util.List;

public interface IFeedActions {
    interface DetailView {
        void onDetailDataGetFromIntent(NewsFeedItem newsFeedItem);
    }

    interface View {
        void onNewsFeedDataLoaded(List<NewsFeedItem> newsFeedItems);
        void onUpdateMarkRead(int position);
    }

    interface ViewHolder {
        void onClickedNewFeedItem(Context context, NewsFeedItem newsFeedItem, int position);
    }

    interface Presenter {
        void onSubcribeNewsFeedDatabase();

        void onClickNewFeedItem(Context context, NewsFeedItem newsFeedItem, int position);

        void onOpenDetailItem(Context context, Intent intent);
        void onMarkReadNewFeed(Context context, NewsFeedItem newsFeedItem);
    }
}
