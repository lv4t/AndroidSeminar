package com.nat.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Sharef {
    private static final String PREF_NAME = "ReadIDs";
    private static final String KEY_READ_IDS = "read_ids";

    private final SharedPreferences sharedPreferences;
    private final Gson gson;

    public Sharef(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void saveReadIDs(List<String> readIDs) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = gson.toJson(readIDs);
        editor.putString(KEY_READ_IDS, json);
        editor.apply();
    }


    public List<String> getReadIDs() {
        String json = sharedPreferences.getString(KEY_READ_IDS, null);
        Type type = new TypeToken<List<String>>() {
        }.getType();
        List<String> readIDs = gson.fromJson(json, type);
        if (readIDs == null) {
            readIDs = new ArrayList<>();
        }
        return readIDs;
    }

    public void addReadID(String id) {
        List<String> readIDs = getReadIDs();
        readIDs.add(id);
        saveReadIDs(readIDs);
    }

    public boolean isIDRead(String id) {
        List<String> readIDs = getReadIDs();
        return readIDs.contains(id);
    }
}
