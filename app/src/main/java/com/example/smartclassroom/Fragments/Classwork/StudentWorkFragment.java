package com.example.smartclassroom.Fragments.Classwork;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.smartclassroom.Activities.DocumentViewActivity;
import com.example.smartclassroom.Activities.StudentWorkActivity;
import com.example.smartclassroom.Adapters.AttachmentViewAdapter;
import com.example.smartclassroom.Adapters.PeopleViewAdapter;
import com.example.smartclassroom.Adapters.StudentWorkViewAdapter;
import com.example.smartclassroom.Models.CommentDetailsModel;
import com.example.smartclassroom.Models.NewClassroomModel;
import com.example.smartclassroom.Models.NewPeopleModel;
import com.example.smartclassroom.Models.NewStreamModel;
import com.example.smartclassroom.R;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class StudentWorkFragment extends Fragment {

    private View view;
    private RecyclerView studentWorkRv;
    private NewStreamModel model;
    private NewClassroomModel classroomModel;
    private CommentDetailsModel detailsModel;
    private CollectionReference allStudents;
    private StudentWorkViewAdapter adapter;
    private ArrayList<NewPeopleModel> studentList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_student_work, container, false);
        getData();
        initialization();
        firebaseInitialization();
        setStudentWorkRv();
        getAllStudents();
        return view;
    }

    private void initialization(){
        studentWorkRv = view.findViewById(R.id.student_work_rv);
    }

    private void firebaseInitialization(){
        allStudents = FirebaseFirestore.getInstance().collection("Classrooms").document(detailsModel.getClassId()).collection("Students");
    }

    private void getAllStudents(){
        allStudents.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                studentList = (ArrayList<NewPeopleModel>) task.getResult().toObjects(NewPeopleModel.class);
                adapterWork();
            }else {
                Toast.makeText(getContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
        });
    }

    private void setStudentWorkRv() {
        studentWorkRv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new StudentWorkViewAdapter();
        studentWorkRv.setAdapter(adapter);
        adapterWork();
    }

    private void adapterWork() {
        adapter.setStudentWorkViewAdapter(studentList,detailsModel, getContext());
        adapter.setOnItemClickListener(new StudentWorkViewAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
            }
        });
    }

    private void getData() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            model = (NewStreamModel) bundle.getSerializable("Model");
            classroomModel = (NewClassroomModel) bundle.getSerializable("ClassroomModel");
            detailsModel = (CommentDetailsModel) bundle.getSerializable("DetailsModel");
        }
    }

}