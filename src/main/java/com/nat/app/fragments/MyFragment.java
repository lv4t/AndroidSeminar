package com.nat.app.fragments;

import static androidx.browser.customtabs.CustomTabsClient.getPackageName;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.nat.app.R;
import com.nat.app.activities.AuthenActivity;
import com.nat.app.activities.NewFeedDetailScreen;
import com.nat.app.activities.QuestionsScreen;
import com.nat.app.adapters.CustomExpandableListAdapter;
import com.nat.app.databases.FirebaseDatabaseSingleton;
import com.nat.app.models.NewsFeedItem;
import com.nat.app.models.QuestionModel;
import com.nat.app.utils.Constants;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MyFragment extends Fragment {

    ImageView imageView;
    TextView textView;
    Button doc, sotaycudan, question, info, call, out;

    public MyFragment() {
        //
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.me_fragment, container, false);
        imageView = view.findViewById(R.id.img);
        textView = view.findViewById(R.id.name);
        doc = view.findViewById(R.id.doc);
        call = view.findViewById(R.id.call);
        sotaycudan = view.findViewById(R.id.sotaycudan);
        out = view.findViewById(R.id.out);
        info = view.findViewById(R.id.info);
        question = view.findViewById(R.id.question);
        Picasso.with(getContext()).load("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQgpBUWmQv0ebdsvI8drRTEKVn-PK1Xwbex7Nt5ABEXpQ&s").into(imageView);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.d("====", currentUser.getEmail());
        textView.setText(currentUser.getEmail());
        doc.setOnClickListener(v -> {
            openPDF(getContext(), R.raw.tailieu);
        });
        sotaycudan.setOnClickListener(v -> openPDF(getContext(), R.raw.jamila));
        question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), QuestionsScreen.class);
                startActivity(intent);
            }
        });
        info.setOnClickListener(v -> {
            FirebaseDatabaseSingleton.getInstance().getDatabaseReference().child("info").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    List<NewsFeedItem> questionModels = new ArrayList<>();
                    for (DataSnapshot snapshotI : snapshot.getChildren()) {
                        NewsFeedItem model = snapshotI.getValue(NewsFeedItem.class);

                        if (model != null) {
                            questionModels.add(model);
                        }
                    }
                    Intent intent = new Intent(getContext(), NewFeedDetailScreen.class);
                    NewsFeedItem newsFeedItem = questionModels.get(0);
                    intent.putExtra(Constants.NEW_FEED_DETAIL, new Gson().toJson(newsFeedItem));
                    intent.putExtra("header", "Thông tin toà nhà");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getContext().startActivity(intent);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });



        });
        call.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", "+84944988947", null));
            startActivity(intent);
        });
        out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), AuthenActivity.class));
            }
        });
        return view;
    }

    public static void openPDF(Context context, int resourceId) {
        // Lấy InputStream từ tệp PDF trong thư mục res/raw
        InputStream inputStream = context.getResources().openRawResource(resourceId);

        // Tạo một tệp tạm thời trong bộ nhớ để lưu trữ nội dung của tệp PDF
        File tempFile = createTempFile(context, inputStream);

        // Tạo Uri cho tệp tạm thời
        assert tempFile != null;
        Uri tempUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", tempFile);

        // Tạo Intent để mở tệp PDF bằng ứng dụng xem PDF mặc định
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(tempUri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Kiểm tra xem có ứng dụng nào có thể xử lý Intent này không
//        if (intent.resolveActivity(context.getPackageManager()) != null) {
        context.startActivity(intent);
//        } else {
        // Xử lý khi không có ứng dụng nào có thể mở tệp PDF
//        }
    }

    private static File createTempFile(Context context, InputStream inputStream) {
        try {
            // Tạo tệp tạm thời
            File tempFile = File.createTempFile("temp_pdf", ".pdf", context.getCacheDir());

            // Copy nội dung từ InputStream vào tệp tạm thời
            OutputStream outputStream = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                outputStream = Files.newOutputStream(tempFile.toPath());
            }
            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();

            return tempFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
