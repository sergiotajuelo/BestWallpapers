package com.sergiotajuelo.bestwallpapers.fragments;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sergiotajuelo.bestwallpapers.CallbackInterface;
import com.sergiotajuelo.bestwallpapers.FollowingUser;
import com.sergiotajuelo.bestwallpapers.LogOutDialogFragment;
import com.sergiotajuelo.bestwallpapers.LoginActivity;
import com.sergiotajuelo.bestwallpapers.ProfileActivity;
import com.sergiotajuelo.bestwallpapers.R;
import com.sergiotajuelo.bestwallpapers.SetWallpaper;
import com.sergiotajuelo.bestwallpapers.adapter.WallpaperAdapterEspecifico;
import com.sergiotajuelo.bestwallpapers.databinding.FragmentSettingsBinding;
import com.sergiotajuelo.bestwallpapers.models.WallpaperModel;
import com.sergiotajuelo.bestwallpapers.utils.SharedPref;
import com.sergiotajuelo.bestwallpapers.utils.UserUtils;

import java.util.ArrayList;
import java.util.List;

public class AjustesFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private FirebaseAuth auth;
    private AlarmManager alarmManager;
    private long time = 20000;

    private List<String> wallpapers = new ArrayList<>();
    private final FirebaseUser firebaseUser = UserUtils.getCurrentUser();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(getLayoutInflater(), container, false);

        auth = FirebaseAuth.getInstance();
        alarmManager = (AlarmManager) requireContext().getSystemService(Service.ALARM_SERVICE);

        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
            binding.toggleButton2.setChecked(true);

        binding.usuarioActual.setText(UserUtils.getNombre());
        binding.bio.setText(UserUtils.getBio());
        binding.usuariosSiguiendo.setText(UserUtils.getUsersFollowing().size() + " " + getString(R.string.usuarios_que_sigo));
        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                startActivity(new Intent(getContext(), LoginActivity.class));
                getActivity().finish();
            }
        });

        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserUtils.isUsuarioAdmin()) {
                    WallpaperModel wallpaperModel = new WallpaperModel(System.currentTimeMillis() + "", UserUtils.getNombre(),
                            UserUtils.getBio(), UserUtils.checkPhotos(), true,
                            UserUtils.getPhoto(), UserUtils.getPhoto());

                    Intent intent = new Intent(getContext(), ProfileActivity.class);
                    intent.putExtra("wallpaper", wallpaperModel);
                    startActivity(intent);
                }
            }
        });

        binding.usuariosSiguiendo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FollowingUser.class);
                startActivity(intent);
            }
        });

        Intent intent = new Intent(getContext(), SetWallpaper.class);
        final PendingIntent pi = PendingIntent.getService(getContext(), 0, intent, 0);

        binding.toggleButton1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    retrieveWallpapers(new CallbackInterface<List<String>>() {
                        @Override
                        public void callback(List<String> data) {
                            wallpapers = data;
                        }
                    });

                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, 5000, 60000, pi);
                    binding.toggleButton1.setChecked(true);
                } else {
                    binding.toggleButton1.setChecked(false);
                    alarmManager.cancel(pi);
                }
            }
        });

        binding.toggleButton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

                    SharedPref.saveData(getContext(), binding.toggleButton2);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

                    SharedPref.saveData(getContext(), binding.toggleButton2);
                }
            }
        });

        showInterval(binding.intervalo);

        Glide.with(getContext()).load(UserUtils.getPhoto()).centerCrop()
                .into(binding.profileImage);

        binding.textoToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        showLogOutDialog();

        return binding.getRoot();
    }

    public void retrieveWallpapers(@NonNull CallbackInterface<List<String>> finishedCallback) {
        List<String> wallpapers = new ArrayList<>();
        DatabaseReference answerDatabase = FirebaseDatabase.getInstance().getReference().child("Favorites").child(firebaseUser.getUid());
        answerDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    String wallpaperModel = d.getValue(WallpaperModel.class).getOriginalUrl();
                    wallpapers.add(wallpaperModel);
                }
                SetWallpaper.imagenes = wallpapers;
                finishedCallback.callback(wallpapers);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void showInterval(LinearLayout intervalo) {
        intervalo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] listItems = getResources().getStringArray(R.array.intervalItems);

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
                mBuilder.setTitle("Elige el intervalo");
                mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (listItems[i]) {
                            case "Cada 2 horas":
                                time = 1000 * 60 * 60 * 2;
                                break;

                            case "Cada 6 horas":
                                time = 1000 * 60 * 60 * 6;
                                break;

                            case "Cada 12 horas":
                                time = 1000 * 60 * 60 * 12;
                                break;

                            case "1 vez al dia":
                                time = 1000 * 60 * 60 * 24;
                                break;

                            case "1 vez a la semana":
                                time = 1000 * 60 * 60 * 24 * 7;
                                break;
                        }
                        dialogInterface.dismiss();
                    }
                });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });
    }

    public void showLogOutDialog() {
        FragmentManager fm = getChildFragmentManager();

        binding.editarBoton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                LogOutDialogFragment logOutDialogFragment = new LogOutDialogFragment();
                logOutDialogFragment.show(fm, "Dialog Fragment");
                logOutDialogFragment.setArguments(bundle);
            }
        });
    }
}
