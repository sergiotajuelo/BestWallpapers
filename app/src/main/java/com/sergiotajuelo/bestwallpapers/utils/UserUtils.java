package com.sergiotajuelo.bestwallpapers.utils;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sergiotajuelo.bestwallpapers.adapter.WallpaperAdapterEspecifico;
import com.sergiotajuelo.bestwallpapers.fragments.SiguiendoFragment;
import com.sergiotajuelo.bestwallpapers.models.WallpaperModel;

import java.util.ArrayList;
import java.util.List;

public class UserUtils {

    private static String nombre = "", bio = "", photo = "";
    private static boolean usuarioAdmin = false;
    private static ArrayList<WallpaperModel> usersFollowing = new ArrayList<>();
    private static ArrayList<WallpaperModel> favouriteWallpapers = new ArrayList<>();
    private static DatabaseReference databaseReference;
    private static FirebaseUser firebaseUser;
    private static int totalPhotos;

    public static String basicPhoto = "https://firebasestorage.googleapis.com/v0/b/bestwallpapers-1d9b2.appspot.com" +
            "/o/wall.png?alt=media&token=f4920a02-331c-4a18-90e9-01ded86bdd79";
    public static String basicBio = "Sin biografía.";

    public static StorageReference reference = FirebaseStorage.getInstance().getReference();
    public static DatabaseReference root = FirebaseDatabase.getInstance().getReference();

    public static FirebaseUser getCurrentUser(){
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser != null)
            loadInfo();

        return firebaseUser;
    }

    public static void setCurrentUser(FirebaseUser firebaseUser){
        UserUtils.firebaseUser = firebaseUser;
    }

    public static void loadInfo() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setNombre(dataSnapshot.child("fullname").getValue().toString());
                setBio(dataSnapshot.child("bio").getValue().toString());
                setPhoto(dataSnapshot.child("profilePhoto").getValue().toString());
                setUsuarioAdmin((Boolean) dataSnapshot.child("tipoUsuario").getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usersFollowing = new ArrayList<>();
                for (DataSnapshot d: dataSnapshot.getChildren()) {
                    String id = d.child("id").getValue(String.class);
                    String bio = d.child("bio").getValue(String.class);
                    String username = d.child("username").getValue(String.class);
                    String nombre = d.child("nombre").getValue(String.class);
                    String photo = d.child("photo").getValue(String.class);

                    usersFollowing.add(new WallpaperModel(id, bio, username, nombre, photo));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static String checkPhotos() {
        totalPhotos = 0;
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).child("photos");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot zoneSnapshot: dataSnapshot.getChildren()) {
                        totalPhotos++;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return totalPhotos + "";
    }

    public static void setFollowingUser(WallpaperModel userModel){
        usersFollowing.add(userModel);
    }

    public static void deleteFollowingUser(String username){
        for(WallpaperModel u : usersFollowing){
            if(u.getUsername().equals(username))
                usersFollowing.remove(u);
        }
    }

    public static ArrayList<WallpaperModel> getUsersFollowing() {
        return usersFollowing;
    }

    public static ArrayList<String> getUsernamesFollowing() {
        ArrayList<String> users = new ArrayList<>();

        for(WallpaperModel w : usersFollowing){
            users.add(w.getUsername());
        }
        return users;
    }

    public static WallpaperModel getUserFromUsername(String username) {
        for (WallpaperModel w : usersFollowing) {
            if(w.getUsername().equals(username))
                return w;
        }

        return null;
    }

    public static void setUsersFollowing(ArrayList<WallpaperModel> usersFollowing) {
        UserUtils.usersFollowing = usersFollowing;
    }

    public static boolean isUsuarioAdmin() {
        return usuarioAdmin;
    }

    public static void setUsuarioAdmin(boolean usuarioAdmin) {
        UserUtils.usuarioAdmin = usuarioAdmin;
    }

    public static String getNombre() {
        return nombre;
    }

    public static void setNombre(String nombre) {
        UserUtils.nombre = nombre;
    }

    public static String getBio() {
        return bio;
    }

    public static void setBio(String bio) {
        UserUtils.bio = bio;
    }

    public static String getPhoto() {
        return photo;
    }

    public static void setPhoto(String photo) {
        UserUtils.photo = photo;
    }
}
