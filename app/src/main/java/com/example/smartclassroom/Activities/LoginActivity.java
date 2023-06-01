package com.example.smartclassroom.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout email, password;
    private ProgressBar progressBar;
    private LinearLayout registerText;
    private String Email, Password;
    private Button continueBtn;
    private FirebaseAuth auth;

    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializations();


        continueBtn.setOnClickListener(view -> {
            takeData();
            signIn(Email,Password);
        });

        registerText.setOnClickListener(view -> {
            startActivity(new Intent(this,RegisterActivity.class));
            finish();
        });
    }

    private void initializations() {
        email= findViewById(R.id.emailLogin);
        password= findViewById(R.id.passwordLogin);
        continueBtn= findViewById(R.id.continueBtnLogin);
        progressBar= findViewById(R.id.progressBarLogin);
        registerText=findViewById(R.id.registerText);
        auth=FirebaseAuth.getInstance();
    }

    private void takeData(){
        Email= Objects.requireNonNull(email.getEditText()).getText().toString().trim();
        Password=Objects.requireNonNull(password.getEditText()).getText().toString().trim();
    }

    private void signIn(String Email,String Password){
        auth.signInWithEmailAndPassword(Email,Password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    if (auth.getCurrentUser().isEmailVerified()) {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification();
                        auth.signOut();
                        Toast.makeText(getApplicationContext(), "Email not verified", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(LoginActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}