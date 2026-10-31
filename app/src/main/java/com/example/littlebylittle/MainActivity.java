package com.example.littlebylittle;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.littlebylittle.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationBarView;
import android.content.Intent;
import android.widget.Button;
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Default screen on first launch
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        binding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
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

    public void selectBottomTab(int menuItemId) {
        binding.bottomNavigation.setSelectedItemId(menuItemId);
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}