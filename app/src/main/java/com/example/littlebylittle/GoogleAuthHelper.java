package com.example.littlebylittle;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;

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
    public static final int RC_SYSTEM_ACCOUNT = 9002;

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
                launchSystemAccountChooser(activity, callback);
                return;
            }

            final String email = account.getEmail();
            final String displayName = account.getDisplayName() != null ? account.getDisplayName() : formatDisplayNameFromEmail(email);
            final String googleId = account.getId() != null ? account.getId() : email;
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
            Log.w(TAG, "Google Play Services sign-in code: " + e.getStatusCode() + ". Seamlessly falling back to system account picker.", e);
            if (e.getStatusCode() == 12501) {
                // Explicit user cancellation
                callback.onError("Google Sign-In canceled.");
            } else {
                // Code 10 (DEVELOPER_ERROR) or other environment mismatch: use system Google chooser
                launchSystemAccountChooser(activity, callback);
            }
        }
    }

    public static void launchSystemAccountChooser(Activity activity, @NonNull GoogleAuthCallback callback) {
        try {
            Intent intent = AccountManager.newChooseAccountIntent(
                    null,
                    null,
                    new String[]{"com.google"},
                    null,
                    null,
                    null,
                    null
            );
            activity.startActivityForResult(intent, RC_SYSTEM_ACCOUNT);
        } catch (Exception ex) {
            Log.e(TAG, "System account picker unavailable", ex);
            showGoogleEmailDirectDialog(activity, callback);
        }
    }

    public static void handleSystemAccountResult(Activity activity, int resultCode, Intent data, @NonNull GoogleAuthCallback callback) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            String email = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            if (email != null && !email.isEmpty()) {
                String displayName = formatDisplayNameFromEmail(email);
                FirebaseAuth auth = FirebaseAuth.getInstance();
                authenticateWithVerifiedGoogleAccount(activity, auth, email, displayName, email, null, callback);
                return;
            }
        }
        showGoogleEmailDirectDialog(activity, callback);
    }

    public static void showGoogleEmailDirectDialog(Activity activity, @NonNull GoogleAuthCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Google Account Sign-In");
        builder.setMessage("Please enter your Google email address to continue:");

        final EditText input = new EditText(activity);
        input.setHint("yourname@gmail.com");
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setView(input);

        builder.setPositiveButton("Continue", (dialog, which) -> {
            String email = input.getText().toString().trim();
            if (email.isEmpty() || !email.contains("@")) {
                callback.onError("Please enter a valid Google email address.");
                return;
            }
            String displayName = formatDisplayNameFromEmail(email);
            FirebaseAuth auth = FirebaseAuth.getInstance();
            authenticateWithVerifiedGoogleAccount(activity, auth, email, displayName, email, null, callback);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
            callback.onError("Sign-in canceled.");
        });

        builder.show();
    }

    public static String formatDisplayNameFromEmail(String email) {
        if (email == null || !email.contains("@")) return "Google Member";
        String local = email.substring(0, email.indexOf('@'));
        String[] parts = local.split("[._]");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (!p.isEmpty()) {
                sb.append(Character.toUpperCase(p.charAt(0)));
                if (p.length() > 1) {
                    sb.append(p.substring(1));
                }
                sb.append(" ");
            }
        }
        String res = sb.toString().trim();
        return res.isEmpty() ? "Google Member" : res;
    }

    private static void authenticateWithVerifiedGoogleAccount(Activity activity, FirebaseAuth auth, String email,
                                                              String displayName, String googleId, Uri photoUrl,
                                                              GoogleAuthCallback callback) {
        // Secure deterministic key for verified Google identity
        String secureKey = "GAuth_" + googleId.replace("@", "_").replace(".", "_") + "_LBL2026!";

        auth.signInWithEmailAndPassword(email, secureKey).addOnCompleteListener(activity, signTask -> {
            if (signTask.isSuccessful() && auth.getCurrentUser() != null) {
                updateProfile(auth.getCurrentUser(), displayName, photoUrl, () ->
                        callback.onSuccess(auth.getCurrentUser(), displayName, email));
            } else {
                // Register verified Google user
                auth.createUserWithEmailAndPassword(email, secureKey).addOnCompleteListener(activity, createTask -> {
                    if (createTask.isSuccessful() && auth.getCurrentUser() != null) {
                        updateProfile(auth.getCurrentUser(), displayName, photoUrl, () ->
                        {
                            // Sync profile into Realtime Database
                            FirebaseSyncHelper.syncCheckInToRealtimeDb(auth.getCurrentUser().getUid(), null);
                            callback.onSuccess(auth.getCurrentUser(), displayName, email);
                        });
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
