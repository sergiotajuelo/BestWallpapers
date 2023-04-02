package com.sergiotajuelo.bestwallpapers;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sergiotajuelo.bestwallpapers.adapter.WallpaperAdapter;
import com.sergiotajuelo.bestwallpapers.adapter.WallpaperAdapterEspecifico;
import com.sergiotajuelo.bestwallpapers.databinding.ActivityCategoriaEspecificaBinding;
import com.sergiotajuelo.bestwallpapers.models.WallpaperModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

public class QueryExplorar extends AppCompatActivity {

    private ActivityCategoriaEspecificaBinding binding;
    private WallpaperAdapterEspecifico adapter;

    private String texto, query = "";
    private List<WallpaperModel> wallpaperModelArrayList = new ArrayList<>();
    private int page = 1;
    int currentItems,totalItems,scrollOutItems;
    private boolean isScrolling  = false;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);

        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
            setTheme(R.style.Theme_BestWallpapersNight);

        binding = ActivityCategoriaEspecificaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        adapter = new WallpaperAdapterEspecifico(this, wallpaperModelArrayList);
        binding.mainRecyclerView.setAdapter(adapter);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        binding.mainRecyclerView.setLayoutManager(gridLayoutManager);

        checkScrollRecyclerView(gridLayoutManager);

        Intent intent = getIntent();
        texto = intent.getStringExtra("QueryName").toLowerCase();

        switch (texto) {
            case "populares":
                query = "popular";
                break;
            case "espacio":
                query = "space";
                break;
            case "océano":
                query = "ocean";
                break;
            case "por color - negro":
                query = "black";
                break;
            case "animales":
                query = "animals";
                break;
            case "neón":
                query = "neon";
                break;
            case "futurista":
                query = "future";
                break;
            case "montaña":
                query = "montain";
                break;
            case "naturaleza":
                query = "nature";
                break;
            case "arquitectura":
                query = "architecture";
                break;
            case "tecnología":
                query = "technology";
                break;
            case "motor":
                query = "motor";
                break;
        }

        binding.idTextSugeridos2.setText(texto.substring(0, 1).toUpperCase() + texto.substring(1));

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getWallpapers(query);
    }

    public void checkScrollRecyclerView(GridLayoutManager gridLayoutManager) {
        binding.mainRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                currentItems = gridLayoutManager.getChildCount();
                totalItems = gridLayoutManager.getItemCount();
                scrollOutItems = gridLayoutManager.findFirstVisibleItemPosition();

                if(isScrolling && (currentItems+scrollOutItems==totalItems)){
                    isScrolling = false;
                    getWallpapers(query);
                }


            }
        });
    }

    public void getWallpapers(String queryText) {
        StringRequest request = new StringRequest(Request.Method.GET, "https://api.unsplash.com/search/photos?query="
                + queryText + "&client_id=b1jJGO64mIzSRFeAt12g5XXE5m2jSLz1pXV1sTFVBy0&per_page=90&page=" + page, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("results");

                            binding.progressBar.setVisibility(View.GONE);

                            for (int i = 0; i < jsonArray.length(); i++){
                                JSONObject object = jsonArray.getJSONObject(i);
                                String id = object.getString("id");

                                String width = object.getInt("width") + "";
                                String height = object.getInt("height") + "";

                                String caption = object.getString("description");

                                JSONObject userObject = object.getJSONObject("user");
                                String creator = userObject.getString("name");
                                String username = userObject.getString("username");
                                String creatorUrl = userObject.getString("portfolio_url");
                                String bio = userObject.getString("bio");
                                int total_photos = userObject.getInt("total_photos");
                                String instagram_username = userObject.getString("instagram_username");
                                String twitter_username = userObject.getString("twitter_username");

                                JSONObject userProfileImage = userObject.getJSONObject("profile_image");
                                String smallProfile = userProfileImage.getString("small");
                                String largeProfile = userProfileImage.getString("medium");

                                JSONObject objectImage = object.getJSONObject("urls");
                                String regularImage = objectImage.getString("regular");
                                String fullImage = objectImage.getString("full");

                                WallpaperModel wallpaperModel = new WallpaperModel(id, fullImage, regularImage,
                                        caption, creator, creatorUrl, width, height, smallProfile, largeProfile,
                                        bio, instagram_username, twitter_username, Integer.toString(total_photos),
                                        username, false);
                                wallpaperModelArrayList.add(wallpaperModel);

                                adapter.notifyDataSetChanged();
                                page++;

                            }
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);
    }
}