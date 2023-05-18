package com.example.smartclassroom.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import com.example.smartclassroom.Adapters.CommentViewAdapter;
import com.example.smartclassroom.Models.CommentDetailsModel;
import com.example.smartclassroom.Models.NewCommentModel;
import com.example.smartclassroom.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;

public class CommentActivity extends AppCompatActivity {

    private Toolbar commentToolbar;
    private EditText message;
    private FloatingActionButton send;
    private RecyclerView recyclerView;
    private FirebaseDatabase database;
    private DatabaseReference reference, msgDetails;
    private CommentViewAdapter adapter;
    private CollectionReference allClasswork, allStreams, allUsers;
    private List<NewCommentModel> comments;
    private CommentDetailsModel detailsModel;
    private NewCommentModel newCommentModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        getData();
        initialisations();
        setSupportActionBar(commentToolbar);
        configureToolbar();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        comments = new ArrayList<>();


        send.setOnClickListener(view -> {
            String msg = message.getText().toString().trim();
            if (!msg.equals("")) {
                sendMessage(msg);
                message.setText("");
            }
        });
        updateNoOfComments();
        getMessage();

    }

    private void initialisations() {
        commentToolbar = findViewById(R.id.comment_toolbar);
        message = findViewById(R.id.message);
        send = findViewById(R.id.send);
        recyclerView = findViewById(R.id.comment_recycler_view);
        allClasswork = FirebaseFirestore.getInstance().collection("Classrooms").document(detailsModel.getClassId()).collection("Classwork");
        allStreams = FirebaseFirestore.getInstance().collection("Classrooms").document(detailsModel.getClassId()).collection("Streams");
        allUsers = FirebaseFirestore.getInstance().collection("Users");
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        msgDetails = reference.child("Messages").child(detailsModel.getClassId()).child(detailsModel.getId());
    }

    private void getData() {
        Intent intent = getIntent();
        Bundle data = intent.getBundleExtra("data");
        detailsModel = (CommentDetailsModel) data.getSerializable("DetailsModel");
    }

    private void configureToolbar() {
        commentToolbar.setNavigationIcon(R.drawable.back);
        commentToolbar.setNavigationOnClickListener(view -> onBackPressed());
    }

    private void sendMessage(String msg) {
        String key = reference.push().getKey();
        String Date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String Time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        String Timestamp = Date + " " + Time;
        newCommentModel = new NewCommentModel(key, msg, detailsModel.getUserId(), detailsModel.getUserName(), detailsModel.getUserEmail(), Date, Time, Timestamp);

        msgDetails.child(key).setValue(newCommentModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                msgDetails.child(key).setValue(newCommentModel);
                updateNoOfComments();
            }
        });
    }

    public void getMessage() {

        msgDetails.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                NewCommentModel modelClass = snapshot.getValue(NewCommentModel.class);
                comments.add(modelClass);
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(comments.size() - 1);
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
        adapter = new CommentViewAdapter(comments, detailsModel.getUserId());
        recyclerView.setAdapter(adapter);
    }

    private void updateNoOfComments() {
        AtomicLong noOfComments = new AtomicLong();
        msgDetails.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                HashMap<String,NewCommentModel> model = (HashMap<String, NewCommentModel>) task.getResult().getValue();
                if(model==null){
                    noOfComments.set(0);
                }else {
                    noOfComments.set(model.size());
                }
                allStreams.document(detailsModel.getId()).update("noOfComments", noOfComments.longValue());
                allClasswork.document(detailsModel.getId()).update("noOfComments", noOfComments.longValue());

                CollectionReference classrooms, classwork, streams;
                classrooms = allUsers.document(detailsModel.getUserId()).collection("Classrooms");
                classwork = classrooms.document(detailsModel.getClassId()).collection("Classwork");
                streams = classrooms.document(detailsModel.getClassId()).collection("Streams");
                streams.document(detailsModel.getId()).update("noOfComments", noOfComments.longValue());
                classwork.document(detailsModel.getId()).update("noOfComments", noOfComments.longValue());
            }
        });
    }
}