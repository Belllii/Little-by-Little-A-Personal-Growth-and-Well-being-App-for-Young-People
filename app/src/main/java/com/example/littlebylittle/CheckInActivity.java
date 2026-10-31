
package com.example.littlebylittle;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.content.Intent;
public class CheckInActivity extends Activity {

    private RadioGroup moodGroup;
    private EditText noteInput;
    private TextView resultText;
    private Button saveButton;

    private final ExecutorService databaseExecutor =
            Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);

        moodGroup = findViewById(R.id.moodGroup);
        noteInput = findViewById(R.id.noteInput);
        resultText = findViewById(R.id.resultText);
        saveButton = findViewById(R.id.saveButton);

        saveButton.setOnClickListener(v -> saveCheckIn());
        Button historyButton = findViewById(R.id.btnViewHistory);

        historyButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    CheckInActivity.this,
                    CheckInHistoryActivity.class
            );
            startActivity(intent);
        });
    }

    private void saveCheckIn() {

        int selectedMoodId = moodGroup.getCheckedRadioButtonId();

        if (selectedMoodId == -1) {
            Toast.makeText(this,
                    "Please select your mood.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedRadioButton =
                findViewById(selectedMoodId);

        String mood = selectedRadioButton.getText().toString();
        String note = noteInput.getText().toString().trim();

        saveButton.setEnabled(false);

        CheckIn checkIn = new CheckIn(
                mood,
                note,
                System.currentTimeMillis()
        );

        databaseExecutor.execute(() -> {

            try {
                AppDatabase.getDatabase(getApplicationContext())
                        .checkInDao()
                        .insertCheckIn(checkIn);

                runOnUiThread(() -> {
                    resultText.setText(
                            "Your check-in has been saved! 🌱"
                    );

                    Toast.makeText(this,
                            "Check-in saved successfully!",
                            Toast.LENGTH_SHORT).show();

                    finish();
                });

            } catch (Exception e) {

                runOnUiThread(() -> {
                    saveButton.setEnabled(true);

                    Toast.makeText(this,
                            "Unable to save check-in. Please try again.",
                            Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}
