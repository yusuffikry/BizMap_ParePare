package com.example.umkmbuhar.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.umkmbuhar.EditProfileActivity;
import com.example.umkmbuhar.R;

public class ProfileFragment extends Fragment {

    private CardView btn_edit_profile;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ambil referensi TextView dari layout
        TextView profileFullname = view.findViewById(R.id.profile_fullname);
        TextView profileUsername = view.findViewById(R.id.profile_username);
        btn_edit_profile = view.findViewById(R.id.ic_profile);

        // Ambil data dari SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", getContext().MODE_PRIVATE);
        String fullname = sharedPreferences.getString("fullname", "Nama Lengkap");
        String username = sharedPreferences.getString("username", "Username");

        // Set nilai ke TextView
        profileFullname.setText(fullname);
        profileUsername.setText(username);

        // Tambahkan listener untuk btn_edit_profile
        btn_edit_profile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });
    }
}
