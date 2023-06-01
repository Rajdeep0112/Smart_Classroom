package com.example.smartclassroom.Adapters;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.smartclassroom.Fragments.Classwork.InstructionsFragment;
import com.example.smartclassroom.Fragments.Classwork.StudentWorkFragment;
import com.example.smartclassroom.Fragments.Classwork.YourWorkFragment;
import com.example.smartclassroom.Fragments.Todo.AssignedFragment;
import com.example.smartclassroom.Fragments.Todo.DoneFragment;
import com.example.smartclassroom.Fragments.Todo.MissingFragment;
import com.example.smartclassroom.Models.CommentDetailsModel;
import com.example.smartclassroom.Models.NewClassroomModel;
import com.example.smartclassroom.Models.NewStreamModel;

import java.util.ArrayList;

public class ClassworkVpAdapter extends FragmentStateAdapter {
    private ArrayList<Fragment> fragments=new ArrayList<>();
    private ArrayList<String> headers=new ArrayList<>();
    private NewStreamModel model = new NewStreamModel();
    private NewClassroomModel classroomModel = new NewClassroomModel();
    private String source;
    private CommentDetailsModel detailsModel;
    public ClassworkVpAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, String source, NewStreamModel model, NewClassroomModel classroomModel, CommentDetailsModel detailsModel) {
        super(fragmentManager, lifecycle);
        this.source = source;
        this.model = model;
        this.classroomModel = classroomModel;
        this.detailsModel = detailsModel;
        initData();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        sendClassDetails(fragments.get(position));
        return fragments.get(position);
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }

    private void initData(){
        addData(new InstructionsFragment(),"Instructions");
        if(classroomModel.getOccupation().toLowerCase().equals("teacher")) addData(new StudentWorkFragment(),"Student work");
        else addData(new YourWorkFragment(),"Your work");
    }

    private void addData(Fragment fragment,String header){
        fragments.add(fragment);
        headers.add(header);
    }

    public String getHeader(int position){
        return headers.get(position);
    }

    private void sendClassDetails(Fragment fragment){
        Bundle bundle=new Bundle();
        bundle.putString("Source",source);
        bundle.putSerializable("Model",model);
        bundle.putSerializable("ClassroomModel",classroomModel);
        bundle.putSerializable("DetailsModel",detailsModel);
        fragment.setArguments(bundle);
    }
}
