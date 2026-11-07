package com.example.littlebylittle;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CommunityFeedActivity extends Activity {

    private static final String TAG = "CommunityFeedActivity";

    private EditText etPostContent;
    private Spinner spinnerTag;
    private Button btnSubmitPost;
    private TextView tvEmptyFeed;
    private RecyclerView rvCommunityFeed;

    private CommunityAdapter adapter;
    private final List<CommunityPost> postList = new ArrayList<>();
    private ListenerRegistration firestoreListener;

    private final String[] categories = {
            "SocialWin",
            "OvercomingWorry",
            "SelfCompassion",
            "DailyGratitude",
            "KindWords"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate() called");
        setContentView(R.layout.activity_community_feed);

        etPostContent = findViewById(R.id.et_new_post_content);
        spinnerTag = findViewById(R.id.spinner_post_tag);
        btnSubmitPost = findViewById(R.id.btn_submit_post);
        tvEmptyFeed = findViewById(R.id.tv_empty_feed);
        rvCommunityFeed = findViewById(R.id.rv_community_feed);

        // Spinner setup
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                categories
        );
        spinnerTag.setAdapter(spinnerAdapter);

        // RecyclerView setup
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = currentUser != null ? currentUser.getUid() : "guest";

        rvCommunityFeed.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CommunityAdapter(this, postList, currentUserId, post -> {
            FirebaseSyncHelper.toggleLikePost(post.getId(), currentUserId);
        });
        rvCommunityFeed.setAdapter(adapter);

        btnSubmitPost.setOnClickListener(v -> publishPost());

        listenToCommunityFeed();
    }

    private void listenToCommunityFeed() {
        try {
            // Lab 11: Firebase Cloud Firestore real-time collection reading
            firestoreListener = FirebaseFirestore.getInstance()
                    .collection("community_feed")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .addSnapshotListener((snapshots, e) -> {
                        if (e != null) {
                            Log.e(TAG, "Listen failed: ", e);
                            loadSampleFallbackPosts();
                            return;
                        }

                        if (snapshots != null) {
                            postList.clear();
                            for (QueryDocumentSnapshot doc : snapshots) {
                                CommunityPost post = doc.toObject(CommunityPost.class);
                                post.setId(doc.getId());
                                postList.add(post);
                            }

                            if (postList.isEmpty()) {
                                loadSampleFallbackPosts();
                            } else {
                                tvEmptyFeed.setVisibility(View.GONE);
                                rvCommunityFeed.setVisibility(View.VISIBLE);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });

        } catch (Exception e) {
            Log.e(TAG, "Error initiating Firestore listener", e);
            loadSampleFallbackPosts();
        }
    }

    private void loadSampleFallbackPosts() {
        if (!postList.isEmpty()) return;

        postList.add(new CommunityPost(
                "sample_1",
                "sample_u1",
                "Maya R.",
                "I attended my study group today even though I felt super nervous about speaking up. One step at a time! 🌱",
                "SocialWin",
                System.currentTimeMillis() - 3600000
        ));
        postList.add(new CommunityPost(
                "sample_2",
                "sample_u2",
                "Jordan K.",
                "Reminder: It's okay to take breaks when social interactions get draining. Protect your energy.",
                "SelfCompassion",
                System.currentTimeMillis() - 7200000
        ));

        tvEmptyFeed.setVisibility(View.GONE);
        rvCommunityFeed.setVisibility(View.VISIBLE);
        adapter.notifyDataSetChanged();
    }

    private void publishPost() {
        String content = etPostContent.getText().toString().trim();
        if (content.isEmpty()) {
            etPostContent.setError("Write something to share!");
            return;
        }

        btnSubmitPost.setEnabled(false);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser != null ? currentUser.getUid() : "guest";
        String authorName = (currentUser != null && currentUser.getDisplayName() != null && !currentUser.getDisplayName().isEmpty())
                ? currentUser.getDisplayName()
                : "Friend";

        String category = categories[spinnerTag.getSelectedItemPosition()];

        CommunityPost post = new CommunityPost(
                "",
                userId,
                authorName,
                content,
                category,
                System.currentTimeMillis()
        );

        FirebaseSyncHelper.postToCommunityFeed(post, () -> {
            btnSubmitPost.setEnabled(true);
            etPostContent.setText("");
            Toast.makeText(this, "Shared to community! 💬🌱", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called - Removing Firestore listeners");
        if (firestoreListener != null) {
            firestoreListener.remove();
        }
    }
}
