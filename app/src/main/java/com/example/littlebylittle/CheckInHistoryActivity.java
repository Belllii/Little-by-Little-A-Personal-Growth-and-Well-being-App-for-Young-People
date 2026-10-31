
package com.example.littlebylittle;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CheckInHistoryActivity extends Activity {

    private ListView historyList;
    private TextView emptyMessage;

    private final ExecutorService databaseExecutor =
            Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in_history);

        historyList = findViewById(R.id.historyList);
        emptyMessage = findViewById(R.id.emptyMessage);

        historyList.setEmptyView(emptyMessage);

        loadHistory();
    }

    private void loadHistory() {

        databaseExecutor.execute(() -> {

            List<CheckIn> checkIns =
                    AppDatabase.getDatabase(getApplicationContext())
                            .checkInDao()
                            .getAllCheckIns();

            List<String> historyItems = new ArrayList<>();

            SimpleDateFormat dateFormat =
                    new SimpleDateFormat(
                            "dd MMM yyyy, hh:mm a",
                            Locale.getDefault()
                    );

            for (CheckIn checkIn : checkIns) {

                String date = dateFormat.format(
                        new Date(checkIn.getTimestamp())
                );

                String item = checkIn.getMood()
                        + "\n" + date;

                if (checkIn.getNote() != null
                        && !checkIn.getNote().isEmpty()) {

                    item += "\nNote: " + checkIn.getNote();
                }

                historyItems.add(item);
            }

            runOnUiThread(() -> {

                ArrayAdapter<String> adapter =
                        new ArrayAdapter<>(
                                this,
                                android.R.layout.simple_list_item_1,
                                historyItems
                        );

                historyList.setAdapter(adapter);
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseExecutor.shutdown();
    }
}
