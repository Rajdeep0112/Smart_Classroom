package com.example.smartclassroom;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements addClassAlert.dialogListener{

    private NavigationView mainNavigationView;
    private DrawerLayout mainDrawerLayout;
    private Toolbar mainToolbar;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private ArrayList<NewClassroomModel> classroomList=new ArrayList<>();
    private ClassroomAdapter adapter;
    private ImageButton moreOption;
    public int checkId=1;

    ActivityResultLauncher<Intent> activityResultLauncherForCreateClass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerActivityForCreateClass();
        initialisations();
        setSupportActionBar(mainToolbar);
        configureToolbar();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        NewClassroomModel newClassroomModel1=new NewClassroomModel("Computer Networks","Cse 2020 batch","123sjnana","Rajdeep","teacher",50);
        NewClassroomModel newClassroomModel2=new NewClassroomModel("DAA","Cse 2020 batch","123sjnana","Rajdeep","student",51);
        NewClassroomModel newClassroomModel3=new NewClassroomModel("IPR","Cse 2020 batch","123sjnana","Rajdeep","teacher",75);
        NewClassroomModel newClassroomModel4=new NewClassroomModel("Soft Computing","Cse 2020 batch","123sjnana","Rajdeep","teacher",15);
        NewClassroomModel newClassroomModel5=new NewClassroomModel("Computer Graphics","Cse 2020 batch","123sjnana","Rajdeep","student",25);
        NewClassroomModel newClassroomModel6=new NewClassroomModel("DSD","Cse 2020 batch","123sjnana","Rajdeep","teacher",52);
        NewClassroomModel newClassroomModel7=new NewClassroomModel("DSA","Cse 2020 batch","123sjnana","Rajdeep","student",55);
        NewClassroomModel newClassroomModel8=new NewClassroomModel("DBMS","Cse 2020 batch","123sjnana","Rajdeep","teacher",57);

        classroomList.add(newClassroomModel1);
        classroomList.add(newClassroomModel2);
        classroomList.add(newClassroomModel3);
        classroomList.add(newClassroomModel4);
        classroomList.add(newClassroomModel5);
        classroomList.add(newClassroomModel6);
        classroomList.add(newClassroomModel7);
        classroomList.add(newClassroomModel8);

        adapter=new ClassroomAdapter();
        recyclerView.setAdapter(adapter);
        adapter.setClasses(classroomList,this);
        adapter.setOnItemClickListener(new ClassroomAdapter.onItemClickListener() {
            @Override
            public void onItemClick(NewClassroomModel classroomModel) {
                Intent intent=new Intent(getApplicationContext(),RoomActivity.class);
                intent.putExtra("className",classroomModel.getClassroomName());
                intent.putExtra("section",classroomModel.getSection());
//                intent.putExtra("list",classroomList);
                startActivity(intent);
            }
        });


        navigationViewMenu(mainNavigationView,classroomList);
        navigationViewController(mainNavigationView, mainDrawerLayout, this);

        fab.setOnClickListener(view -> {
            addClass();
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.home_toolbar_menu,menu);
        return true;
    }

    private void initialisations(){
        mainNavigationView=findViewById(R.id.main_navigation);
        mainDrawerLayout=findViewById(R.id.main_drawer_layout);
        mainToolbar=findViewById(R.id.main_toolbar);
        fab=findViewById(R.id.main_fab);
        recyclerView=findViewById(R.id.main_recycler_view);
        moreOption=findViewById(R.id.more_option);
    }

    private void configureToolbar(){
        mainToolbar.setTitle(R.string.app_name);
        mainToolbar.setNavigationIcon(R.drawable.main_menu);
        mainToolbar.setNavigationOnClickListener(view -> mainDrawerLayout.openDrawer(GravityCompat.START));
    }

    public void navigationViewMenu(NavigationView navigationView,ArrayList<NewClassroomModel> classroomList){
        Menu menu=navigationView.getMenu();
        menu.clear();
        mainMenu(menu,0);
        teachingMenu(menu,10,classroomList);
        enrolledMenu(menu,20,classroomList);
        othersMenu(menu,30);
    }

    public void navigationViewController(NavigationView navigationView, DrawerLayout drawerLayout, Context context){
        navigationView.setNavigationItemSelectedListener(item -> {
            if(item.getItemId()==1 && item.isChecked()==false){
                context.startActivity(new Intent(context,MainActivity.class));
                item.setChecked(true);
//                checkId=item.getItemId();
            }

            if(item.getItemId()==2 && item.isChecked()==false){
                context.startActivity(new Intent(context,NotificationActivity.class));
//                item.setChecked(true);
//                checkId=item.getItemId();
            }

            if(item.getItemId()==220 && item.isChecked()==false){
                context.startActivity(new Intent(context,TodoActivity.class));
//                item.setChecked(true);
//                checkId=item.getItemId();
            }

            if(item.getItemId()==41 && item.isChecked()==false){
                context.startActivity(new Intent(context,SettingsActivity.class));
//                item.setChecked(true);
//                checkId=item.getItemId();
            }

            if(item.getItemId()==42){
                Toast.makeText(context, "Logout", Toast.LENGTH_SHORT).show();
            }

            if (drawerLayout.isDrawerOpen(GravityCompat.START))
                drawerLayout.closeDrawer(GravityCompat.START);
            return false;
        });
    }

    public void mainMenu(Menu menu,int id){
        MenuItem classes=menu.add(id,id+1,Menu.NONE,R.string.Classes);
        classes.setIcon(R.drawable.classes);
        classes.setChecked(true);
//        if(uncheckId==1) classes.setChecked(false);

        MenuItem notifications=menu.add(id,id+2,Menu.NONE,R.string.Notifications);
        notifications.setIcon(R.drawable.notification);
//        if(uncheckId==2) notifications.setChecked(false);
    }

    public void teachingMenu(Menu menu,int id,ArrayList<NewClassroomModel> classroomList){
        SubMenu teaching=menu.addSubMenu(id,SubMenu.NONE,SubMenu.NONE,R.string.Teaching);
        for(NewClassroomModel ncm:classroomList){
            if(ncm.getOccupation().toLowerCase().equals("teacher")) {
                MenuItem menuItem = teaching.add(id, ncm.getNoOfStudents(), Menu.NONE, ncm.getClassroomName());
                teachingItemDesign(menuItem, ncm);
//                if(menuItem.getItemId()==uncheckId) menuItem.setChecked(false);
            }
        }
    }

    public void enrolledMenu(Menu menu,int id,ArrayList<NewClassroomModel>classroomList){
        SubMenu enrolled=menu.addSubMenu(id,SubMenu.NONE,SubMenu.NONE,R.string.Enrolled);
        MenuItem todo=enrolled.add(id, id+200,Menu.NONE,R.string.Todo);
        todo.setIcon(R.drawable.todo);
//        if(uncheckId==220) todo.setChecked(false);

        for(NewClassroomModel ncm:classroomList){
            if(ncm.getOccupation().toLowerCase().equals("student")) {
                MenuItem menuItem = enrolled.add(id, ncm.getNoOfStudents(), Menu.NONE, ncm.getClassroomName());
                enrolledItemDesign(menuItem, ncm);
//                if(menuItem.getItemId()==uncheckId) menuItem.setChecked(false);
            }
        }
    }

    public void othersMenu(Menu menu,int id){
        MenuItem setting=menu.add(id,id+11,Menu.NONE,R.string.Settings);
        setting.setIcon(R.drawable.settings);
//        if(uncheckId==41) setting.setChecked(false);

        MenuItem logout=menu.add(id,id+12,Menu.NONE,R.string.Logout);
        logout.setIcon(R.drawable.logout);
    }

    private void teachingItemDesign(MenuItem menuItem,NewClassroomModel ncm){
        menuItem.setIcon(R.drawable.copyright);
        menuItem.setCheckable(true);
    }

    private void enrolledItemDesign(MenuItem menuItem,NewClassroomModel ncm){
        menuItem.setIcon(R.drawable.copyright);
        menuItem.setCheckable(true);
    }

    private void addClass(){
        addClassAlert alert=new addClassAlert();
        alert.show(getSupportFragmentManager(),"add class alert");
    }

    public void registerActivityForCreateClass(){
        activityResultLauncherForCreateClass=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        int resultCode=result.getResultCode();
                        Intent data=result.getData();

                        if(resultCode==RESULT_OK && data!=null){
                            String ClassName=data.getStringExtra("ClassName");
                            String Section=data.getStringExtra("Section");

                            NewClassroomModel newClassroomModel=new NewClassroomModel(ClassName,Section,"dsfsaa","Rajdeep","Teacher",20);
                            classroomList.add(newClassroomModel);
                            navigationViewMenu(mainNavigationView,classroomList);
                        }
                    }
                });
    }

    @Override
    public void setText(String string) {
        if(string.equals("CreateClass")){
            Intent intent=new Intent(getApplicationContext(),AddClassroomActivity.class);
            activityResultLauncherForCreateClass.launch(intent);
        }else{
            startActivity(new Intent(getApplicationContext(),JoinClassroomActivity.class));
        }
    }
}