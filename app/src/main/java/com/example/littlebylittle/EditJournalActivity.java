package com.example.littlebylittle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditJournalActivity extends Activity {

    private static final String TAG = "LIFECYCLE_EditJournal";

    private TextView tvDate;
    private EditText etContent;
    private Button btnUpdate;
    private Button btnShare;

    private int entryId = -1;
    private final ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate() called");
        setContentView(R.layout.activity_edit_journal);

        tvDate = findViewById(R.id.tv_edit_date);
        etContent = findViewById(R.id.et_edit_content);
        btnUpdate = findViewById(R.id.btn_update_journal);
        btnShare = findViewById(R.id.btn_share_edit_journal);

        // Lab 8: Passing Data Between Activities - Retrieve data from Intent extras
        Intent intent = getIntent();
        if (intent != null) {
            entryId = intent.getIntExtra("ENTRY_ID", -1);
            String initialContent = intent.getStringExtra("CONTENT");
            long timestamp = intent.getLongExtra("TIMESTAMP", System.currentTimeMillis());

            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
            tvDate.setText("Reflection from " + sdf.format(new Date(timestamp)));

            if (initialContent != null) {
                etContent.setText(initialContent);
                etContent.setSelection(initialContent.length());
            }
        }

        btnUpdate.setOnClickListener(v -> updateJournalEntry());

        // Lab 8: Implicit Intent
        btnShare.setOnClickListener(v -> {
            String textToShare = etContent.getText().toString().trim();
            if (!textToShare.isEmpty()) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Reflection from Little by Little 🌱");
                shareIntent.putExtra(Intent.EXTRA_TEXT, textToShare);
                startActivity(Intent.createChooser(shareIntent, "Share reflection via"));
            }
        });
    }

    private void updateJournalEntry() {
        String newContent = etContent.getText().toString().trim();
        if (newContent.isEmpty()) {
            etContent.setError("Reflection cannot be empty");
            return;
        }

        if (entryId == -1) {
            Toast.makeText(this, "Invalid entry ID", Toast.LENGTH_SHORT).show();
            return;
        }

        btnUpdate.setEnabled(false);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String userId = currentUser != null ? currentUser.getUid() : "anonymous";

        databaseExecutor.execute(() -> {
            try {
                // CRUD: Update in local Room Database
                AppDatabase.getDatabase(getApplicationContext())
                        .journalDao()
                        .updateEntryContent(entryId, newContent);

                // CRUD: Update in Cloud Firestore
                FirebaseSyncHelper.updateJournalInFirestore(userId, entryId, newContent);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Reflection updated successfully! 🌱", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                });

            } catch (Exception e) {
                Log.e(TAG, "Failed to update entry", e);
                runOnUiThread(() -> {
                    btnUpdate.setEnabled(true);
                    Toast.makeText(this, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
        databaseExecutor.shutdown();
    }
}
