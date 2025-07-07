package com.example.umkmbuhar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.umkmbuhar.API.SupabaseApiService;
import com.example.umkmbuhar.Model.User;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText etFullname = findViewById(R.id.et_fullname);
        EditText etUsername = findViewById(R.id.et_username);
        EditText etPassword = findViewById(R.id.et_password);
        Button btnRegister = findViewById(R.id.btn_register);

        // Konfigurasi Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://xnjpskqbnnzemxdyzfnr.supabase.co/") // Ganti dengan URL proyek Anda
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SupabaseApiService apiService = retrofit.create(SupabaseApiService.class);

        btnRegister.setOnClickListener(view -> {
            String fullname = etFullname.getText().toString();
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();

            // Validasi input
            if (fullname.isEmpty() || username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Semua field harus diisi!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Membuat objek User
            User user = new User(fullname, username, password);

            // Mengirim data ke Supabase
            Call<Void> call = apiService.registerUser(user);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Registrasi berhasil!", Toast.LENGTH_SHORT).show();

                        // Arahkan ke LoginActivity
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish(); // Tutup RegisterActivity agar tidak kembali ke sini
                    } else {
                        try {
                            String errorBody = response.errorBody().string();
                            Toast.makeText(RegisterActivity.this, "Gagal registrasi: " + errorBody, Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(RegisterActivity.this, "Gagal registrasi: " + response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }



                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(RegisterActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}

