package com.sergiotajuelo.bestwallpapers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.sergiotajuelo.bestwallpapers.databinding.ActivityFollowingUserBinding;
import com.sergiotajuelo.bestwallpapers.databinding.ActivityRecuperarContrasenyaBinding;
import com.sergiotajuelo.bestwallpapers.utils.AppUtils;

public class RecuperarContrasenya extends AppCompatActivity {

    private ActivityRecuperarContrasenyaBinding binding;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecuperarContrasenyaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        AppUtils.noBorders(this);

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(binding.emailLogin.getText().toString())){
                    Toast.makeText(RecuperarContrasenya.this, "El email no puede estar vacío.",
                            Toast.LENGTH_SHORT).show();
                }
                else{
                    auth.sendPasswordResetEmail(binding.emailLogin.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(RecuperarContrasenya.this, "Por favor, compruebe su bandeja de entrada.",
                                        Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RecuperarContrasenya.this, LoginActivity.class));
                            }
                        }
                    });
                }
            }
        });
    }
}