package com.example.littlebylittle;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CheckInHistoryActivity extends Activity {

    private static final String TAG = "LIFECYCLE_CheckInHistory";
    private ListView historyList;
    private TextView emptyMessage;

    private CheckInListAdapter adapter;
    private final List<CheckIn> checkInItems = new ArrayList<>();

    private final ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate() called");
        setContentView(R.layout.activity_check_in_history);

        historyList = findViewById(R.id.historyList);
        emptyMessage = findViewById(R.id.emptyMessage);

        historyList.setEmptyView(emptyMessage);

        adapter = new CheckInListAdapter(this, checkInItems, this::deleteCheckIn);
        historyList.setAdapter(adapter);

        loadHistory();
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
        loadHistory();
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

    private void loadHistory() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String userId = currentUser != null ? currentUser.getUid() : "anonymous";

        databaseExecutor.execute(() -> {
            List<CheckIn> checkIns = AppDatabase.getDatabase(getApplicationContext())
                    .checkInDao()
                    .getAllCheckIns(userId);

            runOnUiThread(() -> {
                if (isFinishing() || isDestroyed()) return;

                checkInItems.clear();
                checkInItems.addAll(checkIns);
                adapter.notifyDataSetChanged();
            });
        });
    }

    private void deleteCheckIn(CheckIn checkIn) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String userId = currentUser != null ? currentUser.getUid() : "anonymous";

        databaseExecutor.execute(() -> {
            try {
                // Delete from local Room database (CRUD: Delete)
                AppDatabase.getDatabase(getApplicationContext())
                        .checkInDao()
                        .deleteCheckIn(checkIn.getId());

                // Delete from Firebase Realtime Database
                FirebaseSyncHelper.deleteCheckInFromRealtimeDb(userId, checkIn.getTimestamp());

                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) return;
                    checkInItems.remove(checkIn);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(this, "Check-in removed.", Toast.LENGTH_SHORT).show();
                });

            } catch (Exception e) {
                Log.e(TAG, "Failed to delete check-in", e);
                runOnUiThread(() -> Toast.makeText(this, "Error deleting check-in", Toast.LENGTH_SHORT).show());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called - Releasing background threads");
        databaseExecutor.shutdown();
    }
}
