package com.example.littlebylittle;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.littlebylittle.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Profile Avatar Click
        binding.ivAvatar.setOnClickListener(v -> 
            Toast.makeText(requireContext(), "Profile: Dominic", Toast.LENGTH_SHORT).show()
        );

        // Daily Emotional Check-in Listeners
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

        // Task Planner items - click to navigate or toggle
        binding.rowTaskCheckin.setOnClickListener(v -> {
            binding.cardMoodCheckin.requestFocus();
            Toast.makeText(requireContext(), "Select your mood above to complete check-in", Toast.LENGTH_SHORT).show();
        });

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

    private void onMoodSelected(String moodTitle, String feedback) {
        // Display the feedback message in the banner
        binding.tvMoodFeedback.setText(feedback);
        binding.tvMoodFeedback.setVisibility(View.VISIBLE);

        // Update the Task Planner check-in status badge to Complete
        binding.badgeTaskCheckin.setText("Complete");
        binding.badgeTaskCheckin.setBackgroundResource(R.drawable.bg_pill_solid);
        binding.badgeTaskCheckin.setTextColor(getResources().getColor(R.color.white));

        Toast.makeText(requireContext(), "Check-in logged: " + moodTitle, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
