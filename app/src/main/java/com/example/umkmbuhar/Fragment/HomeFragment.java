package com.example.umkmbuhar.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.umkmbuhar.API.SupabaseApiService;
import com.example.umkmbuhar.Adapter.UmkmAdapter;
import com.example.umkmbuhar.AddUmkmActivity;
import com.example.umkmbuhar.LoginActivity;
import com.example.umkmbuhar.Model.Umkm;
import com.example.umkmbuhar.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {

    private SharedPreferences sharedPreferences;
    private CardView cardLogin, cardLogout, buttonUmkm;
    private TextView welcomeText, loginText;

    private RecyclerView recyclerView;
    private UmkmAdapter umkmAdapter;
    private List<Umkm> umkmList = new ArrayList<>();
    private List<Umkm> filteredList = new ArrayList<>();
    private SearchView searchView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Inisialisasi views
        cardLogin = view.findViewById(R.id.card_login);
        cardLogout = view.findViewById(R.id.card_logout);
        loginText = view.findViewById(R.id.login_text);
        welcomeText = view.findViewById(R.id.welcome_text);
        buttonUmkm = view.findViewById(R.id.button_umkm);

        // RecyclerView setup
        recyclerView = view.findViewById(R.id.recyclerView_umkm);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        umkmAdapter = new UmkmAdapter(getContext(), filteredList);
        recyclerView.setAdapter(umkmAdapter);

        // Inisialisasi SearchView
        searchView = view.findViewById(R.id.search_data);

        // Ambil SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);

        // Update UI berdasarkan status login
        updateUI();

        // Listener untuk tombol logout
        cardLogout.setOnClickListener(v -> logout());

        // Listener untuk tombol login
        cardLogin.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        });

        // Listener untuk menambahkan UMKM
        buttonUmkm.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddUmkmActivity.class);
            startActivity(intent);
        });

        // Load data UMKM
        loadData();

        // Menambahkan listener untuk pencarian
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterUmkmList(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterUmkmList(newText);
                return false;
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
        loadData();
    }

    private void updateUI() {
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        String fullname = sharedPreferences.getString("fullname", null);

        if (isLoggedIn && fullname != null && !fullname.isEmpty()) {
            // User sudah login
            cardLogin.setVisibility(View.GONE);
            cardLogout.setVisibility(View.VISIBLE);
            welcomeText.setVisibility(View.VISIBLE);
            welcomeText.setText("Welcome, " + fullname);
            buttonUmkm.setVisibility(View.VISIBLE); // Tampilkan tombol UMKM
        } else {
            // User belum login
            cardLogin.setVisibility(View.VISIBLE);
            cardLogout.setVisibility(View.GONE);
            welcomeText.setVisibility(View.GONE);
            buttonUmkm.setVisibility(View.GONE); // Sembunyikan tombol UMKM
        }
    }

    private void logout() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Konfirmasi Logout")
                .setMessage("Apakah Anda yakin ingin keluar?")
                .setPositiveButton("Ya", (dialog, which) -> {
                    // Hapus data login dari SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();

                    // Tampilkan pesan logout berhasil
                    Toast.makeText(getContext(), "Logout berhasil", Toast.LENGTH_SHORT).show();

                    // Refresh fragment
                    requireActivity().recreate();
                })
                .setNegativeButton("Tidak", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void loadData() {
        SupabaseApiService apiService = RetrofitClient.getInstance().create(SupabaseApiService.class);

        // Panggil API untuk mendapatkan data
        Call<List<Umkm>> call = apiService.getUmkm(
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InhuanBza3Fibm56ZW14ZHl6Zm5yIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTE0NTk5MDksImV4cCI6MjA2NzAzNTkwOX0.ZkUQ16Pa-G0V-dUWzYH5KN3pmkmdBDY7NrJd-6II_qA",
                "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InhuanBza3Fibm56ZW14ZHl6Zm5yIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTE0NTk5MDksImV4cCI6MjA2NzAzNTkwOX0.ZkUQ16Pa-G0V-dUWzYH5KN3pmkmdBDY7NrJd-6II_qA"
        );

        call.enqueue(new Callback<List<Umkm>>() {
            @Override
            public void onResponse(Call<List<Umkm>> call, Response<List<Umkm>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Umkm> allUmkm = response.body();
                    umkmList.clear();

                    // Filter data jika user login
                    boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
                    int userId = sharedPreferences.getInt("user_id", -1);

                    if (isLoggedIn && userId != -1) {
                        // Tampilkan UMKM berdasarkan user ID
                        for (Umkm umkm : allUmkm) {
                            if (umkm.getUser_id() == userId) {
                                umkmList.add(umkm);
                            }
                        }
                    } else {
                        // Tampilkan semua UMKM
                        umkmList.addAll(allUmkm);
                    }

                    // Perbarui filteredList dan adapter RecyclerView
                    filteredList.clear();
                    filteredList.addAll(umkmList);
                    umkmAdapter.notifyDataSetChanged();
                    Log.d("API Success", "Data berhasil dimuat: " + umkmList.size() + " UMKM");
                } else {
                    Log.e("API Error", "Respons tidak berhasil: " + response.code());
                    Toast.makeText(getContext(), "Gagal memuat data UMKM", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Umkm>> call, Throwable t) {
                Log.e("API Error", "Gagal memuat data: " + t.getMessage());
                Toast.makeText(getContext(), "Terjadi kesalahan saat memuat data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Fungsi untuk memfilter data UMKM berdasarkan nama
    private void filterUmkmList(String query) {
        filteredList.clear();
        for (Umkm umkm : umkmList) {
            if (umkm.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(umkm);
            }
        }
        umkmAdapter.notifyDataSetChanged();
    }

    // Kelas RetrofitClient dalam file yang sama
    private static class RetrofitClient {
        private static Retrofit retrofit;

        public static Retrofit getInstance() {
            if (retrofit == null) {
                retrofit = new Retrofit.Builder()
                        .baseUrl("https://xnjpskqbnnzemxdyzfnr.supabase.co/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
            return retrofit;
        }
    }
}
