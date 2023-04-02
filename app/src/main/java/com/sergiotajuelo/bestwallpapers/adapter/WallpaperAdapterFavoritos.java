package com.sergiotajuelo.bestwallpapers.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sergiotajuelo.bestwallpapers.FullScreen;
import com.sergiotajuelo.bestwallpapers.R;
import com.sergiotajuelo.bestwallpapers.models.WallpaperModel;
import com.sergiotajuelo.bestwallpapers.utils.FavouriteWallpaper;
import com.sergiotajuelo.bestwallpapers.utils.WallpaperUtils;

import java.util.List;

public class WallpaperAdapterFavoritos extends RecyclerView.Adapter<WallpaperAdapterFavoritosViewHolder> {

    private Context context;
    private List<WallpaperModel> wallpaperModelList;

    public WallpaperAdapterFavoritos(Context context, List<WallpaperModel> wallpaperModelList) {
        this.wallpaperModelList = wallpaperModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public WallpaperAdapterFavoritosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.wallpaper_item_grande, parent, false);
        return new WallpaperAdapterFavoritosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WallpaperAdapterFavoritosViewHolder holder, int position) {
        Glide.with(context).load(wallpaperModelList.get(position).getMediumUrl()).apply(RequestOptions
                .placeholderOf(WallpaperUtils.loadPlaceholder(context))).into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FullScreen.class);
                intent.putExtra("wallpaper", wallpaperModelList.get(holder.getAdapterPosition()));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return wallpaperModelList.size();
    }
}

class WallpaperAdapterFavoritosViewHolder extends RecyclerView.ViewHolder {
    ImageView imageView;
    TextView verMas;
    Context context;
    TextView textView;

    public WallpaperAdapterFavoritosViewHolder(View itemView) {
        super(itemView);
        verMas = itemView.findViewById(R.id.verMas);
        imageView = itemView.findViewById(R.id.imagen);
        context = itemView.getContext();
        textView = itemView.findViewById(R.id.idTexto);
    }
}
