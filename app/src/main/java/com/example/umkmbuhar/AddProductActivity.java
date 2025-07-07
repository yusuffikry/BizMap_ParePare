package com.example.umkmbuhar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.umkmbuhar.API.SupabaseApiService;
import com.example.umkmbuhar.Model.Products;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddProductActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText etProductName, etDescription, etPrice;
    private ImageView ivProductPhoto;
    private Button btnSubmit;
    private Uri selectedImageUri;
    private int umkmId;  // Tambahkan variabel umkmId

    private SupabaseApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        etProductName = findViewById(R.id.etProductName);
        etDescription = findViewById(R.id.etDescription);
        etPrice = findViewById(R.id.etPrice);
        ivProductPhoto = findViewById(R.id.iv_product_photo);
        btnSubmit = findViewById(R.id.btnSubmit);

        ivProductPhoto.setOnClickListener(view -> {
            Intent openGallery = new Intent(Intent.ACTION_PICK);
            openGallery.setType("image/*");
            launcherIntentGallery.launch(openGallery);

        });

        // **Ambil umkm_id dari Intent**
        umkmId = getIntent().getIntExtra("umkm_id", -1);
        if (umkmId == -1) {
            Toast.makeText(this, "UMKM ID tidak valid", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://xnjpskqbnnzemxdyzfnr.supabase.co/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(SupabaseApiService.class);

        btnSubmit.setOnClickListener(v -> submitProduct());
    }


    private final ActivityResultLauncher<Intent> launcherIntentGallery = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    ivProductPhoto.setImageURI(selectedImageUri); // Tampilkan gambar yang dipilih di ImageView
                }
            }
    );

    private void submitProduct() {
        String productName = etProductName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();

        if (productName.isEmpty() || description.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Semua bidang harus diisi!", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceStr);
        String photoUrl = selectedImageUri != null ? selectedImageUri.toString() : "https://example.com/default-photo.jpg";

        // **Tambahkan umkmId ke objek produk**
        Products product = new Products(productName, description, price, photoUrl, umkmId);

        String apiKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InhuanBza3Fibm56ZW14ZHl6Zm5yIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTE0NTk5MDksImV4cCI6MjA2NzAzNTkwOX0.ZkUQ16Pa-G0V-dUWzYH5KN3pmkmdBDY7NrJd-6II_qA";
        String authorization = "Bearer " + apiKey;

        apiService.addProduct(product, apiKey, authorization).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddProductActivity.this, "Produk berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Log.e("API_ERROR", "Gagal menambahkan produk: " + response.code());
                    Toast.makeText(AddProductActivity.this, "Gagal menambahkan produk", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API_FAILURE", "Terjadi kesalahan: " + t.getMessage(), t);
                Toast.makeText(AddProductActivity.this, "Terjadi kesalahan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
