package com.oladapo.ampersand.SessionManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.oladapo.ampersand.MainActivity;
import com.oladapo.ampersand.Utils.AppController;
import com.oladapo.ampersand.WelcomeActivity;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SessionManager {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context mContext;

    private static final String PREF_NAME = "SessionPref";
    private static final String IS_LOGIN = "IsLoggedIn";

    public SessionManager(Context context) {

        this.mContext = context;
        int PRIVATE_MODE = 0;
        sharedPreferences = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
        editor.apply();
    }

    public void createSession() {

        editor.putBoolean(IS_LOGIN, true);
        editor.apply();
    }

    public void checkLogin(Activity activity) {

        if (!this.isLoggedIn()) {

            Intent intent = new Intent(mContext, WelcomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
            activity.finish();
        } else {

            Intent intent = new Intent(mContext, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
            activity.finish();
        }
    }

    public interface DataCallback {
        void onSuccess(JSONObject result);
        void onError(String error);
    }

    private void getUserDetails(final DataCallback dataCallback) {

        final String token = sharedPreferences.getString("token", "");

        String id = sharedPreferences.getString("id", "");
        String url = "https://ampersand-contact-exchange-api.herokuapp.com/api/v1/profile/"+ id;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dataCallback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dataCallback.onError(error.toString());
                    }
                }
        ) {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                headers.put("x-access-token", Objects.requireNonNull(token));

                return headers;
            }
        };
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    public void userDetails(final DataCallback dataCallback) {
        getUserDetails(new DataCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                dataCallback.onSuccess(result);
                createSession();
            }

            @Override
            public void onError(String error) {
                dataCallback.onError(error);
                Log.i("vkv", error);
            }
        });
    }

    public void logout() {

        editor.clear();
        editor.apply();

        Intent intent = new Intent(mContext, WelcomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    private boolean isLoggedIn() {
        return sharedPreferences.getBoolean(IS_LOGIN, false);
    }
}
