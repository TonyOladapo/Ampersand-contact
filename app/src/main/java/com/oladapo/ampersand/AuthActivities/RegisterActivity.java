package com.oladapo.ampersand.AuthActivities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
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
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements IPickResult {

    SessionManager sessionManager;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Bitmap bitmap;

    private static final String ID_KEY = "id";
    private static final String TOKEN_KEY = "token";
    private static final String PREF_NAME = "SessionPref";
    int PRIVATE_MODE = 0;

    String url = "https://ampersand-contact-exchange-api.herokuapp.com/api/v1/register";

    EditText firstNameEditText;
    EditText lastNameEditText;
    EditText emailEditText;
    EditText passwordEditText;
    EditText phoneNumberEditText;
    EditText twitterEditText;
    EditText linkedInEditeText;
    EditText websiteEditText;
    EditText roleEditText;
    Button button;
    ImageButton pickImage;
    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        relativeLayout = findViewById(R.id.reg_transparent_layout);

        sessionManager = new SessionManager(this);

        sharedPreferences = getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
        editor.apply();

        firstNameEditText = findViewById(R.id.reg_firstName);
        lastNameEditText = findViewById(R.id.reg_lastName);
        emailEditText = findViewById(R.id.reg_email);
        passwordEditText = findViewById(R.id.reg_password);
        phoneNumberEditText = findViewById(R.id.reg_phone);
        twitterEditText = findViewById(R.id.reg_twitter);
        linkedInEditeText = findViewById(R.id.reg_linkedin);
        roleEditText = findViewById(R.id.reg_role);
        websiteEditText = findViewById(R.id.reg_website);

        button = findViewById(R.id.register_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeLayout.setVisibility(View.VISIBLE);
                register();
            }
        });

        pickImage = findViewById(R.id.reg_photo);
        pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickPhoto();
            }
        });
    }

    public void pickPhoto() {
        PickImageDialog.build(new PickSetup()).show(this);
    }

    @Override
    public void onPickResult(PickResult pickResult) {
        if (pickResult.getError() == null) {

            bitmap = pickResult.getBitmap();

            pickImage.setImageBitmap(bitmap);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            final String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            editor.putString("photo", imageString);
            editor.apply();
        }
    }

    private interface Datacallback {
        void onSuccess(JSONObject result);
        void onError(String error);
    }

    private void registerUser(final Datacallback datacallback) {

        final String photo = sharedPreferences.getString("photo", "");

        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String phone = phoneNumberEditText.getText().toString();
        String role = roleEditText.getText().toString();
        String twitter = twitterEditText.getText().toString();
        String linkedIn = linkedInEditeText.getText().toString();
        String website = websiteEditText.getText().toString();

        try {

            JSONObject jsonObject = new JSONObject();

            jsonObject.put("email", email);
            jsonObject.put("password", password);
            jsonObject.put("firstName", firstName);
            jsonObject.put("lastName", lastName);
            jsonObject.put("photo", photo);
            jsonObject.put("role", role);
            jsonObject.put("phoneNumber", phone);
            jsonObject.put("twitter", twitter);
            jsonObject.put("linkedIn", linkedIn);
            jsonObject.put("website", website);

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
                                Toast.makeText(getApplicationContext(), "This Email is already registered, please use a different one", Toast.LENGTH_LONG).show();

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
            AppController.getInstance().addToRequestQueue(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void register() {

        registerUser(new Datacallback() {
            @Override
            public void onSuccess(JSONObject result) {
                sessionManager.createSession();

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

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor edit = preferences.edit();
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

                    editor.putString(ID_KEY, id);
                    editor.putString(TOKEN_KEY, token);
                    editor.apply();

                    sessionManager.userDetails(new SessionManager.DataCallback() {
                        @Override
                        public void onSuccess(JSONObject result) {

                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
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
