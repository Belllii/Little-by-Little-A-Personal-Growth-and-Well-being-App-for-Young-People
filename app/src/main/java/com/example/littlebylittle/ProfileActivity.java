package com.example.littlebylittle;

import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    TextView tvProfileEmail;
    Button btnLogout;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvProfileEmail = findViewById(R.id.tvProfileEmail);
        btnLogout = findViewById(R.id.btnLogout);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            tvProfileEmail.setText(user.getEmail());
        } else {
            openLoginScreen();
            return;
        }

        btnLogout.setOnClickListener(v -> {

            mAuth.signOut();

            Toast.makeText(this,
                    "Logged out successfully!",
                    Toast.LENGTH_SHORT).show();

            openLoginScreen();
        });
    }

    private void openLoginScreen() {
        Intent intent = new Intent(
                ProfileActivity.this,
                LoginActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        finish();
    }
}
