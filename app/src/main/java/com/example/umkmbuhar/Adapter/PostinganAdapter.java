package com.example.umkmbuhar.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.umkmbuhar.API.SupabaseApiService;
import com.example.umkmbuhar.Model.Products;
import com.example.umkmbuhar.Model.Umkm;
import com.example.umkmbuhar.R;

import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PostinganAdapter extends RecyclerView.Adapter<PostinganAdapter.ViewHolder> {
    private Context context;
    private List<Products> productsList;
    private List<Umkm> umkmList;
    private SupabaseApiService apiService;

    public PostinganAdapter(Context context, List<Products> productsList, List<Umkm> umkmList) {
        this.context = context;
        this.productsList = productsList;
        this.umkmList = umkmList;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://xnjpskqbnnzemxdyzfnr.supabase.co/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(SupabaseApiService.class);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_postingan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Products product = productsList.get(position);
        Umkm umkm = getUmkmById(product.getUmkm_id());

        holder.namaProduk.setText(product.getName());

        Glide.with(context)
                .load(product.getPhoto_url())
                .placeholder(R.drawable.kain_batik)
                .into(holder.imageProduk);

        if (umkm != null) {
            holder.namaUmkm.setText(umkm.getName());
            holder.alamatUmkm.setText(umkm.getAddress());

            Glide.with(context)
                    .load(umkm.getPhoto_url())
                    .placeholder(R.drawable.logo_parepare)
                    .into(holder.imageUmkm);
        } else {
            holder.namaUmkm.setText("UMKM tidak ditemukan");
            holder.alamatUmkm.setText("Alamat tidak tersedia");
        }
    }

    @Override
    public int getItemCount() {
        return productsList != null ? productsList.size() : 0;
    }

    private Umkm getUmkmById(int umkmId) {
        for (Umkm umkm : umkmList) {
            if (umkm.getId() == umkmId) {
                return umkm;
            }
        }
        return null;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageProduk, imageUmkm;
        TextView namaProduk, namaUmkm, alamatUmkm;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProduk = itemView.findViewById(R.id.avatar_postingan);
            imageUmkm = itemView.findViewById(R.id.image_postingan);
            namaProduk = itemView.findViewById(R.id.produk_name);
            namaUmkm = itemView.findViewById(R.id.name_postingan);
            alamatUmkm = itemView.findViewById(R.id.alamat_produk);
        }
    }

    public void updateData(List<Products> newProductsList, List<Umkm> newUmkmList) {
        productsList = newProductsList;
        umkmList = newUmkmList;
        notifyDataSetChanged();
    }
}
