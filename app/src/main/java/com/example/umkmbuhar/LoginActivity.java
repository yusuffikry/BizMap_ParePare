package com.example.umkmbuhar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.umkmbuhar.API.SupabaseApiService;
import com.example.umkmbuhar.Model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InhuanBza3Fibm56ZW14ZHl6Zm5yIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTE0NTk5MDksImV4cCI6MjA2NzAzNTkwOX0.ZkUQ16Pa-G0V-dUWzYH5KN3pmkmdBDY7NrJd-6II_qA";
    private static final String AUTHORIZATION = "Bearer " + API_KEY;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inisialisasi view
        EditText etUsername = findViewById(R.id.et_username);
        EditText etPassword = findViewById(R.id.et_password);
        Button btnLogin = findViewById(R.id.btn_login);
        TextView tvRegister = findViewById(R.id.tv_register);

        // SharedPreferences untuk menyimpan status login
        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Konfigurasi Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://xnjpskqbnnzemxdyzfnr.supabase.co/") // Ganti dengan URL proyek Anda
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SupabaseApiService apiService = retrofit.create(SupabaseApiService.class);

        // Login button
        btnLogin.setOnClickListener(view -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Semua field harus diisi!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Logging untuk debugging
            Log.d(TAG, "Attempting login with username: " + username);

            // Call API
            Call<List<User>> call = apiService.loginUser(
                    "eq." + username,
                    "eq." + password,
                    "*",
                    API_KEY,
                    AUTHORIZATION
            );
            call.enqueue(new Callback<List<User>>() {
                @Override
                public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                    // Debugging response
                    Log.d(TAG, "Response code: " + response.code());
                    Log.d(TAG, "Response body: " + response.body());

                    if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                        User user = response.body().get(0);
                        Toast.makeText(LoginActivity.this, "Login berhasil, Selamat datang: " + user.getFullname(), Toast.LENGTH_SHORT).show();

                        // Simpan status login
                        editor.putBoolean("isLoggedIn", true);
                        editor.putString("username", user.getUsername());
                        editor.putString("fullname", user.getFullname());
                        editor.apply();

                        // Simpan user_id menggunakan metode saveUserData
                        saveUserData(user.getId());

                        // Pindah ke MainActivity
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.d(TAG, "Login gagal: Username atau password salah");
                        Toast.makeText(LoginActivity.this, "Login gagal: Username atau password salah", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<User>> call, Throwable t) {
                    // Debugging error
                    Log.e(TAG, "Login error: " + t.getMessage(), t);
                    Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Register button
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    // Fungsi untuk menyimpan user_id
    // Fungsi untuk menyimpan user_id
    private void saveUserData(Integer userId) {
        if (userId != null) {
            SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("user_id", userId);
            editor.apply(); // Simpan user_id
            Log.d(TAG, "User ID saved: " + userId); // Debugging log
        } else {
            Log.e(TAG, "User ID is null, unable to save.");
        }
    }

}
