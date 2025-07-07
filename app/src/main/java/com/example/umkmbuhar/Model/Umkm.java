package com.example.umkmbuhar.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Umkm implements Serializable {

    @SerializedName("name")
    private String name; // Nama UMKM
    @SerializedName("address")
    private String address; // Alamat UMKM
    @SerializedName("phone")
    private String phone; // Nomor telepon UMKM
    @SerializedName("location")
    private String location; // Lokasi (link Google Maps)
    @SerializedName("nib")
    private String nib; // Nomor Induk Berusaha (NIB)
    @SerializedName("photo_url")
    private String photo_url; // URL foto profil UMKM
    @SerializedName("link_ig")
    private String link_ig; // URL foto profil UMKM
    @SerializedName("link_fb")
    private String link_fb; // URL foto profil UMKM
    @SerializedName("user_id")
    private Integer user_id;
    @SerializedName("id")
    private Integer id;
    @SerializedName("products")
    private List<Products> products;

    public Umkm(String name, String address, String phone, String location, String nib, String photo_url, String link_ig, String link_fb, Integer user_id) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.location = location;
        this.nib = nib;
        this.photo_url = photo_url;
        this.link_ig = link_ig;
        this.link_fb = link_fb;
        this.user_id = user_id;
    }

    // Getter dan Setter
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getNib() { return nib; }
    public void setNib(String nib) { this.nib = nib; }

    public String getPhoto_url() { return photo_url; }
    public void setPhoto_url(String photo_url) { this.photo_url = photo_url; }
    public String getLink_ig() {
        return link_ig;
    }

    public void setLink_ig(String link_ig) {
        this.link_ig = link_ig;
    }

    public String getLink_fb() {
        return link_fb;
    }

    public void setLink_fb(String link_fb) {
        this.link_fb = link_fb;
    }
    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public List<Products> getProducts() {
        return products;
    }


}



