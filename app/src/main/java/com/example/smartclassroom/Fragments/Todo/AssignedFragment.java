package com.example.smartclassroom.Fragments.Todo;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.smartclassroom.Activities.CommentActivity;
import com.example.smartclassroom.Activities.DocumentViewActivity;
import com.example.smartclassroom.Adapters.AttachmentViewAdapter;
import com.example.smartclassroom.Adapters.TodoViewAdapter;
import com.example.smartclassroom.Classes.CommonFuncClass;
import com.example.smartclassroom.Models.CommentDetailsModel;
import com.example.smartclassroom.Models.NewClassroomModel;
import com.example.smartclassroom.Models.NewStreamModel;
import com.example.smartclassroom.R;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class AssignedFragment extends Fragment {

    private View view;
    private String tag;
    private CommonFuncClass cfc;
    private RecyclerView todoRv;
    private TodoViewAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_assigned, container, false);
        cfc = new CommonFuncClass(getContext());
        getData();
        initialisations();
        setData();

        todoRv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TodoViewAdapter();
        todoRv.setAdapter(adapter);
        adapterWork();
        return view;
    }

    private void initialisations() {
        todoRv = view.findViewById(R.id.todo_rv);
//        database = FirebaseDatabase.getInstance();
    }

    private void getData() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            tag = bundle.getString("Tag");
        }
    }

    private void sendDetails() {
//        Intent intent = new Intent(getContext(), CommentActivity.class);
//        Bundle data = new Bundle();
//        data.putSerializable("DetailsModel", detailsModel);
//        intent.putExtra("data", data);
//        startActivity(intent);
    }

    private void setData() {
//        cfc.toastShort(tag);
    }

    private void adapterWork() {
        adapter.setTodo(tag, getContext());
        adapter.setOnItemClickListener(new TodoViewAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }
        });
    }
}