
package com.example.littlebylittle;

import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class WelcomeActivity extends AppCompatActivity {

    Button btnWelcomeLogin, btnWelcomeRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // If user is already logged in, redirect directly to Home
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_welcome);

        btnWelcomeLogin = findViewById(R.id.btnWelcomeLogin);
        btnWelcomeRegister = findViewById(R.id.btnWelcomeRegister);

        btnWelcomeLogin.setOnClickListener(v -> {
            Intent intent = new Intent(
                    WelcomeActivity.this,
                    LoginActivity.class);
            startActivity(intent);
        });

        btnWelcomeRegister.setOnClickListener(v -> {
            Intent intent = new Intent(
                    WelcomeActivity.this,
                    RegisterActivity.class);
            startActivity(intent);
        });
    }
}
