package com.example.umkmbuhar.API;

import com.example.umkmbuhar.Model.LoginRequest;
import com.example.umkmbuhar.Model.Products;
import com.example.umkmbuhar.Model.Umkm;
import com.example.umkmbuhar.Model.User;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SupabaseApiService {
    // Endpoint untuk menambahkan data pengguna
    @Headers({
            "apikey: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InhuanBza3Fibm56ZW14ZHl6Zm5yIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTE0NTk5MDksImV4cCI6MjA2NzAzNTkwOX0.ZkUQ16Pa-G0V-dUWzYH5KN3pmkmdBDY7NrJd-6II_qA",
            "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InhuanBza3Fibm56ZW14ZHl6Zm5yIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTE0NTk5MDksImV4cCI6MjA2NzAzNTkwOX0.ZkUQ16Pa-G0V-dUWzYH5KN3pmkmdBDY7NrJd-6II_qA",
            "Content-Type: application/json"
    })
    @POST("rest/v1/users") // Ganti "users" dengan nama tabel Anda
    Call<Void> registerUser(@Body User user);

    @GET("rest/v1/users")
    Call<List<User>> loginUser(
            @Query("username") String username,
            @Query("password") String password,
            @Query("select") String select,
            @Header("apikey") String apiKey,
            @Header("Authorization") String authorization
    );

    @POST("rest/v1/umkm")
    Call<Void> addUmkm(
            @Body Umkm umkm,
            @Header("apikey") String apiKey,
            @Header("Authorization") String authorization
    );

    @Multipart
    @POST("storage/v1/object/{path}")
    Call<Void> uploadImageToStorage(
            @Path("path") String path,
            @Header("Authorization") String authorization,
            @Part MultipartBody.Part file
    );

    @GET("rest/v1/umkm")
    Call<List<Umkm>> getUmkm(
            @Header("apikey") String apiKey,
            @Header("Authorization") String authorization
    );

    @POST("rest/v1/products")
    Call<Void> addProduct(
            @Body Products products,
            @Header("apikey") String apiKey,
            @Header("Authorization") String authorization
    );

    @GET("rest/v1/products")
    Call<List<Products>> getProductsByUmkmId(
            @Query("umkm_id") String umkmId,
            @Header("apikey") String apiKey,
            @Header("Authorization") String authorization
    );

    @PATCH("rest/v1/products")
    Call<Void> updateProduct(
            @Query("id=eq.{id}") String id,
            @Body Products product,
            @Header("apikey") String apiKey,
            @Header("Authorization") String authorization
    );

    @DELETE("rest/v1/products")
    Call<Void> deleteProductsById(
            @Header("Authorization") String authorization,
            @Header("apikey") String apiKey,
            @Query("id=eq.{id}") String id
    );

    @PATCH("rest/v1/umkm")
    Call<Void> updateUmkm(
            @Query("id=eq.{id}") String id,
            @Body Umkm umkm,
            @Header("apikey") String apiKey,
            @Header("Authorization") String authorization
    );

    @DELETE("rest/v1/umkm")
    Call<Void> deleteUmkm(
            @Header("Authorization") String authorization,
            @Header("apikey") String apiKey,
            @Query("id=eq.{id}") String id
    );

    @PATCH("rest/v1/users")
    Call<Void> updateUsers(
            @Query("id=eq.{id}") String id,
            @Body User user,
            @Header("apikey") String apiKey,
            @Header("Authorization") String authorization
    );

    @GET("rest/v1/umkm")
    Call<List<Umkm>> getUmkm(
            @Query("id") String id, // Harus sesuai dengan format Supabase
            @Query("select") String select,
            @Header("apikey") String apiKey,
            @Header("Authorization") String authorization
    );








}
