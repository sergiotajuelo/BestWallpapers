package com.sergiotajuelo.bestwallpapers.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sergiotajuelo.bestwallpapers.R;
import com.sergiotajuelo.bestwallpapers.adapter.WallpaperAdapterEspecifico;
import com.sergiotajuelo.bestwallpapers.adapter.WallpaperAdapterFavoritos;
import com.sergiotajuelo.bestwallpapers.databinding.FragmentExplorarBinding;
import com.sergiotajuelo.bestwallpapers.databinding.FragmentHomeScreenBinding;
import com.sergiotajuelo.bestwallpapers.models.WallpaperModel;
import com.sergiotajuelo.bestwallpapers.utils.UserUtils;
import com.sergiotajuelo.bestwallpapers.utils.WallpaperUtils;

import java.util.ArrayList;

public class SiguiendoFragment extends Fragment {

    private FragmentHomeScreenBinding binding;
    private ArrayList<WallpaperModel> wallpaperModelList;
    private DatabaseReference databaseReference;
    private WallpaperAdapterFavoritos adapter;

    private final FirebaseUser firebaseUser = UserUtils.getCurrentUser();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeScreenBinding.inflate(getLayoutInflater(), container, false);

        binding.recyclerViewFavoritos.setHasFixedSize(true);
        binding.recyclerViewFavoritos.setLayoutManager(new GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL,
                false));
        binding.recyclerViewFavoritos.setNestedScrollingEnabled(false);
        wallpaperModelList = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Favorites").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                wallpaperModelList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    WallpaperModel wallpaperModel = dataSnapshot.getValue(WallpaperModel.class);
                    wallpaperModelList.add(wallpaperModel);
                }
                adapter = new WallpaperAdapterFavoritos(getContext(), wallpaperModelList);
                binding.recyclerViewFavoritos.setAdapter(adapter);

                adapter.notifyDataSetChanged();

                if(wallpaperModelList.isEmpty())
                    binding.noFavoritos.setVisibility(View.VISIBLE);
                else
                    binding.noFavoritos.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        return binding.getRoot();
    }
}
