package com.example.umkmbuhar.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.umkmbuhar.API.SupabaseApiService;
import com.example.umkmbuhar.DetailActivity;
import com.example.umkmbuhar.Model.Products;
import com.example.umkmbuhar.R;
import com.example.umkmbuhar.UpdateProductActivity;

import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Products> productList;
    private OnDeleteClickListener deleteClickListener;
    boolean isLoggedIn;
    private SupabaseApiService apiService;

    // Constructor
    public ProductAdapter(Context context, List<Products> productList, OnDeleteClickListener deleteClickListener, boolean isLoggedIn) {
        this.context = context;
        this.productList = productList;
        this.deleteClickListener = deleteClickListener;
        this.isLoggedIn = isLoggedIn;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://xnjpskqbnnzemxdyzfnr.supabase.co/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(SupabaseApiService.class);
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Products product = productList.get(position);
        holder.titleProduct.setText(product.getName());
        holder.descProduct.setText(product.getDescription());
        holder.priceProduct.setText(String.format("Rp %.2f", product.getPrice()));
        // Set the image using Glide or Picasso if needed, for now just using the URL as text
        Glide.with(context).load(product.getPhoto_url()).error(R.drawable.logo_parepare).into(holder.imageProduct);

        // Mengecek apakah sudah login untuk menampilkan atau menyembunyikan tombol edit dan delete
        if (!isLoggedIn) {
            holder.iconEdit.setVisibility(View.GONE);
            holder.iconDelete.setVisibility(View.GONE);
        } else {
            holder.iconEdit.setVisibility(View.VISIBLE);
            holder.iconDelete.setVisibility(View.VISIBLE);
        }

        holder.iconEdit.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), UpdateProductActivity.class);
            intent.putExtra("product", product); // Kirim objek ChildProfile
            v.getContext().startActivity(intent);
        });

        // Menambahkan listener untuk tombol delete
        holder.iconDelete.setOnClickListener(v -> {
            // Menampilkan alert dialog konfirmasi
            new AlertDialog.Builder(v.getContext())
                    .setMessage("Apakah anda yakin ingin menghapusnya?")
                    .setCancelable(false)
                    .setPositiveButton("Ya", (dialog, id) -> {
                        // Menghapus item dari list dan database
                        deleteClickListener.onDeleteClick(product, position);
                    })
                    .setNegativeButton("Tidak", null)
                    .show();
        });


    }


    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void updateData(List<Products> newData) {
        productList = newData;
        notifyDataSetChanged(); // Memberitahukan adapter untuk memperbarui data
    }

    // ViewHolder class
    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView titleProduct, descProduct, priceProduct;
        ImageView imageProduct, iconEdit, iconDelete;

        public ProductViewHolder(View itemView) {
            super(itemView);
            titleProduct = itemView.findViewById(R.id.title_product);
            descProduct = itemView.findViewById(R.id.desc_product);
            priceProduct = itemView.findViewById(R.id.price_product);
            imageProduct = itemView.findViewById(R.id.image_product);
            iconEdit = itemView.findViewById(R.id.icon_edit);
            iconDelete = itemView.findViewById(R.id.icon_delete);
        }
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(Products products, int position);
    }
}
