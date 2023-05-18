package com.example.smartclassroom.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import com.example.smartclassroom.R;

public class NotificationActivity extends AppCompatActivity {

    private Toolbar NotificationToolbar;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.home_toolbar_menu,menu);
        menu.removeItem(R.id.profile);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        initialisations();
        setSupportActionBar(NotificationToolbar);
        configureToolbar();
    }

    private void initialisations(){
        NotificationToolbar=findViewById(R.id.notification_toolbar);
    }

    private void configureToolbar(){
        NotificationToolbar.setTitle(R.string.Notifications);
        NotificationToolbar.setNavigationIcon(R.drawable.close);
        NotificationToolbar.setNavigationOnClickListener(view -> onBackPressed());
    }
}