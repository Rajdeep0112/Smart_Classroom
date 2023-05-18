package com.example.smartclassroom.Fragments.Room;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.smartclassroom.Adapters.PeopleViewAdapter;
import com.example.smartclassroom.Models.NewClassroomModel;
import com.example.smartclassroom.Models.NewPeopleModel;
import com.example.smartclassroom.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class PeopleFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private ArrayList<NewPeopleModel> peopleList=new ArrayList<>();
    private SectionedRecyclerViewAdapter adapter;
    private NewClassroomModel classroomModel;
    private FirebaseAuth auth;
    private DocumentReference user,room;
    private CollectionReference teachers,students;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_people, container, false);
        initialisations();
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            classroomModel = (NewClassroomModel) bundle.getSerializable("ClassroomModel");
            firebaseInitialisation();
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new SectionedRecyclerViewAdapter();
        recyclerView.setAdapter(adapter);
        extractTeachers();

        return view;
    }

    private void initialisations(){
        recyclerView=view.findViewById(R.id.people_recycler_view);
    }

    private void firebaseInitialisation(){
        auth= FirebaseAuth.getInstance();
        user= FirebaseFirestore.getInstance().collection("Users").document(auth.getCurrentUser().getUid());
        room=user.collection("Classrooms").document(classroomModel.getClassId());
        teachers=FirebaseFirestore.getInstance().collection("Classrooms").document(classroomModel.getClassId()).collection("Teachers");
        students=FirebaseFirestore.getInstance().collection("Classrooms").document(classroomModel.getClassId()).collection("Students");
    }

    private void extractTeachers(){
        teachers.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                peopleList = (ArrayList<NewPeopleModel>) task.getResult().toObjects(NewPeopleModel.class);
                adapter.addSection(new PeopleViewAdapter(getContext(),peopleList,"Teachers"));
                extractStudents();
            }else {
                Toast.makeText(getContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
        });
    }

    private void extractStudents(){
        students.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                peopleList = (ArrayList<NewPeopleModel>) task.getResult().toObjects(NewPeopleModel.class);
                adapter.addSection(new PeopleViewAdapter(getContext(),peopleList,"Classmates"));
                recyclerView.setAdapter(adapter);
            }else {
                Toast.makeText(getContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
        });
    }
}