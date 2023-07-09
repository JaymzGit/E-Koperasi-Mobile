package com.gnj.e_koperasi.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.gnj.e_koperasi.AboutUs;
import com.gnj.e_koperasi.MainActivity;
import com.gnj.e_koperasi.R;

public class GuestFragment extends Fragment {
    String id;
    public GuestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_guest, container, false);

        Button aboutButton = view.findViewById(R.id.btnAboutUs2);
        Button logoutButton = view.findViewById(R.id.btnLogOut2);

        Bundle bundle = getActivity().getIntent().getExtras();
        id = bundle.getString("id");

        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AboutUs.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), MainActivity.class));
            }
        });

        return view;
    }
}