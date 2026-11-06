
package com.example.littlebylittle;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JournalHistoryActivity extends Activity {

    private RecyclerView recyclerView;
    private TextView emptyMessage;

    private JournalAdapter adapter;

    private final ExecutorService databaseExecutor =
            Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_journal_history);

        recyclerView = findViewById(R.id.rv_journal_history);
        emptyMessage = findViewById(R.id.tv_empty_journal);

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        loadJournalEntries();
    }

    private void loadJournalEntries() {

        databaseExecutor.execute(() -> {

            List<JournalEntry> entries =
                    AppDatabase.getDatabase(getApplicationContext())
                            .journalDao()
                            .getAllEntries();

            runOnUiThread(() -> {

                if (isFinishing() || isDestroyed()) {
                    return;
                }

                if (entries.isEmpty()) {

                    recyclerView.setVisibility(View.GONE);
                    emptyMessage.setVisibility(View.VISIBLE);

                } else {

                    recyclerView.setVisibility(View.VISIBLE);
                    emptyMessage.setVisibility(View.GONE);

                    adapter = new JournalAdapter(
                            entries,
                            this::deleteJournalEntry
                    );

                    recyclerView.setAdapter(adapter);
                }
            });
        });
    }

    private void deleteJournalEntry(JournalEntry entry) {

        databaseExecutor.execute(() -> {

            try {

                AppDatabase.getDatabase(getApplicationContext())
                        .journalDao()
                        .deleteEntry(entry.getId());

                runOnUiThread(() -> {

                    if (isFinishing() || isDestroyed() || adapter == null) {
                        return;
                    }

                    adapter.removeEntry(entry);

                    if (adapter.getItemCount() == 0) {

                        recyclerView.setVisibility(View.GONE);
                        emptyMessage.setVisibility(View.VISIBLE);

                    }

                    Toast.makeText(
                            this,
                            "Reflection deleted.",
                            Toast.LENGTH_SHORT
                    ).show();

                });

            } catch (Exception e) {

                runOnUiThread(() -> {

                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    Toast.makeText(
                            this,
                            "Unable to delete entry. Please try again.",
                            Toast.LENGTH_SHORT
                    ).show();

                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseExecutor.shutdown();
    }
}
