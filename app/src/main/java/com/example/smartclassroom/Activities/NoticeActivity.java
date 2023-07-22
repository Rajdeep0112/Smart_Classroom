package com.example.smartclassroom.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.smartclassroom.Adapters.AttachmentViewAdapter;
import com.example.smartclassroom.Models.CommentDetailsModel;
import com.example.smartclassroom.Models.NewCommentModel;
import com.example.smartclassroom.Models.NewStreamModel;
import com.example.smartclassroom.Models.UploadFileModel;
import com.example.smartclassroom.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

public class NoticeActivity extends AppCompatActivity {

    private Toolbar noticeToolbar;
    private ImageView userProfile;
    private TextView userName,date,noticeShare,noOfComments,txtAttachments;
    private LinearLayout comment;
    private RecyclerView attachment_rv;
    private ProgressBar progressBar;
    private AttachmentViewAdapter adapter;
    private NewStreamModel model=new NewStreamModel();
    private CommentDetailsModel detailsModel;
    private CollectionReference allStreams;
    private DatabaseReference reference, msgDetails, attachments;
    private FirebaseDatabase database;
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
        menu.removeItem(R.id.edit);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
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
        noticeToolbar = findViewById(R.id.notice_toolbar);
        userProfile = findViewById(R.id.notice_user_profile);
        userName = findViewById(R.id.notice_user_name);
        date = findViewById(R.id.notice_date);
        noticeShare = findViewById(R.id.notice_desc);
        noOfComments = findViewById(R.id.notice_no_of_comments);
        comment = findViewById(R.id.notice_comment);
        attachment_rv = findViewById(R.id.notice_attachments_recycler_view);
        txtAttachments = findViewById(R.id.notice_attachments);
        progressBar = findViewById(R.id.progress_bar);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        msgDetails = reference.child("Messages").child(detailsModel.getClassId()).child(detailsModel.getId());
        attachments = reference.child("Attachments").child(detailsModel.getClassId()).child(detailsModel.getId());
        allStreams = FirebaseFirestore.getInstance().collection("Classrooms").document(detailsModel.getClassId()).collection("Streams");
    }

    private void configureToolbar(){
        noticeToolbar.setTitle("");
        setSupportActionBar(noticeToolbar);
        noticeToolbar.setNavigationIcon(R.drawable.back);
        noticeToolbar.setNavigationOnClickListener(view -> onBackPressed());
    }

    private void updateNoOfComments() {
        AtomicLong noComments = new AtomicLong();
        msgDetails.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                HashMap<String, NewCommentModel> map = (HashMap<String, NewCommentModel>) task.getResult().getValue();
                if(map==null){
                    noComments.set(0);
                    model.setNoOfComments(noComments.longValue());
                    noOfComments.setText(model.getNoOfComments()+" class comments");

                }else {
                    noComments.set(map.size());
                    model.setNoOfComments(noComments.longValue());
                    noOfComments.setText(model.getNoOfComments()+" class comments");
                }
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

//    private void extractData() {
//        noticeList = new ArrayList<>();
//        allStreams.orderBy("timestamp", Query.Direction.DESCENDING).get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                noticeList = (ArrayList<NewNoticeModel>) task.getResult().toObjects(NewNoticeModel.class);
//                model.setNoOfComments(noticeList.get(noticeList.indexOf(model)).getNoOfComments());
//                noOfComments.setText(model.getNoOfComments()+" class comments");
//            }
//        }).addOnFailureListener(e -> {
//            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
//        });
//    }

    private void getData(){
        Intent intent=getIntent();
        Bundle data=intent.getBundleExtra("data");
        model= (NewStreamModel) data.getSerializable("Model");
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
        setUserProfile(model.getUserId(),userProfile,this);
        userName.setText(model.getUserName());
        date.setText(model.getTime());
        noticeShare.setText(model.getNoticeShare());
        noOfComments.setText(model.getNoOfComments()+" class comments");
    }

    private void adapterWork() {
        adapter.setAttachmentViewAdapter(uploadList,this);
        adapter.setOnItemClickListener(new AttachmentViewAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(NoticeActivity.this,DocumentViewActivity.class);
                Bundle data = new Bundle();
                data.putSerializable("UploadModel",uploadList.get(position));
                intent.putExtra("data",data);
                startActivity(intent);
            }
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

    public void setUserProfile(String userId, ImageView imageView, Context context) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference();
        DatabaseReference profile = reference.child("Profiles").child(userId);
        profile.child("profileUrl").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    String url = snapshot.getValue().toString();
                    setProfileImg(url, imageView, context);

                } else {
                    String url = "";
                    setProfileImg(url, imageView, context);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setProfileImg(String url, ImageView imageView, Context context) {
        if (url.equals("")) {
            progressBar.setVisibility(View.GONE);
            imageView.setBackground(context.getDrawable(R.drawable.circle_boundary));
            imageView.setImageResource(R.drawable.default_profile);
        } else {
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
}