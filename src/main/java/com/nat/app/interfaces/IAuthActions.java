package com.nat.app.interfaces;

import android.content.Context;

public interface IAuthActions {
    interface View {
        void onLoginSuccess();
        void onLoginFailed(String errorMessage);

        void onRegisterSuccess();
        void onRegisterFailure(String errorMessage);
        void onUserHasLogin();
    }

    interface Presenter {
        void onCheckCurrentUser();
        void onLogin(String email, String password);

        void onRegister(String email, String password);

        void onLogout();

        void onNavigateToHome(Context context);
    }
}
