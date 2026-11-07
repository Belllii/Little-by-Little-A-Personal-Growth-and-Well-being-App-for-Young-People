package com.example.littlebylittle;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import android.content.Intent;
import android.widget.TextView;

public class RegisterActivity extends AppCompatActivity {

    EditText etName, etEmail, etPassword;
    Button btnRegister;
    TextView tvLogin;
    android.view.View btnRegisterGoogle;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            navigateToHome();
            return;
        }

        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnRegisterGoogle = findViewById(R.id.btnRegisterGoogle);
        tvLogin = findViewById(R.id.tvLogin);

        btnRegister.setOnClickListener(v -> registerUser());

        if (btnRegisterGoogle != null) {
            btnRegisterGoogle.setOnClickListener(v -> startGoogleSignIn());
        }

        if (tvLogin != null) {
            tvLogin.setOnClickListener(v -> {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            });
        }
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
            Toast.makeText(RegisterActivity.this, "Welcome, " + displayName + "! 🌱", Toast.LENGTH_SHORT).show();
            navigateToHome();
        }

        @Override
        public void onError(String errorMessage) {
            Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();
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

    private void registerUser() {

        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this,
                    "Please fill in all fields",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this,
                    "Password must be at least 6 characters",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()) {

                        FirebaseUser user = mAuth.getCurrentUser();

                        if (user != null) {

                            UserProfileChangeRequest profileUpdates =
                                    new UserProfileChangeRequest.Builder()
                                            .setDisplayName(name)
                                            .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(profileTask -> {

                                        if (profileTask.isSuccessful()) {
                                            Toast.makeText(this,
                                                    "Account created successfully!",
                                                    Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(this,
                                                    "Account created, but name could not be saved.",
                                                    Toast.LENGTH_LONG).show();
                                        }

                                        navigateToHome();
                                    });
                        } else {
                            navigateToHome();
                        }
                    } else {

                        Toast.makeText(this,
                                "Registration failed: " +
                                        task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void navigateToHome() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
