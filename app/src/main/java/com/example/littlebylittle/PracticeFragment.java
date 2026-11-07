
package com.example.littlebylittle;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class PracticeFragment extends Fragment {

    private TextView tvScenarioCount;
    private ProgressBar pbScenario;
    private TextView tvScenarioTitle;
    private TextView tvScenarioDescription;
    private TextView tvFeedback;

    private Button btnOptionA;
    private Button btnOptionB;
    private Button btnOptionC;
    private Button btnNextScenario;

    private View cardCompletion;
    private Button btnPracticeAgain;

    private int currentScenario = 0;

    private final String[] titles = {
            "Starting a conversation",
            "Joining a group conversation",
            "Asking someone for help",
            "Expressing disagreement politely",
            "Setting personal boundaries"
    };

    private final String[] descriptions = {
            "You are sitting beside a classmate before class begins. You would like to start a conversation. What could you say?",
            "A group of classmates are discussing a topic that interests you. You would like to join their conversation. What could you do?",
            "You are having difficulty understanding an assignment. What could you say to someone who might help?",
            "A friend suggests something you do not agree with. How could you express your opinion respectfully?",
            "Someone asks you to do something, but you are not comfortable with it. How could you respond?"
    };

    private final String[][] options = {
            {
                    "Hi! How is your day going?",
                    "Wait for them to start every conversation.",
                    "Ask several personal questions immediately."
            },
            {
                    "Listen briefly, then ask a related question.",
                    "Interrupt and immediately change the topic.",
                    "Assume they do not want you to join."
            },
            {
                    "Could you please explain this part to me?",
                    "Pretend you understand everything.",
                    "Demand that someone complete it for you."
            },
            {
                    "I see your point, but I think differently.",
                    "Tell them their opinion is completely stupid.",
                    "Agree even though you have a different opinion."
            },
            {
                    "I'm sorry, but I'm not comfortable with that.",
                    "Agree even though you do not want to.",
                    "Respond angrily without explaining."
            }
    };

    private final String[][] feedback = {
            {
                    "A friendly, simple question is a good way to open a conversation. You do not need a perfect opening line.",
                    "Waiting is also an option, but starting with a small greeting can create an opportunity to connect.",
                    "Personal questions may feel overwhelming at first. Begin with a simple, friendly topic."
            },
            {
                    "Listening first and asking a related question can help you enter the conversation naturally.",
                    "Interrupting may make it difficult for others to follow. Try finding a natural moment to speak.",
                    "You cannot always know what others are thinking. A polite question can help you find out."
            },
            {
                    "This clearly explains what you need while giving the other person a chance to help.",
                    "It is okay not to understand something. Asking questions is a normal part of learning.",
                    "Asking for an explanation is more respectful and useful than expecting someone to do the work for you."
            },
            {
                    "You can disagree while still respecting another person's perspective. This is a useful communication skill.",
                    "Strong language can hurt the conversation. Try explaining your point calmly instead.",
                    "You are allowed to have a different opinion. You do not have to agree with everything."
            },
            {
                    "This communicates your boundary clearly and respectfully. You are allowed to say no.",
                    "You do not have to accept every request. It is okay to communicate what you are comfortable with.",
                    "Expressing your boundary calmly can make your message easier to understand."
            }
    };

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        return inflater.inflate(
                R.layout.fragment_practice,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        tvScenarioCount = view.findViewById(R.id.tv_scenario_count);
        pbScenario = view.findViewById(R.id.pb_practice_scenario);
        tvScenarioTitle = view.findViewById(R.id.tv_scenario_title);
        tvScenarioDescription = view.findViewById(R.id.tv_scenario_description);
        tvFeedback = view.findViewById(R.id.tv_feedback);

        btnOptionA = view.findViewById(R.id.btn_option_a);
        btnOptionB = view.findViewById(R.id.btn_option_b);
        btnOptionC = view.findViewById(R.id.btn_option_c);
        btnNextScenario = view.findViewById(R.id.btn_next_scenario);

        cardCompletion = view.findViewById(R.id.card_practice_completion);
        btnPracticeAgain = view.findViewById(R.id.btn_practice_again);

        btnOptionA.setOnClickListener(v -> showFeedback(0));
        btnOptionB.setOnClickListener(v -> showFeedback(1));
        btnOptionC.setOnClickListener(v -> showFeedback(2));

        btnNextScenario.setOnClickListener(v -> {
            recordPracticeCompleted();
            currentScenario++;

            if (currentScenario >= titles.length) {
                // Completed all scenarios!
                if (cardCompletion != null) {
                    cardCompletion.setVisibility(View.VISIBLE);
                }
                btnNextScenario.setVisibility(View.GONE);
                tvFeedback.setVisibility(View.GONE);
            } else {
                loadScenario();
            }
        });

        if (btnPracticeAgain != null) {
            btnPracticeAgain.setOnClickListener(v -> {
                currentScenario = 0;
                if (cardCompletion != null) {
                    cardCompletion.setVisibility(View.GONE);
                }
                loadScenario();
            });
        }

        loadScenario();
    }

    private void loadScenario() {
        tvScenarioCount.setText(
                "SCENARIO " + (currentScenario + 1) + " OF " + titles.length
        );

        if (pbScenario != null) {
            pbScenario.setMax(titles.length);
            pbScenario.setProgress(currentScenario + 1);
        }

        tvScenarioTitle.setText(titles[currentScenario]);
        tvScenarioDescription.setText(descriptions[currentScenario]);

        btnOptionA.setText(options[currentScenario][0]);
        btnOptionB.setText(options[currentScenario][1]);
        btnOptionC.setText(options[currentScenario][2]);

        tvFeedback.setVisibility(View.GONE);
        btnNextScenario.setVisibility(View.GONE);

        btnOptionA.setEnabled(true);
        btnOptionB.setEnabled(true);
        btnOptionC.setEnabled(true);
    }

    private void showFeedback(int selectedOption) {
        tvFeedback.setText(
                feedback[currentScenario][selectedOption]
        );

        tvFeedback.setVisibility(View.VISIBLE);

        btnOptionA.setEnabled(true);
        btnOptionB.setEnabled(true);
        btnOptionC.setEnabled(true);

        if (currentScenario == titles.length - 1) {
            btnNextScenario.setText("Complete Practice 🎉");
        } else {
            btnNextScenario.setText("Next Scenario ➡️");
        }

        btnNextScenario.setVisibility(View.VISIBLE);
    }

    private void recordPracticeCompleted() {
        Context context = getContext();
        if (context == null) return;

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser != null ? currentUser.getUid() : "anonymous";

        SharedPreferences prefs = context.getSharedPreferences("little_by_little_prefs", Context.MODE_PRIVATE);
        int currentCount = prefs.getInt("practice_count_" + userId, 0);
        int newCount = currentCount + 1;
        prefs.edit().putInt("practice_count_" + userId, newCount).apply();

        // Sync to Firebase Realtime Database
        if (currentUser != null) {
            try {
                FirebaseDatabase.getInstance()
                        .getReference("users")
                        .child(userId)
                        .child("growth_stats")
                        .child("practiceCount")
                        .setValue(newCount);
            } catch (Exception ignored) {}
        }

        // Lab 10: Custom Broadcast Event
        Intent broadcastIntent = new Intent(SystemStatusReceiver.ACTION_GROWTH_DATA_UPDATED);
        broadcastIntent.setPackage(context.getPackageName());
        context.sendBroadcast(broadcastIntent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        tvScenarioCount = null;
        pbScenario = null;
        tvScenarioTitle = null;
        tvScenarioDescription = null;
        tvFeedback = null;

        btnOptionA = null;
        btnOptionB = null;
        btnOptionC = null;
        btnNextScenario = null;
        cardCompletion = null;
        btnPracticeAgain = null;
    }
}
