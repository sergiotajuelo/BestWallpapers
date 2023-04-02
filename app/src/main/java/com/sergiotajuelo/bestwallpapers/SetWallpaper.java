package com.sergiotajuelo.bestwallpapers;

import android.app.Service;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class SetWallpaper extends Service {
    WallpaperManager wallpaperManager;
    int current = 0;
    public static List<String> imagenes;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (current >= imagenes.size()) {
            current = 0;
        }

        try {
            URL url = new URL(imagenes.get(current++));
            Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            wallpaperManager.setBitmap(image);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Initialize WallpaperManager
        wallpaperManager = WallpaperManager.getInstance(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
