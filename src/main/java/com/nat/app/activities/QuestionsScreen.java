package com.nat.app.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.nat.app.R;
import com.nat.app.adapters.CustomExpandableListAdapter;
import com.nat.app.adapters.ExpandableListDataPump;
import com.nat.app.databases.FirebaseDatabaseSingleton;
import com.nat.app.models.QuestionModel;
import com.nat.app.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QuestionsScreen extends AppCompatActivity {

    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    HashMap<String, List<String>> expandableListDetail;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_questions_screen);
        backButton = findViewById(R.id.backBtn);

        backButton.setOnClickListener(v -> {
            finish();
        });
        expandableListView = findViewById(R.id.expandableListView);

        expandableListDetail = new HashMap<>();
        FirebaseDatabaseSingleton.getInstance().getDatabaseReference().child(Constants.QUESTIONS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot snapshotI : snapshot.getChildren()) {
                    List<QuestionModel> questionModels = new ArrayList<>();
                    QuestionModel model = snapshotI.getValue(QuestionModel.class);

                    if (model != null) {
                        questionModels.add(model);
                        List<String> cricket = new ArrayList<String>();
                        cricket.add(model.getAnswer());
                        expandableListDetail.put(model.getQuestion(), cricket);
                    }
                }

                expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
                expandableListAdapter = new CustomExpandableListAdapter(getApplicationContext(), expandableListTitle, expandableListDetail);
                expandableListView.setAdapter(expandableListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}