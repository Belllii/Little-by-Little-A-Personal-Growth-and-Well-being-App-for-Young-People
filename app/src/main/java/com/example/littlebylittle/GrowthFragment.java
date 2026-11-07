package com.example.littlebylittle;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GrowthFragment extends Fragment {

    private static final String TAG = "GrowthFragment";

    private MoodProgressView moodProgressView;
    private TextView totalCheckIns;
    private TextView totalJournals;
    private TextView latestMood;
    private TextView growthMessage;

    private ExecutorService databaseExecutor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_growth, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        moodProgressView = view.findViewById(R.id.mood_progress_view);
        totalCheckIns = view.findViewById(R.id.tv_total_checkins);
        totalJournals = view.findViewById(R.id.tv_total_journals);
        latestMood = view.findViewById(R.id.tv_latest_mood);
        growthMessage = view.findViewById(R.id.tv_growth_message);

        databaseExecutor = Executors.newSingleThreadExecutor();

        loadGrowthData();
    }

    private void loadGrowthData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String userId = currentUser != null ? currentUser.getUid() : "anonymous";

        databaseExecutor.execute(() -> {
            try {
                AppDatabase db = AppDatabase.getDatabase(requireContext().getApplicationContext());

                int checkInCount = db.checkInDao().getTotalCheckIns(userId);
                int journalCount = db.journalDao().getTotalJournalEntries(userId);
                CheckIn latestCheckIn = db.checkInDao().getLatestCheckIn(userId);

                // Sync stats to Firebase Realtime Database
                FirebaseSyncHelper.updateStatsInRealtimeDb(userId, checkInCount, journalCount);

                if (getActivity() == null) return;

                requireActivity().runOnUiThread(() -> {
                    if (!isAdded()) return;

                    totalCheckIns.setText(String.valueOf(checkInCount));
                    totalJournals.setText(String.valueOf(journalCount));

                    if (moodProgressView != null) {
                        moodProgressView.setProgress(checkInCount, 7);
                    }

                    if (latestCheckIn != null && latestCheckIn.getMood() != null) {
                        latestMood.setText(latestCheckIn.getMood());
                    } else {
                        latestMood.setText("No check-ins yet");
                    }

                    if (checkInCount == 0 && journalCount == 0) {
                        growthMessage.setText("Your journey starts with one little step. 💜");
                    } else {
                        growthMessage.setText(
                                "You've taken " + checkInCount
                                        + " mood check-ins and written "
                                        + journalCount
                                        + " journal entries. Keep going! 🌱"
                        );
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Error loading growth data", e);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (databaseExecutor != null) {
            loadGrowthData();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (databaseExecutor != null) {
            databaseExecutor.shutdown();
            databaseExecutor = null;
        }

        moodProgressView = null;
        totalCheckIns = null;
        totalJournals = null;
        latestMood = null;
        growthMessage = null;
    }
}
