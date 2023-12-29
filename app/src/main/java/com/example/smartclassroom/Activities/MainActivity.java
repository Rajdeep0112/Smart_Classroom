package com.example.smartclassroom.Activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.net.ConnectivityManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.smartclassroom.Adapters.ClassroomAdapter;
import com.example.smartclassroom.Classes.CommonFuncClass;
import com.example.smartclassroom.Classes.addClassAlert;
import com.example.smartclassroom.Models.NewClassroomModel;
import com.example.smartclassroom.Models.NewPeopleModel;
import com.example.smartclassroom.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.auth.User;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class MainActivity extends AppCompatActivity implements addClassAlert.dialogListener{

    private NavigationView mainNavigationView;
    private DrawerLayout mainDrawerLayout;
    private Toolbar mainToolbar;
    private RecyclerView recyclerView;
    private ProgressBar progressBar,progressBar1;
    private FloatingActionButton fab;
    private ArrayList<NewClassroomModel> classroomList=new ArrayList<>(),allClassroomList=new ArrayList<>();
    private ArrayList<String> sc=new ArrayList<>();
    private ClassroomAdapter adapter;
    private ImageButton moreOption;
    private ImageView accountProfile;
    public int checkId=1;
    private FirebaseAuth auth;
    private String UserName,Email,UserID,ClassId;
    final String[] url = {""};
    private CollectionReference classrooms;
    private DocumentReference user,room,classwork,stream,people,todo;
    private CollectionReference allClassrooms;
    private CommonFuncClass cfc;

    ActivityResultLauncher<Intent> activityResultLauncherForCreateClass;
    ActivityResultLauncher<Intent> activityResultLauncherForJoinClass;


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()== R.id.refresh) extractData();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cfc = new CommonFuncClass(this);
        registerActivityForCreateClass();
        registerActivityForJoinClass();
        initialisations();
        setSupportActionBar(mainToolbar);
        configureToolbar();

        extractUser(mainNavigationView,user,this);
        navigationViewMenu(mainNavigationView,classroomList);
        navigationViewController(mainNavigationView, mainDrawerLayout, classroomList, MainActivity.this,auth);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter=new ClassroomAdapter();
        recyclerView.setAdapter(adapter);
        adapterWork(adapter);
        extractData();

        fab.setOnClickListener(view -> {
            addClass();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.home_toolbar_menu,menu);
        menu.removeItem(R.id.profile);
        menu.removeItem(R.id.edit);
        return true;
    }

    private void initialisations(){
        mainNavigationView=findViewById(R.id.main_navigation);
        mainDrawerLayout=findViewById(R.id.main_drawer_layout);
        mainToolbar=findViewById(R.id.main_toolbar);
        fab=findViewById(R.id.main_fab);
        recyclerView=findViewById(R.id.main_recycler_view);
//        moreOption=findViewById(R.id.more_option);
        accountProfile = findViewById(R.id.account_profile);
        progressBar = findViewById(R.id.progress_bar);
        progressBar1 = findViewById(R.id.progress_bar1);
        auth=FirebaseAuth.getInstance();
        UserID=auth.getCurrentUser().getUid();
        user= FirebaseFirestore.getInstance().collection("Users").document(UserID);
        classrooms=user.collection("Classrooms");
        allClassrooms = FirebaseFirestore.getInstance().collection("Classrooms");
    }

    private void adapterWork(ClassroomAdapter adapter){
        progressBar1.setVisibility(View.VISIBLE);
        adapter.setClasses(classroomList,progressBar1,MainActivity.this);
        adapter.setOnItemClickListener(new ClassroomAdapter.onItemClickListener() {
            @Override
            public void onItemClick(NewClassroomModel classroomModel) {
                passData(classroomModel,classroomList,getApplicationContext());
            }
        });
    }

    private void extractData(){
        classroomList = new ArrayList<>();
        user.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                DocumentSnapshot snapshot = task.getResult();
                UserName=snapshot.getString("userName");
                Email=snapshot.getString("email");
                extractClassrooms();
            }
        }).addOnFailureListener(e -> {
            cfc.toastShort(e.getMessage());
        });

    }

    private void extractClassrooms(){
        AtomicInteger tot = new AtomicInteger();
        classrooms.get().addOnCompleteListener(task1 -> {
            if(task1.isSuccessful()) {
                List<Long> students = new ArrayList<>();
                tot.set(task1.getResult().size());
                for (QueryDocumentSnapshot snapshot1 : task1.getResult()) {
                    ClassId = snapshot1.getId();
                    updateNoOfStudents(ClassId,tot,students);
                }
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
        });
    }

    private void classData(){
        classrooms.orderBy("timestamp", Query.Direction.DESCENDING).get().addOnCompleteListener(task2 -> {
            if(task2.isSuccessful()){
                classroomList = (ArrayList<NewClassroomModel>) task2.getResult().toObjects(NewClassroomModel.class);
                adapterWork(adapter);
                navigationViewMenu(mainNavigationView, classroomList);
                navigationViewController(mainNavigationView, mainDrawerLayout, classroomList, MainActivity.this,auth);
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
        });
    }

    public static boolean isValidContextForGlide(final Context context) {
        if (context == null) {
            return false;
        }
        if (context instanceof Activity) {
            final Activity activity = (Activity) context;
            if (activity.isDestroyed() || activity.isFinishing()) {
                return false;
            }
        }
        return true;
    }

    public void extractUser(NavigationView navigationView,DocumentReference user,Context context){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference();
        user.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                DocumentSnapshot snapshot = task.getResult();
                UserName = snapshot.getString("userName");
                Email = snapshot.getString("email");
                DatabaseReference profile = reference.child("Profiles").child(snapshot.getId());
                profile.child("profileUrl").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.getValue()!=null) {
                            url[0] = snapshot.getValue().toString();
                            getHeaderView(navigationView,UserName,Email,url[0],context);
                            if(context==MainActivity.this) setProfileImg(url[0],accountProfile,progressBar,context);

                        }else {
                            url[0] = "";
                            getHeaderView(navigationView,UserName,Email,url[0],context);
                            if(context==MainActivity.this) setProfileImg(url[0],accountProfile,progressBar,context);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    public void setProfileImg(String url, ImageView imageView, ProgressBar progressBar,Context context){
        if(url.equals("")){
            progressBar.setVisibility(View.GONE);
            imageView.setBackground(context.getDrawable(R.drawable.circle_boundary));
            imageView.setImageResource(R.drawable.default_profile);
        }else {
            if(context!=null) {
                if (isValidContextForGlide(context)) {
                    Glide.with(context).load(url)
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    progressBar.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    progressBar.setVisibility(View.GONE);
                                    imageView.setBackground(context.getDrawable(R.drawable.circle_no_boundary));
                                    return false;
                                }
                            }).into(imageView);
                }
            }
        }
    }

    private void configureToolbar(){
        mainToolbar.setTitle(R.string.app_name);
        mainToolbar.setNavigationIcon(R.drawable.main_menu);
        mainToolbar.setNavigationOnClickListener(view -> mainDrawerLayout.openDrawer(GravityCompat.START));
    }

    public void navigationViewMenu(NavigationView navigationView,ArrayList<NewClassroomModel> classroomList){
        Menu menu=navigationView.getMenu();
        menu.clear();
//        extractUser(navigationView);
        mainMenu(menu,0);
        teachingMenu(menu,10,classroomList);
        enrolledMenu(menu,20,classroomList);
        othersMenu(menu,30);
    }

    public void getHeaderView(NavigationView navigationView,String UserName,String Email,String url,Context context){
        View view = navigationView.getHeaderView(0);
        ImageView profile = view.findViewById(R.id.user_profile);
        TextView userName = view.findViewById(R.id.user_name);
        TextView email = view.findViewById(R.id.user_email);
        ProgressBar progressBar = view.findViewById(R.id.progress_bar);
        ProgressBar progressBar1 = view.findViewById(R.id.progress_bar1);
        ProgressBar progressBar2 = view.findViewById(R.id.progress_bar2);
        setProfileImg(url,profile,progressBar,context);
        progressBar1.setVisibility(View.GONE);
        userName.setText(UserName);
        progressBar2.setVisibility(View.GONE);
        email.setText(Email);
        profile.setOnClickListener(view1 -> {
            context.startActivity(new Intent(context,ProfileImageActivity.class));
        });
    }

    public void navigationViewController(NavigationView navigationView, DrawerLayout drawerLayout, ArrayList<NewClassroomModel> classroomList, Context context, FirebaseAuth auth){
        navigationView.setNavigationItemSelectedListener(item -> {
            if(item.getItemId()==1){
                Intent intent = new Intent(context,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                item.setChecked(true);
                checkId=item.getItemId();
            }

            if(item.getItemId()==2){
                context.startActivity(new Intent(context,NotificationActivity.class));
//                navigationView.getMenu().getItem(checkId).setChecked(false);
//                item.setChecked(true);
                checkId=item.getItemId();
            }

            if(item.getItemId()==21){
                context.startActivity(new Intent(context,TodoActivity.class));
//                item.setChecked(true);
//                checkId=item.getItemId();
            }

            if(item.getItemId()==41){
                context.startActivity(new Intent(context,SettingsActivity.class));
//                item.setChecked(true);
//                checkId=item.getItemId();
            }

            if(item.getItemId()==42){
                auth.signOut();
                Intent intent = new Intent(context,LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
                Toast.makeText(context, "Logout", Toast.LENGTH_SHORT).show();
            }

            for(NewClassroomModel ncm:classroomList){
                if(item.getItemId()==ncm.getKey()){
                    sidePassData(ncm,classroomList,context);
                    break;
                }
            }

            if (drawerLayout.isDrawerOpen(GravityCompat.START))
                drawerLayout.closeDrawer(GravityCompat.START);
            return true;
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
                MenuItem menuItem = teaching.add(id, ncm.getKey(), Menu.NONE, ncm.getClassroomName());
                teachingItemDesign(menuItem, ncm);
//                if(menuItem.getItemId()==uncheckId) menuItem.setChecked(false);
            }
        }
    }

    public void enrolledMenu(Menu menu,int id,ArrayList<NewClassroomModel>classroomList){
        SubMenu enrolled=menu.addSubMenu(id,SubMenu.NONE,SubMenu.NONE,R.string.Enrolled);
//        if(uncheckId==220) todo.setChecked(false);

        int x=0;
        for(NewClassroomModel ncm:classroomList){
            if(ncm.getOccupation().toLowerCase().equals("student")) {
                x++;
                if(x==1){
                    MenuItem todo=enrolled.add(id, id+1,Menu.NONE,R.string.Todo);
                    todo.setIcon(R.drawable.todo);
                }
                MenuItem menuItem = enrolled.add(id, ncm.getKey(), Menu.NONE, ncm.getClassroomName());
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

    public void sidePassData(NewClassroomModel classroomModel, ArrayList<NewClassroomModel> classroomList, Context context){
        Intent intent=new Intent(context,RoomActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Bundle data=new Bundle();
        data.putSerializable("classroomList",(Serializable) classroomList);
        data.putSerializable("ClassroomModel",classroomModel);
        intent.putExtra("data",data);
        context.startActivity(intent);
    }

    public void passData(NewClassroomModel classroomModel, ArrayList<NewClassroomModel> classroomList, Context context){
        Intent intent=new Intent(context,RoomActivity.class);
        Bundle data=new Bundle();
        data.putString("UserName",UserName);
        data.putString("Email",Email);
        data.putString("UserId",UserID);
        data.putSerializable("classroomList",(Serializable) classroomList);
        data.putSerializable("ClassroomModel",classroomModel);
        intent.putExtra("data",data);
        startActivity(intent);
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
                            String Date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                            String Time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                            String Timestamp = Date+" "+Time;

                            ClassId = classrooms.document().getId();

                            AtomicLong key = new AtomicLong();
                            allClassrooms.get().addOnCompleteListener(task -> {
                                key.set(task.getResult().size());
                                NewClassroomModel newClassroomModel=new NewClassroomModel(ClassName,Section,ClassId,UserID,UserName,"teacher",0,100+key.intValue()+1,Timestamp);
                                setClassroom(newClassroomModel,ClassId,Timestamp);
                            });
                        }
                    }
                });
    }

    public void registerActivityForJoinClass(){
        activityResultLauncherForJoinClass=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        int resultCode=result.getResultCode();
                        Intent data=result.getData();

                        if(resultCode==RESULT_OK && data!=null){
                            ArrayList<NewClassroomModel> cL= (ArrayList<NewClassroomModel>) data.getSerializableExtra("CL");
                            NewClassroomModel model = cL.get(0);
                            String Date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                            String Time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                            String Timestamp = Date+" "+Time;

                            NewClassroomModel newClassroomModel=new NewClassroomModel(model.getClassroomName(),model.getSection(),model.getClassId(),model.getUserId(),model.getUserName(),model.getOccupation(),model.getNoOfStudents()+1,model.getKey(),Timestamp);

                            NewPeopleModel newPeopleModel = new NewPeopleModel(UserID,UserName,Email,Timestamp);

                            allClassrooms.document(model.getClassId())
                                    .collection("Students")
                                    .document(UserID)
                                    .set(newPeopleModel)
                                    .addOnCompleteListener(task -> {
                                        if(task.isSuccessful()){
                                            classrooms.document(model.getClassId()).set(newClassroomModel).addOnCompleteListener(task1 -> {
                                                if (task1.isSuccessful()) {
                                                    Toast.makeText(MainActivity.this, "Class joined", Toast.LENGTH_SHORT).show();
                                                    extractClassrooms();
                                                } else {
                                                    Toast.makeText(MainActivity.this, "Class not joined", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(e -> {
                                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            });
                                        }
                                    }).addOnFailureListener(e -> {
                                        Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                    });
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
            extractAllClassroom();
        }
    }


    private void setClassroom(NewClassroomModel newClassroomModel,String ClassId,String Timestamp){
        allClassrooms.document(ClassId).set(newClassroomModel).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                classrooms.document(ClassId).set(newClassroomModel).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        NewPeopleModel newPeopleModel = new NewPeopleModel(UserID,UserName,Email,Timestamp);
                        allClassrooms.document(ClassId)
                                .collection("Teachers")
                                .document(UserID)
                                .set(newPeopleModel)
                                .addOnCompleteListener(task2 -> {
                                    if(task2.isSuccessful()){
                                        extractClassrooms();
                                        Toast.makeText(MainActivity.this, "Class added", Toast.LENGTH_SHORT).show();
                                    }else {
                                        Toast.makeText(MainActivity.this, "Class not added", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(e -> {
                                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
        });
//      adapter.notifyDataSetChanged();
//      navigationViewMenu(mainNavigationView,classroomList);
    }

    private void extractAllClassroom(){
        AtomicInteger tot = new AtomicInteger();
        allClassrooms.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                tot.set(task.getResult().size());
                allClassroomList = (ArrayList<NewClassroomModel>) task.getResult().toObjects(NewClassroomModel.class);
                Intent intent=new Intent(getApplicationContext(),JoinClassroomActivity.class);
                intent.putExtra("allClassroomList",(Serializable) allClassroomList);
                activityResultLauncherForJoinClass.launch(intent);
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
        });
    }

    private void updateNoOfStudents(String ClassId, AtomicInteger tot, List<Long> students){
        allClassrooms.document(ClassId)
                .collection("Students")
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        long NoOfStudents = task.getResult().size();
                        students.add(NoOfStudents);
                        HashMap<String,Object> map = new HashMap<>();
                        map.put("noOfStudents",NoOfStudents);
                        allClassrooms.document(ClassId)
                                .update(map)
                                .addOnCompleteListener(task1 -> {
                                    if(task1.isSuccessful()){
                                        classrooms.document(ClassId)
                                                .update(map)
                                                .addOnCompleteListener(task2 -> {
                                                    if(task2.isSuccessful()){
                                                        if(students.size()==tot.intValue()) {
                                                            classData();
                                                        }
                                                    }else {
                                                        Toast.makeText(MainActivity.this,task1.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                                    }
                                                }).addOnFailureListener(e -> {
                                                    Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                                });
                                    }else {
                                        Toast.makeText(MainActivity.this,task1.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(e -> {
                                    Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                });
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                });
    }
}