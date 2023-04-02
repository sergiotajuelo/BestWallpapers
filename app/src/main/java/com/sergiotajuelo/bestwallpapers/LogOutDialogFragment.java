package com.sergiotajuelo.bestwallpapers;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sergiotajuelo.bestwallpapers.databinding.FragmentLogOutDialogBinding;
import com.sergiotajuelo.bestwallpapers.utils.UserUtils;

public class LogOutDialogFragment extends DialogFragment {

    private FragmentLogOutDialogBinding binding;
    FirebaseAuth auth;
    private Uri imageUri;
    private StorageReference reference = FirebaseStorage.getInstance().getReference();
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLogOutDialogBinding.inflate(getLayoutInflater(), container, false);
        setInfo();

        Toast.makeText(getContext(), "Ahora se puede editar la información.", Toast.LENGTH_SHORT).show();

        auth = FirebaseAuth.getInstance();

        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.name.setEnabled(false);
                binding.bio.setEnabled(false);

                if(!binding.name.getText().toString().equals(UserUtils.getNombre())){
                    root.child("Users").child(UserUtils.getCurrentUser().getUid()).child("fullname")
                            .setValue(binding.name.getText().toString());
                    UserUtils.setNombre(binding.name.getText().toString());

                    TextView txtView = (TextView) ((Activity) getContext()).findViewById(R.id.usuarioActual);
                    txtView.setText(binding.name.getText().toString());
                }


                if(!binding.bio.getText().toString().equals(UserUtils.getBio())) {
                    root.child("Users").child(UserUtils.getCurrentUser().getUid()).child("bio")
                            .setValue(binding.bio.getText().toString());
                    UserUtils.setBio(binding.bio.getText().toString());

                    TextView txtView = (TextView) ((Activity) getContext()).findViewById(R.id.bio);
                    txtView.setText(binding.bio.getText().toString());
                }
            }
        });

        binding.mejorarCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.mejorarCuenta.setVisibility(View.GONE);
                binding.creador.setVisibility(View.VISIBLE);

                UserUtils.setUsuarioAdmin(true);
                root.child("Users").child(UserUtils.getCurrentUser().getUid()).child("tipoUsuario")
                        .setValue(true);

                Toast.makeText(getContext(), "Ahora, puedes acceder a tu perfil pulsando en tu imagen.",
                        Toast.LENGTH_LONG).show();
            }
        });

        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImage();
            }
        });

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return binding.getRoot();
    }

    public void setInfo() {
        if(!UserUtils.getBio().equals("null")) binding.bio.setText(UserUtils.getBio());
        if(!UserUtils.getPhoto().equals("null")) {
            Glide.with(getContext()).load(UserUtils.getPhoto()).centerCrop()
                    .into(binding.profileImage);
        }

        if(UserUtils.isUsuarioAdmin()) {
            binding.mejorarCuenta.setVisibility(View.GONE);
            binding.creador.setVisibility(View.VISIBLE);
        }
        else {
            binding.creador.setVisibility(View.GONE);
            binding.mejorarCuenta.setVisibility(View.VISIBLE);
        }

        binding.name.setText(UserUtils.getNombre());
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
                        binding.profileImage.setImageURI(imageUri);

                        binding.logout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                uploadToFirebase();

                                ImageView imgView = (ImageView) ((Activity) getContext()).findViewById(R.id.profile_image);
                                imgView.setImageURI(imageUri);
                            }
                        });
                    }
                }
            });

    public void uploadToFirebase() {
        StorageReference fileRef = reference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        root.child("Users").child(UserUtils.getCurrentUser().getUid()).child("profilePhoto")
                                .setValue(uri.toString());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Error al subir la imagen.", Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    public String getFileExtension(Uri imageUri) {
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }
}