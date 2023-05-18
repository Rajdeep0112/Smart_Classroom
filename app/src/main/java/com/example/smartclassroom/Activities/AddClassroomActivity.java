package com.example.smartclassroom.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.smartclassroom.R;

public class AddClassroomActivity extends AppCompatActivity {

    private Toolbar addClassToolbar;
    private EditText className, section;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_classroom);
        initializations();
        setSupportActionBar(addClassToolbar);
        configureToolbar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.add_classroom_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.create:
                createClass();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initializations(){
        addClassToolbar=findViewById(R.id.add_classroom_toolbar);
        className=findViewById(R.id.etClassName);
        section=findViewById(R.id.etSection);
    }

    private void configureToolbar(){
        addClassToolbar.setTitle(R.string.CreateClass);
        addClassToolbar.setNavigationIcon(R.drawable.close);
        addClassToolbar.setNavigationOnClickListener(view -> onBackPressed());
    }

    public void createClass(){
        String ClassName=className.getText().toString().trim();
        String Section=section.getText().toString().trim();

        Intent i=new Intent();
        i.putExtra("ClassName",ClassName);
        i.putExtra("Section",Section);
        setResult(RESULT_OK,i);
        finish();
    }
}