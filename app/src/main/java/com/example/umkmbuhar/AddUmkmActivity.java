package com.example.umkmbuhar;

import android.content.SharedPreferences;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.umkmbuhar.API.SupabaseApiService;
import com.example.umkmbuhar.Model.Umkm;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddUmkmActivity extends AppCompatActivity {
    private EditText etNama, etAlamat, etTelepon, etLokasi, etNib, etLinkIg, etLinkFb;
    private Button btnRegister;
    private ImageView ivPhoto;
    private SupabaseApiService apiService;
    private Uri selectedImageUri; // URI gambar yang dipilih
    private Integer userId; // Untuk menyimpan ID user yang login

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_umkm);

        // Inisialisasi View
        etNama = findViewById(R.id.et_umkm_name);
        etAlamat = findViewById(R.id.et_umkm_address);
        etTelepon = findViewById(R.id.et_umkm_telepon);
        etLokasi = findViewById(R.id.et_umkm_location);
        etNib = findViewById(R.id.et_nib);
        btnRegister = findViewById(R.id.btn_register);
        ivPhoto = findViewById(R.id.iv_umkm_photo);
        etLinkIg = findViewById(R.id.tambahLinkIg);
        etLinkFb = findViewById(R.id.tambahLinkFb);


        // Ambil user_id dari SharedPreferences
        // Ambil user_id dari SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", 0);

        if (userId == 0) { // Periksa apakah user_id valid
            Toast.makeText(this, "Gagal memuat ID pengguna. Silakan login ulang.", Toast.LENGTH_SHORT).show();
            Log.e("AddUmkmActivity", "Invalid user ID. Redirecting to login.");
            startActivity(new Intent(this, LoginActivity.class)); // Kembali ke login
            finish(); // Hentikan aktivitas saat ini
            return;
        }

        // Konfigurasi Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://xnjpskqbnnzemxdyzfnr.supabase.co/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Inisialisasi API Service
        apiService = retrofit.create(SupabaseApiService.class);

        // Tombol untuk memilih gambar
        ivPhoto.setOnClickListener(view -> {
            Intent openGallery = new Intent(Intent.ACTION_PICK);
            openGallery.setType("image/*");
            launcherIntentGallery.launch(openGallery);
        });

        // Tombol Daftar
        btnRegister.setOnClickListener(v -> {
            String nama = etNama.getText().toString().trim();
            String alamat = etAlamat.getText().toString().trim();
            String telepon = etTelepon.getText().toString().trim();
            String lokasi = etLokasi.getText().toString().trim();
            String nib = etNib.getText().toString().trim();
            String linkIg = etLinkIg.getText().toString().trim();
            String linkFb = etLinkFb.getText().toString().trim();


            // Validasi input
            if (!isInputValid(nama, alamat, telepon, lokasi, nib)) {
                return;
            }

            // Konversi URI gambar menjadi string (URL atau path lokal)
            String photoUrl = selectedImageUri != null ? selectedImageUri.toString() : "https://example.com/default-photo.jpg";

            // Buat objek UMKM dengan ID user
            Umkm umkm = new Umkm(nama, alamat, telepon, lokasi, nib, photoUrl, linkIg, linkFb, userId);

            // Kirim data ke API
            addUmkmToApi(umkm);
        });
    }

    // Fungsi untuk menangani hasil dari galeri
    private final ActivityResultLauncher<Intent> launcherIntentGallery = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    ivPhoto.setImageURI(selectedImageUri); // Tampilkan gambar yang dipilih di ImageView
                }
            }
    );

    // Fungsi untuk validasi input
    private boolean isInputValid(String nama, String alamat, String telepon, String lokasi, String nib) {
        if (TextUtils.isEmpty(nama)) {
            Toast.makeText(this, "Nama UMKM tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(alamat)) {
            Toast.makeText(this, "Alamat UMKM tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(telepon)) {
            Toast.makeText(this, "Nomor telepon tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!telepon.matches("\\d+")) {
            Toast.makeText(this, "Nomor telepon harus berupa angka", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (telepon.length() < 10 || telepon.length() > 13) {
            Toast.makeText(this, "Nomor telepon harus antara 10-13 digit", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(lokasi)) {
            Toast.makeText(this, "Lokasi UMKM tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(nib)) {
            Toast.makeText(this, "NIB tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (nib.length() != 13) {
            Toast.makeText(this, "NIB harus berisi 13 karakter", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void addUmkmToApi(Umkm umkm) {
        if (userId == null || userId == 0) {
            Toast.makeText(this, "User ID tidak valid. Silakan login ulang.", Toast.LENGTH_SHORT).show();
            Log.e("AddUmkmActivity", "Cannot add UMKM: Invalid user ID.");
            return;
        }

        String apiKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InhuanBza3Fibm56ZW14ZHl6Zm5yIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTE0NTk5MDksImV4cCI6MjA2NzAzNTkwOX0.ZkUQ16Pa-G0V-dUWzYH5KN3pmkmdBDY7NrJd-6II_qA";
        String authorization = "Bearer " + apiKey;

        Log.d("API_CALL", "Mengirim data UMKM: " + umkm.toString());

        apiService.addUmkm(umkm, apiKey, authorization).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddUmkmActivity.this, "UMKM berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Log.e("API_ERROR", "Gagal menambahkan UMKM: " + errorBody);
                        Toast.makeText(AddUmkmActivity.this, "Gagal menambahkan UMKM: " + errorBody, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e("API_ERROR", "Exception: " + e.getMessage(), e);
                        Toast.makeText(AddUmkmActivity.this, "Gagal menambahkan UMKM: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API_FAILURE", "Terjadi kesalahan: " + t.getMessage(), t);
                Toast.makeText(AddUmkmActivity.this, "Terjadi kesalahan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


}
