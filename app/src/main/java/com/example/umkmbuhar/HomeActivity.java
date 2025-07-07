package com.example.umkmbuhar;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.umkmbuhar.Fragment.HomeFragment;
import com.example.umkmbuhar.Fragment.PostinganFragment;
import com.example.umkmbuhar.Fragment.ProfileFragment;
import com.example.umkmbuhar.Fragment.Profile2Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Load fragment awal (HomeFragment)
        loadFragment(new HomeFragment());

        // Inisialisasi BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.navigation_home) {
                selectedFragment = new HomeFragment();
            } else if (item.getItemId() == R.id.navigation_posting) {
                selectedFragment = new PostinganFragment();
            } else if (item.getItemId() == R.id.navigation_info) {
                // Cek status login hanya saat tombol Profile ditekan
                SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
                boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

                if (isLoggedIn) {
                    selectedFragment = new ProfileFragment();
                } else {
                    selectedFragment = new Profile2Fragment();
                }
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }
            return true;
        });
    }

    // Method untuk mengganti fragment
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
