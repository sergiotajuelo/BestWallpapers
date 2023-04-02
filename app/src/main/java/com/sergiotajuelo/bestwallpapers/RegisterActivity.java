package com.sergiotajuelo.bestwallpapers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sergiotajuelo.bestwallpapers.databinding.ActivityRegisterBinding;
import com.sergiotajuelo.bestwallpapers.models.WallpaperModel;
import com.sergiotajuelo.bestwallpapers.utils.AppUtils;
import com.sergiotajuelo.bestwallpapers.utils.UserUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private ArrayList<WallpaperModel> wallpaperModel = new ArrayList<>();
    FirebaseAuth auth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AppUtils.noBorders(this);

        auth = FirebaseAuth.getInstance();

        binding.loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });

        binding.buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validarCampos(binding.emailRegister.getText().toString(), binding.passwordRegister.getText().toString())
                        && binding.nombreRegister.getText().toString().length() > 0){

                    ProgressDialog pd = new ProgressDialog(RegisterActivity.this);
                    pd.setMessage("Espere por favor...");
                    pd.show();

                    auth.createUserWithEmailAndPassword(binding.emailRegister.getText().toString(),
                            binding.passwordRegister.getText().toString()).addOnCompleteListener(
                            RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        FirebaseUser firebaseUser = auth.getCurrentUser();
                                        String userID = firebaseUser.getUid();

                                        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
                                        UserUtils.setCurrentUser(FirebaseAuth.getInstance().getCurrentUser());

                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("id", userID);
                                        hashMap.put("fullname", binding.nombreRegister.getText().toString());
                                        hashMap.put("profilePhoto", UserUtils.basicPhoto);
                                        hashMap.put("bio", UserUtils.basicBio);
                                        hashMap.put("tipoUsuario", false);
                                        hashMap.put("photos", wallpaperModel);

                                        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    pd.dismiss();
                                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }
                                        });

                                    }
                                    else{
                                        pd.dismiss();
                                        Toast.makeText(getApplicationContext(), "No puedes registrarte con este email o contraseña.",
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    public static boolean validarCampos(String email, String contraseña) {
        if(email == null || email.length() == 0 || !isValidEmail(email))
            return false;
        else if(contraseña.length() == 0 &&!isValidPassword(contraseña))
            return false;
        else
            return true;

    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public static boolean isValidPassword(final String password) {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }
}