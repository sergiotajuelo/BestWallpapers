package com.sergiotajuelo.bestwallpapers.utils;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class SharedPref {

    public static void saveData(Context context, SwitchMaterial switchMaterial) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("SharedPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Add other Switches, Buttons, Texboxes, etc to save
        editor.putBoolean("SwitchState", switchMaterial.isChecked());

        editor.apply();
    }

    public static boolean loadData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("SharedPrefs", MODE_PRIVATE);
        return sharedPreferences.getBoolean("SwitchState", false);
    }
}
