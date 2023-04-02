package com.sergiotajuelo.bestwallpapers;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sergiotajuelo.bestwallpapers.databinding.ActivityFullScreenBinding;
import com.sergiotajuelo.bestwallpapers.models.WallpaperModel;
import com.sergiotajuelo.bestwallpapers.utils.AnimationUtils;
import com.sergiotajuelo.bestwallpapers.utils.AppUtils;
import com.sergiotajuelo.bestwallpapers.utils.FavouriteWallpaper;
import com.sergiotajuelo.bestwallpapers.utils.WallpaperUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public class FullScreen extends AppCompatActivity {

    WallpaperModel wallpaperModel;
    private ActivityFullScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
            setTheme(R.style.Theme_BestWallpapersNight);

        binding = ActivityFullScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        wallpaperModel = (WallpaperModel) intent.getSerializableExtra("wallpaper");

        FavouriteWallpaper.animateFavIcon(binding.favoriteIcon);
        FavouriteWallpaper.isLiked(wallpaperModel, binding.favoriteIcon);
        FavouriteWallpaper.onFavIconClick(binding.favoriteIcon, wallpaperModel);

        if(wallpaperModel.getHeight() != null)
            binding.tamanyo.setText(wallpaperModel.getHeight() + " x " + wallpaperModel.getWidth());

        AppUtils.noBorders(this);

        Glide.with(this).asDrawable().load(wallpaperModel.getOriginalUrl()).apply(RequestOptions
                .placeholderOf(WallpaperUtils.loadPlaceholder(this))).into(binding.fullImage);
        Glide.with(this).asDrawable().load(wallpaperModel.getOriginalUrl()).into(binding.imagenFondo);

        binding.creator.setText(wallpaperModel.getCreator());

        binding.creadorLink.setOnClickListener(v -> {
            Intent intent2 = new Intent(FullScreen.this, ProfileActivity.class);
            intent2.putExtra("wallpaper", wallpaperModel);
            startActivity(intent2);
        });
        binding.backButton.setOnClickListener(v -> finish());
        binding.download.setOnClickListener(v -> descargarImagen(binding.fullImage));

        binding.establecerFondo.setOnClickListener(v -> establecerFondo(binding.fullImage,
                wallpaperModel.getOriginalUrl()));
    }

    public void descargarImagen(ImageView imagen) {

        ActivityCompat.requestPermissions(FullScreen.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        ActivityCompat.requestPermissions(FullScreen.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        try{
            BitmapDrawable drawable = (BitmapDrawable) imagen.getDrawable();
            Bitmap bitmap = drawable.getBitmap();

            String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();

            File myDir = new File(root + "/Best Wallpapers");
            myDir.mkdirs();
            Random generator = new Random();
            int n = 10000;
            n = generator.nextInt(n);
            String fname = "Image-" + n + ".jpg";
            File file = new File(myDir, fname);
            if (file.exists())
                file.delete();
            try {
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            MediaScannerConnection.scanFile(FullScreen.this, new String[]{file.toString()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        }
                    });

            Toast.makeText(FullScreen.this, "Descarga completada.", Toast.LENGTH_SHORT).show();


        }
        catch(Exception e) {
        }

    }

    public void establecerFondo(ImageView imagen, String originalUrl) {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_layout);

        LinearLayout lock = dialog.findViewById(R.id.lock);
        LinearLayout main = dialog.findViewById(R.id.main);
        LinearLayout both = dialog.findViewById(R.id.both);
        LinearLayout close = dialog.findViewById(R.id.close);

        try{
            BitmapDrawable drawable = (BitmapDrawable) imagen.getDrawable();
            Bitmap bitmap = drawable.getBitmap();

            lock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());

                            wallpaperManager.setBitmap(bitmap,null, true, WallpaperManager.FLAG_LOCK);

                            Toast.makeText(FullScreen.this, "El fondo de pantalla se ha cambiado correctamente.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Intent intent = new Intent("android.intent.action.ATTACH_DATA");
                            intent.addCategory("android.intent.category.DEFAULT");
                            String str = "image/*";
                            intent.setDataAndType(Uri.fromFile(new File(originalUrl)), str);
                            intent.putExtra("mimeType", str);
                            startActivity(Intent.createChooser(intent, "Set As:"));
                        }
                    } catch (IOException e) {
                        Toast.makeText(FullScreen.this, "Error. El fondo de pantalla no se pudo cambiar.",
                                Toast.LENGTH_SHORT).show();
                    }

                }
            });

            main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
                    try{
                        wallpaperManager.setBitmap(bitmap);
                        Toast.makeText(FullScreen.this, "El fondo de pantalla se ha cambiado correctamente.",
                                Toast.LENGTH_SHORT).show();
                    }
                    catch (IOException e) {
                        Toast.makeText(FullScreen.this, "Error. El fondo de pantalla no se pudo cambiar.",
                                Toast.LENGTH_SHORT).show();
                    }

                }
            });

            both.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();

                    WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
                    try{
                        wallpaperManager.setBitmap(bitmap);
                        Toast.makeText(FullScreen.this, "El fondo de la pantalla principal se ha cambiado correctamente.",
                                Toast.LENGTH_SHORT).show();
                    }
                    catch (IOException e) {
                        Toast.makeText(FullScreen.this, "Error. El fondo de la pantalla principal no se pudo cambiar.",
                                Toast.LENGTH_SHORT).show();
                    }

                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            WallpaperManager wallpaperManager2 = WallpaperManager.getInstance(getApplicationContext());

                            wallpaperManager2.setBitmap(bitmap,null, true, WallpaperManager.FLAG_LOCK);

                            Toast.makeText(FullScreen.this, "El fondo de la pantalla de bloqueo se ha cambiado correctamente.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Intent intent = new Intent("android.intent.action.ATTACH_DATA");
                            intent.addCategory("android.intent.category.DEFAULT");
                            String str = "image/*";
                            intent.setDataAndType(Uri.fromFile(new File(originalUrl)), str);
                            intent.putExtra("mimeType", str);
                            startActivity(Intent.createChooser(intent, "Set As:"));
                        }
                    } catch (IOException e) {
                        Toast.makeText(FullScreen.this, "Error. El fondo de la pantalla de bloqueo no se pudo cambiar.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });

            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            dialog.getWindow().setGravity(Gravity.BOTTOM);


        }
        catch(Exception e) {
        }

    }
}