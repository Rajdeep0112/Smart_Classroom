package com.example.smartclassroom.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartclassroom.Fragments.Room.ClassworkFragment;
import com.example.smartclassroom.Fragments.Room.PeopleFragment;
import com.example.smartclassroom.Fragments.Room.StreamFragment;
import com.example.smartclassroom.Models.NewClassroomModel;
import com.example.smartclassroom.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;

public class RoomActivity extends AppCompatActivity{

    private NavigationView roomNavigationView;
    private DrawerLayout roomDrawerLayout;
    private Toolbar roomToolbar;
    MainActivity mainActivity=new MainActivity();
    private String ClassName,UserId,Email,UserName;
    private TextView className,section;
    private BottomNavigationView roomBottomNavigationView;
    private ArrayList<NewClassroomModel> classroomL=new ArrayList<>();
    private ArrayList<String> sc=new ArrayList<>();
    private NewClassroomModel classroomModel;
    private FirebaseAuth auth;
    private DocumentReference user;

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
        setContentView(R.layout.activity_room);
        initializations();
        configureToolbar();
        setSupportActionBar(roomToolbar);
        roomToolbar.setNavigationOnClickListener(view -> roomDrawerLayout.openDrawer(GravityCompat.START));
        getData();
        extractUser();

    }

    private void initializations(){
        roomNavigationView=findViewById(R.id.room_navigation);
        roomDrawerLayout=findViewById(R.id.room_drawer_layout);
        roomToolbar=findViewById(R.id.room_toolbar);
        roomBottomNavigationView=findViewById(R.id.room_bottom_navigation);
        auth=FirebaseAuth.getInstance();
        user= FirebaseFirestore.getInstance().collection("Users").document(auth.getCurrentUser().getUid());
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
        sendClassDetails(classroomModel,fragment);
        fragmentTransaction.replace(R.id.room_frame,fragment).commit();
    }

    private void getData(){
        Intent intent=getIntent();
        Bundle data=intent.getBundleExtra("data");
        classroomL= (ArrayList<NewClassroomModel>) data.getSerializable("classroomList");
        classroomModel= (NewClassroomModel) data.getSerializable("ClassroomModel");
        ClassName = classroomModel.getClassroomName();
        UserId=data.getString("UserId");
        Email=data.getString("Email");
        UserName=data.getString("UserName");
    }

    private void sendClassDetails(NewClassroomModel classroomModel, Fragment fragment){
        Bundle bundle=new Bundle();
        bundle.putSerializable("ClassroomModel",classroomModel);
        bundle.putString("UserName",UserName);
        bundle.putString("Email",Email);
        bundle.putString("UserId",UserId);
        fragment.setArguments(bundle);
    }

    private void extractUser(){
        user.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                DocumentSnapshot snapshot = task.getResult();
                UserId = auth.getCurrentUser().getUid();
                UserName = snapshot.getString("userName");
                Email = snapshot.getString("email");
                mainActivity.extractUser(roomNavigationView,user,this);
                mainActivity.navigationViewMenu(roomNavigationView,classroomL);
                mainActivity.navigationViewController(roomNavigationView,roomDrawerLayout,classroomL,this,auth);

                replaceFragments(new StreamFragment());
                bottomNavigationViewController();
            }
        });
    }
}