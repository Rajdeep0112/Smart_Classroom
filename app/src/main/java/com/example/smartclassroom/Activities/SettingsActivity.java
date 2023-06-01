package com.example.smartclassroom.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import com.example.smartclassroom.R;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar settingsToolbar;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.home_toolbar_menu,menu);
        menu.removeItem(R.id.profile);
        menu.removeItem(R.id.edit);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initialisations();
        setSupportActionBar(settingsToolbar);
        configureToolbar();
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