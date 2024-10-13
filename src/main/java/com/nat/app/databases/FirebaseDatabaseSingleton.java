package com.nat.app.databases;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nat.app.utils.Constants;

/**
 * Idealy, firebase created singleton, but drop here to know how to write a singleton class
 */
public class FirebaseDatabaseSingleton {

    private static volatile FirebaseDatabaseSingleton instance;
    private DatabaseReference databaseReference;

    private FirebaseDatabaseSingleton() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public static FirebaseDatabaseSingleton getInstance() {
        if (instance == null) {
            synchronized (FirebaseDatabaseSingleton.class) {
                if (instance == null) {
                    instance = new FirebaseDatabaseSingleton();
                }
            }
        }
        return instance;
    }

    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }
}
