package com.example.smartclassroom.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.smartclassroom.Adapters.AttachmentViewAdapter;
import com.example.smartclassroom.Adapters.ClassworkVpAdapter;
import com.example.smartclassroom.Adapters.TodoVpAdapter;
import com.example.smartclassroom.Models.CommentDetailsModel;
import com.example.smartclassroom.Models.NewClassroomModel;
import com.example.smartclassroom.Models.NewCommentModel;
import com.example.smartclassroom.Models.NewStreamModel;
import com.example.smartclassroom.Models.UploadFileModel;
import com.example.smartclassroom.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

public class ClassworkActivity extends AppCompatActivity {

    private Toolbar classworkToolbar;
    private TabLayout classworkTl;
    private ViewPager2 classworkVp;
    private NewStreamModel model = new NewStreamModel();
    private NewClassroomModel classroomModel = new NewClassroomModel();
    private String Source;
    private CommentDetailsModel detailsModel;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_toolbar_menu, menu);
        menu.removeItem(R.id.profile);
        menu.removeItem(R.id.edit);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classwork);
        getData();
        initialisations();
        configureToolbar();
        setTlVp();
    }

    private void initialisations() {
        classworkToolbar = findViewById(R.id.classwork_toolbar);
        classworkTl=findViewById(R.id.classwork_tl);
        classworkVp=findViewById(R.id.classwork_vp);
    }

    private void configureToolbar() {
        classworkToolbar.setTitle("");
        setSupportActionBar(classworkToolbar);
        classworkToolbar.setNavigationIcon(R.drawable.back);
        classworkToolbar.setNavigationOnClickListener(view -> onBackPressed());
    }

    private void setTlVp(){
        ClassworkVpAdapter classworkVpAdapter=new ClassworkVpAdapter(getSupportFragmentManager(),getLifecycle(),Source,model,classroomModel,detailsModel);
        classworkVp.setAdapter(classworkVpAdapter);

        TabLayoutMediator tabLayoutMediator=new TabLayoutMediator(classworkTl,classworkVp,(tab, position) -> {
            tab.setText(classworkVpAdapter.getHeader(position));
        });
        tabLayoutMediator.attach();
    }
    private void getData() {
        Intent intent = getIntent();
        Bundle data = intent.getBundleExtra("data");
        Source = data.getString("Source");
        model = (NewStreamModel) data.getSerializable("Model");
        classroomModel = (NewClassroomModel) data.getSerializable("ClassroomModel");
        detailsModel = (CommentDetailsModel) data.getSerializable("DetailsModel");
    }
}