package com.oladapo.mseries.Misc;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.oladapo.mseries.Adapters.UserWatchlistAdapter;
import com.oladapo.mseries.AppController;
import com.oladapo.mseries.ArrayLists.UserShows;
import com.oladapo.mseries.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AllUserShows extends AppCompatActivity {

    public AllUserShows() {
    }

    private ArrayList<UserShows> list;
    private UserWatchlistAdapter userWatchlistAdapter;
    private RecyclerView recyclerView;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_user_shows_activity);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int startColor = ContextCompat.getColor(this, R.color.colorPrimaryDark);
            int endColor = ContextCompat.getColor(this, R.color.white);
            ObjectAnimator.ofArgb(getWindow(),"statusBarColor", startColor, endColor).start();
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView textView = findViewById(R.id.toolbar_text);
        textView.setText("My Shows");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(null);

        recyclerView = findViewById(R.id.all_shows_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        userWatch