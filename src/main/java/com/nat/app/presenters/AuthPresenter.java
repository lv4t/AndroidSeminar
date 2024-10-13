package com.nat.app.presenters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.nat.app.MainActivity;
import com.nat.app.R;
import com.nat.app.activities.AuthenActivity;
import com.nat.app.interfaces.IAuthActions;

import java.util.Objects;

public class AuthPresenter implements IAuthActions.Presenter {
    private final FirebaseAuth firebaseAuth;
    private final IAuthActions.View view;

    public AuthPresenter(IAuthActions.View authActionView) {
        firebaseAuth = FirebaseAuth.getInstance();
        this.view = authActionView;
    }

    @Override
    public void onCheckCurrentUser() {
        if (firebaseAuth.getCurrentUser() != null) {
            view.onUserHasLogin();
        }
    }

    @Override
    public void onLogin(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                view.onLoginSuccess();
                return;
            }
            String errorMessage = Objects.requireNonNull(task.getException()).getMessage();

            if (errorMessage != null) {
                view.onLoginFailed(errorMessage);
                return;
            }
            view.onLoginFailed(String.valueOf(R.string.login_failed));
        });
    }

    @Override
    public void onRegister(String email, String password) {
        // Logout before registering
        firebaseAuth.signOut();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                view.onRegisterSuccess();
                return;
            }
            String errorMessage = Objects.requireNonNull(task.getException()).getMessage();
            if (errorMessage != null) {
                view.onRegisterFailure(errorMessage);
                return;
            }
            view.onRegisterFailure(String.valueOf(R.string.signup_failed));

        });
    }

    @Override
    public void onLogout() {
        firebaseAuth.signOut();
    }

    @Override
    public void onNavigateToHome(Context context) {
        context.startActivity(new Intent(context, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}
