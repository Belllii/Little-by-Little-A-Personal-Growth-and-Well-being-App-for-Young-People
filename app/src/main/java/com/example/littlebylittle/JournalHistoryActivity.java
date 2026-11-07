package com.example.littlebylittle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JournalHistoryActivity extends Activity {

    private static final String TAG = "LIFECYCLE_JournalHistory";

    private RecyclerView recyclerView;
    private TextView emptyMessage;

    private JournalAdapter adapter;
    private final ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate() called");
        setContentView(R.layout.activity_journal_history);

        recyclerView = findViewById(R.id.rv_journal_history);
        emptyMessage = findViewById(R.id.tv_empty_journal);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadJournalEntries();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called - Reloading user journal entries");
        loadJournalEntries();
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

    private void loadJournalEntries() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String userId = currentUser != null ? currentUser.getUid() : "anonymous";

        databaseExecutor.execute(() -> {
            List<JournalEntry> entries = AppDatabase.getDatabase(getApplicationContext())
                    .journalDao()
                    .getAllEntries(userId);

            runOnUiThread(() -> {
                if (isFinishing() || isDestroyed()) return;

                if (entries.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    emptyMessage.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyMessage.setVisibility(View.GONE);

                    adapter = new JournalAdapter(
                            entries,
                            this::openEditJournal,
                            this::deleteJournalEntry
                    );

                    recyclerView.setAdapter(adapter);
                }
            });
        });
    }

    // Lab 8: Explicit Intent and Passing Data Between Activities
    private void openEditJournal(JournalEntry entry) {
        Intent intent = new Intent(JournalHistoryActivity.this, EditJournalActivity.class);
        intent.putExtra("ENTRY_ID", entry.getId());
        intent.putExtra("CONTENT", entry.getContent());
        intent.putExtra("TIMESTAMP", entry.getTimestamp());
        startActivity(intent);
    }

    private void deleteJournalEntry(JournalEntry entry) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String userId = currentUser != null ? currentUser.getUid() : "anonymous";

        databaseExecutor.execute(() -> {
            try {
                // Room Delete (CRUD: Delete)
                AppDatabase.getDatabase(getApplicationContext())
                        .journalDao()
                        .deleteEntry(entry.getId());

                // Cloud Firestore Delete
                FirebaseSyncHelper.deleteJournalFromFirestore(userId, entry.getId());

                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed() || adapter == null) return;

                    adapter.removeEntry(entry);

                    if (adapter.getItemCount() == 0) {
                        recyclerView.setVisibility(View.GONE);
                        emptyMessage.setVisibility(View.VISIBLE);
                    }

                    Toast.makeText(this, "Reflection deleted.", Toast.LENGTH_SHORT).show();
                });

            } catch (Exception e) {
                Log.e(TAG, "Error deleting journal", e);
                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) return;
                    Toast.makeText(this, "Unable to delete entry. Please try again.", Toast.LENGTH_SHORT).show();
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
