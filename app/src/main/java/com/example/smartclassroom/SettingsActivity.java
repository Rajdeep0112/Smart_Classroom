package com.example.smartclassroom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar settingsToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initialisations();
        setSupportActionBar(settingsToolbar);
        configureToolbar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.home_toolbar_menu,menu);
        menu.removeItem(R.id.profile);
        return true;
    }

    private void initialisations(){
        settingsToolbar=findViewById(R.id.settings_toolbar);
    }

    private void configureToolbar(){
        settingsToolbar.setTitle(R.string.Settings);
        settingsToolbar.setNavigationIcon(R.drawable.back);
        settingsToolbar.setNavigationOnClickListener(view -> onBackPressed());
    }
}