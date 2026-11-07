
package com.example.littlebylittle;

import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class ProfileActivity extends AppCompatActivity {

    TextView tvProfileName, tvProfileEmail;
    EditText etEditName;
    Button btnUpdateName, btnLogout;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Connect XML views
        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);
        etEditName = findViewById(R.id.etEditName);
        btnUpdateName = findViewById(R.id.btnUpdateName);
        btnLogout = findViewById(R.id.btnLogout);

        mAuth = FirebaseAuth.getInstance();

        // Get currently logged-in user
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

        // Update profile name
        btnUpdateName.setOnClickListener(v -> {

            String newName = etEditName.getText()
                    .toString().trim();

            if (newName.isEmpty()) {
                etEditName.setError("Please enter your name");
                return;
            }

            FirebaseUser currentUser = mAuth.getCurrentUser();

            if (currentUser != null) {

                UserProfileChangeRequest profileUpdates =
                        new UserProfileChangeRequest.Builder()
                                .setDisplayName(newName)
                                .build();

                currentUser.updateProfile(profileUpdates)
                        .addOnCompleteListener(task -> {

                            if (task.isSuccessful()) {

                                tvProfileName.setText(newName);

                                Toast.makeText(this,
                                        "Name updated successfully!",
                                        Toast.LENGTH_SHORT).show();

                            } else {

                                Toast.makeText(this,
                                        "Failed to update name",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        // Logout
        btnLogout.setOnClickListener(v -> {

            mAuth.signOut();

            Toast.makeText(this,
                    "Logged out successfully!",
                    Toast.LENGTH_SHORT).show();

            openWelcomeScreen();
        });
    }

    private void openWelcomeScreen() {

        Intent intent = new Intent(
                ProfileActivity.this,
                WelcomeActivity.class);

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        finish();
    }
}
