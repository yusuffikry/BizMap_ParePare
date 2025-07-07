package com.example.umkmbuhar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.umkmbuhar.API.SupabaseApiService;
import com.example.umkmbuhar.Adapter.ProductAdapter;
import com.example.umkmbuhar.AddProductActivity;
import com.example.umkmbuhar.Model.Products;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailActivity extends AppCompatActivity {

    private TextView titleUMKM;
    private Button btnAddProduct;
    private ImageView ivLinkIg, ivLinkFb, bgImage;
    private String linkIg, linkFb;

    private int umkmId;
    private RecyclerView productRecyclerView;
    private ProductAdapter productAdapter;
    private List<Products> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        titleUMKM = findViewById(R.id.title_umkm);
        btnAddProduct = findViewById(R.id.add_product);
        ivLinkIg = findViewById(R.id.ic_instagram);
        ivLinkFb = findViewById(R.id.ic_facebook);
        bgImage = findViewById(R.id.bg_image);

        // Setup RecyclerView
        productRecyclerView = findViewById(R.id.recycler_produk);
        productRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        productList = new ArrayList<>();
        // Cek status login dari SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        // Sembunyikan tombol jika belum login
        if (!isLoggedIn) {
            btnAddProduct.setVisibility(Button.GONE);
        }

        // Inisialisasi adapter dengan parameter isLoggedIn yang benar
        productAdapter = new ProductAdapter(this, productList, (products, position) -> deleteProducts(position), isLoggedIn);
        productRecyclerView.setAdapter(productAdapter);


        Intent intent = getIntent();
        String umkmName = intent.getStringExtra("umkm_name");
        linkIg = getIntent().getStringExtra("link_ig");
        linkFb = getIntent().getStringExtra("link_fb");
        String photoUrl = intent.getStringExtra("image"); // Perbaikan


// Setup klik untuk membuka link IG
        ivLinkIg.setOnClickListener(v -> {
            if (linkIg != null && !linkIg.isEmpty()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(linkIg));
                startActivity(browserIntent);
            } else {
                Toast.makeText(DetailActivity.this, "Link Instagram tidak tersedia", Toast.LENGTH_SHORT).show();
            }
        });

// Setup klik untuk membuka link FB
        ivLinkFb.setOnClickListener(v -> {
            if (linkFb != null && !linkFb.isEmpty()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(linkFb));
                startActivity(browserIntent);
            } else {
                Toast.makeText(DetailActivity.this, "Link Facebook tidak tersedia", Toast.LENGTH_SHORT).show();
            }
        });

        // Tampilkan Gambar Menggunakan Glide
        if (photoUrl != null && !photoUrl.isEmpty()) {
            Glide.with(this)
                    .load(photoUrl)
                    .placeholder(R.drawable.batik) // Gambar default jika loading
                    .error(R.drawable.baseline_error_24) // Gambar jika gagal load
                    .into(bgImage);
        } else {
            bgImage.setImageResource(R.drawable.baseline_camera_alt_24); // Gambar default jika tidak ada gambar
        }



        // Tampilkan nama UMKM di title
        if (umkmName != null && !umkmName.isEmpty()) {
            titleUMKM.setText(umkmName);
        }

        // Ambil umkm_id dari Intent
        umkmId = getIntent().getIntExtra("umkm_id", -1);

        if (umkmId != -1) {
            loadProductsByUmkmId(umkmId);
        } else {
            Toast.makeText(this, "UMKM ID tidak valid", Toast.LENGTH_SHORT).show();
        }

        btnAddProduct.setOnClickListener(v -> {
            Intent addProductIntent = new Intent(DetailActivity.this, AddProductActivity.class);
            addProductIntent.putExtra("umkm_id", umkmId);
            startActivity(addProductIntent);
        });
    }

    private void deleteProducts(int position) {
        SupabaseApiService apiService = RetrofitClient.getInstance().create(SupabaseApiService.class);

        Products productsToDelete = productList.get(position);

        String productId = "eq." + productsToDelete.getId();


        String authorization = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InhuanBza3Fibm56ZW14ZHl6Zm5yIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTE0NTk5MDksImV4cCI6MjA2NzAzNTkwOX0.ZkUQ16Pa-G0V-dUWzYH5KN3pmkmdBDY7NrJd-6II_qA";
        String apiKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InhuanBza3Fibm56ZW14ZHl6Zm5yIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTE0NTk5MDksImV4cCI6MjA2NzAzNTkwOX0.ZkUQ16Pa-G0V-dUWzYH5KN3pmkmdBDY7NrJd-6II_qA";

        Call<Void> call = apiService.deleteProductsById(authorization, apiKey, productId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    productList.remove(position); // Hapus data dari list
                    productAdapter.updateData(productList); // Update RecyclerView dengan data terbaru
                    Toast.makeText(DetailActivity.this, "Produk berhasil dihapus!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DetailActivity.this, "Gagal menghapus produk!", Toast.LENGTH_SHORT).show();
                    Log.e("API_ERROR", "Error deleting product: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(DetailActivity.this, "Kesalahan jaringan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("API_ERROR", "onFailure: ", t);
            }
        });
    }

    private void loadProductsByUmkmId(int umkmId) {
        SupabaseApiService apiService = RetrofitClient.getInstance().create(SupabaseApiService.class);

        Call<List<Products>> call = apiService.getProductsByUmkmId(
                "eq." + umkmId,
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InhuanBza3Fibm56ZW14ZHl6Zm5yIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTE0NTk5MDksImV4cCI6MjA2NzAzNTkwOX0.ZkUQ16Pa-G0V-dUWzYH5KN3pmkmdBDY7NrJd-6II_qA",
                "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InhuanBza3Fibm56ZW14ZHl6Zm5yIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTE0NTk5MDksImV4cCI6MjA2NzAzNTkwOX0.ZkUQ16Pa-G0V-dUWzYH5KN3pmkmdBDY7NrJd-6II_qA"
        );

        call.enqueue(new Callback<List<Products>>() {
            @Override
            public void onResponse(Call<List<Products>> call, Response<List<Products>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productList.clear();
                    productList.addAll(response.body());
                    productAdapter.notifyDataSetChanged();
                    Log.d("API Success", "Produk berhasil dimuat, total: " + productList.size());
                } else {
                    try {
                        // Ambil body response sebagai string
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "null";

                        Log.e("API Error", "Gagal memuat produk: " + errorBody);
                        Log.e("API Error", "Response Code: " + response.code());
                        Log.e("API Error", "Pesan error: " + response.message());

                        Toast.makeText(DetailActivity.this, "Gagal memuat produk. Coba lagi nanti.", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e("API Error", "Gagal membaca error body", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Products>> call, Throwable t) {
                Log.e("API Failure", "Terjadi kesalahan saat memuat data: " + t.getMessage(), t);
                Toast.makeText(DetailActivity.this, "Terjadi kesalahan saat memuat data. Periksa koneksi internet Anda.", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private static class RetrofitClient {
        private static Retrofit retrofit;

        public static Retrofit getInstance() {
            if (retrofit == null) {
                retrofit = new Retrofit.Builder()
                        .baseUrl("https://xnjpskqbnnzemxdyzfnr.supabase.co/") // Ganti dengan URL Supabase Anda
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
            return retrofit;
        }
    }
}
