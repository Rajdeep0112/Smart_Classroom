package com.example.smartclassroom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

public class JoinClassroomActivity extends AppCompatActivity {

    private Toolbar joinClassToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_classroom);
        initialisations();
        setSupportActionBar(joinClassToolbar);
        configureToolbar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.join_clasroom_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void initialisations(){
        joinClassToolbar=findViewById(R.id.join_classroom_toolbar);
    }

    private void configureToolbar(){
        joinClassToolbar.setTitle(R.string.JoinClass);
        joinClassToolbar.setNavigationIcon(R.drawable.close);
        joinClassToolbar.setNavigationOnClickListener(view -> onBackPressed());
    }
}