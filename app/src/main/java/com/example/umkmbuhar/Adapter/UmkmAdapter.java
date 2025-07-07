package com.example.umkmbuhar.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.umkmbuhar.API.SupabaseApiService;
import com.example.umkmbuhar.DetailActivity;
import com.example.umkmbuhar.Model.Umkm;
import com.example.umkmbuhar.R;
import com.example.umkmbuhar.UpdateUmkmActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UmkmAdapter extends RecyclerView.Adapter<UmkmAdapter.UmkmViewHolder> {

    private Context context;
    private List<Umkm> umkmList;
    private SupabaseApiService apiService;
    private boolean isLoggedIn; // Status login

    public UmkmAdapter(Context context, List<Umkm> umkmList) {
        this.context = context;
        this.umkmList = umkmList;

        // Ambil status login dari SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        this.isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://xnjpskqbnnzemxdyzfnr.supabase.co/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(SupabaseApiService.class);
    }

    @NonNull
    @Override
    public UmkmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_umkm_card, parent, false);
        return new UmkmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UmkmViewHolder holder, int position) {
        Umkm umkm = umkmList.get(position);

        holder.tvName.setText(umkm.getName());
        holder.tvAddress.setText(": " + umkm.getAddress());
        holder.tvPhone.setText(": " + umkm.getPhone());

        Glide.with(context).load(umkm.getPhoto_url()).error(R.drawable.logo_parepare).into(holder.ivImage);

        holder.btn_detail.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra( "umkm_name", umkm.getName());  // Mengirim nama UMKM
            intent.putExtra("umkm_id", umkm.getId()); // Kirimkan ID UMKM
            intent.putExtra("link_ig", umkm.getLink_ig());
            intent.putExtra("link_fb", umkm.getLink_fb());
            intent.putExtra("image", umkm.getPhoto_url());
            context.startActivity(intent);
        });

        // Cek apakah user sudah login, jika tidak sembunyikan ikon edit & delete
        if (!isLoggedIn) {
            holder.ivEdit.setVisibility(View.GONE);
            holder.ivDelete.setVisibility(View.GONE);
        } else {
            holder.ivEdit.setVisibility(View.VISIBLE);
            holder.ivDelete.setVisibility(View.VISIBLE);
        }

        holder.ivEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, UpdateUmkmActivity.class);
            intent.putExtra("umkm", umkm);
            context.startActivity(intent);
        });

        holder.ivLocation.setOnClickListener(v -> {
            String mapUrl = umkm.getLocation(); // Lokasi sudah berbentuk link Google Maps
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapUrl));

            // Cek apakah Google Maps terinstal, jika tidak, buka di browser
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            } else {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapUrl));
                context.startActivity(browserIntent);
            }
        });

        holder.ivDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setMessage("Apakah Anda yakin ingin menghapus UMKM ini?")
                    .setCancelable(false)
                    .setPositiveButton("Ya", (dialog, id) -> deleteUmkm(umkm, position))
                    .setNegativeButton("Tidak", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return umkmList.size();
    }

    private void deleteUmkm(Umkm umkm, int position) {
        String authToken = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InhuanBza3Fibm56ZW14ZHl6Zm5yIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTE0NTk5MDksImV4cCI6MjA2NzAzNTkwOX0.ZkUQ16Pa-G0V-dUWzYH5KN3pmkmdBDY7NrJd-6II_qA";
        String apiKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InhuanBza3Fibm56ZW14ZHl6Zm5yIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTE0NTk5MDksImV4cCI6MjA2NzAzNTkwOX0.ZkUQ16Pa-G0V-dUWzYH5KN3pmkmdBDY7NrJd-6II_qA";
        String formattedId = "eq." + umkm.getId(); // Menambahkan "eq." sesuai format Supabase

        Call<Void> call = apiService.deleteUmkm(authToken, apiKey, formattedId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    umkmList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, umkmList.size());
                    Log.d("UmkmAdapter", "UMKM berhasil dihapus");
                } else {
                    Log.e("UmkmAdapter", "Gagal menghapus UMKM: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("UmkmAdapter", "Kesalahan jaringan: " + t.getMessage());
            }
        });
    }



    public static class UmkmViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvAddress, tvPhone;
        ImageView ivImage, ivEdit, ivDelete, ivLocation;
        Button btn_detail;

        public UmkmViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_umkm_name);
            tvAddress = itemView.findViewById(R.id.tv_umkm_alamat);
            tvPhone = itemView.findViewById(R.id.tv_umkm_phone);
            ivImage = itemView.findViewById(R.id.iv_umkm_image);
            btn_detail = itemView.findViewById(R.id.btn_detail);
            ivEdit = itemView.findViewById(R.id.iv_edit);
            ivDelete = itemView.findViewById(R.id.iv_delete);
            ivLocation = itemView.findViewById(R.id.map);
        }
    }
}
