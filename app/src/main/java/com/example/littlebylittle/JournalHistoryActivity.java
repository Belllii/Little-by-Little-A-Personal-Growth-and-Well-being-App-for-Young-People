
package com.example.littlebylittle;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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

                if (entries.isEmpty()) {

                    recyclerView.setVisibility(View.GONE);
                    emptyMessage.setVisibility(View.VISIBLE);

                } else {

                    recyclerView.setVisibility(View.VISIBLE);
                    emptyMessage.setVisibility(View.GONE);

                    adapter = new JournalAdapter(entries);
                    recyclerView.setAdapter(adapter);
                }
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseExecutor.shutdown();
    }
}
