package com.sergiotajuelo.bestwallpapers.utils;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sergiotajuelo.bestwallpapers.MainActivity;
import com.sergiotajuelo.bestwallpapers.models.WallpaperModel;

public class FavouriteWallpaper {

    private static FirebaseUser user = UserUtils.getCurrentUser();
    private static DatabaseReference reference;

    public static void animateFavIcon(ToggleButton toggleButton) {
        AnimationUtils.animateFavButton(toggleButton);
    }

    public static void onFavIconClick(ToggleButton toggleButton, WallpaperModel wallpaperModel) {
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference = FirebaseDatabase.getInstance().getReference().child("Favorites").child(user.getUid())
                        .child(wallpaperModel.getId());

                if(toggleButton.isChecked()){
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            reference.setValue(wallpaperModel);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
                else{
                    reference.removeValue();
                }
            }
        });
    }

    public static void isLiked(WallpaperModel wallpaperModel, ToggleButton toggleButton) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Favorites")
                .child(user.getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(wallpaperModel.getId()).exists()) {
                    toggleButton.setChecked(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
