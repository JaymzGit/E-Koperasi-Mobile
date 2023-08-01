package com.gnj.e_koperasi.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.gnj.e_koperasi.AboutUs;
import com.gnj.e_koperasi.HistoryPage;
import com.gnj.e_koperasi.MainActivity;
import com.gnj.e_koperasi.PasswordChange;
import com.gnj.e_koperasi.R;
import com.gnj.e_koperasi.SharedPrefManager;
import com.gnj.e_koperasi.UserProfile;

public class UserFragment extends Fragment {
    String id;
    AlertDialog alertDialog;

    public UserFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        Button proflButton = view.findViewById(R.id.btnProfile);
        Button changepassButton = view.findViewById(R.id.btnChangePass);
        Button histButton = view.findViewById(R.id.btnHistory);
        Button aboutButton = view.findViewById(R.id.btnAboutUs);
        Button logoutButton = view.findViewById(R.id.btnLogOut);

        Bundle bundle = getActivity().getIntent().getExtras();
        id = bundle.getString("id");

        proflButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UserProfile.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });

        changepassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PasswordChange.class);
                intent.putExtra("id", id);
                intent.putExtra("frompage","Settings");
                startActivity(intent);
            }
        });

        histButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), HistoryPage.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });

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
                View customLayout = getLayoutInflater().inflate(R.layout.custom_layout, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setView(customLayout);

                // Find the YES and NO buttons in the custom layout
                Button yesButton = customLayout.findViewById(R.id.buttonPayAtCounter);
                Button noButton = customLayout.findViewById(R.id.buttonOnlinePayment);

                yesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SharedPrefManager sharedPrefManager = new SharedPrefManager(getActivity());
                        sharedPrefManager.setLoggedIn(false);
                        startActivity(new Intent(getActivity(), MainActivity.class));
                        alertDialog.dismiss(); // Dismiss the AlertDialog when YES is clicked
                    }
                });

                noButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss(); // Dismiss the AlertDialog when NO is clicked
                    }
                });

                alertDialog = alertDialogBuilder.create(); // Create the AlertDialog
                alertDialog.show();
            }
        });
        return view;
    }
}