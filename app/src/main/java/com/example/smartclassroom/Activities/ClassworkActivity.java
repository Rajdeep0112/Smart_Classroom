package com.example.smartclassroom.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartclassroom.Adapters.AttachmentViewAdapter;
import com.example.smartclassroom.Models.CommentDetailsModel;
import com.example.smartclassroom.Models.NewAssignmentModel;
import com.example.smartclassroom.Models.NewClassroomModel;
import com.example.smartclassroom.Models.NewCommentModel;
import com.example.smartclassroom.Models.NewNoticeModel;
import com.example.smartclassroom.Models.UploadFileModel;
import com.example.smartclassroom.R;
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
    private TextView title,dueDate,points,desc,noOfComments,txtAttachments;
    private LinearLayout comment;
    private RecyclerView attachment_rv;
    private AttachmentViewAdapter adapter;
    private NewNoticeModel model=new NewNoticeModel();
    private NewAssignmentModel model1 = new NewAssignmentModel();
    private NewClassroomModel classroomModel = new NewClassroomModel();
    private ArrayList<String> DocName = new ArrayList<>();
    private String Source;
    private CommentDetailsModel detailsModel;
    private DatabaseReference reference, msgDetails, attachments;
    private FirebaseDatabase database;
    private CollectionReference allClasswork,allStreams;
    private ArrayList<NewNoticeModel> noticeList = new ArrayList<>();
    private ArrayList<NewAssignmentModel> assignmentList = new ArrayList<>();
    private ArrayList<UploadFileModel> uploadList = new ArrayList<>();

    @Override
    protected void onResume() {
        super.onResume();
        updateNoOfComments();
    }

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
        setContentView(R.layout.activity_classwork);
        getData();
        initialisations();
        configureToolbar();
        setData();

        attachment_rv.setLayoutManager(new GridLayoutManager(this,2));
        adapter = new AttachmentViewAdapter();
        attachment_rv.setAdapter(adapter);
        adapterWork();

        comment.setOnClickListener(view -> {
            sendDetails();
        });
        getAttachments();
    }

    private void initialisations(){
        classworkToolbar = findViewById(R.id.classwork_toolbar);
        title = findViewById(R.id.assignment_title);
        dueDate = findViewById(R.id.due_date);
        points = findViewById(R.id.points);
        desc = findViewById(R.id.desc);
        noOfComments = findViewById(R.id.classwork_no_of_comments);
        comment = findViewById(R.id.classwork_comment);
        attachment_rv = findViewById(R.id.classwork_attachments_recycler_view);
        txtAttachments = findViewById(R.id.attachments);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        msgDetails = reference.child("Messages").child(detailsModel.getClassId()).child(detailsModel.getId());
        attachments = reference.child("Attachments").child(detailsModel.getClassId()).child(detailsModel.getId());
        allStreams = FirebaseFirestore.getInstance().collection("Classrooms").document(detailsModel.getClassId()).collection("Streams");
        allClasswork = FirebaseFirestore.getInstance().collection("Classrooms").document(detailsModel.getClassId()).collection("Classwork");
    }

    private void configureToolbar(){
        classworkToolbar.setTitle("");
        setSupportActionBar(classworkToolbar);
        classworkToolbar.setNavigationIcon(R.drawable.back);
        classworkToolbar.setNavigationOnClickListener(view -> onBackPressed());
    }

    private void updateNoOfComments() {
        AtomicLong noComments = new AtomicLong();
        msgDetails.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                HashMap<String, NewCommentModel> map = (HashMap<String, NewCommentModel>) task.getResult().getValue();
                if(map==null){
                    noComments.set(0);
                    if(Source.equals("stream")) {
                        model.setNoOfComments(noComments.longValue());
                        noOfComments.setText(model.getNoOfComments() + " class comments");
                    }else {
                        model1.setNoOfComments(noComments.longValue());
                        noOfComments.setText(model1.getNoOfComments() + " class comments");
                    }

                }else {
                    noComments.set(map.size());
                    if(Source.equals("stream")) {
                        model.setNoOfComments(noComments.longValue());
                        noOfComments.setText(model.getNoOfComments() + " class comments");
                    }else {
                        model1.setNoOfComments(noComments.longValue());
                        noOfComments.setText(model1.getNoOfComments() + " class comments");
                    }
                }
            }
        });
    }

    private void getData(){
        Intent intent=getIntent();
        Bundle data=intent.getBundleExtra("data");
        Source = data.getString("Source");
        if(Source.equals("stream")) {
            model = (NewNoticeModel) data.getSerializable("Model");
        }else {
            model1 = (NewAssignmentModel) data.getSerializable("Model");
        }
        classroomModel = (NewClassroomModel) data.getSerializable("ClassroomModel");
        detailsModel = (CommentDetailsModel) data.getSerializable("DetailsModel");
    }

    private void sendDetails(){
        Intent intent=new Intent(this,CommentActivity.class);
        Bundle data=new Bundle();
        data.putSerializable("DetailsModel", (Serializable) detailsModel);
        intent.putExtra("data",data);
        startActivity(intent);
    }

    private void setData(){
        if(Source.equals("stream")) {
            title.setText(model.getTitle());
            dueDate.setText("Due " + model.getDueDate());
            points.setText(model.getPoints() + " points");
            desc.setText(model.getDesc());
            noOfComments.setText(model.getNoOfComments() + " class comments");
        }else {
            title.setText(model1.getTitle());
            dueDate.setText("Due " + model1.getDueDate());
            points.setText(model1.getPoints() + " points");
            desc.setText(model1.getDesc());
            noOfComments.setText(model1.getNoOfComments() + " class comments");
        }
    }

    private void adapterWork() {
        if(Source.equals("stream")) {
            adapter.setAttachmentViewAdapter(uploadList,this);
        }else {
            adapter.setAttachmentViewAdapter(uploadList,this);
        }
        adapter.setOnItemClickListener(new AttachmentViewAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(ClassworkActivity.this, "" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getAttachments(){
        uploadList = new ArrayList<>();
        attachments.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                UploadFileModel modelClass = snapshot.getValue(UploadFileModel.class);
                uploadList.add(modelClass);
                if(uploadList.size()>0){
                    txtAttachments.setVisibility(View.VISIBLE);
                }else {
                    txtAttachments.setVisibility(View.GONE);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        adapterWork();
    }
}