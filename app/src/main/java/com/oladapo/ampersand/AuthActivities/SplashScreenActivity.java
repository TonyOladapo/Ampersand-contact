package com.oladapo.ampersand.AuthActivities;

import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.oladapo.ampersand.R;
import com.oladapo.ampersand.SessionManager.SessionManager;

public class SplashScreenActivity extends AppCompatActivity {

    SessionManager sessionManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        sessionManager = new SessionManager(getApplicationContext());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sessionManager.checkLogin(SplashScreenActivity.this);
            }
        }, 2000);
    }
}
