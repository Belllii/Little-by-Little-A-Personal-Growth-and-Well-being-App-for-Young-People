package com.example.littlebylittle;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class FirebaseSyncHelper {

    private static final String TAG = "FirebaseSyncHelper";

    public interface OnUploadListener {
        void onSuccess(String downloadUrl);
        void onFailure(Exception exception);
    }

    // ==========================================
    // 1. FIREBASE REALTIME DATABASE (Check-Ins & Stats)
    // ==========================================

    public static void syncCheckInToRealtimeDb(String userId, CheckIn checkIn) {
        if (userId == null || userId.isEmpty()) return;

        try {
            DatabaseReference userCheckInsRef = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(userId)
                    .child("check_ins");

            String key = String.valueOf(checkIn.getTimestamp());
            userCheckInsRef.child(key).setValue(checkIn)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Check-in synced to Realtime DB: " + key))
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to sync check-in to Realtime DB", e));

        } catch (Exception e) {
            Log.e(TAG, "Realtime DB sync error", e);
        }
    }

    public static void deleteCheckInFromRealtimeDb(String userId, long timestamp) {
        if (userId == null || userId.isEmpty()) return;

        try {
            FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(userId)
                    .child("check_ins")
                    .child(String.valueOf(timestamp))
                    .removeValue()
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Check-in deleted from Realtime DB: " + timestamp))
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to delete check-in from Realtime DB", e));
        } catch (Exception e) {
            Log.e(TAG, "Realtime DB delete error", e);
        }
    }

    public static void updateStatsInRealtimeDb(String userId, int totalCheckIns, int totalJournals) {
        if (userId == null || userId.isEmpty()) return;

        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalCheckIns", totalCheckIns);
            stats.put("totalJournals", totalJournals);
            stats.put("lastActive", System.currentTimeMillis());

            FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(userId)
                    .child("growth_stats")
                    .updateChildren(stats);
        } catch (Exception e) {
            Log.e(TAG, "Realtime DB stats error", e);
        }
    }

    // ==========================================
    // 2. FIREBASE CLOUD FIRESTORE (Journals & Community Social Feed)
    // ==========================================

    public static void syncJournalToFirestore(String userId, JournalEntry entry) {
        if (userId == null || userId.isEmpty()) return;

        try {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String docId = String.valueOf(entry.getId() != 0 ? entry.getId() : entry.getTimestamp());

            db.collection("users")
                    .document(userId)
                    .collection("journals")
                    .document(docId)
                    .set(entry, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Journal synced to Firestore: " + docId))
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to sync journal to Firestore", e));

        } catch (Exception e) {
            Log.e(TAG, "Firestore sync error", e);
        }
    }

    public static void updateJournalInFirestore(String userId, int entryId, String newContent) {
        if (userId == null || userId.isEmpty()) return;

        try {
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(userId)
                    .collection("journals")
                    .document(String.valueOf(entryId))
                    .update("content", newContent)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Journal updated in Firestore: " + entryId))
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to update journal in Firestore", e));
        } catch (Exception e) {
            Log.e(TAG, "Firestore update error", e);
        }
    }

    public static void deleteJournalFromFirestore(String userId, int entryId) {
        if (userId == null || userId.isEmpty()) return;

        try {
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(userId)
                    .collection("journals")
                    .document(String.valueOf(entryId))
                    .delete()
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Journal deleted from Firestore: " + entryId))
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to delete journal from Firestore", e));
        } catch (Exception e) {
            Log.e(TAG, "Firestore delete error", e);
        }
    }

    public static void postToCommunityFeed(CommunityPost post, @NonNull final Runnable onComplete) {
        try {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference newDocRef = db.collection("community_feed").document();
            post.setId(newDocRef.getId());

            newDocRef.set(post)
                    .addOnSuccessListener(aVoid -> onComplete.run())
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to create community post", e);
                        onComplete.run();
                    });
        } catch (Exception e) {
            Log.e(TAG, "Community post error", e);
            onComplete.run();
        }
    }

    public static void toggleLikePost(String postId, String userId) {
        if (postId == null || userId == null) return;

        try {
            DocumentReference postRef = FirebaseFirestore.getInstance()
                    .collection("community_feed")
                    .document(postId);

            postRef.get().addOnSuccessListener(snapshot -> {
                if (snapshot.exists()) {
                    CommunityPost post = snapshot.toObject(CommunityPost.class);
                    if (post != null) {
                        boolean isLiked = post.getLikedBy() != null && post.getLikedBy().contains(userId);
                        if (isLiked) {
                            postRef.update(
                                    "likedBy", FieldValue.arrayRemove(userId),
                                    "likeCount", FieldValue.increment(-1)
                            );
                        } else {
                            postRef.update(
                                    "likedBy", FieldValue.arrayUnion(userId),
                                    "likeCount", FieldValue.increment(1)
                            );
                        }
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Toggle like error", e);
        }
    }

    // ==========================================
    // 3. FIREBASE STORAGE (Images & Media)
    // ==========================================

    public static void uploadProfileImage(String userId, Uri imageUri, final OnUploadListener listener) {
        if (userId == null || imageUri == null) {
            if (listener != null) listener.onFailure(new IllegalArgumentException("Invalid user or URI"));
            return;
        }

        try {
            StorageReference storageRef = FirebaseStorage.getInstance()
                    .getReference("profile_images")
                    .child(userId + ".jpg");

            storageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                if (listener != null) listener.onSuccess(uri.toString());
                            })
                            .addOnFailureListener(e -> {
                                if (listener != null) listener.onFailure(e);
                            }))
                    .addOnFailureListener(e -> {
                        if (listener != null) listener.onFailure(e);
                    });
        } catch (Exception e) {
            if (listener != null) listener.onFailure(e);
        }
    }
}
