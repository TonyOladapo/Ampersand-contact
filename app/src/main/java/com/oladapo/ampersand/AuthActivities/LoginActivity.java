package com.oladapo.ampersand.AuthActivities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.oladapo.ampersand.MainActivity;
import com.oladapo.ampersand.R;
import com.oladapo.ampersand.SessionManager.SessionManager;
import com.oladapo.ampersand.Utils.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    SharedPreferences.Editor edit;

    RelativeLayout relativeLayout;

    private static final String ID_KEY = "id";
    private static final String TOKEN_KEY = "token";
    private static final String PREF_NAME = "SessionPref";
    int PRIVATE_MODE = 0;

    Button login;
    EditText emailEditText;
    EditText passwordEditText;
    TextView textView;
    SessionManager sessionManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(getApplicationContext());

        textView = findViewById(R.id.error_textview);
        emailEditText = findViewById(R.id.email_editText);
        passwordEditText = findViewById(R.id.password_editText);

        relativeLayout = findViewById(R.id.transparent_layout);

        login = findViewById(R.id.signin_button);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeLayout.setVisibility(View.VISIBLE);
                login();
            }
        });
    }

    private interface Datacallback {
        void onSuccess(JSONObject result);
        void onError(String error);
    }

    private void loginUser(final Datacallback datacallback) {

        String url = "https://ampersand-contact-exchange-api.herokuapp.com/api/v1/login";
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("email", email);
            jsonObject.put("password", password);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            datacallback.onSuccess(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            datacallback.onError(error.toString());

                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                //This indicates that the request has either time out or there is no connection
                                Toast.makeText(getApplicationContext(),"Check your internet and try again!", Toast.LENGTH_LONG).show();

                            } else if (error instanceof AuthFailureError) {
                                //Error indicating that there was an Authentication Failure while performing the request
                                textView.setVisibility(View.VISIBLE);

                            } else if (error instanceof ServerError) {
                                //Indicates that the server responded with a error response
                                Toast.makeText(getApplicationContext(), "Server error! Try again later", Toast.LENGTH_LONG).show();

                            } else if (error instanceof NetworkError) {
                                //Indicates that there was network error while performing the request
                                Toast.makeText(getApplicationContext(), "Network error", Toast.LENGTH_LONG).show();

                            } else if (error instanceof ParseError) {
                                // Indicates that the server response could not be parsed
                                Toast.makeText(getApplicationContext(), "Parse Error", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Accept", "application/json");

                    return headers;
                }
            };
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            AppController.getInstance().addToRequestQueue(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void login() {

        loginUser(new Datacallback() {
            @Override
            public void onSuccess(JSONObject result) {

                try {

                    JSONObject mainObject = new JSONObject(result.toString());
                    JSONObject jsonObject = mainObject.getJSONObject("user");

                    String id = jsonObject.getString("_id");
                    String firstName = jsonObject.getString("firstName");
                    String lastName = jsonObject.getString("lastName");
                    String phoneNumber = jsonObject.getString("phoneNumber");
                    String email = jsonObject.getString("email");
                    String linkedIn = jsonObject.getString("linkedIn");
                    String twitter = jsonObject.getString("twitter");
                    String website = jsonObject.getString("website");
                    String role = jsonObject.getString("role");
                    String photo = jsonObject.getString("photo");
                    String token = mainObject.getString("token");

                    preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                    edit = preferences.edit();
                    edit.putString("firstname", firstName);
                    edit.putString("lastname", lastName);
                    edit.putString("phoneNumber", phoneNumber);
                    edit.putString("email", email);
                    edit.putString("linkedin", linkedIn);
                    edit.putString("twitter", twitter);
                    edit.putString("website", website);
                    edit.putString("role", role);
                    edit.putString("photo", photo);
                    edit.apply();

                    sharedPreferences = getSharedPreferences(PREF_NAME, PRIVATE_MODE);

                    editor = sharedPreferences.edit();
                    editor.putString(ID_KEY, id);
                    editor.putString(TOKEN_KEY, token);
                    editor.apply();

                    sessionManager.userDetails(new SessionManager.DataCallback() {
                        @Override
                        public void onSuccess(JSONObject result) {

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onError(String error) {

                        }
                    });

                    relativeLayout.setVisibility(View.GONE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
                Log.i("vkv", error);
                relativeLayout.setVisibility(View.GONE);
            }
        });
    }
}
