package com.sergiotajuelo.bestwallpapers;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sergiotajuelo.bestwallpapers.adapter.WallpaperAdapterPerfil;
import com.sergiotajuelo.bestwallpapers.databinding.ActivityProfileBinding;
import com.sergiotajuelo.bestwallpapers.models.WallpaperModel;
import com.sergiotajuelo.bestwallpapers.utils.AppUtils;
import com.sergiotajuelo.bestwallpapers.utils.UserUtils;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private WallpaperModel wallpaperModel;
    private ActivityProfileBinding binding;
    private WallpaperAdapterPerfil adapter1;
    private final List<WallpaperModel> wallpaperModelArrayList = new ArrayList<>();
    private final FirebaseUser firebaseUser = UserUtils.getCurrentUser();

    private DatabaseReference reference = UserUtils.root;
    private Uri imageUri;
    public static boolean permisos = false;

    private int page = 1;
    int currentItems,totalItems,scrollOutItems;
    private boolean isScrolling  = false;

    private String ic_twitter = "https://firebasestorage.googleapis.com/v0/b/bestwallpapers-1d9b2." +
            "appspot.com/o/ic_twitter.png?alt=media&token=896388ed-6c82-4eed-b070-d2e60534cf94";
    private String ic_instagram = "https://firebasestorage.googleapis.com/v0/b/bestwallpapers-1d9b2." +
            "appspot.com/o/ic_instagram.png?alt=media&token=710e36f4-6f48-4f9f-95c8-25db668c255b";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.Theme_BestWallpapersNight);
        }

        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AppUtils.noBorders(this);
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
            binding.imagenFondoPerfil.setAlpha(0.5f);

        setBasicInfo();
        twitterAndInstagram();
        setHeightSlidePanel(binding.slidingPanel);

        isFollowing(wallpaperModel.getId(), firebaseUser);
        binding.backButton.setOnClickListener(v -> finish());

        //----------Adapter----------
        adapter1 = new WallpaperAdapterPerfil(this, wallpaperModelArrayList);
        binding.recyclerViewPerfil.setAdapter(adapter1);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        binding.recyclerViewPerfil.setLayoutManager(gridLayoutManager);

        checkScrollRecyclerView(gridLayoutManager);

        botonSeguir_SubirFoto();

        //----------Fondos----------
        if(!wallpaperModel.isUsuarioActual())
            getWallpapers(wallpaperModel.getUsername());
        else
            getWallpapersFirebase();
    }

    public void checkScrollRecyclerView(GridLayoutManager gridLayoutManager) {
        binding.recyclerViewPerfil.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                    getWallpapers(wallpaperModel.getUsername());
                }


            }
        });
    }

    public void getWallpapers(String username) {
        StringRequest request = new StringRequest(Request.Method.GET, "https://api.unsplash.com/users/"
                + username + "/photos?client_id=b1jJGO64mIzSRFeAt12g5XXE5m2jSLz1pXV1sTFVBy0&per_page=20&page=" + page,
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONArray jsonArray = new JSONArray(response);

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
                                bio, instagram_username, twitter_username, Integer.toString(total_photos), username, false);
                        wallpaperModelArrayList.add(wallpaperModel);

                        adapter1.notifyDataSetChanged();
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

    public void getWallpapersFirebase() {
        reference = UserUtils.root.child("Users").child(UserUtils.getCurrentUser().getUid()).child("photos");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot d : snapshot.getChildren()){
                    wallpaperModelArrayList.add(new WallpaperModel(d.child("id").getValue().toString(),
                            d.child("creator").getValue().toString(),
                            d.child("mediumUrl").getValue().toString(),
                            d.child("originalUrl").getValue().toString(), true,
                            UserUtils.getBio(), UserUtils.getPhoto(), UserUtils.getPhoto()));
                }
                adapter1.notifyDataSetChanged();
                binding.total.setText(snapshot.getChildrenCount() + " wallpapers");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void isFollowing(String userid, FirebaseUser firebaseUser) {
        if(!wallpaperModel.isUsuarioActual()) {
            reference = FirebaseDatabase.getInstance().getReference().child("Follow")
                    .child(firebaseUser.getUid()).child("following");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child(userid).exists()) {
                        binding.button.setText("siguiendo");
                    } else
                        binding.button.setText("seguir");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else {
            binding.button.setText("subir imagen");
        }
    }

    public void botonSeguir_SubirFoto() {
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.button.getText().toString().equals("seguir")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(wallpaperModel.getId()).setValue(getHashMap());

                    UserUtils.setFollowingUser(wallpaperModel);
                }
                else if(binding.button.getText().toString().equals("siguiendo")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(wallpaperModel.getId()).removeValue();

                    UserUtils.deleteFollowingUser(wallpaperModel.getUsername());
                }
                else if(binding.button.getText().toString().equals("subir imagen")){
                    getImage();
                }
            }
        });
    }

    public HashMap<String, Object> getHashMap() {
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("id", wallpaperModel.getId());
        hashMap.put("nombre", wallpaperModel.getCreator());
        hashMap.put("bio", wallpaperModel.getBio());
        hashMap.put("username", wallpaperModel.getUsername());
        hashMap.put("photo", wallpaperModel.getProfileImageLarge());

        return hashMap;
    }

    public void getImage() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        someActivityResultLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        imageUri = result.getData().getData();

                        confirmacionDerechosImagen();
                        if(permisos)
                            uploadToFirebase();
                    }
                }
            });

    public void confirmacionDerechosImagen() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Derechos de la imagen");
        builder.setMessage("¿Posee todos los derechos de esta imagen?");

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which){
                    case DialogInterface.BUTTON_POSITIVE:
                        permisos = true;
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        builder.setPositiveButton("Si", dialogClickListener);
        builder.setNegativeButton("Cancelar", dialogClickListener);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void uploadToFirebase() {
        StorageReference fileRef = UserUtils.reference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        WallpaperModel w2 = new WallpaperModel(System.currentTimeMillis() + "",
                                UserUtils.getNombre(), uri.toString(), uri.toString(),
                                true, UserUtils.getBio(), UserUtils.getPhoto(), UserUtils.getPhoto());
                        UserUtils.root.child("Users").child(UserUtils.getCurrentUser().getUid()).child("photos")
                                .child(System.currentTimeMillis() + "").setValue(w2);

                        wallpaperModelArrayList.add(w2);
                        binding.total.setText(wallpaperModelArrayList.size() + " wallpapers");
                        adapter1.notifyDataSetChanged();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error al subir la imagen.", Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    public String getFileExtension(Uri imageUri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }

    public void twitterAndInstagram() {
        Glide.with(this).load(ic_twitter).into(binding.twitter);
        Glide.with(this).load(ic_instagram).into(binding.instagram);

        //Instagram
        binding.instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(wallpaperModel.getInstagram_username() != null && !wallpaperModel.getInstagram_username().equals("null"))
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/"
                        + wallpaperModel.getInstagram_username())));
                else
                    Toast.makeText(getApplicationContext(),
                            "No tiene perfil de Instagram este usuario.", Toast.LENGTH_SHORT).show();
            }
        });

        //Twitter
        binding.twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(wallpaperModel.getTwitter_username() != null && !wallpaperModel.getTwitter_username().equals("null"))
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/"
                        + wallpaperModel.getTwitter_username())));
                else
                    Toast.makeText(getApplicationContext(),
                            "No tiene perfil de Twitter este usuario.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setHeightSlidePanel(SlidingUpPanelLayout slidePanel){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;

        if(!wallpaperModel.isUsuarioActual()) {
            slidePanel.setPanelHeight(height / 2 + 170);
        }
        else{
            slidePanel.setPanelHeight(height / 2 + 300);
        }
    }

    public void setBasicInfo() {
        Intent intent = getIntent();
        wallpaperModel = (WallpaperModel) intent.getSerializableExtra("wallpaper");

        //----------Info básica (Nombre - bio - total - Imagen de fondo - Imagen Perfil)----------
        if(wallpaperModel.getCreator() != null) binding.nombre.setText(wallpaperModel.getCreator());
        if(wallpaperModel.getBio() != null && !wallpaperModel.getBio().equals("null")) {
            if(wallpaperModel.getBio().length() > 55)
                binding.bio.setText(wallpaperModel.getBio().substring(0, 55) + "...");
            else
                binding.bio.setText(wallpaperModel.getBio());
        }
        else
            binding.bio.setVisibility(View.GONE);

        if(wallpaperModel.getTotal_photos() != null) binding.total.setText(wallpaperModel.getTotal_photos() + " wallpapers");

        if(!wallpaperModel.isUsuarioActual()) {
            Glide.with(this).asDrawable().load(wallpaperModel.getOriginalUrl()).into(binding.imagenFondoPerfil);
            Glide.with(this).asDrawable().load(wallpaperModel.getProfileImageLarge()).into(binding.imagenPerfil);
        }
        else {
            Glide.with(this).load(UserUtils.getPhoto()).centerCrop().into(binding.imagenPerfil);
            Glide.with(this).load(UserUtils.getPhoto()).into(binding.imagenFondoPerfil);
        }
    }
}