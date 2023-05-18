package com.example.smartclassroom.Fragments.Room;

import static android.app.Activity.RESULT_OK;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartclassroom.Activities.AddNoticeActivity;
import com.example.smartclassroom.Activities.ClassworkActivity;
import com.example.smartclassroom.Activities.NoticeActivity;
import com.example.smartclassroom.Adapters.NoticeViewAdapter;
import com.example.smartclassroom.Models.CommentDetailsModel;
import com.example.smartclassroom.Models.NewClassroomModel;
import com.example.smartclassroom.Models.NewFileModel;
import com.example.smartclassroom.Models.NewNoticeModel;
import com.example.smartclassroom.Models.UploadFileModel;
import com.example.smartclassroom.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class StreamFragment extends Fragment{

    private String NoticeId;
    private TextView className, section;
    private CardView addNotice;
    private ImageButton noticeMore;
    private RecyclerView recyclerView;
    private ArrayList<NewNoticeModel> noticeList = new ArrayList<>();
    private CommentDetailsModel detailsModel;
    private NewClassroomModel classroomModel;
    private NoticeViewAdapter adapter;
    private FirebaseAuth auth;
    private DocumentReference user, room;
    private CollectionReference streams, allStreams;
    private View view;
    private String UserName,Email,UserID;
    private ArrayList<NewFileModel> fileList = new ArrayList<>();
    private Uri uri;
    private StorageReference storageReference;
    private FirebaseDatabase database;
    private DatabaseReference reference, documents;

    ActivityResultLauncher<Intent> activityResultLauncherForCreateNotice;

    @Override
    public void onResume() {
        super.onResume();
        extractData(view);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_stream, container, false);
        registerActivityForCreateNotice();
        initialisation(view);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            classroomModel = (NewClassroomModel) bundle.getSerializable("ClassroomModel");
            UserID=bundle.getString("UserId");
            Email=bundle.getString("Email");
            UserName=bundle.getString("UserName");
            firebaseInitialisation();
        }

        className.setText(classroomModel.getClassroomName());
        section.setText(classroomModel.getSection());

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        adapter = new NoticeViewAdapter();
        recyclerView.setAdapter(adapter);
        adapterWork(view, adapter);
        extractData(view);


        addNotice.setOnClickListener(view1 -> {
            Intent intent = new Intent(view.getContext(), AddNoticeActivity.class);
            activityResultLauncherForCreateNotice.launch(intent);
        });

        return view;
    }

    private void initialisation(View view) {
        className = view.findViewById(R.id.room_name);
        section = view.findViewById(R.id.room_section);
        addNotice = view.findViewById(R.id.addNotices);
        noticeMore = view.findViewById(R.id.noticeMore);
        recyclerView = view.findViewById(R.id.noticeView);
        addNotice = view.findViewById(R.id.addNotices);
    }

    private void firebaseInitialisation() {
        auth = FirebaseAuth.getInstance();
        user = FirebaseFirestore.getInstance().collection("Users").document(auth.getCurrentUser().getUid());
        room = user.collection("Classrooms").document(classroomModel.getClassId());
        streams = room.collection("Streams");
        allStreams = FirebaseFirestore.getInstance().collection("Classrooms").document(classroomModel.getClassId()).collection("Streams");
        storageReference = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
    }

    private void adapterWork(View view, NoticeViewAdapter adapter) {
        detailsModel = new CommentDetailsModel(classroomModel.getClassId(),UserID,UserName,Email);
        adapter.setNotices(noticeList, detailsModel, view.getContext());
        adapter.setOnItemClickListener(new NoticeViewAdapter.onItemClickListener() {
            @Override
            public void onItemClick(NewNoticeModel newNoticeModel) {
                int position = noticeList.indexOf(newNoticeModel);
                if (noticeList.get(position).getNoticeId() != null) {
                    detailsModel = new CommentDetailsModel(classroomModel.getClassId(),noticeList.get(position).getNoticeId(),UserID,UserName,Email);
                    Intent intent = new Intent(getContext(), NoticeActivity.class);
                    passData(classroomModel, noticeList.get(position), detailsModel, intent);
                } else {
                    detailsModel = new CommentDetailsModel(classroomModel.getClassId(),noticeList.get(position).getAssignmentId(),UserID,UserName,Email);
                    Intent intent = new Intent(getContext(), ClassworkActivity.class);
                    passData(classroomModel, noticeList.get(position), detailsModel, intent);
                }
            }
        });
    }

    private void passData(NewClassroomModel newClassroomModel,NewNoticeModel model, CommentDetailsModel detailsModel, Intent intent){
        Bundle data=new Bundle();
        data.putString("Source","stream");
        data.putSerializable("DetailsModel", (Serializable) detailsModel);
        data.putSerializable("ClassroomModel",newClassroomModel);
        data.putSerializable("Model",model);
        intent.putExtra("data",data);
        startActivity(intent);
    }

    private void extractData(View view) {
        noticeList = new ArrayList<>();
        allStreams.orderBy("timestamp", Query.Direction.DESCENDING).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                noticeList = (ArrayList<NewNoticeModel>) task.getResult().toObjects(NewNoticeModel.class);
                adapterWork(view, adapter);
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    public void registerActivityForCreateNotice() {
        activityResultLauncherForCreateNotice = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        int resultCode = result.getResultCode();
                        Intent data = result.getData();

                        if (resultCode == RESULT_OK && data != null) {
                            Bundle bundle = data.getBundleExtra("data");
                            String NoticeShare = data.getStringExtra("NoticeShare");
                            fileList = new ArrayList<>();
                            fileList = (ArrayList<NewFileModel>) bundle.getSerializable("FileModel");
                            String Date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                            String Time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                            String Timestamp = Date + " " + Time;
                            String NoticeId = streams.document().getId();

                            user.get().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot snapshot = task.getResult();
                                    String UserName = snapshot.getString("userName");
                                    NewNoticeModel newNoticeModel = new NewNoticeModel(NoticeId,NoticeShare,classroomModel.getClassId(),UserName,Timestamp,Date,Time,fileList.size());

                                    allStreams.document(NoticeId).set(newNoticeModel).addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            streams.document(NoticeId).set(newNoticeModel).addOnCompleteListener(task2 -> {
                                                if (task2.isSuccessful()) {
                                                    extractData(view);
                                                    if(getContext()!=null) {
                                                        Toast.makeText(getContext(), "Notice posted", Toast.LENGTH_SHORT).show();
                                                    }
                                                } else {
                                                    Toast.makeText(getContext(), task2.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(e -> {
                                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                            });
                                        }
                                    }).addOnFailureListener(e -> {
                                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                                }
                            }).addOnFailureListener(e -> {
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            });

                            uploadFile(classroomModel.getClassId(),NoticeId);
                        }
                    }
                });
    }

    private void uploadFile(String ClassId,String NoticeId){
        documents = reference.child("Attachments").child(ClassId).child(NoticeId);
        for(NewFileModel model : fileList){
            if(model.getFileType()!=null) {
                StorageReference reference = storageReference.child(ClassId+"/"+NoticeId+"/"+System.currentTimeMillis()+"."+getFileExtension(Uri.parse(model.getFilepath())));
                reference.putFile(Uri.parse(model.getFilepath()))
                        .continueWithTask(task -> {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return reference.getDownloadUrl();
                        }).addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                Toast.makeText(getContext(),"File uploaded",Toast.LENGTH_SHORT).show();
                                Uri url = task.getResult();
                                UploadFileModel fileModel = new UploadFileModel(model.getFileName(),url.toString());
                                String id = documents.push().getKey();
                                documents.child(id).setValue(fileModel);
                            }
                        });
            }
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}