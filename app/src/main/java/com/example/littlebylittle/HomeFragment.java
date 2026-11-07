package com.example.littlebylittle;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.SharedPreferences;
import com.example.littlebylittle.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private FragmentHomeBinding binding;
    private MindfulCalmService calmService;
    private boolean isBound = false;
    private ExecutorService backgroundExecutor;

    private final Handler timerUpdateHandler = new Handler(Looper.getMainLooper());
    private final Runnable timerUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            if (isBound && calmService != null && calmService.isRunning()) {
                int secs = calmService.getElapsedSeconds();
                int minutes = secs / 60;
                int remainingSeconds = secs % 60;
                if (binding != null && binding.tvCalmTimer != null) {
                    binding.tvCalmTimer.setText(String.format(Locale.getDefault(), "Timer: %02d:%02d", minutes, remainingSeconds));
                }
                timerUpdateHandler.postDelayed(this, 1000);
            }
        }
    };

    // Lab 10: Bound Service Connection
    private final ServiceConnection calmServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MindfulCalmService.LocalBinder binder = (MindfulCalmService.LocalBinder) service;
            calmService = binder.getService();
            isBound = true;
            Log.d(TAG, "Bound to MindfulCalmService successfully");

            if (calmService.isRunning()) {
                updateCalmButtonState(true);
                timerUpdateHandler.post(timerUpdateRunnable);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            calmService = null;
            isBound = false;
            Log.d(TAG, "Disconnected from MindfulCalmService");
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        backgroundExecutor = Executors.newSingleThreadExecutor();

        // Bind to Mindful Calm Service
        Intent serviceIntent = new Intent(requireContext(), MindfulCalmService.class);
        requireContext().bindService(serviceIntent, calmServiceConnection, Context.BIND_AUTO_CREATE);

        // Personalize greeting
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && currentUser.getDisplayName() != null && !currentUser.getDisplayName().isEmpty()) {
            binding.tvGreetingTitle.setText("Welcome back, " + currentUser.getDisplayName() + "! 🌱");
        }

        // Profile Avatar Click
        binding.ivAvatar.setOnClickListener(v -> {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                Intent intent = new Intent(requireContext(), ProfileActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(requireContext(), WelcomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        // Lab 11: HTTP GET Request & JSON Parsing for Daily Inspiration Quote
        loadDailyQuote();

        // Daily Emotional Check-in Listeners (Pass selected mood via Intent extra)
        binding.btnMoodOverwhelmed.setOnClickListener(v -> onMoodSelected(
                "🌧️ Overwhelmed",
                "It's completely okay to feel overloaded. Pick just one tiny step for right now — anything else can wait."
        ));

        binding.btnMoodAnxious.setOnClickListener(v -> onMoodSelected(
                "💭 Anxious",
                "Take a slow breath. Notice the ground beneath your feet. You are safe in this present moment."
        ));

        binding.btnMoodLonely.setOnClickListener(v -> onMoodSelected(
                "🌿 Lonely",
                "Feeling disconnected is deeply human. When you're ready, try a 1-minute scenario in the Practice Gym."
        ));

        binding.btnMoodOkay.setOnClickListener(v -> onMoodSelected(
                "🌤️ Okay",
                "Steady days are good foundations. Use this calm space to practice a conversation or write a small thought."
        ));

        binding.btnMoodHopeful.setOnClickListener(v -> onMoodSelected(
                "✨ Hopeful",
                "Hold on to this feeling! Take a moment to capture what brought you optimism today in your Journal."
        ));

        // Quick Access - Social Practice Card
        binding.cardActionPractice.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).selectBottomTab(R.id.nav_practice);
            }
        });

        // Quick Access - Thought Journal Card
        binding.cardActionJournal.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).selectBottomTab(R.id.nav_journal);
            }
        });

        // Lab 11 & Social Feature: Supportive Social Community Feed
        if (binding.cardActionCommunity != null) {
            binding.cardActionCommunity.setOnClickListener(v -> {
                Intent intent = new Intent(requireContext(), CommunityFeedActivity.class);
                startActivity(intent);
            });
        }

        // Lab 10: Foreground & Bound Service Toggle Button
        if (binding.btnToggleCalmSession != null) {
            binding.btnToggleCalmSession.setOnClickListener(v -> toggleCalmSession());
        }

        // Task Planner items
        binding.rowTaskCheckin.setOnClickListener(v -> openCheckInActivity(null));

        binding.rowTaskPractice.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).selectBottomTab(R.id.nav_practice);
            }
        });

        binding.rowTaskJournal.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).selectBottomTab(R.id.nav_journal);
            }
        });

        // Recent Insights "View All"
        binding.tvViewAllInsights.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).selectBottomTab(R.id.nav_growth);
            }
        });
    }

    private void loadDailyQuote() {
        QuoteApiClient client = new QuoteApiClient();
        client.fetchDailyInspirationalQuote(new QuoteApiClient.OnQuoteListener() {
            @Override
            public void onReceived(String quote, String author) {
                if (binding != null && binding.tvDailyQuoteText != null) {
                    binding.tvDailyQuoteText.setText("“" + quote + "”");
                    binding.tvDailyQuoteAuthor.setText("— " + author);
                }
            }

            @Override
            public void onError(String message) {
                if (binding != null && binding.tvDailyQuoteText != null) {
                    binding.tvDailyQuoteText.setText("“One little step at a time is all it takes.”");
                    binding.tvDailyQuoteAuthor.setText("— Little by Little");
                }
            }
        });
    }

    private void toggleCalmSession() {
        if (!isBound || calmService == null) return;

        Intent serviceIntent = new Intent(requireContext(), MindfulCalmService.class);

        if (!calmService.isRunning()) {
            serviceIntent.setAction(MindfulCalmService.ACTION_START);
            requireContext().startService(serviceIntent);
            calmService.startCalmSession();
            updateCalmButtonState(true);
            timerUpdateHandler.post(timerUpdateRunnable);
            Toast.makeText(requireContext(), "Mindful calm session started. Check your notifications! 🧘", Toast.LENGTH_SHORT).show();
        } else {
            serviceIntent.setAction(MindfulCalmService.ACTION_STOP);
            requireContext().startService(serviceIntent);
            calmService.stopCalmSession();
            updateCalmButtonState(false);
            timerUpdateHandler.removeCallbacks(timerUpdateRunnable);
            Toast.makeText(requireContext(), "Calm session ended. Well done! 🌱", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateCalmButtonState(boolean isRunning) {
        if (binding == null || binding.btnToggleCalmSession == null) return;
        if (isRunning) {
            binding.btnToggleCalmSession.setText("Stop Session 🛑");
            binding.btnToggleCalmSession.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
        } else {
            binding.btnToggleCalmSession.setText("Start Calm 🧘");
            binding.btnToggleCalmSession.setBackgroundColor(getResources().getColor(R.color.purple_primary));
            binding.tvCalmTimer.setText("Timer: 00:00");
        }
    }

    private void onMoodSelected(String moodTitle, String feedback) {
        // Show message directly on card without navigating to CheckInActivity
        binding.tvMoodFeedback.setText("🌱 " + moodTitle + "\n\n" + feedback);
        binding.tvMoodFeedback.setVisibility(View.VISIBLE);

        binding.badgeTaskCheckin.setText("Complete");
        binding.badgeTaskCheckin.setBackgroundResource(R.drawable.bg_pill_complete);
        binding.badgeTaskCheckin.setTextColor(getResources().getColor(R.color.white));

        saveQuickCheckIn(moodTitle);
    }

    private void saveQuickCheckIn(String moodTitle) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String userId = currentUser != null ? currentUser.getUid() : "anonymous";
        long now = System.currentTimeMillis();

        if (backgroundExecutor != null && !backgroundExecutor.isShutdown() && getContext() != null) {
            backgroundExecutor.execute(() -> {
                try {
                    AppDatabase db = AppDatabase.getDatabase(requireContext().getApplicationContext());
                    CheckIn latestCheckIn = db.checkInDao().getLatestCheckIn(userId);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
                    String todayStr = sdf.format(new Date());

                    boolean isToday = false;
                    if (latestCheckIn != null && latestCheckIn.getTimestamp() > 0) {
                        isToday = todayStr.equals(sdf.format(new Date(latestCheckIn.getTimestamp())));
                    }

                    if (isToday && latestCheckIn != null) {
                        // Update today's existing check-in to avoid duplicate counts
                        latestCheckIn.setMood(moodTitle);
                        latestCheckIn.setTimestamp(now);
                        db.checkInDao().updateCheckIn(latestCheckIn);
                        FirebaseSyncHelper.syncCheckInToRealtimeDb(userId, latestCheckIn);
                    } else {
                        CheckIn checkIn = new CheckIn(userId, moodTitle, "Quick daily check-in from Home.", now);
                        db.checkInDao().insertCheckIn(checkIn);
                        FirebaseSyncHelper.syncCheckInToRealtimeDb(userId, checkIn);
                    }

                    if (getActivity() != null) {
                        requireActivity().runOnUiThread(this::refreshHomeStatus);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error saving quick check-in", e);
                }
            });
        }
    }

    private void openCheckInActivity(String preselectedMood) {
        Intent intent = new Intent(requireContext(), CheckInActivity.class);
        if (preselectedMood != null) {
            intent.putExtra("SELECTED_MOOD", preselectedMood);
        }
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshHomeStatus();
    }

    private void refreshHomeStatus() {
        if (binding == null) return;

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUser.reload().addOnCompleteListener(t -> {
                if (binding != null && currentUser.getDisplayName() != null && !currentUser.getDisplayName().isEmpty()) {
                    binding.tvGreetingTitle.setText("Welcome back, " + currentUser.getDisplayName() + "! 🌱");
                }
            });
            if (currentUser.getDisplayName() != null && !currentUser.getDisplayName().isEmpty()) {
                binding.tvGreetingTitle.setText("Welcome back, " + currentUser.getDisplayName() + "! 🌱");
            }
        }

        final String userId = currentUser != null ? currentUser.getUid() : "anonymous";

        // Read Practice Gym stats from SharedPreferences
        if (getContext() != null) {
            SharedPreferences prefs = requireContext().getSharedPreferences("little_by_little_prefs", Context.MODE_PRIVATE);
            int practiceCount = prefs.getInt("practice_count_" + userId, 0);
            if (binding.pbPractice != null) {
                binding.pbPractice.setMax(5);
                binding.pbPractice.setProgress(Math.min(5, practiceCount));
            }
            if (binding.tvPracticeRatio != null) {
                binding.tvPracticeRatio.setText(Math.min(5, practiceCount) + "/5");
            }
        }

        // Query check-in & streak in background thread
        if (backgroundExecutor != null && !backgroundExecutor.isShutdown() && getContext() != null) {
            backgroundExecutor.execute(() -> {
                try {
                    AppDatabase db = AppDatabase.getDatabase(requireContext().getApplicationContext());
                    List<CheckIn> allCheckIns = db.checkInDao().getAllCheckIns(userId);
                    CheckIn latestCheckIn = db.checkInDao().getLatestCheckIn(userId);

                    boolean isToday = false;
                    if (latestCheckIn != null && latestCheckIn.getTimestamp() > 0) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
                        String todayStr = sdf.format(new Date());
                        String checkInStr = sdf.format(new Date(latestCheckIn.getTimestamp()));
                        isToday = todayStr.equals(checkInStr);
                    }

                    final boolean completedToday = isToday;
                    final int streakDays = calculateConsecutiveDayStreak(allCheckIns);

                    if (getActivity() != null) {
                        requireActivity().runOnUiThread(() -> {
                            if (binding == null) return;
                            if (completedToday) {
                                binding.badgeTaskCheckin.setText("Complete");
                                binding.badgeTaskCheckin.setBackgroundResource(R.drawable.bg_pill_complete);
                                binding.badgeTaskCheckin.setTextColor(getResources().getColor(R.color.white));
                            } else {
                                binding.badgeTaskCheckin.setText("Pending");
                                binding.badgeTaskCheckin.setBackgroundResource(R.drawable.bg_pill_pending);
                                binding.badgeTaskCheckin.setTextColor(getResources().getColor(R.color.text_secondary));
                            }
                            binding.tvStreakValue.setText(streakDays + (streakDays == 1 ? " day" : " days"));
                        });
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error refreshing home status", e);
                }
            });
        }
    }

    private int calculateConsecutiveDayStreak(List<CheckIn> checkIns) {
        if (checkIns == null || checkIns.isEmpty()) {
            return 0;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        String todayStr = sdf.format(new Date());

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        String yesterdayStr = sdf.format(cal.getTime());

        TreeSet<String> distinctDatesDesc = new TreeSet<>(Collections.reverseOrder());
        for (CheckIn c : checkIns) {
            if (c.getTimestamp() > 0) {
                distinctDatesDesc.add(sdf.format(new Date(c.getTimestamp())));
            }
        }

        if (distinctDatesDesc.isEmpty()) {
            return 0;
        }

        String latestDate = distinctDatesDesc.first();
        if (!latestDate.equals(todayStr) && !latestDate.equals(yesterdayStr)) {
            return 0;
        }

        int streak = 0;
        Calendar checkCal = Calendar.getInstance();
        if (latestDate.equals(yesterdayStr)) {
            checkCal.add(Calendar.DAY_OF_YEAR, -1);
        }

        while (true) {
            String expectedDate = sdf.format(checkCal.getTime());
            if (distinctDatesDesc.contains(expectedDate)) {
                streak++;
                checkCal.add(Calendar.DAY_OF_YEAR, -1);
            } else {
                break;
            }
        }

        return Math.max(1, streak);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        timerUpdateHandler.removeCallbacks(timerUpdateRunnable);

        if (backgroundExecutor != null) {
            backgroundExecutor.shutdown();
            backgroundExecutor = null;
        }

        if (isBound) {
            try {
                requireContext().unbindService(calmServiceConnection);
            } catch (Exception ignored) {}
            isBound = false;
        }

        binding = null;
    }
}
