package com.sergiotajuelo.bestwallpapers.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sergiotajuelo.bestwallpapers.FullScreen;
import com.sergiotajuelo.bestwallpapers.QueryExplorar;
import com.sergiotajuelo.bestwallpapers.R;
import com.sergiotajuelo.bestwallpapers.adapter.ParentAdapter;
import com.sergiotajuelo.bestwallpapers.adapter.SugerenciasAdapter;
import com.sergiotajuelo.bestwallpapers.models.ParentItem;
import com.sergiotajuelo.bestwallpapers.models.SugerenciaModel;
import com.sergiotajuelo.bestwallpapers.models.WallpaperModel;
import com.sergiotajuelo.bestwallpapers.databinding.FragmentExplorarBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ExplorarFragment extends Fragment {
    private FragmentExplorarBinding binding;
    private SugerenciasAdapter sugerenciasAdapter;
    private List<SugerenciaModel> sugerenciaModelList;

    private List<WallpaperModel> wallpaperModelArrayList = new ArrayList<>();
    private List<WallpaperModel> wallpaperModelArrayList2 = new ArrayList<>();
    private List<WallpaperModel> wallpaperModelArrayList3 = new ArrayList<>();
    private List<WallpaperModel> wallpaperModelArrayList4 = new ArrayList<>();
    private List<WallpaperModel> wallpaperModelArrayList5 = new ArrayList<>();
    private ParentAdapter parentItemAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentExplorarBinding.inflate(getLayoutInflater(), container, false);
        binding.buscador.setFilters(new InputFilter[] { filter });

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        parentItemAdapter = new ParentAdapter(ParentItemList(), getContext());

        binding.parentRecyclerView.setAdapter(parentItemAdapter);
        binding.parentRecyclerView.setLayoutManager(layoutManager);

        sugerencias();

        binding.buscador.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    buscar();
                    handled = true;
                }
                return handled;
            }
        });

        binding.buscador.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(binding.buscador.getText().toString().length() == 0)
                    binding.iconoBuscador.setImageResource(R.drawable.ic_search);
                else
                    binding.iconoBuscador.setImageResource(R.drawable.ic_delete);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.iconoBuscador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.buscador.getText().toString().length() > 0)
                    binding.buscador.setText("");
            }
        });

        return binding.getRoot();
    }

    private void buscar() {
        if(!binding.buscador.getText().toString().isEmpty())
            this.startActivity(new Intent(getContext(), QueryExplorar.class)
                    .putExtra("QueryName", binding.buscador.getText().toString()));
    }

    private List<ParentItem> ParentItemList() {
        List<ParentItem> itemList = new ArrayList<>();

        String url = "https://api.unsplash.com/search/photos?per_page=20&client_id=b1jJGO64mIzSRFeAt12g5XXE5m2jSLz1pXV1sTFVBy0&";

        ParentItem item = new ParentItem("Populares",
                getWallpapers(url + "query=trending", wallpaperModelArrayList));
        itemList.add(item);

        ParentItem item1 = new ParentItem("Espacio",
                getWallpapers(url + "query=space", wallpaperModelArrayList2));
        itemList.add(item1);

        ParentItem item2 = new ParentItem("Océano",
                getWallpapers(url + "query=ocean", wallpaperModelArrayList3));
        itemList.add(item2);

        ParentItem item3 = new ParentItem("Por color - Negro",
                getWallpapers(url + "query=black&color=black", wallpaperModelArrayList4));
        itemList.add(item3);

        ParentItem item4 = new ParentItem("Animales",
                getWallpapers(url + "query=animals", wallpaperModelArrayList5));
        itemList.add(item4);

        return itemList;
    }

    public void sugerencias() {
        sugerenciaModelList = new ArrayList<>();
        sugerenciasAdapter = new SugerenciasAdapter(getContext(), sugerenciaModelList);

        sugerenciaModelList.add(new SugerenciaModel("Neón"));
        sugerenciaModelList.add(new SugerenciaModel("Motor"));
        sugerenciaModelList.add(new SugerenciaModel("Futurista"));
        sugerenciaModelList.add(new SugerenciaModel("Montaña"));
        sugerenciaModelList.add(new SugerenciaModel("Naturaleza"));
        sugerenciaModelList.add(new SugerenciaModel("Arquitectura"));
        sugerenciaModelList.add(new SugerenciaModel("Tecnología"));

        binding.recyclerView2.setHasFixedSize(true);
        binding.recyclerView2.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerView2.setAdapter(sugerenciasAdapter);
    }

    public List<WallpaperModel> getWallpapers(String queryText, List<WallpaperModel> list) {
        StringRequest request = new StringRequest(Request.Method.GET, queryText, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("results");

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
                                username,false);
                        list.add(wallpaperModel);

                        parentItemAdapter.notifyDataSetChanged();

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

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(request);

        return list;
    }

    InputFilter filter = (source, start, end, dest, dstart, dend) -> {
        for (int i = start; i < end; i++) {
            if (!Character.isLetterOrDigit(source.charAt(i))) {
                return "";
            }
        }
        return null;
    };
}
