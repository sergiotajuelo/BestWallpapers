package com.sergiotajuelo.bestwallpapers.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sergiotajuelo.bestwallpapers.R;
import com.sergiotajuelo.bestwallpapers.models.WallpaperModel;
import com.sergiotajuelo.bestwallpapers.utils.UserUtils;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListViewAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private ArrayList<WallpaperModel> names;

    public ListViewAdapter(Context context, int layout, ArrayList<WallpaperModel> names) {
        this.context = context;
        this.layout = layout;
        this.names = names;
    }

    @Override
    public int getCount() {
        return this.names.size();
    }

    @Override
    public Object getItem(int position) {
        return this.names.get(position);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View v = convertView;

        LayoutInflater layoutInflater = LayoutInflater.from(this.context);

        v = layoutInflater.inflate(R.layout.user_item, null);

        String currentName = names.get(position).getUsername();
        String photo = names.get(position).getProfileImageLarge();

        TextView textView = (TextView) v.findViewById(R.id.nombreUsuario);
        textView.setText(currentName);

        CircleImageView circleImageView = (CircleImageView) v.findViewById(R.id.imagenPerfilFollowing);

        Glide.with(context).load(photo).centerCrop()
                .into(circleImageView);
        return v;
    }
}