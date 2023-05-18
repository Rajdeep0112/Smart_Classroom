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
import android.widget.Toast;

import com.example.smartclassroom.Models.NewClassroomModel;
import com.example.smartclassroom.R;
import com.google.firebase.auth.FirebaseAuth;

import java.io.Serializable;
import java.util.ArrayList;

public class JoinClassroomActivity extends AppCompatActivity {


    private Toolbar joinClassToolbar;
    private EditText joinCode;
    private ArrayList<NewClassroomModel> allClassroomList;
    FirebaseAuth auth;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.join_clasroom_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.join:
                joinClass();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_classroom);

        initialisations();
        setSupportActionBar(joinClassToolbar);
        configureToolbar();
        getData();
    }

    private void initialisations(){
        joinClassToolbar=findViewById(R.id.join_classroom_toolbar);
        joinCode=findViewById(R.id.join_code);
        auth=FirebaseAuth.getInstance();
    }

    private void configureToolbar(){
        joinClassToolbar.setTitle(R.string.JoinClass);
        joinClassToolbar.setNavigationIcon(R.drawable.close);
        joinClassToolbar.setNavigationOnClickListener(view -> onBackPressed());
    }

    private void getData(){
        Intent intent=getIntent();
        allClassroomList = (ArrayList<NewClassroomModel>) intent.getSerializableExtra("allClassroomList");
    }

    private void joinClass(){
        String ClassCode=joinCode.getText().toString().trim();
        int x=0;

        if(allClassroomList.size()>0) {
            for (NewClassroomModel ncm : allClassroomList) {
                String id = ncm.getClassId();
                String code = id.substring(0, 7) + id.substring(17);
                if (ClassCode.equals(code)) {
                    if (!ncm.getUserId().equals(auth.getCurrentUser().getUid())) {
                        x = 1;
                        ncm.setOccupation("student");
                        ArrayList<NewClassroomModel> cL = new ArrayList<>();
                        cL.add(ncm);
                        Intent i = new Intent();
                        i.putExtra("CL", (Serializable) cL);
                        i.putExtra("ClassCode", ClassCode);
                        setResult(RESULT_OK, i);
                        finish();
                        break;
                    } else Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                } else x = 0;
            }
        }
        if(x==0) Toast.makeText(this, "Wrong class code", Toast.LENGTH_SHORT).show();
    }
}