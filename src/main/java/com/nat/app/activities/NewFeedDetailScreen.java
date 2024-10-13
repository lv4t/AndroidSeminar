package com.nat.app.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.nat.app.R;
import com.nat.app.interfaces.IFeedActions;
import com.nat.app.models.NewsFeedItem;
import com.nat.app.presenters.NewFeedPresenter;
import com.nat.app.utils.Constants;

public class NewFeedDetailScreen extends AppCompatActivity implements IFeedActions.DetailView {
    private NewFeedPresenter newFeedPresenter;
    private TextView content, headera;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_feed_detail_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        newFeedPresenter = new NewFeedPresenter(this);
        content = findViewById(R.id.newfeedContent);
        backButton = findViewById(R.id.backBtn);
        headera = findViewById(R.id.headera);
        backButton.setOnClickListener(v -> {
            finish();
        });

        // Get data from bundle
        Intent intent = getIntent();
        String headerStr = intent.getStringExtra("header");
        newFeedPresenter.onOpenDetailItem(getApplicationContext(), intent);
        if (headerStr != null ){
            headera.setText(headerStr);
        } else {
            headera.setText("Thông tin");
        }
    }

    @Override
    public void onDetailDataGetFromIntent(NewsFeedItem newsFeedItem) {
        content.setText(Html.fromHtml(newsFeedItem.getHtml(), Html.FROM_HTML_MODE_COMPACT));
        setResult(RESULT_OK, getIntent().putExtra(Constants.NEW_FEED_DETAIL, newsFeedItem.getId()));
        newFeedPresenter.onMarkReadNewFeed(getApplicationContext(), newsFeedItem);
    }
}