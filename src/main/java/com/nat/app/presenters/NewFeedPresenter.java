package com.nat.app.presenters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.nat.app.R;
import com.nat.app.databases.FirebaseDatabaseSingleton;
import com.nat.app.interfaces.IFeedActions;
import com.nat.app.models.NewsFeedItem;
import com.nat.app.utils.Constants;
import com.nat.app.utils.Sharef;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class NewFeedPresenter implements IFeedActions.Presenter {
    enum PRESENTER_TYPE {
        DETAIL_VIEW,
        VIEW_HOLDER,
        VIEW,
    }

    private Sharef preferenceManager;
    private IFeedActions.View view;
    private IFeedActions.ViewHolder viewHolder;
    private IFeedActions.DetailView detailView;

    public NewFeedPresenter(IFeedActions.View view, IFeedActions.DetailView detailView) {
        this.view = view;
        this.detailView = detailView;
    }

    public NewFeedPresenter(IFeedActions.DetailView detailView) {
        this.detailView = detailView;
    }

    public NewFeedPresenter(IFeedActions.View view, IFeedActions.ViewHolder viewHolder) {
        this.view = view;
        this.viewHolder = viewHolder;
    }

    public NewFeedPresenter(IFeedActions.View authActionView) {
        this.view = authActionView;
    }

    public NewFeedPresenter(IFeedActions.ViewHolder viewHolder) {
        this.viewHolder = viewHolder;
    }

    @SuppressLint("CheckResult")
    @Override
    public void onSubcribeNewsFeedDatabase() {
        Observable.create((ObservableOnSubscribe<DataSnapshot>) emitter -> {
                    ValueEventListener valueEventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            emitter.onNext(dataSnapshot);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            emitter.onError(databaseError.toException());
                        }
                    };
                    FirebaseDatabaseSingleton.getInstance().getDatabaseReference().child(Constants.NEW_FEEDS).addValueEventListener(valueEventListener);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(dataSnapshot -> {
                    List<NewsFeedItem> newsFeedItems = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        NewsFeedItem model = snapshot.getValue(NewsFeedItem.class);
                        if (model != null) {
                            newsFeedItems.add(model);
                        }
                    }
                    view.onNewsFeedDataLoaded(newsFeedItems);
                }, Throwable::printStackTrace);
    }

    @Override
    public void onClickNewFeedItem(Context context, NewsFeedItem newsFeedItem, int position) {
        onMarkReadNewFeed(context, newsFeedItem);
        viewHolder.onClickedNewFeedItem(context, newsFeedItem, position);
        if (view != null) {
            view.onUpdateMarkRead(position);
        }
    }

    @Override
    public void onOpenDetailItem(Context context, Intent intent) {
        NewsFeedItem newsFeedItem = new Gson().fromJson(intent.getStringExtra(Constants.NEW_FEED_DETAIL), NewsFeedItem.class);
        detailView.onDetailDataGetFromIntent(newsFeedItem);
    }

    @Override
    public void onMarkReadNewFeed(Context context, NewsFeedItem newsFeedItem) {
        preferenceManager = new Sharef(context);
        String id = newsFeedItem.getId();
        if (!(preferenceManager.isIDRead(id))) {
            preferenceManager.addReadID(newsFeedItem.getId());
        }
        if (view != null) {
            view.onUpdateMarkRead(Integer.parseInt(newsFeedItem.getId()));
        }
    }
}
