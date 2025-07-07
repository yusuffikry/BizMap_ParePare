package com.example.umkmbuhar;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.umkmbuhar.API.SupabaseApiService;
import com.example.umkmbuhar.Model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditProfileActivity extends AppCompatActivity {
    private static final String TAG = "EditProfileActivity";
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InhuanBza3Fibm56ZW14ZHl6Zm5yIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTE0NTk5MDksImV4cCI6MjA2NzAzNTkwOX0.ZkUQ16Pa-G0V-dUWzYH5KN3pmkmdBDY7NrJd-6II_qA";
    private static final String AUTHORIZATION = "Bearer " + API_KEY;

    private EditText nameEditText, usernameEditText, passwordEditText;
    private Button saveProfileButton;
    private SupabaseApiService apiService;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Inisialisasi view
        nameEditText = findViewById(R.id.nameedittext);
        usernameEditText = findViewById(R.id.usernameedittext);
        passwordEditText = findViewById(R.id.passwordedittext);
        saveProfileButton = findViewById(R.id.saveProfileButton);

        // Ambil data dari SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", -1);
        String fullname = sharedPreferences.getString("fullname", "");
        String username = sharedPreferences.getString("username", "");

        // Tampilkan data di EditText
        nameEditText.setText(fullname);
        usernameEditText.setText(username);

        // Konfigurasi Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://xnjpskqbnnzemxdyzfnr.supabase.co/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(SupabaseApiService.class);

        // Handle tombol simpan
        saveProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserProfile();
            }
        });
    }

    private void updateUserProfile() {
        if (userId == -1) {
            Toast.makeText(this, "User ID tidak ditemukan", Toast.LENGTH_SHORT).show();
            return;
        }

        String newFullname = nameEditText.getText().toString().trim();
        String newUsername = usernameEditText.getText().toString().trim();
        String newPassword = passwordEditText.getText().toString().trim();

        if (newFullname.isEmpty() || newUsername.isEmpty()) {
            Toast.makeText(this, "Nama dan Username harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        User updatedUser;
        if (newPassword.isEmpty()) {
            updatedUser = new User(newFullname, newUsername, null);
        } else {
            updatedUser = new User(newFullname, newUsername, newPassword);
        }

        String userIdQuery = "eq." + userId;
        Call<Void> call = apiService.updateUsers(userIdQuery, updatedUser, API_KEY, AUTHORIZATION);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditProfileActivity.this, "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show();

                    // Simpan data terbaru ke SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("fullname", newFullname);
                    editor.putString("username", newUsername);
                    editor.apply();

                    finish(); // Kembali ke halaman sebelumnya
                } else {
                    Log.e(TAG, "Gagal update: " + response.errorBody());
                    Toast.makeText(EditProfileActivity.this, "Gagal memperbarui profil", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e(TAG, "Error: " + t.getMessage(), t);
                Toast.makeText(EditProfileActivity.this, "Terjadi kesalahan jaringan", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
