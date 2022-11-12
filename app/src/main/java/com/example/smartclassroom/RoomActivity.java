package com.example.smartclassroom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartclassroom.RoomFragments.ClassworkFragment;
import com.example.smartclassroom.RoomFragments.PeopleFragment;
import com.example.smartclassroom.RoomFragments.StreamFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class RoomActivity extends AppCompatActivity {

    private NavigationView roomNavigationView;
    private DrawerLayout roomDrawerLayout;
    private Toolbar roomToolbar;
    MainActivity mainActivity=new MainActivity();
    String ClassName,Section;
    private TextView className,section;
    private BottomNavigationView roomBottomNavigationView;
    private ArrayList<NewClassroomModel> classroomL=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        initializations();
        configureToolbar();
        setSupportActionBar(roomToolbar);
        roomToolbar.setNavigationOnClickListener(view -> roomDrawerLayout.openDrawer(GravityCompat.START));
        getData();
        mainActivity.navigationViewMenu(roomNavigationView,classroomL);
        mainActivity.navigationViewController(roomNavigationView,roomDrawerLayout,this);

        replaceFragments(new StreamFragment());
        bottomNavigationViewController();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.home_toolbar_menu,menu);
        menu.removeItem(R.id.profile);
        return true;
    }

    private void initializations(){
        roomNavigationView=findViewById(R.id.room_navigation);
        roomDrawerLayout=findViewById(R.id.room_drawer_layout);
        roomToolbar=findViewById(R.id.room_toolbar);
        roomBottomNavigationView=findViewById(R.id.room_bottom_navigation);
    }

    private void configureToolbar(){
        roomToolbar.setTitle("");
        roomToolbar.setNavigationIcon(R.drawable.main_menu);
    }

    private void bottomNavigationViewController(){
        roomBottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.stream:
                    roomToolbar.setTitle("");
                    replaceFragments(new StreamFragment());
                    break;
                case R.id.classwork:
                    replaceFragments(new ClassworkFragment());
                    roomToolbar.setTitle(ClassName);
                    break;
                case R.id.people:
                    replaceFragments(new PeopleFragment());
                    roomToolbar.setTitle(ClassName);
                    break;
            }
            return true;
        });
    }

    private void replaceFragments(Fragment fragment){
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        sendClassDetails(ClassName,Section,fragment);
        fragmentTransaction.replace(R.id.room_frame,fragment).commit();
    }

    private void getData(){
        Intent intent=getIntent();
        ClassName=intent.getStringExtra("className");
        Section=intent.getStringExtra("section");
//        classroomL= (ArrayList<NewClassroomModel>) intent.getSerializableExtra("list");
    }

    public void setTEData(ArrayList<NewClassroomModel> classroomL){
        this.classroomL=classroomL;
    }

    private void sendClassDetails(String ClassName,String Section,Fragment fragment){
        Bundle bundle=new Bundle();
        bundle.putString("ClassName",ClassName);
        bundle.putString("Section",Section);
        fragment.setArguments(bundle);
    }
}