package com.sergiotajuelo.bestwallpapers.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.sergiotajuelo.bestwallpapers.QueryExplorar;
import com.sergiotajuelo.bestwallpapers.R;
import com.sergiotajuelo.bestwallpapers.models.SugerenciaModel;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SugerenciasAdapter extends RecyclerView.Adapter<SugerenciasAdapter.SugerenciasViewHolder> {

    private List<SugerenciaModel> sugerenciaModels;
    private Context context;

    public SugerenciasAdapter(Context context, List<SugerenciaModel> sugerenciaModels) {
        this.context = context;
        this.sugerenciaModels = sugerenciaModels;
    }

    @NonNull
    @Override
    public SugerenciasAdapter.SugerenciasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sugeridos_item, parent, false);
        return new SugerenciasViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SugerenciasAdapter.SugerenciasViewHolder holder, int position) {
        SugerenciaModel sugerenciaModel = sugerenciaModels.get(position);
        holder.texto.setText(sugerenciaModel.getTitle());

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, QueryExplorar.class)
                        .putExtra("QueryName", holder.texto.getText()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return sugerenciaModels.size();
    }

    public class SugerenciasViewHolder extends RecyclerView.ViewHolder {
        TextView texto;
        CardView card;

        public SugerenciasViewHolder(@NonNull View itemView) {
            super(itemView);
            texto = itemView.findViewById(R.id.textoSugerencia);
            card = itemView.findViewById(R.id.card);

        }
    }
}
