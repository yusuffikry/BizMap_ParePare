package com.example.umkmbuhar.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.umkmbuhar.API.SupabaseApiService;
import com.example.umkmbuhar.Adapter.PostinganAdapter;
import com.example.umkmbuhar.Model.Products;
import com.example.umkmbuhar.Model.Umkm;
import com.example.umkmbuhar.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PostinganFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostinganAdapter adapter;
    private SupabaseApiService apiService;
    private List<Products> productsList = new ArrayList<>();
    private List<Umkm> umkmList = new ArrayList<>();
    private List<Products> filteredProducts = new ArrayList<>();

    private static final String BASE_URL = "https://xnjpskqbnnzemxdyzfnr.supabase.co/";
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InhuanBza3Fibm56ZW14ZHl6Zm5yIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTE0NTk5MDksImV4cCI6MjA2NzAzNTkwOX0.ZkUQ16Pa-G0V-dUWzYH5KN3pmkmdBDY7NrJd-6II_qA";
    private static final String AUTH_KEY = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InhuanBza3Fibm56ZW14ZHl6Zm5yIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTE0NTk5MDksImV4cCI6MjA2NzAzNTkwOX0.ZkUQ16Pa-G0V-dUWzYH5KN3pmkmdBDY7NrJd-6II_qA";

    private SearchView searchView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_postingan, container, false);

        // Inisialisasi views
        recyclerView = view.findViewById(R.id.recycler_postingan);
        searchView = view.findViewById(R.id.search_produk);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PostinganAdapter(getContext(), filteredProducts, umkmList);
        recyclerView.setAdapter(adapter);

        initApi();
        fetchPostingan();

        // Listener untuk search
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Tidak perlu melakukan apa-apa pada submit
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Filter berdasarkan nama UMKM dan nama produk
                filterProducts(newText);
                return true;
            }
        });

        return view;
    }

    private void initApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(SupabaseApiService.class);
    }

    private void fetchPostingan() {
        Call<List<Umkm>> call = apiService.getUmkm(
                null,  // Hapus filter "eq.umkmId", ambil semua data
                "id,name,address,phone,location,nib,photo_url,link_ig,link_fb,user_id,products(id,name,description,price,photo_url,umkm_id)",
                API_KEY,
                AUTH_KEY  // Sama karena digunakan untuk Authorization
        );

        call.enqueue(new Callback<List<Umkm>>() {
            @Override
            public void onResponse(Call<List<Umkm>> call, Response<List<Umkm>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    umkmList = response.body();
                    productsList.clear();

                    for (Umkm umkm : umkmList) {
                        if (umkm.getProducts() != null) {
                            productsList.addAll(umkm.getProducts());
                        }
                    }

                    Log.d("API_RESPONSE", "Data UMKM: " + umkmList.size());
                    Log.d("API_RESPONSE", "Data Produk: " + productsList.size());

                    // Setelah data diproses, filter produk berdasarkan query pencarian
                    filterProducts(searchView.getQuery().toString());

                    if (adapter == null) {
                        adapter = new PostinganAdapter(getContext(), filteredProducts, umkmList);
                        recyclerView.setAdapter(adapter);
                    } else {
                        adapter.updateData(filteredProducts, umkmList);
                    }
                } else {
                    if (!response.isSuccessful()) {
                        try {
                            Log.e("API_ERROR", "Response gagal: " + response.errorBody().string());
                        } catch (IOException e) {
                            Log.e("API_ERROR", "Gagal membaca error body", e);
                        }
                        Toast.makeText(getContext(), "Gagal mengambil data UMKM", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Umkm>> call, Throwable t) {
                Log.e("API_FAILURE", "Kesalahan jaringan: " + t.getMessage());
                Toast.makeText(getContext(), "Kesalahan jaringan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterProducts(String query) {
        filteredProducts.clear();
        if (query.isEmpty()) {
            filteredProducts.addAll(productsList); // Tampilkan semua produk jika query kosong
        } else {
            String queryLower = query.toLowerCase();
            for (Umkm umkm : umkmList) {
                // Memeriksa apakah nama UMKM cocok dengan query
                if (umkm.getName().toLowerCase().contains(queryLower)) {
                    // Jika nama UMKM cocok, masukkan semua produk dari UMKM tersebut
                    filteredProducts.addAll(umkm.getProducts());
                } else {
                    // Memeriksa nama produk dalam UMKM jika nama UMKM tidak cocok
                    for (Products product : umkm.getProducts()) {
                        if (product.getName().toLowerCase().contains(queryLower)) {
                            filteredProducts.add(product);
                        }
                    }
                }
            }
        }
        adapter.updateData(filteredProducts, umkmList); // Update RecyclerView dengan data yang difilter
    }

}
