package com.sergiotajuelo.bestwallpapers.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sergiotajuelo.bestwallpapers.QueryExplorar;
import com.sergiotajuelo.bestwallpapers.R;
import com.sergiotajuelo.bestwallpapers.models.ParentItem;

import java.util.List;

public class ParentAdapter extends RecyclerView.Adapter<ParentAdapter.ParentViewHolder>{

    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private List<ParentItem> itemList;
    private Context context;

    // Constructor
    public ParentAdapter(List<ParentItem> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    @NonNull
    @Override
    public ParentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_child,viewGroup, false);
        return new ParentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParentViewHolder parentViewHolder, int position){
        ParentItem parentItem = itemList.get(position);

        LinearLayoutManager layoutManager = new LinearLayoutManager(parentViewHolder.ChildRecyclerView.getContext(),
                LinearLayoutManager.HORIZONTAL, false);

        layoutManager.setInitialPrefetchItemCount(parentItem.getChildItemList().size());
        WallpaperAdapter childItemAdapter = new WallpaperAdapter(context, parentItem.getChildItemList());
        parentViewHolder.ChildRecyclerView.setLayoutManager(layoutManager);
        parentViewHolder.ChildRecyclerView.setAdapter(childItemAdapter);
        parentViewHolder.ChildRecyclerView.setRecycledViewPool(viewPool);

        parentViewHolder.texto.setText(parentItem.getParentItemTitle());

        parentViewHolder.verMas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, QueryExplorar.class)
                        .putExtra("QueryName", parentViewHolder.texto.getText().toString()));
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return itemList.size();
    }

    class ParentViewHolder extends RecyclerView.ViewHolder {

        private RecyclerView ChildRecyclerView;
        TextView texto;
        TextView verMas;

        public ParentViewHolder(@NonNull View itemView) {
            super(itemView);

            texto = itemView.findViewById(R.id.idTexto);
            verMas = itemView.findViewById(R.id.verMas);
            ChildRecyclerView = itemView.findViewById(R.id.childRecyclerView);
        }
    }
}
