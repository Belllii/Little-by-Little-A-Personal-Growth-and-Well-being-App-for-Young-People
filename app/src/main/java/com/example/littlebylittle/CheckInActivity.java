package com.example.littlebylittle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CheckInActivity extends Activity {

    private static final String TAG = "LIFECYCLE_CheckIn";

    private RadioGroup moodGroup;
    private EditText noteInput;
    private TextView resultText;
    private Button saveButton;

    private final ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate() called");
        setContentView(R.layout.activity_check_in);

        moodGroup = findViewById(R.id.moodGroup);
        noteInput = findViewById(R.id.noteInput);
        resultText = findViewById(R.id.resultText);
        saveButton = findViewById(R.id.saveButton);

        // Lab 8: Passing Data Between Activities - check for intent extras
        Intent incomingIntent = getIntent();
        if (incomingIntent != null && incomingIntent.hasExtra("SELECTED_MOOD")) {
            String preselectedMood = incomingIntent.getStringExtra("SELECTED_MOOD");
            preselectMoodInRadioGroup(preselectedMood);
        }

        saveButton.setOnClickListener(v -> saveCheckIn());

        Button historyButton = findViewById(R.id.btnViewHistory);
        historyButton.setOnClickListener(v -> {
            Intent intent = new Intent(CheckInActivity.this, CheckInHistoryActivity.class);
            startActivity(intent);
        });
    }

    private void preselectMoodInRadioGroup(String preselectedMood) {
        if (preselectedMood == null) return;
        for (int i = 0; i < moodGroup.getChildCount(); i++) {
            if (moodGroup.getChildAt(i) instanceof RadioButton) {
                RadioButton rb = (RadioButton) moodGroup.getChildAt(i);
                if (preselectedMood.contains(rb.getText().toString()) || rb.getText().toString().contains(preselectedMood)) {
                    rb.setChecked(true);
                    break;
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    private void saveCheckIn() {
        int selectedMoodId = moodGroup.getCheckedRadioButtonId();

        if (selectedMoodId == -1) {
            Toast.makeText(this, "Please select your mood.", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedRadioButton = findViewById(selectedMoodId);
        String mood = selectedRadioButton.getText().toString();
        String note = noteInput.getText().toString().trim();

        saveButton.setEnabled(false);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String userId = currentUser != null ? currentUser.getUid() : "anonymous";
        long timestamp = System.currentTimeMillis();

        CheckIn checkIn = new CheckIn(userId, mood, note, timestamp);

        databaseExecutor.execute(() -> {
            try {
                // 1. Room DB (Local, offline private storage)
                AppDatabase.getDatabase(getApplicationContext())
                        .checkInDao()
                        .insertCheckIn(checkIn);

                // 2. Firebase Realtime Database Sync
                FirebaseSyncHelper.syncCheckInToRealtimeDb(userId, checkIn);

                // 3. Update stats in Realtime DB
                int totalCheckIns = AppDatabase.getDatabase(getApplicationContext())
                        .checkInDao()
                        .getTotalCheckIns(userId);
                int totalJournals = AppDatabase.getDatabase(getApplicationContext())
                        .journalDao()
                        .getTotalJournalEntries(userId);

                FirebaseSyncHelper.updateStatsInRealtimeDb(userId, totalCheckIns, totalJournals);

                // Lab 10: Custom Broadcast Event
                Intent broadcastIntent = new Intent(SystemStatusReceiver.ACTION_GROWTH_DATA_UPDATED);
                broadcastIntent.setPackage(getPackageName());
                sendBroadcast(broadcastIntent);

                runOnUiThread(() -> {
                    resultText.setText("Your check-in has been saved! 🌱");
                    Toast.makeText(this, "Check-in saved successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                });

            } catch (Exception e) {
                Log.e(TAG, "Error saving check-in", e);
                runOnUiThread(() -> {
                    saveButton.setEnabled(true);
                    Toast.makeText(this, "Unable to save check-in. Please try again.", Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
        databaseExecutor.shutdown();
    }
}
