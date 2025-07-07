package com.example.umkmbuhar;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.umkmbuhar.API.SupabaseApiService;
import com.example.umkmbuhar.Model.Products;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UpdateProductActivity extends AppCompatActivity {

    private EditText editProductName, editDescription, editPrice;
    private ImageView editProductPhoto;
    private Button btnUpdate;
    private SupabaseApiService apiService;
    private Products currentProduct;
    private static final String TAG = "UpdateProductActivity";
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InhuanBza3Fibm56ZW14ZHl6Zm5yIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTE0NTk5MDksImV4cCI6MjA2NzAzNTkwOX0.ZkUQ16Pa-G0V-dUWzYH5KN3pmkmdBDY7NrJd-6II_qA";
    private static final String AUTHORIZATION = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InhuanBza3Fibm56ZW14ZHl6Zm5yIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTE0NTk5MDksImV4cCI6MjA2NzAzNTkwOX0.ZkUQ16Pa-G0V-dUWzYH5KN3pmkmdBDY7NrJd-6II_qA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_product);

        // Inisialisasi komponen
        editProductName = findViewById(R.id.editProductName);
        editDescription = findViewById(R.id.editDescription);
        editPrice = findViewById(R.id.editPrice);
        editProductPhoto = findViewById(R.id.edit_product_photo);
        btnUpdate = findViewById(R.id.btnUpdate);

        // Ambil data dari intent
        currentProduct = (Products) getIntent().getSerializableExtra("product");
        if (currentProduct != null) {
            populateFields(currentProduct);
        } else {
            Toast.makeText(this, "Data tidak ditemukan!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://xnjpskqbnnzemxdyzfnr.supabase.co/") // Pastikan URL ini sesuai dengan yang digunakan Supabase
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(SupabaseApiService.class);

        btnUpdate.setOnClickListener(v -> {
            if (validateInput()) {
                updateProduct();
            }
        });
    }

    private void populateFields(Products product) {
        editProductName.setText(product.getName());
        editDescription.setText(product.getDescription());
        editPrice.setText(String.valueOf(product.getPrice()));

        Glide.with(this)
                .load(product.getPhoto_url())
                .placeholder(R.drawable.baseline_camera_alt_24)
                .into(editProductPhoto);
    }

    private boolean validateInput() {
        if (TextUtils.isEmpty(editProductName.getText())) {
            editProductName.setError("Nama produk tidak boleh kosong");
            return false;
        }
        if (TextUtils.isEmpty(editDescription.getText())) {
            editDescription.setError("Deskripsi tidak boleh kosong");
            return false;
        }
        if (TextUtils.isEmpty(editPrice.getText())) {
            editPrice.setError("Harga tidak boleh kosong");
            return false;
        }

        try {
            Double.parseDouble(editPrice.getText().toString());
        } catch (NumberFormatException e) {
            editPrice.setError("Harga tidak valid");
            return false;
        }

        return true;
    }

    private void updateProduct() {
        if (currentProduct == null || currentProduct.getId() == 0) {
            Toast.makeText(this, "ID produk tidak valid!", Toast.LENGTH_SHORT).show();
            return;
        }

        currentProduct.setName(editProductName.getText().toString());
        currentProduct.setDescription(editDescription.getText().toString());
        currentProduct.setPrice(Double.parseDouble(editPrice.getText().toString()));

        Log.d(TAG, "Mengupdate produk dengan ID: " + currentProduct.getId());


        String productIdQuery = "eq." + currentProduct.getId();

        Call<Void> call = apiService.updateProduct(
                productIdQuery,
                currentProduct,
                API_KEY,
                AUTHORIZATION
        );

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(UpdateProductActivity.this, "Produk berhasil diperbarui", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Update berhasil untuk ID: " + currentProduct.getId());
                    finish();
                } else {
                    Toast.makeText(UpdateProductActivity.this, "Gagal memperbarui produk", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Response error: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(UpdateProductActivity.this, "Kesalahan jaringan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Network error: ", t);
            }
        });
    }
}
