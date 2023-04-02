package com.sergiotajuelo.bestwallpapers;

import static com.sergiotajuelo.bestwallpapers.MainActivity.sharedPref;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.sergiotajuelo.bestwallpapers.adapter.ListViewAdapter;
import com.sergiotajuelo.bestwallpapers.databinding.ActivityFollowingUserBinding;
import com.sergiotajuelo.bestwallpapers.utils.AppUtils;
import com.sergiotajuelo.bestwallpapers.utils.SharedPref;
import com.sergiotajuelo.bestwallpapers.utils.UserUtils;

import java.util.ArrayList;

public class FollowingUser extends AppCompatActivity {

    ActivityFollowingUserBinding binding;
    private final ArrayList<String> followingList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*sharedPref = new SharedPref(this);
        if(sharedPref.loadNightModeState()) setTheme(R.style.Theme_BestWallpapersNight);
        else setTheme(R.style.Theme_BestWallpapers);*/

        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
            setTheme(R.style.Theme_BestWallpapersNight);

        binding = ActivityFollowingUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        followingList.addAll(UserUtils.getUsernamesFollowing());
        ListViewAdapter myAdapter = new ListViewAdapter(this, R.layout.user_item, UserUtils.getUsersFollowing());
        binding.listview.setAdapter(myAdapter);

        binding.listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent2 = new Intent(FollowingUser.this, ProfileActivity.class);
                intent2.putExtra("wallpaper", UserUtils.getUserFromUsername(followingList.get(position)));
                startActivity(intent2);
            }
        });

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}