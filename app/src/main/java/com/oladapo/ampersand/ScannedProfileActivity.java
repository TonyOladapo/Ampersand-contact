package com.oladapo.ampersand;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.oladapo.ampersand.Utils.AppController;

import java.util.ArrayList;

public class ScannedProfileActivity extends AppCompatActivity {

    ImageView photo;
    TextView name, email, role, phone, twitter, linkedin, website;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_profile);

        photo = findViewById(R.id.scanned_photo);
        name = findViewById(R.id.scanned_name);
        email = findViewById(R.id.scanned_email);
        role = findViewById(R.id.scanned_role);
        phone = findViewById(R.id.scanned_phone);
        twitter = findViewById(R.id.scanned_twitter);
        linkedin = findViewById(R.id.scanned_linkedin);
        website = findViewById(R.id.scanned_website);

        String DisplayName = getIntent().getStringExtra("name");
        String DisplayEmail = getIntent().getStringExtra("email");
        String MobileNumber = getIntent().getStringExtra("phone");

        name.setText(DisplayName);
        email.setText(DisplayEmail);
        role.setText(getIntent().getStringExtra("role"));
        phone.setText(MobileNumber);
        twitter.setText(getIntent().getStringExtra("twitter"));
        linkedin.setText(getIntent().getStringExtra("linkedin"));
        website.setText(getIntent().getStringExtra("website"));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS}, 1);
                return;
            }
        }

        ArrayList<ContentProviderOperation>operations = new ArrayList<>();

        operations.add(ContentProviderOperation.newInsert(
                ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        if (DisplayName != null) {

            operations.add(ContentProviderOperation.newInsert(
                    ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                            DisplayName)
                .build());
        }

        if (MobileNumber != null) {

            operations.add(ContentProviderOperation.
                    newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, MobileNumber)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                    .build());
        }

        if (DisplayEmail != null) {
            operations.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Email.DATA, DisplayEmail)
                    .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                    .build());
        }

        try {

            getContentResolver().applyBatch(ContactsContract.AUTHORITY, operations);
        } catch (Exception e) {
            e.printStackTrace();
        }

        getUserPhoto();
    }

    private void getUserPhoto() {

        String url = getIntent().getStringExtra("photo");

        ImageLoader imageLoader = AppController.getInstance().getImageLoader();
        imageLoader.get(url, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                photo.setImageBitmap(response.getBitmap());
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }
}
