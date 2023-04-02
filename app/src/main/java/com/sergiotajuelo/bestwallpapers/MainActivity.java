package com.sergiotajuelo.bestwallpapers;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sergiotajuelo.bestwallpapers.databinding.ActivityMainBinding;
import com.sergiotajuelo.bestwallpapers.fragments.AjustesFragment;
import com.sergiotajuelo.bestwallpapers.fragments.ExplorarFragment;
import com.sergiotajuelo.bestwallpapers.fragments.SiguiendoFragment;
import com.sergiotajuelo.bestwallpapers.utils.SharedPref;
import com.sergiotajuelo.bestwallpapers.utils.UserUtils;
import com.sergiotajuelo.bestwallpapers.utils.WallpaperUtils;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private final FirebaseUser firebaseUser = UserUtils.getCurrentUser();
    private long pressedTime;
    public static SharedPref sharedPref;

    @Override
    protected void onStart() {
        super.onStart();

        if(firebaseUser == null){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);

        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
            setTheme(R.style.Theme_BestWallpapersNight);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loadFragment(new ExplorarFragment());

        binding.bottomNavigation.addBubbleListener(item -> {
            switch (item){
                case R.id.explorar:
                    loadFragment(new ExplorarFragment());
                    break;
                case R.id.siguiendo:
                    loadFragment(new SiguiendoFragment());
                    break;
                case R.id.ajustes:
                    loadFragment(new AjustesFragment());
                    break;
            }
        });
    }

    public void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (pressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            finish();
        } else {
            Toast.makeText(getBaseContext(), "Pulse de nuevo para salir", Toast.LENGTH_SHORT).show();
        }
        pressedTime = System.currentTimeMillis();
    }
}
