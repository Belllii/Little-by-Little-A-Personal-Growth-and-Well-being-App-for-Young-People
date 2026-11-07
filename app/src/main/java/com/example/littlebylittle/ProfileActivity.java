package com.example.littlebylittle;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.io.InputStream;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "LIFECYCLE_Profile";
    private static final int REQUEST_CODE_PICK_IMAGE = 401;

    private ImageView ivProfileAvatar;
    private TextView tvProfileName, tvProfileEmail;
    private EditText etEditName;
    private Button btnUpdateName, btnLogout, btnOpenSupportBuddy, btnTestRestApi;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate() called");
        setContentView(R.layout.activity_profile);

        ivProfileAvatar = findViewById(R.id.ivProfileAvatar);
        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);
        etEditName = findViewById(R.id.etEditName);
        btnUpdateName = findViewById(R.id.btnUpdateName);
        btnLogout = findViewById(R.id.btnLogout);
        btnOpenSupportBuddy = findViewById(R.id.btnOpenSupportBuddy);
        btnTestRestApi = findViewById(R.id.btnTestRestApi);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            tvProfileEmail.setText(user.getEmail());
            String name = user.getDisplayName();
            if (name != null && !name.isEmpty()) {
                tvProfileName.setText(name);
                etEditName.setText(name);
            } else {
                tvProfileName.setText("LittleByLittle User");
            }
        } else {
            openWelcomeScreen();
            return;
        }

        // Lab 10: Content Provider (Accessing Media Files) & Firebase Storage
        ivProfileAvatar.setOnClickListener(v -> {
            Intent pickImageIntent = new Intent(Intent.ACTION_GET_CONTENT);
            pickImageIntent.setType("image/*");
            startActivityForResult(pickImageIntent, REQUEST_CODE_PICK_IMAGE);
        });

        // Update profile name
        btnUpdateName.setOnClickListener(v -> {
            String newName = etEditName.getText().toString().trim();
            if (newName.isEmpty()) {
                etEditName.setError("Please enter your name");
                return;
            }

            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(newName)
                        .build();

                currentUser.updateProfile(profileUpdates).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        tvProfileName.setText(newName);
                        Toast.makeText(this, "Name updated successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to update name", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Lab 10: Content Provider navigation - Open Support Circle Buddy
        btnOpenSupportBuddy.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, TrustedContactActivity.class);
            startActivity(intent);
        });

        // Lab 11: HTTP POST Operation demonstration
        btnTestRestApi.setOnClickListener(v -> {
            Toast.makeText(this, "Sending HTTP POST request...", Toast.LENGTH_SHORT).show();
            QuoteApiClient client = new QuoteApiClient();
            client.postReflectionSync("Daily Goal", "Take small steady steps every day.", new QuoteApiClient.OnPostListener() {
                @Override
                public void onPostSuccess(String responseJson) {
                    Toast.makeText(ProfileActivity.this, "HTTP POST succeeded! Response received. 🌐", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "HTTP POST response: " + responseJson);
                }

                @Override
                public void onPostError(String message) {
                    Toast.makeText(ProfileActivity.this, "HTTP POST error: " + message, Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Logout
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_SHORT).show();
            openWelcomeScreen();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                try {
                    // Display preview immediately via ContentResolver
                    InputStream is = getContentResolver().openInputStream(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    ivProfileAvatar.setImageBitmap(bitmap);

                    // Lab 11: Firebase Storage - Upload Profile Picture
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        Toast.makeText(this, "Uploading photo to Firebase Storage...", Toast.LENGTH_SHORT).show();
                        FirebaseSyncHelper.uploadProfileImage(user.getUid(), imageUri, new FirebaseSyncHelper.OnUploadListener() {
                            @Override
                            public void onSuccess(String downloadUrl) {
                                Toast.makeText(ProfileActivity.this, "Profile photo uploaded! 📸", Toast.LENGTH_SHORT).show();
                                UserProfileChangeRequest updates = new UserProfileChangeRequest.Builder()
                                        .setPhotoUri(Uri.parse(downloadUrl))
                                        .build();
                                user.updateProfile(updates);
                            }

                            @Override
                            public void onFailure(Exception exception) {
                                Toast.makeText(ProfileActivity.this, "Photo upload failed: " + exception.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                } catch (Exception e) {
                    Log.e(TAG, "Error displaying or uploading selected image", e);
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }

    private void openWelcomeScreen() {
        Intent intent = new Intent(ProfileActivity.this, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
