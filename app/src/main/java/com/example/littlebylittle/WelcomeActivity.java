
package com.example.littlebylittle;

import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class WelcomeActivity extends AppCompatActivity {

    Button btnWelcomeLogin, btnWelcomeRegister;
    android.view.View btnWelcomeGoogle;

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
        btnWelcomeGoogle = findViewById(R.id.btnWelcomeGoogle);

        if (btnWelcomeGoogle != null) {
            btnWelcomeGoogle.setOnClickListener(v -> startGoogleSignIn());
        }

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

    private void startGoogleSignIn() {
        com.google.android.gms.auth.api.signin.GoogleSignInClient client =
                GoogleAuthHelper.getGoogleSignInClient(this);
        client.signOut().addOnCompleteListener(this, t -> {
            Intent signInIntent = client.getSignInIntent();
            startActivityForResult(signInIntent, GoogleAuthHelper.RC_SIGN_IN);
        });
    }

    private final GoogleAuthHelper.GoogleAuthCallback googleAuthCallback = new GoogleAuthHelper.GoogleAuthCallback() {
        @Override
        public void onSuccess(com.google.firebase.auth.FirebaseUser user, String displayName, String email) {
            android.widget.Toast.makeText(WelcomeActivity.this, "Welcome, " + displayName + "! 🌱", android.widget.Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        @Override
        public void onError(String errorMessage) {
            android.widget.Toast.makeText(WelcomeActivity.this, errorMessage, android.widget.Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GoogleAuthHelper.RC_SIGN_IN) {
            com.google.android.gms.tasks.Task<com.google.android.gms.auth.api.signin.GoogleSignInAccount> task =
                    com.google.android.gms.auth.api.signin.GoogleSignIn.getSignedInAccountFromIntent(data);
            GoogleAuthHelper.handleSignInResult(this, task, googleAuthCallback);
        } else if (requestCode == GoogleAuthHelper.RC_SYSTEM_ACCOUNT) {
            GoogleAuthHelper.handleSystemAccountResult(this, resultCode, data, googleAuthCallback);
        }
    }
}
