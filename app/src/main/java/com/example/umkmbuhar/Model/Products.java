package com.example.umkmbuhar.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Products implements Serializable {

    @SerializedName("id")
    private Integer id;
    @SerializedName("name")
    private String name; // Nama produk
    @SerializedName("description")
    private String description; // Deskripsi produk
    @SerializedName("price")
    private double price; // Harga produk
    @SerializedName("photo_url")
    private String photo_url; // URL gambar produk
    @SerializedName("umkm_id")
    private Integer umkm_id;


    public Products(String name, String description, double price, String photo_url, Integer umkm_id) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.photo_url = photo_url;
        this.umkm_id = umkm_id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }
    public Integer getUmkm_id() {
        return umkm_id;
    }

    public void setUmkm_id(Integer umkm_id) {
        this.umkm_id = umkm_id;
    }

}
