package com.example.littlebylittle;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class TrustedContactActivity extends Activity {

    private static final String TAG = "TrustedContactActivity";
    private static final int REQUEST_CODE_CONTACT_PERMISSION = 301;
    private static final int REQUEST_CODE_PICK_CONTACT = 302;
    private static final String PREFS_NAME = "LittleByLittleSupportPrefs";

    private TextView tvContactName;
    private TextView tvContactPhone;
    private LinearLayout layoutContactActions;
    private Button btnCallBuddy;
    private Button btnSmsBuddy;
    private Button btnPickContact;

    private String savedName = "";
    private String savedPhone = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate() called");
        setContentView(R.layout.activity_trusted_contact);

        tvContactName = findViewById(R.id.tv_contact_name);
        tvContactPhone = findViewById(R.id.tv_contact_phone);
        layoutContactActions = findViewById(R.id.layout_contact_actions);
        btnCallBuddy = findViewById(R.id.btn_call_buddy);
        btnSmsBuddy = findViewById(R.id.btn_sms_buddy);
        btnPickContact = findViewById(R.id.btn_pick_contact);

        loadSavedContact();

        btnPickContact.setOnClickListener(v -> checkPermissionAndPickContact());

        // Implicit Intent: ACTION_DIAL
        btnCallBuddy.setOnClickListener(v -> {
            if (!savedPhone.isEmpty()) {
                Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + savedPhone));
                startActivity(dialIntent);
            }
        });

        // Implicit Intent: ACTION_SENDTO (SMS)
        btnSmsBuddy.setOnClickListener(v -> {
            if (!savedPhone.isEmpty()) {
                Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + savedPhone));
                smsIntent.putExtra("sms_body", "Hey, I'm feeling a bit overwhelmed right now and wanted to reach out. 🌱");
                startActivity(smsIntent);
            }
        });
    }

    private void checkPermissionAndPickContact() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            // Lab 10: Runtime Permissions handling
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    REQUEST_CODE_CONTACT_PERMISSION
            );
        } else {
            launchContactPicker();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_CONTACT_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Contacts permission granted! 🌱", Toast.LENGTH_SHORT).show();
                launchContactPicker();
            } else {
                Toast.makeText(this, "Contacts permission is needed to pick a trusted buddy.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void launchContactPicker() {
        // Content Provider: Querying contacts through ACTION_PICK Intent
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(pickContactIntent, REQUEST_CODE_PICK_CONTACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_CONTACT && resultCode == RESULT_OK && data != null) {
            Uri contactUri = data.getData();
            if (contactUri != null) {
                // Lab 10: Accessing Contacts via Content Provider & ContentResolver
                String[] projection = new String[]{
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                };

                try (Cursor cursor = getContentResolver().query(contactUri, projection, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                        int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                        savedName = cursor.getString(nameIndex);
                        savedPhone = cursor.getString(phoneIndex);

                        saveContact(savedName, savedPhone);
                        displayContact(savedName, savedPhone);

                        Toast.makeText(this, "Support buddy set: " + savedName + " 🤝", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error accessing contact ContentProvider", e);
                    Toast.makeText(this, "Could not access contact information.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void saveContact(String name, String phone) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user != null ? user.getUid() : "default";

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .putString("buddy_name_" + userId, name)
                .putString("buddy_phone_" + userId, phone)
                .apply();
    }

    private void loadSavedContact() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user != null ? user.getUid() : "default";

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        savedName = prefs.getString("buddy_name_" + userId, "");
        savedPhone = prefs.getString("buddy_phone_" + userId, "");

        if (!savedName.isEmpty()) {
            displayContact(savedName, savedPhone);
        }
    }

    private void displayContact(String name, String phone) {
        tvContactName.setText(name + " 🤝");
        tvContactPhone.setText("Phone: " + phone);
        layoutContactActions.setVisibility(View.VISIBLE);
    }
}
