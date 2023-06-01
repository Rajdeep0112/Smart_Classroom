package com.example.smartclassroom.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.smartclassroom.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout username, email, password;
    private LinearLayout loginText;
    private Button continueBtn;
    private ProgressBar progressBar;
    private String UserName, Email, Password;
    private FirebaseAuth auth;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this,LoginActivity.class));
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initializations();


        continueBtn.setOnClickListener(view -> {
            takeData();
            signUp(Email,Password,UserName);
        });

        loginText.setOnClickListener(view -> {
            startActivity(new Intent(this,LoginActivity.class));
            finish();
        });
    }

    private void initializations() {
        username = findViewById(R.id.userName);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        continueBtn = findViewById(R.id.continueBtn);
        progressBar = findViewById(R.id.progressBar);
        loginText = findViewById(R.id.loginText);
//        database=FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();
//        users=database.getReference().child("Users");
    }

    private void takeData(){
        UserName= Objects.requireNonNull(username.getEditText()).getText().toString().trim();
        Email=Objects.requireNonNull(email.getEditText()).getText().toString().trim();
        Password=Objects.requireNonNull(password.getEditText()).getText().toString().trim();
    }

    private void signUp(String Email,String Password,String UserName){
//        if(auth.getCurrentUser()==null) {
        auth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    auth.getCurrentUser().sendEmailVerification().addOnSuccessListener(runnable -> {
                        Toast.makeText(RegisterActivity.this, "Email verification link sent to " + Email, Toast.LENGTH_LONG).show();
                        continueBtn.setVisibility(View.VISIBLE);
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("userName", UserName);
                        hashMap.put("email", Email);
                        FirebaseFirestore.getInstance()
                                .collection("Users")
                                .document(auth.getUid())
                                .set(hashMap)
                                .addOnSuccessListener(runnable1 -> {
                                    auth.signOut();
                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                    finish();
                                }).addOnFailureListener(e -> {
                                    Toast.makeText(RegisterActivity.this, "Data not added. " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    auth.signOut();
                                });
                        progressBar.setVisibility(View.GONE);
                    }).addOnFailureListener(e -> {
                        Toast.makeText(RegisterActivity.this, "Email verification link not sent. " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
                } else {
                    Toast.makeText(RegisterActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    continueBtn.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }

        });
//        }
    }
}