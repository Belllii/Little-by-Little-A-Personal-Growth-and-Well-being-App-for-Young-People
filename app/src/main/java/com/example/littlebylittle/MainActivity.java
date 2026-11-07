
package com.example.littlebylittle;

import android.os.Bundle;
import android.view.MenuItem;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.littlebylittle.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check Firebase login status
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            openWelcomeScreen();
            return;
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        binding.bottomNavigation.setOnItemSelectedListener(
                new NavigationBarView.OnItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        int itemId = item.getItemId();

                        if (itemId == R.id.nav_home) {
                            loadFragment(new HomeFragment());
                            return true;

                        } else if (itemId == R.id.nav_practice) {
                            loadFragment(new PracticeFragment());
                            return true;

                        } else if (itemId == R.id.nav_journal) {
                            loadFragment(new JournalFragment());
                            return true;

                        } else if (itemId == R.id.nav_growth) {
                            loadFragment(new GrowthFragment());
                            return true;
                        }

                        return false;
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() == null && !isFinishing()) {
            openWelcomeScreen();
        }
    }

    public void selectBottomTab(int menuItemId) {
        binding.bottomNavigation.setSelectedItemId(menuItemId);
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void openWelcomeScreen() {

        Intent intent = new Intent(
                MainActivity.this,
                WelcomeActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        finish();
    }
}
