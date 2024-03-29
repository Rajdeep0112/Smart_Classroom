package com.example.smartclassroom.Fragments.Classwork;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.smartclassroom.Activities.ClassworkActivity;
import com.example.smartclassroom.Activities.CommentActivity;
import com.example.smartclassroom.Activities.DocumentViewActivity;
import com.example.smartclassroom.Adapters.AttachmentViewAdapter;
import com.example.smartclassroom.Models.CommentDetailsModel;
import com.example.smartclassroom.Models.NewClassroomModel;
import com.example.smartclassroom.Models.NewCommentModel;
import com.example.smartclassroom.Models.NewStreamModel;
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

public class InstructionsFragment extends Fragment {

    private View view;
    private TextView title, dueDate, points, desc, noOfComments, txtAttachments;
    private LinearLayout comment;
    private RecyclerView attachment_rv;
    private AttachmentViewAdapter adapter;
    private NewStreamModel model = new NewStreamModel();
    private NewClassroomModel classroomModel = new NewClassroomModel();
    private ArrayList<String> DocName = new ArrayList<>();
    private String Source;
    private CommentDetailsModel detailsModel;
    private DatabaseReference reference, msgDetails, attachments;
    private FirebaseDatabase database;
    private CollectionReference allClasswork, allStreams;
    private ArrayList<UploadFileModel> uploadList = new ArrayList<>();

    @Override
    public void onResume() {
        super.onResume();
        updateNoOfComments();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_instructions, container, false);
        getData();
        initialisations();
        setData();

        attachment_rv.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new AttachmentViewAdapter();
        attachment_rv.setAdapter(adapter);
        adapterWork();

        comment.setOnClickListener(view -> {
            sendDetails();
        });
        getAttachments();
        return view;
    }

    private void initialisations() {
        title = view.findViewById(R.id.assignment_title);
        dueDate = view.findViewById(R.id.due_date);
        points = view.findViewById(R.id.points);
        desc = view.findViewById(R.id.desc);
        noOfComments = view.findViewById(R.id.classwork_no_of_comments);
        comment = view.findViewById(R.id.classwork_comment);
        attachment_rv = view.findViewById(R.id.classwork_attachments_recycler_view);
        txtAttachments = view.findViewById(R.id.attachments);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        msgDetails = reference.child("Messages").child(detailsModel.getClassId()).child(detailsModel.getId());
        attachments = reference.child("Attachments").child(detailsModel.getClassId()).child(detailsModel.getId()).child("Attachments");
        allStreams = FirebaseFirestore.getInstance().collection("Classrooms").document(detailsModel.getClassId()).collection("Streams");
        allClasswork = FirebaseFirestore.getInstance().collection("Classrooms").document(detailsModel.getClassId()).collection("Classwork");
    }

    private void updateNoOfComments() {
        AtomicLong noComments = new AtomicLong();
        msgDetails.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                HashMap<String, NewCommentModel> map = (HashMap<String, NewCommentModel>) task.getResult().getValue();
                if (map == null) {
                    noComments.set(0);
                    model.setNoOfComments(noComments.longValue());
                    noOfComments.setText(model.getNoOfComments() + " class comments");
                } else {
                    noComments.set(map.size());
                    model.setNoOfComments(noComments.longValue());
                    noOfComments.setText(model.getNoOfComments() + " class comments");
                }
            }
        });
    }

    private void getData() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            Source = bundle.getString("Source");
            model = (NewStreamModel) bundle.getSerializable("Model");
            classroomModel = (NewClassroomModel) bundle.getSerializable("ClassroomModel");
            detailsModel = (CommentDetailsModel) bundle.getSerializable("DetailsModel");
        }
    }

    private void sendDetails() {
        Intent intent = new Intent(getContext(), CommentActivity.class);
        Bundle data = new Bundle();
        data.putSerializable("DetailsModel", detailsModel);
        intent.putExtra("data", data);
        startActivity(intent);
    }

    private void setData() {
        title.setText(model.getTitle());
        dueDate.setText("Due " + model.getDueDate());
        points.setText(model.getPoints() + " points");
        desc.setText(model.getDesc());
        noOfComments.setText(model.getNoOfComments() + " class comments");
    }

    private void adapterWork() {
        adapter.setAttachmentViewAdapter(uploadList, getContext());
        adapter.setOnItemClickListener(new AttachmentViewAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getContext(), DocumentViewActivity.class);
                Bundle data = new Bundle();
                data.putSerializable("UploadModel", uploadList.get(position));
                intent.putExtra("data", data);
                startActivity(intent);
            }
        });
    }

    private void getAttachments() {
        uploadList = new ArrayList<>();
        attachments.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                UploadFileModel modelClass = snapshot.getValue(UploadFileModel.class);
                uploadList.add(modelClass);
                if (uploadList.size() > 0) {
                    txtAttachments.setVisibility(View.VISIBLE);
                } else {
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