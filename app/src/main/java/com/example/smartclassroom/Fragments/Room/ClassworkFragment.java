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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.example.smartclassroom.Activities.AddAssignmentActivity;
import com.example.smartclassroom.Activities.ClassworkActivity;
import com.example.smartclassroom.Adapters.ClassworkViewAdapter;
import com.example.smartclassroom.Models.CommentDetailsModel;
import com.example.smartclassroom.Models.NewAssignmentModel;
import com.example.smartclassroom.Models.NewClassroomModel;
import com.example.smartclassroom.Models.NewFileModel;
import com.example.smartclassroom.Models.NewNoticeModel;
import com.example.smartclassroom.Models.UploadFileModel;
import com.example.smartclassroom.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ClassworkFragment extends Fragment {

    private ArrayList<NewAssignmentModel> assignmentList=new ArrayList<>();
    private RecyclerView recyclerView;
    private ClassworkViewAdapter adapter;
    private FloatingActionButton fab;
    private View view;
    private FirebaseAuth auth;
    private DocumentReference user,room;
    private CollectionReference classwork,allClasswork,streams,allStreams;
    private NewClassroomModel classroomModel;
    private String NoticeId;
    private CommentDetailsModel detailsModel;
    private String UserID,UserName,Email;
    private ArrayList<NewFileModel> fileList = new ArrayList<>();
    private Uri uri;
    private StorageReference storageReference;
    private FirebaseDatabase database;
    private DatabaseReference reference, documents;

    ActivityResultLauncher<Intent> activityResultLauncherForCreateAssignment;

    @Override
    public void onResume() {
        super.onResume();
        extractData(view);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_classwork, container, false);
        registerForActivityCreateAssignment();
        initialisations(view);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            classroomModel = (NewClassroomModel) bundle.getSerializable("ClassroomModel");
            UserID=bundle.getString("UserId");
            Email=bundle.getString("Email");
            UserName=bundle.getString("UserName");
            firebaseInitialisation();
        }

        if(classroomModel.getOccupation().toLowerCase().equals("teacher")) fab.setVisibility(View.VISIBLE);
        else fab.setVisibility(View.GONE);

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        adapter=new ClassworkViewAdapter();
        recyclerView.setAdapter(adapter);
        adapterWork(view,adapter);
        extractData(view);

        fab.setOnClickListener(view1 -> {
            Intent intent=new Intent(view.getContext(), AddAssignmentActivity.class);
            activityResultLauncherForCreateAssignment.launch(intent);
        });

        return view;
    }

    private void initialisations(View view){
        recyclerView=view.findViewById(R.id.classwork_recycler_view);
        fab=view.findViewById(R.id.classwork_fab);
    }

    private void firebaseInitialisation(){
        auth=FirebaseAuth.getInstance();
        user= FirebaseFirestore.getInstance().collection("Users").document(auth.getCurrentUser().getUid());
        room=user.collection("Classrooms").document(classroomModel.getClassId());
        classwork=room.collection("Classwork");
        allClasswork=FirebaseFirestore.getInstance().collection("Classrooms").document(classroomModel.getClassId()).collection("Classwork");
        streams=room.collection("Streams");
        allStreams=FirebaseFirestore.getInstance().collection("Classrooms").document(classroomModel.getClassId()).collection("Streams");
        storageReference = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
    }

    private void extractData(View view) {
        assignmentList = new ArrayList<>();
        allClasswork.orderBy("timestamp", Query.Direction.DESCENDING).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                assignmentList = (ArrayList<NewAssignmentModel>) task.getResult().toObjects(NewAssignmentModel.class);
                adapterWork(view, adapter);
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }


    private void adapterWork(View view, ClassworkViewAdapter adapter) {
        adapter.setAssignments(assignmentList,view.getContext());
        adapter.setOnItemClickListener(new ClassworkViewAdapter.onItemClickListener() {
            @Override
            public void onItemClick(NewAssignmentModel newAssignmentModel) {
                int position = assignmentList.indexOf(newAssignmentModel);
                detailsModel = new CommentDetailsModel(classroomModel.getClassId(),assignmentList.get(position).getAssignmentId(),UserID,UserName,Email);
                passData(classroomModel,assignmentList.get(position),detailsModel);
            }
        });
    }

    private void passData(NewClassroomModel newClassroomModel,NewAssignmentModel model,CommentDetailsModel detailsModel){
        Intent intent = new Intent(getContext(), ClassworkActivity.class);
        Bundle data=new Bundle();
        data.putString("Source","classwork");
        data.putSerializable("ClassroomModel",newClassroomModel);
        data.putSerializable("DetailsModel",detailsModel);
        data.putSerializable("Model",model);
        intent.putExtra("data",data);
        startActivity(intent);
    }

    public void registerForActivityCreateAssignment(){
        activityResultLauncherForCreateAssignment=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        int resultCode=result.getResultCode();
                        Intent data=result.getData();

                        if(resultCode==RESULT_OK && data!=null){
                            Bundle bundle = data.getBundleExtra("data");
                            String AssignmentId=classwork.document().getId();
                            fileList = new ArrayList<>();
                            fileList = (ArrayList<NewFileModel>) bundle.getSerializable("FileModel");
                            String Title=data.getStringExtra("assignmentTitle");
                            String Desc=data.getStringExtra("assignmentDesc");
                            String ClassId=classroomModel.getClassId();
                            String Date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                            String Time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                            String Timestamp = Date + " " + Time;
                            String DueDate=Timestamp;
                            String Points="100";
                            String CurrentTimestamp=Timestamp;
                            long NoOfAttachment=0;
                            long NoOfComments=0;

                            NewAssignmentModel newAssignmentModel = new NewAssignmentModel(AssignmentId,Title,Desc,ClassId,DueDate,Points,Timestamp,Date,Time,CurrentTimestamp,NoOfAttachment,NoOfComments);
                            NewNoticeModel newNoticeModel = new NewNoticeModel(AssignmentId,Title,Desc,ClassId,DueDate,Points,Timestamp,Date,Time,CurrentTimestamp,NoOfAttachment,NoOfComments);

                            allClasswork.document(AssignmentId).set(newAssignmentModel).addOnCompleteListener(task -> {
                                if(task.isSuccessful()){
                                    classwork.document(AssignmentId).set(newAssignmentModel).addOnCompleteListener(task1 -> {
                                        if(task1.isSuccessful()){
                                            allStreams.document(AssignmentId).set(newNoticeModel).addOnCompleteListener(task2 -> {
                                                if(task2.isSuccessful()){
                                                    streams.document(AssignmentId).set(newNoticeModel).addOnCompleteListener(task3 -> {
                                                        if(task3.isSuccessful()){
                                                            extractData(view);
                                                            if(getContext()!=null) {
                                                                Toast.makeText(getContext(), "Assignment posted", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }else {
                                                            Toast.makeText(getContext(), task3.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
                                }
                            }).addOnFailureListener(e -> {
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            });

                            uploadFile(classroomModel.getClassId(),AssignmentId);
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