package com.example.littlebylittle;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.util.Log;
import android.content.Intent;
public class JournalFragment extends Fragment {

    private EditText journalEntry;
    private Button saveButton;
    private Button viewButton;
    private TextView messageText;

    private ExecutorService databaseExecutor;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        return inflater.inflate(
                R.layout.fragment_journal,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        journalEntry = view.findViewById(R.id.et_journal_entry);
        saveButton = view.findViewById(R.id.btn_save_journal);
        viewButton = view.findViewById(R.id.btn_view_journal);
        messageText = view.findViewById(R.id.tv_journal_message);

        databaseExecutor = Executors.newSingleThreadExecutor();

        saveButton.setOnClickListener(v -> saveJournalEntry());

        viewButton.setOnClickListener(v -> {

            Intent intent = new Intent(
                    requireContext(),
                    JournalHistoryActivity.class
            );

            startActivity(intent);
        });
    }

    private void saveJournalEntry() {

        String content = journalEntry.getText().toString().trim();

        if (content.isEmpty()) {
            journalEntry.setError("Please write something first.");
            return;
        }

        saveButton.setEnabled(false);

        JournalEntry entry = new JournalEntry(
                content,
                System.currentTimeMillis()
        );

        databaseExecutor.execute(() -> {

            try {
                AppDatabase.getDatabase(requireContext().getApplicationContext())
                        .journalDao()
                        .insertEntry(entry);

                if (getActivity() == null) {
                    return;
                }

                requireActivity().runOnUiThread(() -> {

                    if (!isAdded() || journalEntry == null) {
                        return;
                    }

                    journalEntry.setText("");

                    messageText.setText("Your reflection has been saved! 🌱");
                    messageText.setVisibility(View.VISIBLE);

                    saveButton.setEnabled(true);

                });

            } catch (Exception e) {

                Log.e("JOURNAL_ERROR", "Failed to save journal entry", e);

                if (getActivity() != null) {
                    requireActivity().runOnUiThread(() -> {

                        if (!isAdded()) {
                            return;
                        }

                        saveButton.setEnabled(true);

                        Toast.makeText(
                                requireContext(),
                                "Unable to save: " + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    });
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (databaseExecutor != null) {
            databaseExecutor.shutdown();
        }

        journalEntry = null;
        saveButton = null;
        viewButton = null;
        messageText = null;
    }
}
