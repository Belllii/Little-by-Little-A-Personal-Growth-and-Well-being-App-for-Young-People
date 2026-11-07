package com.example.littlebylittle;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.littlebylittle.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "LIFECYCLE_MainActivity";
    private static final int REQUEST_CODE_POST_NOTIFICATIONS = 501;

    private ActivityMainBinding binding;
    private SystemStatusReceiver systemStatusReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate() called");

        // Check Firebase login status
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            openWelcomeScreen();
            return;
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Lab 10: Notification Channels setup
        NotificationHelper.createNotificationChannels(this);

        // Lab 10: Runtime permissions for Notifications (Android 13+)
        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(this, "android.permission.POST_NOTIFICATIONS")
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{"android.permission.POST_NOTIFICATIONS"},
                        REQUEST_CODE_POST_NOTIFICATIONS
                );
            }
        }

        // Initialize BroadcastReceiver for system status
        systemStatusReceiver = new SystemStatusReceiver();

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
        Log.d(TAG, "onStart() called");
        if (FirebaseAuth.getInstance().getCurrentUser() == null && !isFinishing()) {
            openWelcomeScreen();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called - Registering System BroadcastReceiver");
        // Lab 10: Dynamic BroadcastReceiver Registration
        if (systemStatusReceiver != null) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
            filter.addAction(Intent.ACTION_BATTERY_LOW);
            filter.addAction(Intent.ACTION_BATTERY_OKAY);
            filter.addAction(SystemStatusReceiver.ACTION_GROWTH_DATA_UPDATED);
            registerReceiver(systemStatusReceiver, filter);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called - Unregistering System BroadcastReceiver");
        // Lab 10: Unregister BroadcastReceiver to prevent memory leaks
        if (systemStatusReceiver != null) {
            try {
                unregisterReceiver(systemStatusReceiver);
            } catch (Exception ignored) {}
        }
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

    public void selectBottomTab(int menuItemId) {
        if (binding != null) {
            binding.bottomNavigation.setSelectedItemId(menuItemId);
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void openWelcomeScreen() {
        Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
