package com.nat.app.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.nat.app.MainActivity;
import com.nat.app.R;
import com.nat.app.components.LoadingButton;
import com.nat.app.interfaces.IAuthActions;
import com.nat.app.presenters.AuthPresenter;
import com.nat.app.utils.Utils;

import java.util.Date;
import java.util.function.Function;


public class AuthenActivity extends AppCompatActivity implements IAuthActions.View {
    enum ACTION_TYPES {
        LOGIN,
        SIGNUP
    }

    private RelativeLayout loginLayout;
    private ImageView logoImageView;
    private EditText emailEditText, passwordEditText;
    private AuthPresenter authPresenter;
    private LoadingButton signUpButton, loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_authen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        loginLayout = findViewById(R.id.main);
        logoImageView = findViewById(R.id.logoImageView);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        signUpButton = findViewById(R.id.signUpButton);
        authPresenter = new AuthPresenter(this);

        // Add listener auth actions
        loginButton.setOnClickListener(v -> {
            loginButton.start();
            onAuthActionNext(ACTION_TYPES.LOGIN);
        });
        signUpButton.setOnClickListener(v -> {
            signUpButton.start();
            onAuthActionNext(ACTION_TYPES.SIGNUP);
        });

        // Add anims
        loginLayout.post(() -> revealAnimation(loginLayout));

        // Check if logged in, navigate.
        authPresenter.onCheckCurrentUser();
    }

    private void revealAnimation(View view) {
        // Animate the root layout
        int cx = view.getWidth() / 2;
        int cy = view.getHeight() / 2;

        float finalRadius = (float) Math.hypot(cx, cy);

        Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);
        anim.setDuration(2000);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.start();

        // Hide and show with animations
        logoImageView.setAlpha(0f);
        loginButton.setAlpha(0f);
        signUpButton.setAlpha(0f);

        // Circle in the logo view
        logoImageView.post(() -> {
            Animation bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom_in_out);
            logoImageView.startAnimation(bounceAnimation);
            logoImageView.animate()
                    .alpha(1f)
                    .setDuration(1000)
                    .setListener(null);
            loginButton.postDelayed(() -> {
                Animation jumpUpAnimation = AnimationUtils.loadAnimation(this, R.anim.jump_up);
                loginButton.startAnimation(jumpUpAnimation);
                loginButton.animate()
                        .alpha(1f)
                        .setDuration(1000)
                        .setListener(null);
            }, 1000);
            signUpButton.postDelayed(() -> {
                Animation jumpUpAnimation = AnimationUtils.loadAnimation(this, R.anim.jump_up);
                signUpButton.startAnimation(jumpUpAnimation);
                signUpButton.animate()
                        .alpha(1f)
                        .setDuration(1000)
                        .setListener(null);
            }, 1000);
        });
    }

    @Override
    public void onLoginSuccess() {
        // navigate to Main
        loginButton.complete(true);
        authPresenter.onNavigateToHome(getApplicationContext());
    }

    @Override
    public void onLoginFailed(String errorMessage) {
        loginButton.complete(false);
        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRegisterSuccess() {
        // Navigate to main
        signUpButton.complete(true);
        authPresenter.onNavigateToHome(getApplicationContext());
    }

    @Override
    public void onRegisterFailure(String errorMessage) {
        signUpButton.complete(false);
        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onUserHasLogin() {
        Toast.makeText(getApplicationContext(), getString(R.string.welcome), Toast.LENGTH_LONG).show();
        authPresenter.onNavigateToHome(getApplicationContext());
    }

    private void onAuthActionNext(Enum<ACTION_TYPES> actionType) {
        // For testing purpose only
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        Log.d("em", email);
        Log.d("pwd", password);
        if (!Utils.isEmailValid(email)) {
            if (actionType == ACTION_TYPES.SIGNUP) {
                onRegisterFailure(getResources().getString(R.string.email_format_failed));
            } else {
                onLoginFailed(getResources().getString(R.string.email_format_failed));
            }
            return;
        }
        if (password.isEmpty())  {
            if (actionType == ACTION_TYPES.SIGNUP) {
                onRegisterFailure(getResources().getString(R.string.password_format_failed));
            } else {
                onLoginFailed(getResources().getString(R.string.password_format_failed));
            }
            return;
        }
        if (actionType == ACTION_TYPES.LOGIN) {
            authPresenter.onLogin(email, password);
        } else if (actionType == ACTION_TYPES.SIGNUP) {
            authPresenter.onRegister(new Date().getTime() + email, password);
        }
    }
}