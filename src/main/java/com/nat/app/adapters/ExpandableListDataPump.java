package com.nat.app.adapters;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.nat.app.databases.FirebaseDatabaseSingleton;
import com.nat.app.models.QuestionModel;
import com.nat.app.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListDataPump {
    public static HashMap<String, List<String>> getData() {
        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();


        FirebaseDatabaseSingleton.getInstance().getDatabaseReference().child(Constants.QUESTIONS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<QuestionModel> questionModels = new ArrayList<>();
                for (DataSnapshot snapshotI : snapshot.getChildren()) {
                    QuestionModel model = snapshotI.getValue(QuestionModel.class);

                    if (model != null) {
                        questionModels.add(model);
                        List<String> cricket = new ArrayList<String>();
                        cricket.add(model.getAnswer());
                        expandableListDetail.put(model.getQuestion(), cricket);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return expandableListDetail;
    }
}
