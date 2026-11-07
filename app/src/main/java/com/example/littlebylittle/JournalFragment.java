package com.example.littlebylittle;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JournalFragment extends Fragment {

    private static final String TAG = "JournalFragment";

    private EditText journalEntry;
    private Button saveButton;
    private Button viewButton;
    private Button shareButton;
    private TextView messageText;

    private TextView tvPromptTitle;
    private TextView tvPromptQuestion;
    private Button btnNextPrompt;

    private List<MindfulnessXmlParser.PromptItem> promptList;
    private int currentPromptIndex = 0;

    private ExecutorService databaseExecutor;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_journal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        journalEntry = view.findViewById(R.id.et_journal_entry);
        saveButton = view.findViewById(R.id.btn_save_journal);
        viewButton = view.findViewById(R.id.btn_view_journal);
        shareButton = view.findViewById(R.id.btn_share_journal);
        messageText = view.findViewById(R.id.tv_journal_message);

        tvPromptTitle = view.findViewById(R.id.tv_prompt_title);
        tvPromptQuestion = view.findViewById(R.id.tv_prompt_question);
        btnNextPrompt = view.findViewById(R.id.btn_next_prompt);

        databaseExecutor = Executors.newSingleThreadExecutor();

        // Lab 11: XML Parsing of guided prompts
        loadGuidedPrompts();

        saveButton.setOnClickListener(v -> saveJournalEntry());

        viewButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), JournalHistoryActivity.class);
            startActivity(intent);
        });

        // Lab 8: Implicit Intent - Share reflection with friends/social apps
        if (shareButton != null) {
            shareButton.setOnClickListener(v -> shareJournalReflection());
        }

        if (btnNextPrompt != null) {
            btnNextPrompt.setOnClickListener(v -> cycleNextPrompt());
        }
    }

    private void loadGuidedPrompts() {
        databaseExecutor.execute(() -> {
            promptList = MindfulnessXmlParser.parsePrompts(requireContext());
            if (getActivity() == null) return;
            requireActivity().runOnUiThread(() -> {
                if (!isAdded() || promptList == null || promptList.isEmpty()) return;
                displayCurrentPrompt();
            });
        });
    }

    private void cycleNextPrompt() {
        if (promptList == null || promptList.isEmpty()) return;
        currentPromptIndex = (currentPromptIndex + 1) % promptList.size();
        displayCurrentPrompt();
    }

    private void displayCurrentPrompt() {
        if (promptList == null || promptList.isEmpty()) return;
        MindfulnessXmlParser.PromptItem item = promptList.get(currentPromptIndex);
        tvPromptTitle.setText(item.getTitle() + " (" + item.getCategory() + ")");
        tvPromptQuestion.setText(item.getQuestion());
    }

    private void shareJournalReflection() {
        String content = journalEntry.getText().toString().trim();
        if (content.isEmpty()) {
            Toast.makeText(requireContext(), "Write a reflection or thought first to share 🌱", Toast.LENGTH_SHORT).show();
            return;
        }

        // Implicit Intent: ACTION_SEND with text/plain
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "A thought from Little by Little 🌱");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "\"" + content + "\"\n\n— Shared from Little by Little App");

        startActivity(Intent.createChooser(shareIntent, "Share reflection via"));
    }

    private void saveJournalEntry() {
        String content = journalEntry.getText().toString().trim();

        if (content.isEmpty()) {
            journalEntry.setError("Please write something first.");
            return;
        }

        saveButton.setEnabled(false);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String userId = currentUser != null ? currentUser.getUid() : "anonymous";
        long timestamp = System.currentTimeMillis();

        String title = "Reflection";
        if (promptList != null && !promptList.isEmpty()) {
            title = promptList.get(currentPromptIndex).getTitle();
        }

        JournalEntry entry = new JournalEntry(userId, title, content, timestamp);

        databaseExecutor.execute(() -> {
            try {
                // 1. Room DB (Create CRUD)
                AppDatabase.getDatabase(requireContext().getApplicationContext())
                        .journalDao()
                        .insertEntry(entry);

                // 2. Firebase Cloud Firestore Sync
                FirebaseSyncHelper.syncJournalToFirestore(userId, entry);

                // 3. Update stats
                int totalCheckIns = AppDatabase.getDatabase(requireContext().getApplicationContext())
                        .checkInDao()
                        .getTotalCheckIns(userId);
                int totalJournals = AppDatabase.getDatabase(requireContext().getApplicationContext())
                        .journalDao()
                        .getTotalJournalEntries(userId);

                FirebaseSyncHelper.updateStatsInRealtimeDb(userId, totalCheckIns, totalJournals);

                if (getActivity() == null) return;
                requireActivity().runOnUiThread(() -> {
                    if (!isAdded() || journalEntry == null) return;

                    journalEntry.setText("");
                    messageText.setText("Your reflection has been saved and synced! 🌱");
                    messageText.setVisibility(View.VISIBLE);
                    saveButton.setEnabled(true);
                });

            } catch (Exception e) {
                Log.e(TAG, "Failed to save journal entry", e);
                if (getActivity() != null) {
                    requireActivity().runOnUiThread(() -> {
                        if (!isAdded()) return;
                        saveButton.setEnabled(true);
                        Toast.makeText(requireContext(), "Unable to save: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
        shareButton = null;
        messageText = null;
        tvPromptTitle = null;
        tvPromptQuestion = null;
        btnNextPrompt = null;
    }
}
