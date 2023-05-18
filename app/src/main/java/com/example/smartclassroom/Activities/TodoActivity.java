package com.example.smartclassroom.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import com.example.smartclassroom.Adapters.TodoVpAdapter;
import com.example.smartclassroom.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class TodoActivity extends AppCompatActivity {

    private Toolbar todoToolbar;
    private TabLayout todoTl;
    private ViewPager2 todoVp;

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
        setContentView(R.layout.activity_todo);
        initialisations();
        setSupportActionBar(todoToolbar);
        configureToolbar();
        setTlVp();
    }

    private void initialisations(){
        todoToolbar=findViewById(R.id.todo_toolbar);
        todoTl=findViewById(R.id.todo_tl);
        todoVp=findViewById(R.id.todo_vp);
    }

    private void configureToolbar(){
        todoToolbar.setTitle(R.string.Todo);
        todoToolbar.setNavigationIcon(R.drawable.close);
        todoToolbar.setNavigationOnClickListener(view -> onBackPressed());
    }

    private void setTlVp(){
        TodoVpAdapter todoVpAdapter=new TodoVpAdapter(getSupportFragmentManager(),getLifecycle());
        todoVp.setAdapter(todoVpAdapter);

        TabLayoutMediator tabLayoutMediator=new TabLayoutMediator(todoTl,todoVp,(tab, position) -> {
            tab.setText(todoVpAdapter.getHeader(position));
        });
        tabLayoutMediator.attach();
    }
}