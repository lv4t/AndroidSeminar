package com.nat.app.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nat.app.R;
import com.nat.app.adapters.NewsFeedAdapter;
import com.nat.app.interfaces.IFeedActions;
import com.nat.app.models.NewsFeedItem;
import com.nat.app.presenters.NewFeedPresenter;
import com.nat.app.utils.Constants;
import com.nat.app.utils.Sharef;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class NewsFeedFragment extends Fragment implements IFeedActions.View {
    private ArrayList<NewsFeedItem> newsFeedItems;
    private RecyclerView recyclerView;
    private NewFeedPresenter newFeedPresenter;
    private NewsFeedAdapter newsFeedAdapter;
    private ActivityResultLauncher<Intent> launcher;

    public NewsFeedFragment() {
        //
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.news_feed_fragment, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        newsFeedItems = new ArrayList<>();
        newFeedPresenter = new NewFeedPresenter(this);
        // Get data
        newFeedPresenter.onSubcribeNewsFeedDatabase();
        // Register data changes
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                recyclerView.invalidate();
                assert result.getData() != null;
                newsFeedAdapter.notifyItemChanged(Integer.parseInt(Objects.requireNonNull(result.getData().getStringExtra(Constants.NEW_FEED_DETAIL))));
            }
        });
        return view;
    }

    @Override
    public void onNewsFeedDataLoaded(List<NewsFeedItem> newsFeeds) {
        newsFeedItems.clear();
        newsFeeds.forEach(item -> {
            boolean isSeen = new Sharef(requireActivity().getApplicationContext()).isIDRead(item.getId());
            item.setSeen(isSeen);
        });
        newsFeedItems.addAll(newsFeeds);
        newsFeedAdapter = new NewsFeedAdapter(requireActivity().getApplicationContext(), newsFeedItems);
        recyclerView.setAdapter(newsFeedAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.invalidate();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onUpdateMarkRead(int position) {
        newsFeedItems.forEach(item -> {
            if (Integer.parseInt(item.getId()) == position) {
                item.setSeen(true);
            }
        });
        newsFeedAdapter.notifyDataSetChanged();
        recyclerView.invalidate();
    }
}
