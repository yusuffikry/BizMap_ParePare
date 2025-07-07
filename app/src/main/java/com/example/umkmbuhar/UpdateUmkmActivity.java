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
import com.example.umkmbuhar.Model.Umkm;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UpdateUmkmActivity extends AppCompatActivity {

    private EditText editUmkmName, editUmkmAddress, editUmkmPhone, editlinkmaps, editNib, editlinkig, editlinkfb;
    private ImageView editUmkmPhoto;
    private Button btnUpdateUmkm;
    private SupabaseApiService apiService;
    private Umkm currentUmkm;
    private static final String TAG = "UpdateUmkmActivity";
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InhuanBza3Fibm56ZW14ZHl6Zm5yIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTE0NTk5MDksImV4cCI6MjA2NzAzNTkwOX0.ZkUQ16Pa-G0V-dUWzYH5KN3pmkmdBDY7NrJd-6II_qA";
    private static final String AUTHORIZATION = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InhuanBza3Fibm56ZW14ZHl6Zm5yIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTE0NTk5MDksImV4cCI6MjA2NzAzNTkwOX0.ZkUQ16Pa-G0V-dUWzYH5KN3pmkmdBDY7NrJd-6II_qA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_umkm);

        // Inisialisasi komponen
        editUmkmName = findViewById(R.id.edit_umkm_name);
        editUmkmAddress = findViewById(R.id.edit_umkm_address);
        editUmkmPhone = findViewById(R.id.edit_umkm_telepon);
        editUmkmPhoto = findViewById(R.id.edit_umkm_photo);
        editlinkmaps = findViewById(R.id.edit_umkm_location);
        editNib = findViewById(R.id.edit_nib);
        editlinkig = findViewById(R.id.editLinkIg);
        editlinkfb = findViewById(R.id.editLinkFb);
        btnUpdateUmkm = findViewById(R.id.btn_update);

        // Ambil data dari intent
        currentUmkm = (Umkm) getIntent().getSerializableExtra("umkm");
        if (currentUmkm != null) {
            populateFields(currentUmkm);
        } else {
            Toast.makeText(this, "Data tidak ditemukan!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Konfigurasi Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://xnjpskqbnnzemxdyzfnr.supabase.co/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(SupabaseApiService.class);

        btnUpdateUmkm.setOnClickListener(v -> {
            if (validateInput()) {
                updateUmkm();
            }
        });
    }

    private void populateFields(Umkm umkm) {
        editUmkmName.setText(umkm.getName());
        editUmkmAddress.setText(umkm.getAddress());
        editUmkmPhone.setText(umkm.getPhone());
        editlinkmaps.setText(umkm.getLocation());
        editNib.setText(umkm.getNib());
        editlinkig.setText(umkm.getLink_ig());
        editlinkfb.setText(umkm.getLink_fb());

        Glide.with(this)
                .load(umkm.getPhoto_url())
                .placeholder(R.drawable.baseline_camera_alt_24)
                .into(editUmkmPhoto);
    }

    private boolean validateInput() {
        if (TextUtils.isEmpty(editUmkmName.getText())) {
            editUmkmName.setError("Nama UMKM tidak boleh kosong");
            return false;
        }
        if (TextUtils.isEmpty(editUmkmAddress.getText())) {
            editUmkmAddress.setError("Alamat tidak boleh kosong");
            return false;
        }
        if (TextUtils.isEmpty(editUmkmPhone.getText())) {
            editUmkmPhone.setError("Nomor telepon tidak boleh kosong");
            return false;
        }
        return true;
    }

    private void updateUmkm() {
        if (currentUmkm == null || currentUmkm.getId() == 0) {
            Toast.makeText(this, "ID UMKM tidak valid!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update data UMKM
        currentUmkm.setName(editUmkmName.getText().toString());
        currentUmkm.setAddress(editUmkmAddress.getText().toString());
        currentUmkm.setPhone(editUmkmPhone.getText().toString());
        currentUmkm.setLocation(editlinkmaps.getText().toString());
        currentUmkm.setNib(editNib.getText().toString());
        currentUmkm.setLink_ig(editlinkig.getText().toString());
        currentUmkm.setLink_fb(editlinkfb.getText().toString());

        Log.d(TAG, "Mengupdate UMKM dengan ID: " + currentUmkm.getId());

        String umkmIdQuery = "eq." + currentUmkm.getId();

        Call<Void> call = apiService.updateUmkm(
                umkmIdQuery,
                currentUmkm,
                API_KEY,
                AUTHORIZATION
        );

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(UpdateUmkmActivity.this, "UMKM berhasil diperbarui", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Update berhasil untuk ID: " + currentUmkm.getId());
                    finish();
                } else {
                    Toast.makeText(UpdateUmkmActivity.this, "Gagal memperbarui UMKM", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Response error: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(UpdateUmkmActivity.this, "Kesalahan jaringan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Network error: ", t);
            }
        });
    }
}
