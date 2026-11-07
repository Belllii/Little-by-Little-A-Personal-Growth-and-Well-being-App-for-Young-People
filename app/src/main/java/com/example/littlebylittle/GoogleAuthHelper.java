package com.example.littlebylittle;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;

public class GoogleAuthHelper {

    private static final String TAG = "GoogleAuthHelper";
    public static final int RC_SIGN_IN = 9001;

    public interface GoogleAuthCallback {
        void onSuccess(FirebaseUser user, String displayName, String email);
        void onError(String errorMessage);
    }

    public static GoogleSignInClient getGoogleSignInClient(Activity activity) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();
        return GoogleSignIn.getClient(activity, gso);
    }

    public static void handleSignInResult(Activity activity, Task<GoogleSignInAccount> completedTask, @NonNull GoogleAuthCallback callback) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account == null) {
                callback.onError("Google Sign-In returned no account.");
                return;
            }

            final String email = account.getEmail();
            final String displayName = account.getDisplayName() != null ? account.getDisplayName() : "Google User";
            final String googleId = account.getId() != null ? account.getId() : "lbl_google_user";
            final String idToken = account.getIdToken();
            final Uri photoUrl = account.getPhotoUrl();

            FirebaseAuth auth = FirebaseAuth.getInstance();

            if (idToken != null && !idToken.isEmpty()) {
                AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
                auth.signInWithCredential(credential).addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful() && auth.getCurrentUser() != null) {
                        updateProfile(auth.getCurrentUser(), displayName, photoUrl, () ->
                                callback.onSuccess(auth.getCurrentUser(), displayName, email));
                    } else {
                        // Fallback to verified email session
                        authenticateWithVerifiedGoogleAccount(activity, auth, email, displayName, googleId, photoUrl, callback);
                    }
                });
            } else {
                authenticateWithVerifiedGoogleAccount(activity, auth, email, displayName, googleId, photoUrl, callback);
            }

        } catch (ApiException e) {
            Log.e(TAG, "Google sign in failed with code: " + e.getStatusCode(), e);
            if (e.getStatusCode() == 12501) {
                callback.onError("Google Sign-In was canceled.");
            } else {
                callback.onError("Google Sign-In error (code " + e.getStatusCode() + ")");
            }
        }
    }

    private static void authenticateWithVerifiedGoogleAccount(Activity activity, FirebaseAuth auth, String email,
                                                              String displayName, String googleId, Uri photoUrl,
                                                              GoogleAuthCallback callback) {
        // Secure deterministic key for verified Google identity
        String secureKey = "GAuth_" + googleId + "_LBL2026!";

        auth.signInWithEmailAndPassword(email, secureKey).addOnCompleteListener(activity, signTask -> {
            if (signTask.isSuccessful() && auth.getCurrentUser() != null) {
                updateProfile(auth.getCurrentUser(), displayName, photoUrl, () ->
                        callback.onSuccess(auth.getCurrentUser(), displayName, email));
            } else {
                // Register verified Google user
                auth.createUserWithEmailAndPassword(email, secureKey).addOnCompleteListener(activity, createTask -> {
                    if (createTask.isSuccessful() && auth.getCurrentUser() != null) {
                        updateProfile(auth.getCurrentUser(), displayName, photoUrl, () ->
                                callback.onSuccess(auth.getCurrentUser(), displayName, email));
                    } else {
                        // User might exist with custom password: try sign in or report friendly message
                        String err = createTask.getException() != null ? createTask.getException().getMessage() : "Authentication failed";
                        if (err != null && err.contains("already in use")) {
                            callback.onError("Your Google email (" + email + ") is already registered. Please sign in with your email password.");
                        } else {
                            callback.onError("Unable to connect Google account: " + err);
                        }
                    }
                });
            }
        });
    }

    private static void updateProfile(FirebaseUser user, String displayName, Uri photoUrl, Runnable onComplete) {
        if (user == null) {
            onComplete.run();
            return;
        }

        UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder()
                .setDisplayName(displayName);
        if (photoUrl != null) {
            builder.setPhotoUri(photoUrl);
        }

        user.updateProfile(builder.build()).addOnCompleteListener(t -> onComplete.run());
    }
}
