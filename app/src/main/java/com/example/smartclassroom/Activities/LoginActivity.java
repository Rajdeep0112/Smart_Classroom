package com.example.smartclassroom.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.regex.Pattern;

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

        (findViewById(R.id.forgotPasswordLogin)).setOnClickListener(view -> {
            Email= Objects.requireNonNull(email.getEditText()).getText().toString().trim();
            EditText editText= new EditText(view.getContext());
            AlertDialog.Builder passwordResetDialog= new AlertDialog.Builder(view.getContext());
            passwordResetDialog.setTitle("Password Reset");
            passwordResetDialog.setMessage("Enter your Email-ID to receive password reset link.");
            passwordResetDialog.setView(editText);
            editText.setText(Email);

            passwordResetDialog.setPositiveButton("Proceed", (dialogInterface, i) -> {
                String mail= editText.getText().toString().trim();
                if(validateEmail(mail))
                    auth.sendPasswordResetEmail(mail).addOnSuccessListener(unused -> Toast.makeText(LoginActivity.this, "Password reset link sent.", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(LoginActivity.this, "Password reset link not sent. "+ e.getMessage(), Toast.LENGTH_SHORT).show());
                else Toast.makeText(this, "Enter Valid Email", Toast.LENGTH_SHORT).show();
            }).setNegativeButton("Cancel", (dialogInterface, i) -> {

            });
            passwordResetDialog.create().show();
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

    public boolean validateEmail(String s){
        if(s==null || s.isEmpty()){
            return false;
        }
        String emailRegex= "^[a-zA-Z0-9_+&-]+(?:\\."+"[a-zA-Z0-9_+&-]+)*@"+"(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern= Pattern.compile(emailRegex);
        return pattern.matcher(s).matches();
    }
}