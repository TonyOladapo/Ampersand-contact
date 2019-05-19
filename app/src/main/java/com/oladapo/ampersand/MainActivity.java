package com.oladapo.ampersand;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.oladapo.ampersand.SessionManager.SessionManager;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    ImageView imageView;
    Button scan;
    TextView fullnameTextView;
    TextView roleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        imageView = findViewById(R.id.qr_code_imageView);
        fullnameTextView = findViewById(R.id.fullname);
        roleTextView = findViewById(R.id.role);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String firstname = sharedPreferences.getString("firstname", "");
        String lastname = sharedPreferences.getString("lastname", "");
        String phone = sharedPreferences.getString("phoneNumber", "");
        String photo = sharedPreferences.getString("photo", "");
        String linkedIn = sharedPreferences.getString("linkedin", "");
        String twitter = sharedPreferences.getString("twitter", "");
        String website = sharedPreferences.getString("website", "");
        String email = sharedPreferences.getString("email", "");
        String fullname = firstname + " " + lastname;
        String role = sharedPreferences.getString("role", "");

        fullnameTextView.setText(fullname);
        roleTextView.setText(role);

        scan = findViewById(R.id.scan);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanCode();
            }
        });

        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(fullname + ", " + role + ", " + phone
                    + ", " + photo + ", " + linkedIn + ", " + twitter + ", " + website + ", "
                    + email, BarcodeFormat.QR_CODE, 400, 400);
            imageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void scanCode() {

        new IntentIntegrator(this).initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Scanned", Toast.LENGTH_LONG).show();
                String QRresult = result.getContents();
                String[] parts = QRresult.split(",");

                String name = parts[0];
                String role = parts[1];
                String phone = parts[2];
                String photo = parts[3];
                String linkedin = parts[4];
                String twitter = parts[5];
                String website = parts[6];
                String email = parts[7];

                Intent intent = new Intent(MainActivity.this, ScannedProfileActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("role", role);
                intent.putExtra("phone", phone);
                intent.putExtra("photo", photo);
                intent.putExtra("linkedin", linkedin);
                intent.putExtra("twitter", twitter);
                intent.putExtra("website", website);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        SessionManager sessionManager = new SessionManager(this);

        int id = item.getItemId();
        if (id == R.id.logout) {
            sessionManager.logout();
            this.finish();
        }
        return true;
    }
}
